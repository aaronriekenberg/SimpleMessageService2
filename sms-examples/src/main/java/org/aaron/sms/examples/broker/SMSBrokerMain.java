package org.aaron.sms.examples.broker;

import com.google.common.util.concurrent.Uninterruptibles;
import io.netty.channel.unix.DomainSocketAddress;
import org.aaron.sms.broker.SMSBroker;

import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class SMSBrokerMain {

    public static void main(String[] args) {
        new SMSBroker().addTCPServer(new InetSocketAddress(10001))
                .addUnixServer(new DomainSocketAddress(Paths.get("/tmp", "sms-unix-socket").toFile())).start();

        while (true) {
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.MINUTES);
        }
    }
}
