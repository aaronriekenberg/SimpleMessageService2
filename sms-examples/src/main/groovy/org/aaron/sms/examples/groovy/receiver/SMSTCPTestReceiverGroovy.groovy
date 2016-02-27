package org.aaron.sms.examples.groovy.receiver

import groovy.util.logging.Slf4j
import org.aaron.sms.api.SMSConnection
import org.aaron.sms.api.SMSTCPConnection

@Slf4j
class SMSTCPTestReceiverGroovy {

    private static final Integer NUM_RECEIVERS = 50

    static void main(String[] args) {
        GroovyReceiver.createAndRunReceivers(NUM_RECEIVERS, SMSTCPTestReceiverGroovy.&createConnection)
    }

    static SMSConnection createConnection() {
        SMSTCPConnection.newBuilder()
                .setBrokerAddress(new InetSocketAddress(InetAddress.getLoopbackAddress(), 10001))
                .build()
    }
}
