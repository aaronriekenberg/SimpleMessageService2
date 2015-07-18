package org.aaron.sms.examples.receiver;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.aaron.sms.api.SMSConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTestReceiver {

	private static final Logger log = LoggerFactory.getLogger(AbstractTestReceiver.class);

	private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

	private final AtomicInteger messagesReceived = new AtomicInteger(0);

	private final String topicName;

	public AbstractTestReceiver(String topicName) {
		this.topicName = checkNotNull(topicName);
	}

	public void start() {
		try {
			final SMSConnection smsConnection = createConnection();

			executor.scheduleAtFixedRate(
					() -> log.info(topicName + " messages received last second = " + messagesReceived.getAndSet(0)), 1,
					1, TimeUnit.SECONDS);

			smsConnection
					.registerConnectionStateListener(newState -> log.info("connection state changed {}", newState));

			smsConnection.subscribeToTopic(topicName, message -> {
				log.debug("handleIncomingMessage topic {} length {}", topicName, message.size());
				messagesReceived.getAndIncrement();
			});

			smsConnection.start();

		} catch (Exception e) {
			log.warn("start", e);
		}
	}

	protected abstract SMSConnection createConnection();

}
