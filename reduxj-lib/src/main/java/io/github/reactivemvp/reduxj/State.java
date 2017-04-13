package io.github.reactivemvp.reduxj;

import io.reactivex.annotations.NonNull;

/**
 * Created by ruoshi on 3/31/17.
 * define a basic shape of the State
 */

public abstract class State {
    protected State(final Builder<? extends State> builder) {
        if (builder == null) {
            throw new NullPointerException("builder is null");
        }
    }

    protected abstract static class Builder<T extends State> {
        public Builder() {
        }

        public Builder(T originalState) {
        }

        @NonNull
        public abstract T build();
    }
}

