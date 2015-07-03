package org.aaron.sms.examples;

import io.netty.channel.unix.DomainSocketAddress;

import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.aaron.sms.api.SMSConnection;
import org.aaron.sms.api.SMSUnixConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.Uninterruptibles;

public class SMSUnixTestReceiver extends AbstractTestReceiver {

	private static final Logger log = LoggerFactory
			.getLogger(SMSUnixTestReceiver.class);

	public SMSUnixTestReceiver(String topicName) {
		super(topicName);
	}

	@Override
	protected SMSConnection createConnection() {
		return new SMSUnixConnection(new DomainSocketAddress(Paths.get("/tmp",
				"sms-unix-socket").toFile()));
	}

	private static final int NUM_RECEIVERS = 50;

	public static void main(String[] args) {
		log.info("NUM_RECEIVERS = {}", NUM_RECEIVERS);

		IntStream.range(0, NUM_RECEIVERS).mapToObj(i -> "test.topic." + i)
				.map(SMSUnixTestReceiver::new)
				.forEach(SMSUnixTestReceiver::start);

		while (true) {
			Uninterruptibles.sleepUninterruptibly(60, TimeUnit.SECONDS);
		}
	}

}
