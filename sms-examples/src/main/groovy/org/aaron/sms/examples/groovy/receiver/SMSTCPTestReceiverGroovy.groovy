package org.aaron.sms.examples.groovy.receiver

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.aaron.sms.api.SMSConnection
import org.aaron.sms.api.SMSTCPConnection

@CompileStatic
@Slf4j
class SMSTCPTestReceiverGroovy {

    private static final int NUM_RECEIVERS = 50

    static void main(String[] args) {
        log.info('NUM_RECEIVERS = {}', NUM_RECEIVERS)

        (0..NUM_RECEIVERS - 1).forEach({ i ->
            new GroovyReceiver(createConnection(), "test.topic.${i}")
        })

        while (true) {
            sleep(60 * 1000)
        }
    }

    static SMSConnection createConnection() {
        SMSTCPConnection.newBuilder()
                .setBrokerAddress(
                new InetSocketAddress(InetAddress.getLoopbackAddress(), 10001))
                .build()
    }
}
