package org.aaron.sms.examples.groovy.receiver

import com.google.protobuf.ByteString
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.aaron.sms.api.SMSConnection
import org.aaron.sms.api.SMSConnectionStateListener

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@CompileStatic
@Slf4j
class GroovyReceiver {

    private static
    final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors())

    GroovyReceiver(SMSConnection smsConnection, GString topicName) {
        AtomicInteger messagesReceived = new AtomicInteger(0)

        smsConnection.registerConnectionStateListener(
                SMSConnectionStateListener.createLoggingListener(log))

        smsConnection.subscribeToTopic(topicName, { ByteString message ->
            log.debug "handleIncomingMessage topic ${topicName} length ${message.size()}"
            messagesReceived.getAndIncrement()
        })

        smsConnection.start()

        EXECUTOR.scheduleAtFixedRate(
                {
                    int currentMessagesReceived = messagesReceived.getAndSet(0)
                    log.info "${topicName} messages received last second = ${currentMessagesReceived}"
                }, 0, 1, TimeUnit.SECONDS
        )
    }

    static void createAndRunReceivers(int numReceivers, Closure<SMSConnection> smsConnectionClosure) {
        log.info "numReceivers = ${numReceivers}"

        (0..<numReceivers).each { i ->
            new GroovyReceiver(smsConnectionClosure(), "test.topic.${i}")
        }

        while (true) {
            sleep TimeUnit.MINUTES.toMillis(1)
        }
    }
}
