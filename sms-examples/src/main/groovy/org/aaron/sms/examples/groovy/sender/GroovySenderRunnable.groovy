package org.aaron.sms.examples.groovy.sender

import com.google.protobuf.ByteString
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.aaron.sms.api.SMSConnection

@CompileStatic
@Slf4j
class GroovySenderRunnable implements Runnable {

    SMSConnection smsConnection

    String topicName

    long messageSizeBytes

    long sleepBetweenSendsMS

    @Override
    void run() {
        smsConnection.registerConnectionStateListener({
            newState -> log.info "connection state changed ${newState}"
        })

        smsConnection.start()

        final ByteString buffer = ByteString.copyFrom(new byte[messageSizeBytes])
        while (true) {
            smsConnection.writeToTopic(topicName, buffer)
            sleep(sleepBetweenSendsMS)
        }
    }

}
