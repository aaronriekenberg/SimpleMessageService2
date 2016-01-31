package org.aaron.sms.examples.groovy.sender

import com.google.protobuf.ByteString
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.aaron.sms.api.SMSConnection

@CompileStatic
@Slf4j
abstract class AbstractTestSenderGroovy implements Runnable {

    String topicName

    int messageSizeBytes

    long sleepBetweenSendsMS

    @Override
    void run() {
        final SMSConnection smsConnection = createConnection()

        smsConnection.registerConnectionStateListener({
            newState -> log.info('connection state changed {}', newState)
        })

        smsConnection.start()

        final ByteString buffer = ByteString.copyFrom(new byte[messageSizeBytes])
        while (true) {
            smsConnection.writeToTopic(topicName, buffer)
            sleep(sleepBetweenSendsMS)
        }
    }

    abstract SMSConnection createConnection()
}
