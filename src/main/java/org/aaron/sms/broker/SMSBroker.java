package org.aaron.sms.broker;

import io.netty.channel.unix.DomainSocketAddress;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public class SMSBroker {

	private final SMSTopicContainer topicContainer = new SMSTopicContainer();

	private final ArrayList<AbstractSMSBrokerServer> servers = new ArrayList<>();

	public SMSBroker() {

	}

	public SMSBroker addTCPServer(InetSocketAddress bindAddress) {
		servers.add(new SMSBrokerTCPServer(topicContainer, bindAddress));
		return this;
	}

	public SMSBroker addUnixServer(DomainSocketAddress bindAddress) {
		servers.add(new SMSBrokerUnixServer(topicContainer, bindAddress));
		return this;
	}

	public SMSBroker start() {
		servers.forEach(AbstractSMSBrokerServer::start);
		return this;
	}

	public SMSBroker destroy() {
		servers.forEach(AbstractSMSBrokerServer::destroy);
		return this;
	}

}
