package org.aaron.sms.examples.groovy.broker

import groovy.time.TimeCategory
import groovy.util.logging.Slf4j
import io.netty.channel.unix.DomainSocketAddress
import org.aaron.sms.broker.SMSBroker

import java.nio.file.Paths

@Slf4j
class SMSBrokerMainGroovy {

    static void main(String[] args) {
        SMSBroker.newBuilder()
                .addTCPServer(new InetSocketAddress(10001))
                .addUnixServer(new DomainSocketAddress(Paths.get("/tmp", "sms-unix-socket").toFile()))
                .build().start()

        use(TimeCategory) {
            while (true) {
                sleep 1.minute.toMilliseconds()
            }
        }
    }
}
