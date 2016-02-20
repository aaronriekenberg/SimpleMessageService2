package org.aaron.sms.examples.groovy.receiver

import groovy.time.TimeCategory
import groovy.util.logging.Slf4j
import io.netty.channel.unix.DomainSocketAddress
import org.aaron.sms.api.SMSConnection
import org.aaron.sms.api.SMSUnixConnection

import java.nio.file.Paths

@Slf4j
class SMSUnixTestReceiverGroovy {

    private static final Integer NUM_RECEIVERS = 50

    static void main(String[] args) {
        log.info "NUM_RECEIVERS = ${NUM_RECEIVERS}"

        NUM_RECEIVERS.times { i ->
            new GroovyReceiver(createConnection(), "test.topic.${i}")
        }

        use(TimeCategory) {
            while (true) {
                sleep 1.minute.toMilliseconds()
            }
        }
    }

    static SMSConnection createConnection() {
        SMSUnixConnection.newBuilder()
                .setBrokerAddress(new DomainSocketAddress(Paths.get("/tmp", "sms-unix-socket").toFile()))
                .build();
    }
}
