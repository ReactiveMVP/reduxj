package io.github.reactivemvp.reduxj;

/**
 * Created by ruoshi on 4/11/17.
 */

public final class StateChangedEventArgs<TState> {
    private final Action action;
    private final TState state;

    public StateChangedEventArgs(Action action, TState state) {
        this.action = action;
        this.state = state;
    }

    public Action getAction() {
        return action;
    }

    public TState getState() {
        return state;
    }
}

