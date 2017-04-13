package io.github.reactivemvp.reduxj;

/**
 * Created by ruoshili on 4/12/2017.
 */

public class CounterState extends State {
    private final int mCount;

    private CounterState(Builder builder) {
        super(builder);
        mCount = builder.mCount;
    }

    public int getCount() {
        return mCount;
    }

    public static class Builder extends State.Builder<CounterState> {
        private int mCount = 0;

        public Builder() {
            mCount = 0;
        }

        public Builder(CounterState originalState) {
            super(originalState);
            mCount = originalState.mCount;
        }

        public Builder setCount(int count) {
            mCount = count;
            return this;
        }

        @Override
        public CounterState build() {
            return new CounterState(this);
        }
    }
}
