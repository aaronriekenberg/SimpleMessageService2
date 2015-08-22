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

    /**
     * Constructor method
     *
     * @param brokerAddress Broker address
     */
    public SMSUnixConnection(DomainSocketAddress brokerAddress) {
        this(brokerAddress, 1, TimeUnit.SECONDS);
    }

    /**
     * Constructor method
     *
     * @param brokerAddress      Broker address
     * @param reconnectDelay     delay reconnect delay time
     * @param reconnectDelayUnit delay unit reconnect delay time unit
     */
    public SMSUnixConnection(DomainSocketAddress brokerAddress, long reconnectDelay, TimeUnit reconnectDelayUnit) {
        super(reconnectDelay, reconnectDelayUnit);

        this.brokerAddress = checkNotNull(brokerAddress, "brokerAddress is null");
    }

    @Override
    public boolean isAvailable() {
        return UnixEventLoopGroupContainer.isAvailable();
    }

    @Override
    protected ChannelFuture doBootstrapConnection(ChannelInitializer<Channel> channelInitializer) {
        return new Bootstrap().group(getEventLoopGroup()).channel(UnixEventLoopGroupContainer.getClientChannelClass())
                .handler(channelInitializer).connect(brokerAddress);
    }

    @Override
    protected EventLoopGroup getEventLoopGroup() {
        return UnixEventLoopGroupContainer.getEventLoopGroup();
    }

}
