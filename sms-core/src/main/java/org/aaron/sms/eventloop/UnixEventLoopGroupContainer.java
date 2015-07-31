package org.aaron.sms.eventloop;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollDomainSocketChannel;
import io.netty.channel.epoll.EpollServerDomainSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnixEventLoopGroupContainer {

    private static final Logger LOG = LoggerFactory.getLogger(UnixEventLoopGroupContainer.class);

    private static final EventLoopGroup EVENT_LOOP_GROUP;

    private static final Class<? extends Channel> CLIENT_CHANNEL_CLASS;

    private static final Class<? extends ServerChannel> SERVER_CHANNEL_CLASS;

    static {
        if (Epoll.isAvailable()) {
            LOG.info("using epoll for unix event loop group");
            EVENT_LOOP_GROUP = EpollEventLoopGroupContainer.EVENT_LOOP_GROUP;
            CLIENT_CHANNEL_CLASS = EpollDomainSocketChannel.class;
            SERVER_CHANNEL_CLASS = EpollServerDomainSocketChannel.class;
        } else {
            LOG.warn("epoll is not available, unix event loop group is usable");
            EVENT_LOOP_GROUP = null;
            CLIENT_CHANNEL_CLASS = null;
            SERVER_CHANNEL_CLASS = null;
        }
    }

    private UnixEventLoopGroupContainer() {

    }

    public static EventLoopGroup getEventLoopGroup() {
        return EVENT_LOOP_GROUP;
    }

    public static Class<? extends Channel> getClientChannelClass() {
        return CLIENT_CHANNEL_CLASS;
    }

    public static Class<? extends ServerChannel> getServerChannelClass() {
        return SERVER_CHANNEL_CLASS;
    }

    public static boolean isAvailable() {
        return (EVENT_LOOP_GROUP != null);
    }

}
