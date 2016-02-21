package org.aaron.sms.examples.groovy.sender

import com.google.common.util.concurrent.Uninterruptibles
import groovy.util.logging.Slf4j
import org.aaron.sms.api.SMSConnection
import org.aaron.sms.api.SMSTCPConnection

@Slf4j
class SMSTCPTestSenderGroovy {

    private static final int NUM_SENDERS = 50

    private static final int MESSAGE_SIZE_BYTES = 5_000

    private static final long SLEEP_BETWEEN_SENDS_MS = 10

    static void main(String[] args) {
        log.info "NUM_SENDERS = ${NUM_SENDERS}"
        log.info "MESSAGE_SIZE_BYTES = ${MESSAGE_SIZE_BYTES}"
        log.info "SLEEP_BETWEEN_SENDS_MS = ${SLEEP_BETWEEN_SENDS_MS}"

        final List<Thread> threadList = (0..<NUM_SENDERS).collect { i ->
            String topicName = "test.topic.${i}"
            GroovySenderRunnable runnable =
                    new GroovySenderRunnable(
                            smsConnection: createConnection(),
                            topicName: topicName,
                            messageSizeBytes: MESSAGE_SIZE_BYTES,
                            sleepBetweenSendsMS: SLEEP_BETWEEN_SENDS_MS)
            Thread.start "sender ${topicName}", runnable.&run
        }

        threadList.each { t -> Uninterruptibles.joinUninterruptibly(t) }
    }

    static SMSConnection createConnection() {
        SMSTCPConnection.newBuilder()
                .setBrokerAddress(new InetSocketAddress(
                InetAddress.getLoopbackAddress(), 10001))
                .build();
    }
}
