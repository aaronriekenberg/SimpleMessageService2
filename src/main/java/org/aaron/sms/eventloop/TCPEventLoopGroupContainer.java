package org.aaron.sms.eventloop;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TCPEventLoopGroupContainer {

	private static final EventLoopGroup EVENT_LOOP_GROUP;

	private static final Class<? extends Channel> CLIENT_CHANNEL_CLASS;

	private static final Class<? extends ServerChannel> SERVER_CHANNEL_CLASS;

	static {
		if (Epoll.isAvailable()) {
			EVENT_LOOP_GROUP = EpollEventLoopGroupContainer.EVENT_LOOP_GROUP;
			CLIENT_CHANNEL_CLASS = EpollSocketChannel.class;
			SERVER_CHANNEL_CLASS = EpollServerSocketChannel.class;
		} else {
			EVENT_LOOP_GROUP = NioEventLoopGroupContainer.EVENT_LOOP_GROUP;
			CLIENT_CHANNEL_CLASS = NioSocketChannel.class;
			SERVER_CHANNEL_CLASS = NioServerSocketChannel.class;
		}
	}

	private TCPEventLoopGroupContainer() {

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

}
