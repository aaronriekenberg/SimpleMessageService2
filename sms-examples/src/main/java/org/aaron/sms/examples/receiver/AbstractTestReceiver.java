package org.aaron.sms.examples.receiver;

import org.aaron.sms.api.SMSConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkNotNull;

abstract class AbstractTestReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTestReceiver.class);

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
                    () -> LOG.info(topicName + " messages received last second = " + messagesReceived.getAndSet(0)), 1,
                    1, TimeUnit.SECONDS);

            smsConnection
                    .registerConnectionStateListener(newState -> LOG.info("connection state changed {}", newState));

            smsConnection.subscribeToTopic(topicName, message -> {
                LOG.debug("handleIncomingMessage topic {} length {}", topicName, message.size());
                messagesReceived.getAndIncrement();
            });

            smsConnection.start();

        } catch (Exception e) {
            LOG.warn("start", e);
        }
    }

    protected abstract SMSConnection createConnection();

}
