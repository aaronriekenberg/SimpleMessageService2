package org.aaron.sms.examples.groovy.sender

import groovy.util.logging.Slf4j
import org.aaron.sms.api.SMSConnection
import org.aaron.sms.api.SMSUnixConnection
import org.aaron.sms.examples.groovy.util.GroovyConstants

@Slf4j
class SMSUnixTestSenderGroovy {

    private static final int NUM_SENDERS = 50

    private static final int MESSAGE_SIZE_BYTES = 5_000

    private static final long SLEEP_BETWEEN_SENDS_MS = 1

    static void main(String[] args) {
        GroovySender.createAndRunSenders(NUM_SENDERS, MESSAGE_SIZE_BYTES, SLEEP_BETWEEN_SENDS_MS, SMSUnixTestSenderGroovy.&createConnection)
    }

    static SMSConnection createConnection() {
        SMSUnixConnection.newBuilder()
                .setBrokerAddress(GroovyConstants.UNIX_ADDRESS)
                .build()
    }
}
