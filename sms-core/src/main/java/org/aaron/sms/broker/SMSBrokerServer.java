package org.aaron.sms.broker;

interface SMSBrokerServer {

    boolean isAvailable();

    void start();

    void destroy();

    boolean isDestroyed();

    void awaitDestroyed() throws InterruptedException;

    void awaitDestroyedUninterruptible();

}
