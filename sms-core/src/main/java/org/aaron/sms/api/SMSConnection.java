package org.aaron.sms.api;

import com.google.protobuf.ByteString;

/**
 * SMSConnection represents a single client connection to an SMS Broker.
 * <p>
 * SMSConnection asynchronously attempts to connect to the SMS Broker when
 * start() is called.
 * <p>
 * If the connection to the SMS Broker is lost, SMSConnection automatically
 * attempts to reconnect. When the connection is reestablished to the SMS
 * Broker, subscriptions to all topics are reestablished automatically.
 * <p>
 * While there is no active connection to the SMS Broker, all calls to
 * writeToTopic will silently discard messages. It is the user's responsibility
 * to manage this if necessary.
 * <p>
 * SMSConnection is safe for use by multiple concurrent threads.
 */
public interface SMSConnection {

    /**
     * Determine if this connection type is available for use
     *
     * @return true if available, false otherwise
     */
    boolean isAvailable();

    /**
     * Register a listener for connection state changes.
     *
     * @param listener
     */
    void registerConnectionStateListener(SMSConnectionStateListener listener);

    /**
     * Unregister a listener for connection state changes.
     *
     * @param listener
     */
    void unregisterConnectionStateListener(SMSConnectionStateListener listener);

    /**
     * Subscribe to a topic to begin receiving messages from it.
     *
     * @param topicName       topic name
     * @param messageListener message listener
     */
    void subscribeToTopic(String topicName, SMSMessageListener messageListener);

    /**
     * Unsubscribe from a topic to stop receiving messages from it
     *
     * @param topicName topic name
     */
    void unsubscribeFromTopic(String topicName);

    /**
     * Start the SMSConnection. Initiates a connection attempt to the SMS
     * Broker.
     */
    void start();

    /**
     * Is the SMSConnection started?
     *
     * @return true if started, false otherwise
     */
    boolean isStarted();

    /**
     * Write a message to a topic asynchronously.
     * <p>
     * If this SMSConnection is not currently connected to an SMS Broker, the
     * message will be silently dropped.
     *
     * @param topicName topic name
     * @param message   message payload
     */
    void writeToTopic(String topicName, ByteString message);

    /**
     * Destroy this SMSConnection. Close the connection to the SMS Broker and
     * destroy all resources.
     * <p>
     * This SMSConnection must not be used after destroy is called.
     * <p>
     * It is the user's responsibility to call destroy on all SMSConnections
     * created.
     */
    void destroy();

}
