package org.aaron.sms.broker;

import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

class SMSTopicContainer {

    private final ConcurrentHashMap<String, SMSTopic> topicNameToInfo = new ConcurrentHashMap<>();

    public SMSTopic getTopic(String topicName) {
        checkNotNull(topicName, "topicName is null");
        checkArgument(topicName.length() > 0, "topicName is empty");

        return topicNameToInfo.computeIfAbsent(topicName, SMSTopic::new);
    }
}
