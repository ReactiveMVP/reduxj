package io.github.reactivemvp.reduxj;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by ruoshi on 3/31/17.
 */

public interface Store<TState extends State> {

    TState getState();

    /**
     * Dispatches an action. This is the only way to trigger a state change.
     *
     * @param action  A plain object describing the change that makes sense for your application.
     * @return The dispatched action
     */
    <TAction extends Action> TAction dispatch(@NonNull TAction action);

    /**
     * Subscribe the state changed event by using traditional listener
     *
     * @param stateChangedListener
     * @return
     */
    Disposable subscribe(@NonNull StateChangedListener<TState> stateChangedListener);

    /**
     * Subscribe the state changed event by using the Rx
     *
     * @return
     */
    Observable<StateChangedEventArgs<TState>> getObservable();

}
