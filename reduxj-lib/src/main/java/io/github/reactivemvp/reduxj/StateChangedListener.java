package io.github.reactivemvp.reduxj;

/**
 * Created by ruoshi on 4/11/17.
 */

public interface StateChangedListener<TState extends State> {
    void onStateChanged(StateChangedEventArgs<TState> eventArgs);
    boolean hasInterestFor(final Action action);
}
