package org.aaron.sms.examples.groovy.sender

import com.google.common.util.concurrent.Uninterruptibles
import com.google.protobuf.ByteString
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.aaron.sms.api.SMSConnection
import org.aaron.sms.api.SMSConnectionStateListener

@CompileStatic
@Slf4j
class GroovySender implements Runnable {

    SMSConnection smsConnection

    GString topicName

    int messageSizeBytes

    long sleepBetweenSendsMS

    @Override
    void run() {
        smsConnection.registerConnectionStateListener(
                SMSConnectionStateListener.createLoggingListener(log))

        smsConnection.start()

        final ByteString buffer = ByteString.copyFrom(new byte[messageSizeBytes])
        while (true) {
            smsConnection.writeToTopic(topicName, buffer)
            sleep(sleepBetweenSendsMS)
        }
    }

    static void createAndRunSenders(int numSenders, int messageSizeBytes, long sleepBetweenSendsMS, Closure<SMSConnection> smsConnectionClosure) {
        log.info "numSenders = ${numSenders}"
        log.info "messageSizeBytes = ${messageSizeBytes}"
        log.info "sleepBetweenSendsMS = ${sleepBetweenSendsMS}"

        final List<Thread> threadList = (0..<numSenders).collect { i ->
            GString topicName = "test.topic.${i}"
            GroovySender runnable =
                    new GroovySender(
                            smsConnection: smsConnectionClosure(),
                            topicName: topicName,
                            messageSizeBytes: messageSizeBytes,
                            sleepBetweenSendsMS: sleepBetweenSendsMS)
            Thread.start "sender ${topicName}", runnable.&run
        }

        threadList.each { t -> Uninterruptibles.joinUninterruptibly(t) }
    }

}
