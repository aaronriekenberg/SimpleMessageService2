package org.aaron.sms.broker;

import static com.google.common.base.Preconditions.checkNotNull;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;

import java.net.InetSocketAddress;

import org.aaron.sms.eventloop.TCPEventLoopGroupContainer;

class SMSBrokerTCPServer extends AbstractSMSBrokerServer {

	private final InetSocketAddress bindAddress;

	public SMSBrokerTCPServer(SMSTopicContainer topicContainer, InetSocketAddress bindAddress) {
		super(topicContainer);
		this.bindAddress = checkNotNull(bindAddress, "bindAddress is null");
	}

	@Override
	protected EventLoopGroup getEventLoopGroup() {
		return TCPEventLoopGroupContainer.getEventLoopGroup();
	}

	@Override
	protected ChannelFuture doBootstrap(ChannelInitializer<Channel> childHandler) {
		final ServerBootstrap b = new ServerBootstrap();
		b.group(getEventLoopGroup()).channel(TCPEventLoopGroupContainer.getServerChannelClass())
				.childHandler(childHandler).option(ChannelOption.SO_REUSEADDR, true);
		return b.bind(bindAddress);
	}

	@Override
	protected void doDestroy() {

	}

}
