package org.aaron.sms.api;

import com.google.protobuf.ByteString;

/**
 * A listener for events from an SMSConnection.
 */
@FunctionalInterface
public interface SMSMessageListener {

    /**
     * Handle an incoming message for a topic subscription.
     *
     * @param message message payload
     */
    void handleIncomingMessage(ByteString message);

}
