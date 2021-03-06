package org.aaron.sms.broker;

import io.netty.channel.*;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.aaron.sms.protocol.SMSProtocolChannelInitializer;
import org.aaron.sms.protocol.protobuf.SMSProtocol;
import org.aaron.sms.protocol.protobuf.SMSProtocol.BrokerToClientMessage.BrokerToClientMessageType;
import org.aaron.sms.util.FunctionalReentrantReadWriteLock;
import org.aaron.sms.util.RunState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.aaron.sms.protocol.SMSProtocolMessageUtil.buildBrokerToCLientMessage;

abstract class AbstractSMSBrokerServer implements SMSBrokerServer {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSMSBrokerServer.class);

    private final DefaultChannelGroup allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private final RunState runState = new RunState();

    private final FunctionalReentrantReadWriteLock destroyLock = new FunctionalReentrantReadWriteLock();

    private final SMSTopicContainer topicContainer;

    public AbstractSMSBrokerServer(SMSTopicContainer topicContainer) {
        this.topicContainer = checkNotNull(topicContainer, "topicContainer is null");
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    protected abstract EventLoopGroup getEventLoopGroup();

    protected abstract ChannelFuture doBootstrap(ChannelInitializer<Channel> childHandler);

    @Override
    public void start() {
        if (!isAvailable()) {
            LOG.warn("{} not available, not staring server", getClass().getSimpleName());
        } else {
            checkState(runState.start(), "Invalid state for start");

            final ChannelInitializer<Channel> childHandler = new SMSProtocolChannelInitializer(ServerHandler::new,
                    SMSProtocol.ClientToBrokerMessage.getDefaultInstance());

            final ChannelFuture channelFuture = doBootstrap(childHandler);

            final Channel serverChannel = channelFuture.syncUninterruptibly().channel();
            allChannels.add(serverChannel);

            LOG.info("{} listening on {} ({})", getClass().getSimpleName(), serverChannel.localAddress(), getEventLoopGroup());
        }
    }

    @Override
    public void destroy() {
        LOG.info("destroy");

        destroyLock.doInWriteLock(() -> {
            if (runState.destroy()) {
                allChannels.close();
            }
        });
    }

    @Override
    public boolean isDestroyed() {
        return (runState.getState() == RunState.State.DESTROYED);
    }

    private void processIncomingMessage(Channel channel, SMSProtocol.ClientToBrokerMessage message) {
        final String topicName = message.getTopicName();
        final SMSTopic topic = topicContainer.getTopic(topicName);

        switch (message.getMessageType()) {
            case CLIENT_SEND_MESSAGE_TO_TOPIC:
                topic.write(
                        buildBrokerToCLientMessage(
                                BrokerToClientMessageType.BROKER_TOPIC_MESSAGE_PUBLISH,
                                topicName,
                                message.getMessagePayload()));
                break;

            case CLIENT_SUBSCRIBE_TO_TOPIC:
                topic.addSubscription(channel);
                break;

            case CLIENT_UNSUBSCRIBE_FROM_TOPIC:
                topic.removeSubscription(channel);
                break;

        }
    }

    private class ServerHandler extends SimpleChannelInboundHandler<SMSProtocol.ClientToBrokerMessage> {

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            LOG.debug("channelRegistered {}", ctx.channel());

			/*
             * Need to synchronize on destroyLock to avoid another thread
			 * calling destroy() between destroyed.get() and allChannels.add()
			 * below.
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
            LOG.info("channelActive {}", ctx.channel());
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            LOG.info("channelInactive {}", ctx.channel());
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
        public void channelRead0(ChannelHandlerContext ctx, SMSProtocol.ClientToBrokerMessage message) {
            try {
                LOG.debug("channelRead0 from {} message = '{}'", ctx.channel(), message);
                processIncomingMessage(ctx.channel(), message);
            } catch (Exception e) {
                LOG.warn("channelRead0", e);
                ctx.channel().close();
            }
        }
    }

}
