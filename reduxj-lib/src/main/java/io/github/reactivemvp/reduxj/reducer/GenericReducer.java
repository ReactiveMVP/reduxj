package io.github.reactivemvp.reduxj.reducer;

import io.github.reactivemvp.reduxj.Action;
import io.github.reactivemvp.reduxj.Reducer;
import io.github.reactivemvp.reduxj.State;
import io.reactivex.annotations.NonNull;

/**
 * Since Java is a strongly typed programming language, we can take advantage of
 * its generic feature to simplify the reducer implication.
 */
public abstract class GenericReducer<TState extends State, TAction extends Action>
        implements Reducer<TState> {

    /**
     * determine if this reducer should handle the action in parameter
     * @param action
     * @return
     */
    protected boolean shouldHandle(final Action action) {
        return action != null && getAcceptableActionClass().equals(action.getClass());
    }

    /**
     * Return the action type this reducer can handle
     *
     * @return
     */
    @NonNull
    protected abstract Class<TAction> getAcceptableActionClass();

    @Override
    @NonNull
    public final TState reduce(TState currentState, Action action) {
        if (shouldHandle(action)) {
            //noinspection unchecked
            return doReduce(currentState, (TAction) action);
        }
        return currentState;
    }

    @NonNull
    protected abstract TState doReduce(TState currentState, TAction action);

}
