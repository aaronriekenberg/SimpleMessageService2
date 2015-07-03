package org.aaron.sms.examples;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.aaron.sms.api.SMSConnection;
import org.aaron.sms.api.SMSTCPConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.Uninterruptibles;

public class SMSTCPTestReceiver extends AbstractTestReceiver {

	private static final Logger log = LoggerFactory
			.getLogger(SMSTCPTestReceiver.class);

	public SMSTCPTestReceiver(String topicName) {
		super(topicName);
	}

	@Override
	protected SMSConnection createConnection() {
		return new SMSTCPConnection(new InetSocketAddress("127.0.0.1", 10001));
	}

	private static final int NUM_RECEIVERS = 50;

	public static void main(String[] args) {
		log.info("NUM_RECEIVERS = {}", NUM_RECEIVERS);

		IntStream.range(0, NUM_RECEIVERS).mapToObj(i -> "test.topic." + i)
				.map(SMSTCPTestReceiver::new)
				.forEach(SMSTCPTestReceiver::start);

		while (true) {
			Uninterruptibles.sleepUninterruptibly(60, TimeUnit.SECONDS);
		}
	}

}
