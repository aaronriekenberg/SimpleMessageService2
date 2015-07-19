package org.aaron.sms.examples.receiver;

import com.google.common.util.concurrent.Uninterruptibles;
import io.netty.channel.unix.DomainSocketAddress;
import org.aaron.sms.api.SMSConnection;
import org.aaron.sms.api.SMSUnixConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class SMSUnixTestReceiver extends AbstractTestReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(SMSUnixTestReceiver.class);

    private static final int NUM_RECEIVERS = 50;

    public SMSUnixTestReceiver(String topicName) {
        super(topicName);
    }

    public static void main(String[] args) {
        LOG.info("NUM_RECEIVERS = {}", NUM_RECEIVERS);

        IntStream.range(0, NUM_RECEIVERS).mapToObj(i -> "test.topic." + i).map(SMSUnixTestReceiver::new)
                .forEach(SMSUnixTestReceiver::start);

        while (true) {
            Uninterruptibles.sleepUninterruptibly(60, TimeUnit.SECONDS);
        }
    }

    @Override
    protected SMSConnection createConnection() {
        return new SMSUnixConnection(new DomainSocketAddress(Paths.get("/tmp", "sms-unix-socket").toFile()));
    }

}
