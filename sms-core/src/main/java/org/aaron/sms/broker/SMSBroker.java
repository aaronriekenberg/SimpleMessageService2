package org.aaron.sms.broker;

import com.google.common.collect.ImmutableList;
import io.netty.channel.unix.DomainSocketAddress;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class SMSBroker {

    private final List<SMSBrokerServer> servers;

    private SMSBroker(List<SMSBrokerServer> servers) {
        this.servers = ImmutableList.copyOf(servers);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public SMSBroker start() {
        servers.forEach(SMSBrokerServer::start);
        return this;
    }

    public SMSBroker destroy() {
        servers.forEach(SMSBrokerServer::destroy);
        return this;
    }

    public static class Builder {

        private final ArrayList<SMSBrokerServer> servers = new ArrayList<>();

        private SMSTopicContainer topicContainer = null;

        public Builder() {
            reset();
        }

        public Builder addTCPServer(InetSocketAddress bindAddress) {
            servers.add(new SMSBrokerTCPServer(getTopicContainer(), bindAddress));
            return this;
        }

        public Builder addUnixServer(DomainSocketAddress bindAddress) {
            servers.add(new SMSBrokerUnixServer(getTopicContainer(), bindAddress));
            return this;
        }

        public SMSBroker build() {
            final SMSBroker broker = new SMSBroker(servers);
            reset();
            return broker;
        }

        public Builder reset() {
            servers.clear();
            topicContainer = null;
            return this;
        }

        private SMSTopicContainer getTopicContainer() {
            if (topicContainer == null) {
                topicContainer = new SMSTopicContainer();
            }
            return topicContainer;
        }
        
    }

}
