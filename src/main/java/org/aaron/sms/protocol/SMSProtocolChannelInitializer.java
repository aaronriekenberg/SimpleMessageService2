package org.aaron.sms.protocol;

import static com.google.common.base.Preconditions.checkNotNull;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.function.Supplier;

import com.google.protobuf.MessageLite;

public class SMSProtocolChannelInitializer extends ChannelInitializer<Channel> {

	private final Supplier<? extends ChannelHandler> handlerSupplier;

	private final MessageLite messagePrototype;

	public SMSProtocolChannelInitializer(Supplier<? extends ChannelHandler> handlerSupplier,
			MessageLite messagePrototype) {
		this.handlerSupplier = checkNotNull(handlerSupplier, "handlerSupplier is null");
		this.messagePrototype = checkNotNull(messagePrototype, "messagePrototype is null");
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		final ChannelPipeline p = ch.pipeline();
		p.addLast("logger", new LoggingHandler(LogLevel.DEBUG));

		p.addLast("frameEncoder", new LengthFieldPrepender(SMSProtocolConstants.MESSAGE_HEADER_LENGTH_BYTES));

		p.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(SMSProtocolConstants.MAX_MESSAGE_LENGTH_BYTES, 0,
				SMSProtocolConstants.MESSAGE_HEADER_LENGTH_BYTES, 0, SMSProtocolConstants.MESSAGE_HEADER_LENGTH_BYTES));

		p.addLast("protobufEncoder", new ProtobufEncoder());

		p.addLast("protobufDecoder", new ProtobufDecoder(messagePrototype));

		p.addLast("customHandler", handlerSupplier.get());
	}
}
