package org.aaron.sms.examples.groovy.receiver

import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.aaron.sms.api.SMSConnection
import org.aaron.sms.api.SMSTCPConnection

@CompileStatic
@InheritConstructors
@Slf4j
class SMSTCPTestReceiverGroovy extends AbstractTestReceiverGroovy {

    private static final int NUM_RECEIVERS = 50

    @Override
    SMSConnection createConnection() {
        SMSTCPConnection.newBuilder()
                .setBrokerAddress(
                new InetSocketAddress(InetAddress.getLoopbackAddress(), 10001))
                .build()
    }

    static void main(String[] args) {
        log.info('NUM_RECEIVERS = {}', NUM_RECEIVERS)

        (0..NUM_RECEIVERS-1).forEach({ i ->
            new SMSTCPTestReceiverGroovy(topicName: "test.topic.${i}").start()
        })

        while (true) {
            sleep(60 * 1000)
        }
    }
}
