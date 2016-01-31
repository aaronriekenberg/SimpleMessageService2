package org.aaron.sms.examples.groovy.receiver

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.netty.channel.unix.DomainSocketAddress
import org.aaron.sms.api.SMSConnection
import org.aaron.sms.api.SMSUnixConnection

import java.nio.file.Paths

@CompileStatic
@Slf4j
class SMSUnixTestReceiverGroovy {

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
        SMSUnixConnection.newBuilder()
                .setBrokerAddress(new DomainSocketAddress(Paths.get("/tmp", "sms-unix-socket").toFile()))
                .build();
    }
}
