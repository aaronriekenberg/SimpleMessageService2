package org.aaron.sms.broker;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.unix.DomainSocketAddress;
import org.aaron.sms.eventloop.UnixEventLoopGroupContainer;

import static com.google.common.base.Preconditions.checkNotNull;

class SMSBrokerUnixServer extends AbstractSMSBrokerServer {

    private final DomainSocketAddress bindAddress;

    public SMSBrokerUnixServer(SMSTopicContainer topicContainer, DomainSocketAddress bindAddress) {
        super(topicContainer);
        this.bindAddress = checkNotNull(bindAddress, "bindAddress is null");
    }

    @Override
    public boolean isAvailable() {
        return UnixEventLoopGroupContainer.isAvailable();
    }

    @Override
    protected EventLoopGroup getEventLoopGroup() {
        return UnixEventLoopGroupContainer.getEventLoopGroup();
    }

    @Override
    protected ChannelFuture doBootstrap(ChannelInitializer<Channel> childHandler) {
        final ServerBootstrap b = new ServerBootstrap();
        b.group(getEventLoopGroup()).channel(UnixEventLoopGroupContainer.getServerChannelClass())
                .childHandler(childHandler).option(ChannelOption.SO_REUSEADDR, true);
        return b.bind(bindAddress);
    }

}
