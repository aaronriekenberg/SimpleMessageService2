package org.aaron.sms.api;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import org.aaron.sms.eventloop.TCPEventLoopGroupContainer;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TCP version of SMSConnection.
 * <p>
 * This version uses epoll if available and nio if not. It and works on all
 * platforms and supports remote network connections to the broker.
 */
public class SMSTCPConnection extends AbstractSMSConnection {

    private final InetSocketAddress brokerAddress;

    private final long connectTimeout;

    private final TimeUnit connectTimeoutTimeUnit;

    private SMSTCPConnection(
            InetSocketAddress brokerAddress,
            long reconnectDelay, TimeUnit reconnectDelayTimeUnit,
            long connectTimeout, TimeUnit connectTimeoutTimeUnit) {
        super(reconnectDelay, reconnectDelayTimeUnit);

        this.brokerAddress = checkNotNull(brokerAddress, "brokerAddress is null");
        checkArgument(connectTimeout > 0, "connectTimeout must be positive");
        this.connectTimeout = connectTimeout;
        this.connectTimeoutTimeUnit = checkNotNull(connectTimeoutTimeUnit, "connectTimeoutTimeUnit is null");
    }

    @Override
    protected ChannelFuture doBootstrapConnection(ChannelInitializer<Channel> channelInitializer) {
        return new Bootstrap()
                .group(getEventLoopGroup())
                .channel(TCPEventLoopGroupContainer.getClientChannelClass())
                .handler(channelInitializer)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
                        (int) connectTimeoutTimeUnit.toMillis(connectTimeout))
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

        private long reconnectDelay = 1;

        private TimeUnit reconnectDelayTimeUnit = TimeUnit.SECONDS;

        private long connectTimeout = 5;

        private TimeUnit connectTimeoutTimeUnit = TimeUnit.SECONDS;

        private InetSocketAddress brokerAddress = null;

        public Builder setReconnectDelay(long reconnectDelay) {
            this.reconnectDelay = reconnectDelay;
            return this;
        }

        public Builder setReconnectDelayTimeUnit(TimeUnit reconnectDelayTimeUnit) {
            this.reconnectDelayTimeUnit = reconnectDelayTimeUnit;
            return this;
        }

        public Builder setConnectTimeout(long connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder setConnectTimeoutTimeUnit(TimeUnit connectTimeoutTimeUnit) {
            this.connectTimeoutTimeUnit = connectTimeoutTimeUnit;
            return this;
        }

        public Builder setBrokerAddress(InetSocketAddress brokerAddress) {
            this.brokerAddress = brokerAddress;
            return this;
        }

        public SMSTCPConnection build() {
            return new SMSTCPConnection(
                    brokerAddress,
                    reconnectDelay, reconnectDelayTimeUnit,
                    connectTimeout, connectTimeoutTimeUnit);
        }
    }

}
