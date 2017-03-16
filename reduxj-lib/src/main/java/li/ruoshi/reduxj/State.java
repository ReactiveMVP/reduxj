package li.ruoshi.reduxj;


import io.reactivex.annotations.NonNull;

/**
 * Created by ruoshili on 3/7/2017.
 * <p>
 * 约定一个State类型基本的形态，包括必须有相应的Builder类，必须有一个接受Builder类型参数的构造函数
 */
public abstract class State {
    protected State(final Builder<? extends State> builder) {
        if (builder == null) {
            throw new NullPointerException("builder is null");
        }
    }

    public abstract static class Builder<T extends State> {
        public Builder() {
        }

        public Builder(T originalState) {
        }

        @NonNull
        public abstract T build();
    }
}
