package org.aaron.sms.examples.receiver;

import com.google.common.util.concurrent.Uninterruptibles;
import org.aaron.sms.api.SMSConnection;
import org.aaron.sms.api.SMSTCPConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class SMSTCPTestReceiver extends AbstractTestReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(SMSTCPTestReceiver.class);

    private static final int NUM_RECEIVERS = 50;

    public SMSTCPTestReceiver(String topicName) {
        super(topicName);
    }

    public static void main(String[] args) {
        LOG.info("NUM_RECEIVERS = {}", NUM_RECEIVERS);

        IntStream.range(0, NUM_RECEIVERS)
                .mapToObj(i -> "test.topic." + i)
                .map(SMSTCPTestReceiver::new)
                .forEach(SMSTCPTestReceiver::start);

        while (true) {
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.MINUTES);
        }
    }

    @Override
    protected SMSConnection createConnection() {
        return SMSTCPConnection.newBuilder()
                .setBrokerAddress(new InetSocketAddress(
                        InetAddress.getLoopbackAddress(), 10001))
                .build();
    }

}
