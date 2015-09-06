package org.aaron.sms.api;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.unix.DomainSocketAddress;
import org.aaron.sms.eventloop.UnixEventLoopGroupContainer;

import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TCP version of SMSConnection.
 * <p>
 * This version uses Unix Domain sockets and works on Linux only. It supports
 * local connections to the broker only.
 */
public class SMSUnixConnection extends AbstractSMSConnection {

    private final DomainSocketAddress brokerAddress;

    private SMSUnixConnection(DomainSocketAddress brokerAddress, long reconnectDelay, TimeUnit reconnectDelayUnit) {
        super(reconnectDelay, reconnectDelayUnit);

        this.brokerAddress = checkNotNull(brokerAddress, "brokerAddress is null");
    }

    @Override
    public boolean isAvailable() {
        return UnixEventLoopGroupContainer.isAvailable();
    }

    @Override
    protected ChannelFuture doBootstrapConnection(ChannelInitializer<Channel> channelInitializer) {
        return new Bootstrap()
                .group(getEventLoopGroup())
                .channel(UnixEventLoopGroupContainer.getClientChannelClass())
                .handler(channelInitializer)
                .connect(brokerAddress);
    }

    @Override
    protected EventLoopGroup getEventLoopGroup() {
        return UnixEventLoopGroupContainer.getEventLoopGroup();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private long reconnectDelay = 1;

        private TimeUnit reconnectDelayUnit = TimeUnit.SECONDS;

        private DomainSocketAddress brokerAddress = null;

        public Builder setReconnectDelay(long reconnectDelay) {
            this.reconnectDelay = reconnectDelay;
            return this;
        }

        public Builder setReconnectDelayUnit(TimeUnit reconnectDelayUnit) {
            this.reconnectDelayUnit = reconnectDelayUnit;
            return this;
        }

        public Builder setBrokerAddress(DomainSocketAddress brokerAddress) {
            this.brokerAddress = brokerAddress;
            return this;
        }

        public SMSUnixConnection build() {
            return new SMSUnixConnection(
                    brokerAddress,
                    reconnectDelay, reconnectDelayUnit);
        }
    }

}
