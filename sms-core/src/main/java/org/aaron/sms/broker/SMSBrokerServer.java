package org.aaron.sms.broker;

interface SMSBrokerServer {

    public boolean isAvailable();

    public void start();

    public void destroy();

    public boolean isDestroyed();

    public void awaitDestroyed() throws InterruptedException;

    public void awaitDestroyedUninterruptible();

}
