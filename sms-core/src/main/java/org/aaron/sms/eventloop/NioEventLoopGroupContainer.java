package org.aaron.sms.eventloop;

import io.netty.channel.nio.NioEventLoopGroup;

class NioEventLoopGroupContainer {

    public static final NioEventLoopGroup EVENT_LOOP_GROUP = new NioEventLoopGroup();

    private NioEventLoopGroupContainer() {

    }

}
