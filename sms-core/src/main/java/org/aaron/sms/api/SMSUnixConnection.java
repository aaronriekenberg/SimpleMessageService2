package org.aaron.sms.api;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.unix.DomainSocketAddress;
import org.aaron.sms.eventloop.UnixEventLoopGroupContainer;
import org.aaron.sms.util.DurationUtils;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.aaron.sms.util.DurationUtils.checkNotNullAndPositive;

/**
 * TCP version of SMSConnection.
 * <p>
 * This version uses Unix Domain sockets and works on Linux only. It supports
 * local connections to the broker only.
 */
public class SMSUnixConnection extends AbstractSMSConnection {

    private final DomainSocketAddress brokerAddress;

    private SMSUnixConnection(DomainSocketAddress brokerAddress, Duration reconnectDelay) {
        super(reconnectDelay);

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

        private Duration reconnectDelay = Duration.ofSeconds(1);

        private DomainSocketAddress brokerAddress = null;

        public Builder setReconnectDelay(Duration reconnectDelay) {
            this.reconnectDelay = checkNotNullAndPositive(reconnectDelay, "reconnectDelay");
            return this;
        }

        public Builder setBrokerAddress(DomainSocketAddress brokerAddress) {
            this.brokerAddress = checkNotNull(brokerAddress, "brokerAddress is null");
            return this;
        }

        public SMSUnixConnection build() {
            return new SMSUnixConnection(
                    brokerAddress, reconnectDelay);
        }
    }

}
