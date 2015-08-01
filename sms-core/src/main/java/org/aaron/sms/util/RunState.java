package org.aaron.sms.util;

import java.util.concurrent.atomic.AtomicReference;

public class RunState {

    public enum State {
        NOT_STARTED,

        RUNNING,

        DESTROYED
    }

    private final AtomicReference<State> state = new AtomicReference<>(State.NOT_STARTED);

    public RunState() {

    }

    public boolean start() {
        return state.compareAndSet(State.NOT_STARTED, State.RUNNING);
    }

    public boolean destroy() {
        return state.compareAndSet(State.RUNNING, State.DESTROYED);
    }

    public State getState() {
        return state.get();
    }

}
