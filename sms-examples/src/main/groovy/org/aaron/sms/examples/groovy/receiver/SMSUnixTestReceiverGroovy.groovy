package org.aaron.sms.examples.groovy.receiver

import groovy.util.logging.Slf4j
import org.aaron.sms.api.SMSConnection
import org.aaron.sms.api.SMSUnixConnection
import org.aaron.sms.examples.groovy.util.GroovyConstants

@Slf4j
class SMSUnixTestReceiverGroovy {

    private static final Integer NUM_RECEIVERS = 50

    static void main(String[] args) {
        GroovyReceiver.createAndRunReceivers(NUM_RECEIVERS, SMSUnixTestReceiverGroovy.&createConnection)
    }

    static SMSConnection createConnection() {
        SMSUnixConnection.newBuilder()
                .setBrokerAddress(GroovyConstants.UNIX_ADDRESS)
                .build();
    }
}
