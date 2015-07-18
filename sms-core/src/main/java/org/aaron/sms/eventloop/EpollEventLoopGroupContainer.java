package org.aaron.sms.eventloop;

import io.netty.channel.epoll.EpollEventLoopGroup;

class EpollEventLoopGroupContainer {

    public static final EpollEventLoopGroup EVENT_LOOP_GROUP = new EpollEventLoopGroup();

    private EpollEventLoopGroupContainer() {

    }

}
