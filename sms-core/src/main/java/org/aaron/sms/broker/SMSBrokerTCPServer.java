package org.aaron.sms.broker;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import org.aaron.sms.eventloop.TCPEventLoopGroupContainer;

import java.net.InetSocketAddress;

import static com.google.common.base.Preconditions.checkNotNull;

class SMSBrokerTCPServer extends AbstractSMSBrokerServer {

    private final InetSocketAddress bindAddress;

    public SMSBrokerTCPServer(SMSTopicContainer topicContainer, InetSocketAddress bindAddress) {
        super(topicContainer);
        this.bindAddress = checkNotNull(bindAddress, "bindAddress is null");
    }

    @Override
    protected EventLoopGroup getEventLoopGroup() {
        return TCPEventLoopGroupContainer.getEventLoopGroup();
    }

    @Override
    protected ChannelFuture doBootstrap(ChannelInitializer<Channel> childHandler) {
        final ServerBootstrap b = new ServerBootstrap();
        b.group(getEventLoopGroup()).channel(TCPEventLoopGroupContainer.getServerChannelClass())
                .childHandler(childHandler).option(ChannelOption.SO_REUSEADDR, true);
        return b.bind(bindAddress);
    }

}
