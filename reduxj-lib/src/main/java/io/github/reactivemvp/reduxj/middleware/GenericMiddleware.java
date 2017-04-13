package io.github.reactivemvp.reduxj.middleware;

import io.github.reactivemvp.reduxj.Action;
import io.github.reactivemvp.reduxj.Middleware;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;

public abstract class GenericMiddleware<TAction extends Action> implements Middleware {
    /**
     * determine if this middleware should handle the action in parameter
     *
     * @param action
     * @return
     */
    protected boolean shouldHandle(final Action action) {
        return action != null && getAcceptableActionClass().equals(action.getClass());
    }

    @NonNull
    @Override
    public final Observable<? extends Action> process(Action action) {
        if (shouldHandle(action)) {
            //noinspection unchecked
            return doProcess((TAction) action);
        }
        return Observable.empty();
    }

    @NonNull
    protected abstract Class<TAction> getAcceptableActionClass();

    @NonNull
    protected abstract Observable<? extends Action> doProcess(TAction action);
}
