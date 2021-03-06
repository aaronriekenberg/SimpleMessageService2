package org.aaron.sms.examples.sender;

import com.google.common.util.concurrent.Uninterruptibles;
import com.google.protobuf.ByteString;
import org.aaron.sms.api.SMSConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

abstract class AbstractTestSender implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTestSender.class);

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
        final SMSConnection smsConnection = createConnection();

        smsConnection
                .registerConnectionStateListener(newState -> LOG.info("connection state changed {}", newState));

        smsConnection.start();

        final ByteString buffer = ByteString.copyFrom(new byte[messageSizeBytes]);
        while (true) {
            smsConnection.writeToTopic(topicName, buffer);
            Uninterruptibles.sleepUninterruptibly(sleepBetweenSendsMS, TimeUnit.MILLISECONDS);
        }
    }

    protected abstract SMSConnection createConnection();

}
