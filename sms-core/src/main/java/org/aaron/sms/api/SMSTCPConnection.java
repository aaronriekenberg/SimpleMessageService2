package org.aaron.sms.api;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import org.aaron.sms.eventloop.TCPEventLoopGroupContainer;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TCP version of SMSConnection.
 * <p>
 * This version uses epoll if available and nio if not. It and works on all
 * platforms and supports remote network connections to the broker.
 */
public class SMSTCPConnection extends AbstractSMSConnection {

    private static final Integer CONNECT_TIMEOUT_MS = 5_000;

    private final InetSocketAddress brokerAddress;

    /**
     * Constructor method
     *
     * @param brokerAddress Broker address
     * @param serverPort
     */
    public SMSTCPConnection(InetSocketAddress brokerAddress) {
        this(brokerAddress, 1, TimeUnit.SECONDS);
    }

    /**
     * Constructor method
     *
     * @param brokerAddress Broker address
     * @param reconnect     delay reconnect delay time
     * @param reconnect     delay unit reconnect delay time unit
     */
    public SMSTCPConnection(InetSocketAddress brokerAddress, long reconnectDelay, TimeUnit reconnectDelayUnit) {
        super(reconnectDelay, reconnectDelayUnit);

        this.brokerAddress = checkNotNull(brokerAddress, "brokerAddress is null");
    }

    @Override
    protected ChannelFuture doBootstrapConnection(ChannelInitializer<Channel> channelInitializer) {
        return new Bootstrap().group(getEventLoopGroup()).channel(TCPEventLoopGroupContainer.getClientChannelClass())
                .handler(channelInitializer).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_MS)
                .connect(brokerAddress);
    }

    @Override
    protected EventLoopGroup getEventLoopGroup() {
        return TCPEventLoopGroupContainer.getEventLoopGroup();
    }

}
