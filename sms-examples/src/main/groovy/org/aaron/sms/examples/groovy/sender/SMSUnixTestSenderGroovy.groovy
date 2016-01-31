package org.aaron.sms.examples.groovy.sender

import com.google.common.util.concurrent.Uninterruptibles
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import io.netty.channel.unix.DomainSocketAddress
import org.aaron.sms.api.SMSConnection
import org.aaron.sms.api.SMSUnixConnection

import java.nio.file.Paths

@CompileStatic
@InheritConstructors
@Slf4j
class SMSUnixTestSenderGroovy extends AbstractTestSenderGroovy {

    private static final int NUM_SENDERS = 50

    private static final int MESSAGE_SIZE_BYTES = 5_000

    private static final long SLEEP_BETWEEN_SENDS_MS = 1

    static void main(String[] args) {
        log.info('NUM_SENDERS = {}', NUM_SENDERS)
        log.info('MESSAGE_SIZE_BYTES = {}', MESSAGE_SIZE_BYTES)
        log.info('SLEEP_BETWEEN_SENDS_MS = {}', SLEEP_BETWEEN_SENDS_MS)

        final List<Thread> threadList = (0..NUM_SENDERS-1).collect({i ->
            Thread t = new Thread(new SMSUnixTestSenderGroovy(
                    topicName: "test.topic.${i}",
                    messageSizeBytes: MESSAGE_SIZE_BYTES,
                    sleepBetweenSendsMS: SLEEP_BETWEEN_SENDS_MS))
            t.start()
            t
        })

        threadList.forEach({ Thread t -> Uninterruptibles.joinUninterruptibly(t) })
    }

    @Override
    SMSConnection createConnection() {
        SMSUnixConnection.newBuilder()
                .setBrokerAddress(new DomainSocketAddress(Paths.get("/tmp", "sms-unix-socket").toFile()))
                .build()
    }
}
