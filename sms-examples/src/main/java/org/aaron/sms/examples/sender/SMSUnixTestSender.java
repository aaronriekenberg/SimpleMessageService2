package org.aaron.sms.examples.sender;

import com.google.common.util.concurrent.Uninterruptibles;
import io.netty.channel.unix.DomainSocketAddress;
import org.aaron.sms.api.SMSConnection;
import org.aaron.sms.api.SMSUnixConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SMSUnixTestSender extends AbstractTestSender {

    private static final Logger LOG = LoggerFactory.getLogger(SMSUnixTestSender.class);

    private static final int NUM_SENDERS = 50;

    private static final int MESSAGE_SIZE_BYTES = 5_000;

    private static final long SLEEP_BETWEEN_SENDS_MS = 1;

    public SMSUnixTestSender(String topicName) {
        super(topicName, MESSAGE_SIZE_BYTES, SLEEP_BETWEEN_SENDS_MS);
    }

    public static void main(String[] args) {
        LOG.info("NUM_SENDERS = {}", NUM_SENDERS);
        LOG.info("MESSAGE_SIZE_BYTES = {}", MESSAGE_SIZE_BYTES);
        LOG.info("SLEEP_BETWEEN_SENDS_MS = {}", SLEEP_BETWEEN_SENDS_MS);

        final List<Thread> threadList = IntStream.range(0, NUM_SENDERS).mapToObj(i -> "test.topic." + i)
                .map(SMSUnixTestSender::new).map(Thread::new).collect(Collectors.toList());

        threadList.forEach(Thread::start);

        threadList.forEach(Uninterruptibles::joinUninterruptibly);
    }

    @Override
    protected SMSConnection createConnection() {
        return new SMSUnixConnection(new DomainSocketAddress(Paths.get("/tmp", "sms-unix-socket").toFile()));
    }
}
