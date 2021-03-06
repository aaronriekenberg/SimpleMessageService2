package org.aaron.sms.broker;

import io.netty.channel.Channel;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.aaron.sms.protocol.protobuf.SMSProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

class SMSTopic {

    private static final Logger LOG = LoggerFactory.getLogger(SMSTopic.class);

    private final DefaultChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public SMSTopic(String topicName) {
        checkNotNull(topicName, "topicName is null");
        LOG.info("create SMSTopic '{}'", topicName);
    }

    public void addSubscription(Channel channel) {
        checkNotNull(channel, "channel is null");

        channelGroup.add(channel);
    }

    public void removeSubscription(Channel channel) {
        checkNotNull(channel, "channel is null");

        channelGroup.remove(channel);
    }

    public void write(SMSProtocol.BrokerToClientMessage message) {
        checkNotNull(message, "message is null");

        channelGroup.writeAndFlush(message);
    }
}