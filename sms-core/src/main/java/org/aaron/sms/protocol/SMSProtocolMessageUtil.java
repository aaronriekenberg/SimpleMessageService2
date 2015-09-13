package org.aaron.sms.protocol;

import com.google.protobuf.ByteString;
import org.aaron.sms.protocol.protobuf.SMSProtocol;

public class SMSProtocolMessageUtil {

    private SMSProtocolMessageUtil() {

    }

    public static SMSProtocol.ClientToBrokerMessage buildClientToBrokerMessage(
            SMSProtocol.ClientToBrokerMessage.ClientToBrokerMessageType type,
            String topicName) {
        return SMSProtocol.ClientToBrokerMessage.newBuilder()
                .setMessageType(type)
                .setTopicName(topicName)
                .build();
    }

    public static SMSProtocol.ClientToBrokerMessage buildClientToBrokerMessage(
            SMSProtocol.ClientToBrokerMessage.ClientToBrokerMessageType type,
            String topicName,
            ByteString payload) {
        return SMSProtocol.ClientToBrokerMessage.newBuilder()
                .setMessageType(type)
                .setTopicName(topicName)
                .setMessagePayload(payload)
                .build();
    }

    public static SMSProtocol.BrokerToClientMessage buildBrokerToCLientMessage(
            SMSProtocol.BrokerToClientMessage.BrokerToClientMessageType type,
            String topicName,
            ByteString payload) {
        return SMSProtocol.BrokerToClientMessage.newBuilder()
                .setMessageType(type)
                .setTopicName(topicName)
                .setMessagePayload(payload)
                .build();
    }
}
