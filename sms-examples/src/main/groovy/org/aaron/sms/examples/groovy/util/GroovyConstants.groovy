package org.aaron.sms.examples.groovy.util

import io.netty.channel.unix.DomainSocketAddress

import java.nio.file.Paths

class GroovyConstants {

    static final DomainSocketAddress UNIX_ADDRESS = new DomainSocketAddress(Paths.get('/tmp', 'sms-unix-socket').toFile())

    static final Integer TCP_PORT = 10001

    static final InetSocketAddress TCP_BROKER_LISTEN_ADDRESS = new InetSocketAddress(TCP_PORT)

    static final InetSocketAddress TCP_BROKER_CONNECT_ADDRESS = new InetSocketAddress(InetAddress.getLoopbackAddress(), TCP_PORT)

}
