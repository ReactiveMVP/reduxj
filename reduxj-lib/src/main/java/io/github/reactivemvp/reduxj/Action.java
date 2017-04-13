package io.github.reactivemvp.reduxj;

import io.reactivex.annotations.NonNull;

/**
 * Created by ruoshi on 3/31/17.
 */

public interface Action {
    /**
     * return the full name of the type of Action
     *
     * @return a Non null string
     */

    @NonNull
    String getActionTypeName();
}
