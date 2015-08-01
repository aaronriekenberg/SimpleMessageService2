package org.aaron.sms.broker;

import com.google.common.collect.ImmutableList;
import io.netty.channel.unix.DomainSocketAddress;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        private final ArrayList<Function<SMSTopicContainer, SMSBrokerServer>> createServerFunctions = new ArrayList<>();

        public Builder addTCPServer(InetSocketAddress bindAddress) {
            createServerFunctions.add((topicContainer) -> new SMSBrokerTCPServer(topicContainer, bindAddress));
            return this;
        }

        public Builder addUnixServer(DomainSocketAddress bindAddress) {
            createServerFunctions.add((topicContainer) -> new SMSBrokerUnixServer(topicContainer, bindAddress));
            return this;
        }

        public SMSBroker build() {
            final SMSTopicContainer topicContainer = new SMSTopicContainer();
            final List<SMSBrokerServer> servers = createServerFunctions.stream().map(f ->
                    f.apply(topicContainer)).collect(Collectors.toList());

            final SMSBroker broker = new SMSBroker(servers);
            createServerFunctions.clear();
            return broker;
        }

    }

}
