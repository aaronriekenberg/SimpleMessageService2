package org.aaron.sms.examples.groovy.broker

import groovy.util.logging.Slf4j
import org.aaron.sms.broker.SMSBroker
import org.aaron.sms.examples.groovy.util.GroovyConstants

import java.util.concurrent.TimeUnit

@Slf4j
class SMSBrokerMainGroovy {

    static void main(String[] args) {
        SMSBroker.newBuilder()
                .addTCPServer(GroovyConstants.TCP_BROKER_LISTEN_ADDRESS)
                .addUnixServer(GroovyConstants.UNIX_ADDRESS)
                .build().start()

        while (true) {
            sleep TimeUnit.MINUTES.toMillis(1)
        }
    }
}
