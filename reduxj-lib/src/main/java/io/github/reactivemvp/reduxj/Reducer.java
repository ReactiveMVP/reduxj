package io.github.reactivemvp.reduxj;

/**
 * Created by ruoshi on 3/31/17.
 */

public interface Reducer<TState extends State> {
    /**
     * A function that returns the next state tree, given
     * the current state tree and the action to handle.
     * @param currentState
     * @param action
     * @return
     */
    TState reduce(TState currentState, Action action);
}
