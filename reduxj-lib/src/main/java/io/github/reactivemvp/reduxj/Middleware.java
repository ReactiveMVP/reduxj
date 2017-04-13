package io.github.reactivemvp.reduxj;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;

/**
 * Created by ruoshi on 4/11/17.
 */

public interface Middleware {

    @NonNull
    Observable<? extends Action> process(final Action action);
}
