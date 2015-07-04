package org.aaron.sms.api;

import com.google.protobuf.ByteString;

/**
 * SMSConnection represents a single client connection to an SMS Broker.
 * 
 * SMSConnection asynchronously attempts to connect to the SMS Broker when
 * start() is called.
 * 
 * If the connection to the SMS Broker is lost, SMSConnection automatically
 * attempts to reconnect. When the connection is reestablished to the SMS
 * Broker, subscriptions to all topics are reestablished automatically.
 * 
 * While there is no active connection to the SMS Broker, all calls to
 * writeToTopic will silently discard messages. It is the user's responsibility
 * to manage this if necessary.
 * 
 * SMSConnection is safe for use by multiple concurrent threads.
 */
public interface SMSConnection {

	/**
	 * Register a listener for connection state changes.
	 * 
	 * @param listener
	 */
	public void registerConnectionStateListener(SMSConnectionStateListener listener);

	/**
	 * Unregister a listener for connection state changes.
	 * 
	 * @param listener
	 */
	public void unregisterConnectionStateListener(SMSConnectionStateListener listener);

	/**
	 * Subscribe to a topic to begin receiving messages from it.
	 * 
	 * @param topicName
	 *            topic name
	 * @param messageListener
	 *            message listener
	 */
	public void subscribeToTopic(String topicName, SMSMessageListener messageListener);

	/**
	 * Unsubscribe from a topic to stop receiving messages from it
	 * 
	 * @param topicName
	 *            topic name
	 */
	public void unsubscribeFromTopic(String topicName);

	/**
	 * Start the SMSConnection. Initiates a connection attempt to the SMS
	 * Broker.
	 */
	public void start();

	/**
	 * Is the SMSConnection started?
	 * 
	 * @return true if started, false otherwise
	 */
	public boolean isStarted();

	/**
	 * Write a message to a topic asynchronously.
	 * 
	 * If this SMSConnection is not currently connected to an SMS Broker, the
	 * message will be silently dropped.
	 * 
	 * @param topicName
	 *            topic name
	 * @param message
	 *            message payload
	 */
	public void writeToTopic(String topicName, ByteString message);

	/**
	 * Destroy this SMSConnection. Close the connection to the SMS Broker and
	 * destroy all resources.
	 * 
	 * This SMSConnection must not be used after destroy is called.
	 * 
	 * It is the user's responsibility to call destroy on all SMSConnections
	 * created.
	 */
	public void destroy();

}
