package org.aaron.sms.examples.groovy.receiver

import com.google.protobuf.ByteString
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.aaron.sms.api.SMSConnection

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@CompileStatic
@Slf4j
class GroovyReceiver {

    private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors())

    GroovyReceiver(SMSConnection smsConnection, String topicName) {
        final AtomicInteger messagesReceived = new AtomicInteger(0)

        smsConnection.registerConnectionStateListener({
            newState -> log.info("connection state changed {}", newState)
        })

        smsConnection.subscribeToTopic(topicName, { ByteString message ->
            log.debug('handleIncomingMessage topic {} length {}', topicName, message.size())
            messagesReceived.getAndIncrement()
        });

        smsConnection.start()

        EXECUTOR.scheduleAtFixedRate(
                {
                    try {
                        log.info('{} messages received last second = {}', topicName, messagesReceived.getAndSet(0))
                    } catch (Throwable t) {
                        log.warn('caught', t)
                    }
                }, 0, 1, TimeUnit.SECONDS
        )
    }
}
