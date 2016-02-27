package org.aaron.sms.examples.groovy.sender

import groovy.util.logging.Slf4j
import io.netty.channel.unix.DomainSocketAddress
import org.aaron.sms.api.SMSConnection
import org.aaron.sms.api.SMSUnixConnection

import java.nio.file.Paths

@Slf4j
class SMSUnixTestSenderGroovy {

    private static final int NUM_SENDERS = 50

    private static final int MESSAGE_SIZE_BYTES = 5_000

    private static final long SLEEP_BETWEEN_SENDS_MS = 1

    static void main(String[] args) {
        GroovySenderRunnable.createAndRun(NUM_SENDERS, MESSAGE_SIZE_BYTES, SLEEP_BETWEEN_SENDS_MS, SMSUnixTestSenderGroovy.&createConnection)
    }

    static SMSConnection createConnection() {
        SMSUnixConnection.newBuilder()
                .setBrokerAddress(new DomainSocketAddress(Paths.get("/tmp", "sms-unix-socket").toFile()))
                .build()
    }
}
