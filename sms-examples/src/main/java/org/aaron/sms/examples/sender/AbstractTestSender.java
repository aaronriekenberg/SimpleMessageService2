package org.aaron.sms.examples.sender;

import static com.google.common.base.Preconditions.checkNotNull;

import org.aaron.sms.api.SMSConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;

abstract class AbstractTestSender implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(AbstractTestSender.class);

	private final String topicName;

	private final int messageSizeBytes;

	private final long sleepBetweenSendsMS;

	public AbstractTestSender(String topicName, int messageSizeBytes, long sleepBetweenSendsMS) {
		this.topicName = checkNotNull(topicName);
		this.messageSizeBytes = messageSizeBytes;
		this.sleepBetweenSendsMS = sleepBetweenSendsMS;
	}

	@Override
	public void run() {
		try {
			final SMSConnection smsConnection = createConnection();

			smsConnection
					.registerConnectionStateListener(newState -> log.info("connection state changed {}", newState));

			smsConnection.start();

			final ByteString buffer = ByteString.copyFrom(new byte[messageSizeBytes]);
			while (true) {
				smsConnection.writeToTopic(topicName, buffer);
				Thread.sleep(sleepBetweenSendsMS);
			}
		} catch (Exception e) {
			log.warn("run", e);
		}
	}

	protected abstract SMSConnection createConnection();

}
