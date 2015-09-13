package org.aaron.sms.api;

import com.google.protobuf.ByteString;
import io.netty.channel.*;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.aaron.sms.protocol.SMSProtocolChannelInitializer;
import org.aaron.sms.protocol.protobuf.SMSProtocol;
import org.aaron.sms.protocol.protobuf.SMSProtocol.ClientToBrokerMessage.ClientToBrokerMessageType;
import org.aaron.sms.util.FunctionalReentrantReadWriteLock;
import org.aaron.sms.util.RunState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.*;
import static org.aaron.sms.protocol.SMSProtocolMessageUtil.buildClientToBrokerMessage;
import static org.aaron.sms.util.DurationUtils.checkNotNullAndPositive;

abstract class AbstractSMSConnection implements SMSConnection {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSMSConnection.class);

    private final DefaultChannelGroup allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private final DefaultChannelGroup connectedChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private final ConcurrentHashMap<String, SMSMessageListener> subscribedTopicToListener = new ConcurrentHashMap<>();

    private final RunState runState = new RunState();

    private final Set<SMSConnectionStateListener> connectionStateListeners = Collections
            .newSetFromMap(new ConcurrentHashMap<>());

    private final FunctionalReentrantReadWriteLock destroyLock = new FunctionalReentrantReadWriteLock();

    private final Duration reconnectDelay;

    public AbstractSMSConnection(Duration reconnectDelay) {
        this.reconnectDelay = checkNotNullAndPositive(reconnectDelay, "reconnectDelay");
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void registerConnectionStateListener(SMSConnectionStateListener listener) {
        checkNotNull(listener, "listener is null");

        connectionStateListeners.add(listener);
    }

    @Override
    public void unregisterConnectionStateListener(SMSConnectionStateListener listener) {
        checkNotNull(listener, "listener is null");

        connectionStateListeners.remove(listener);
    }

    @Override
    public void start() {
        if (!isAvailable()) {
            throw new IllegalStateException(getClass().getSimpleName() + " is not available");
        } else {
            checkState(runState.start(), "Invalid state for start");

            reconnectAsync(0, TimeUnit.SECONDS);
        }
    }

    @Override
    public boolean isStarted() {
        return (runState.getState() == RunState.State.RUNNING);
    }

    protected abstract ChannelFuture doBootstrapConnection(ChannelInitializer<Channel> channelInitializer);

    private void bootstrapConnection() {
        if (!isStarted()) {
            return;
        }

        final ChannelInitializer<Channel> channelInitializer = new SMSProtocolChannelInitializer(ClientHandler::new,
                SMSProtocol.BrokerToClientMessage.getDefaultInstance());
        final ChannelFuture future = doBootstrapConnection(channelInitializer);
        future.addListener(f -> {
            final boolean success = f.isSuccess();
            LOG.debug("connect success {}", success);
            if (!success) {
                reconnectAsync();
            }
        });
    }

    protected abstract EventLoopGroup getEventLoopGroup();

    private void reconnectAsync() {
        reconnectAsync(reconnectDelay.toMillis(), TimeUnit.MILLISECONDS);
    }

    private void reconnectAsync(long delay, TimeUnit delayTimeUnit) {
        if (!isStarted()) {
            return;
        }

        getEventLoopGroup().schedule(this::bootstrapConnection, delay, delayTimeUnit);
    }

    private void resubscribeToTopics() {
        if (!isStarted()) {
            return;
        }

        LOG.debug("resubscribeToTopics {}", subscribedTopicToListener);
        subscribedTopicToListener.keySet()
                .forEach(topicName -> connectedChannels.write(
                        buildClientToBrokerMessage(
                                ClientToBrokerMessageType.CLIENT_SUBSCRIBE_TO_TOPIC, topicName)));
        connectedChannels.flush();
    }

    @Override
    public void subscribeToTopic(String topicName, SMSMessageListener messageListener) {
        checkNotNull(topicName, "topicName is null");
        checkArgument(topicName.length() > 0, "topicName is empty");
        checkNotNull(messageListener, "messageListener is null");

        if (subscribedTopicToListener.put(topicName, messageListener) == null) {
            connectedChannels.writeAndFlush(
                    buildClientToBrokerMessage(
                            ClientToBrokerMessageType.CLIENT_SUBSCRIBE_TO_TOPIC, topicName));
        }
    }

    @Override
    public void unsubscribeFromTopic(String topicName) {
        checkNotNull(topicName, "topicName is null");
        checkArgument(topicName.length() > 0, "topicName is empty");

        if (subscribedTopicToListener.remove(topicName) != null) {
            connectedChannels.writeAndFlush(
                    buildClientToBrokerMessage(
                            ClientToBrokerMessageType.CLIENT_UNSUBSCRIBE_FROM_TOPIC, topicName));
        }
    }

    @Override
    public void writeToTopic(String topicName, ByteString message) {
        checkNotNull(topicName, "topicName is null");
        checkArgument(topicName.length() > 0, "topicName is empty");
        checkNotNull(message, "message is null");

        connectedChannels.writeAndFlush(
                buildClientToBrokerMessage(
                        ClientToBrokerMessageType.CLIENT_SEND_MESSAGE_TO_TOPIC, topicName, message));
    }

    @Override
    public void destroy() {
        destroyLock.doInWriteLock(() -> {
            if (runState.destroy()) {

                connectionStateListeners.clear();

                subscribedTopicToListener.clear();

                allChannels.close();

            }
        });
    }

    private void handleBrokerTopicMessagePublish(SMSProtocol.BrokerToClientMessage message) {
        checkNotNull(message, "message is null");
        checkNotNull(message.getTopicName(), "topic name is null");
        checkArgument(message.getTopicName().length() > 0, "topic name is empty");
        checkNotNull(message.getMessagePayload(), "message payload is null");

        final SMSMessageListener listener = subscribedTopicToListener.get(message.getTopicName());
        if (listener != null) {
            fireMessageListenerCallback(listener, message.getMessagePayload());
        }
    }

    private void fireConnectionStateListenerCallback(SMSConnectionState newState) {
        connectionStateListeners.forEach(listener -> {
            try {
                listener.connectionStateChanged(newState);
            } catch (Exception e) {
                LOG.warn("fireConnectionStateListenerCallback", e);
            }
        });
    }

    private void fireMessageListenerCallback(SMSMessageListener listener, ByteString message) {
        try {
            listener.handleIncomingMessage(message);
        } catch (Exception e) {
            LOG.warn("fireMessageListenerCallback", e);
        }
    }

    private class ClientHandler extends SimpleChannelInboundHandler<SMSProtocol.BrokerToClientMessage> {

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            LOG.debug("channelRegistered {}", ctx.channel());

			/*
             * Need to synchronize on destroyLock to avoid another thread
			 * calling destroy() between connectionState.get() and
			 * allChannels.add() below.
			 */
            destroyLock.doInReadLock(() -> {
                if (runState.getState() == RunState.State.DESTROYED) {
                    ctx.channel().close();
                } else {
                    allChannels.add(ctx.channel());
                }
            });
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            LOG.debug("channelActive {}", ctx.channel());
            connectedChannels.add(ctx.channel());
            resubscribeToTopics();
            fireConnectionStateListenerCallback(SMSConnectionState.CONNECTED_TO_BROKER);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            LOG.debug("channelInactive {}", ctx.channel());
            fireConnectionStateListenerCallback(SMSConnectionState.NOT_CONNECTED_TO_BROKER);
            reconnectAsync();
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            LOG.debug("channelUnregistered {}", ctx.channel());
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            LOG.debug("exceptionCaught {}", ctx.channel(), cause);
            ctx.channel().close();
        }

        @Override
        public void channelRead0(ChannelHandlerContext ctx, SMSProtocol.BrokerToClientMessage message) {
            try {
                LOG.debug("channelRead0 from {} message = '{}'", ctx.channel(), message);
                switch (message.getMessageType()) {
                    case BROKER_TOPIC_MESSAGE_PUBLISH:
                        handleBrokerTopicMessagePublish(message);
                        break;
                }
            } catch (Exception e) {
                LOG.warn("channelRead0", e);
                ctx.channel().close();
            }
        }
    }
}
