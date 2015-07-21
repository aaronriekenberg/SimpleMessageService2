package org.aaron.sms.api;

/**
 * A listener for connection state events from an SMSConnection.
 */
@FunctionalInterface
public interface SMSConnectionStateListener {

    /**
     * Notification that the state of the network connection between
     * SMSConnection and the broker has changed.
     *
     * @param newState new connection state
     */
    void connectionStateChanged(SMSConnectionState newState);

}
