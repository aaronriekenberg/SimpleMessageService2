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

        private final SMSTopicContainer topicContainer = new SMSTopicContainer();

        private final ArrayList<SMSBrokerServer> servers = new ArrayList<>();

        public Builder addTCPServer(InetSocketAddress bindAddress) {
            servers.add(new SMSBrokerTCPServer(topicContainer, bindAddress));
            return this;
        }

        public Builder addUnixServer(DomainSocketAddress bindAddress) {
            servers.add(new SMSBrokerUnixServer(topicContainer, bindAddress));
            return this;
        }

        public SMSBroker build() {
            return new SMSBroker(servers);
        }

    }

}
