package org.aaron.sms.api;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import org.aaron.sms.eventloop.TCPEventLoopGroupContainer;

import java.net.InetSocketAddress;
import java.time.Duration;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.aaron.sms.util.DurationUtils.checkNotNullAndPositive;

/**
 * TCP version of SMSConnection.
 * <p>
 * This version uses epoll if available and nio if not. It and works on all
 * platforms and supports remote network connections to the broker.
 */
public class SMSTCPConnection extends AbstractSMSConnection {

    private final InetSocketAddress brokerAddress;

    private final Duration connectTimeout;

    private SMSTCPConnection(
            InetSocketAddress brokerAddress, Duration reconnectDelay, Duration connectTimeout) {
        super(reconnectDelay);

        this.brokerAddress = checkNotNull(brokerAddress, "brokerAddress is null");
        this.connectTimeout = checkNotNullAndPositive(connectTimeout, "connectTimeout");
    }

    @Override
    protected ChannelFuture doBootstrapConnection(ChannelInitializer<Channel> channelInitializer) {
        return new Bootstrap()
                .group(getEventLoopGroup())
                .channel(TCPEventLoopGroupContainer.getClientChannelClass())
                .handler(channelInitializer)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
                        (int) connectTimeout.toMillis())
                .connect(brokerAddress);
    }

    @Override
    protected EventLoopGroup getEventLoopGroup() {
        return TCPEventLoopGroupContainer.getEventLoopGroup();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private Duration reconnectDelay = Duration.ofSeconds(1);

        private Duration connectTimeout = Duration.ofSeconds(5);

        private InetSocketAddress brokerAddress = null;

        public Builder setReconnectDelay(Duration reconnectDelay) {
            this.reconnectDelay = checkNotNullAndPositive(reconnectDelay, "reconnectDelay");
            return this;
        }

        public Builder setConnectTimeout(Duration connectTimeout) {
            this.connectTimeout = checkNotNullAndPositive(connectTimeout, "connectTimeout");
            return this;
        }

        public Builder setBrokerAddress(InetSocketAddress brokerAddress) {
            this.brokerAddress = checkNotNull(brokerAddress, "brokerAddress is null");
            return this;
        }

        public SMSTCPConnection build() {
            return new SMSTCPConnection(
                    brokerAddress, reconnectDelay, connectTimeout);
        }
    }

}
