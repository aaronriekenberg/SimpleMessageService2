package org.aaron.sms.broker;

import static com.google.common.base.Preconditions.checkNotNull;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.unix.DomainSocketAddress;

import org.aaron.sms.eventloop.UnixEventLoopGroupContainer;

class SMSBrokerUnixServer extends AbstractSMSBrokerServer {

	private final DomainSocketAddress bindAddress;

	public SMSBrokerUnixServer(SMSTopicContainer topicContainer, DomainSocketAddress bindAddress) {
		super(topicContainer);
		this.bindAddress = checkNotNull(bindAddress, "bindAddress is null");
	}

	@Override
	public boolean isAvailable() {
		return UnixEventLoopGroupContainer.isAvailable();
	}

	@Override
	protected EventLoopGroup getEventLoopGroup() {
		return UnixEventLoopGroupContainer.getEventLoopGroup();
	}

	@Override
	protected ChannelFuture doBootstrap(ChannelInitializer<Channel> childHandler) {
		final ServerBootstrap b = new ServerBootstrap();
		b.group(getEventLoopGroup()).channel(UnixEventLoopGroupContainer.getServerChannelClass())
				.childHandler(childHandler).option(ChannelOption.SO_REUSEADDR, true);
		return b.bind(bindAddress);
	}

	@Override
	protected void doDestroy() {

	}

}
