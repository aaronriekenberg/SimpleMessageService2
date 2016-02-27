package org.aaron.sms.examples.groovy.sender

import groovy.util.logging.Slf4j
import org.aaron.sms.api.SMSConnection
import org.aaron.sms.api.SMSTCPConnection

@Slf4j
class SMSTCPTestSenderGroovy {

    private static final int NUM_SENDERS = 50

    private static final int MESSAGE_SIZE_BYTES = 5_000

    private static final long SLEEP_BETWEEN_SENDS_MS = 10

    static void main(String[] args) {
        GroovySenderRunnable.createAndRun(NUM_SENDERS, MESSAGE_SIZE_BYTES, SLEEP_BETWEEN_SENDS_MS, SMSTCPTestSenderGroovy.&createConnection)
    }

    static SMSConnection createConnection() {
        SMSTCPConnection.newBuilder()
                .setBrokerAddress(new InetSocketAddress(
                InetAddress.getLoopbackAddress(), 10001))
                .build();
    }
}
