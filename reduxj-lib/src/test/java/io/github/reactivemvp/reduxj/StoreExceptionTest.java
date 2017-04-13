package io.github.reactivemvp.reduxj;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.github.reactivemvp.reduxj.action.UpdateCounterAction;
import io.github.reactivemvp.reduxj.reducer.UpdateCounterReducer;
import io.github.reactivemvp.reduxj.store.CounterStore;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class StoreExceptionTest {
    private final static String TEST_ERROR = "for test";
    private CounterStore mStore;
    private int mLocalCount;
    private Disposable mDisposable;
    private Throwable mStateChangedListenerError;

    @Before
    public void before() {
        mStore = new CounterStore() {
            @Override
            protected void onStateChangedListenerError(@NonNull Throwable throwable) {
                super.onStateChangedListenerError(throwable);
                mStateChangedListenerError = throwable;
            }
        };
        mStore.init(
                new CounterState.Builder().build(),
                new UpdateCounterReducer());

        mLocalCount = mStore.getState().getCount();
    }

    @Test
    public void testListenerError() {
        mDisposable = mStore.subscribe(new StateChangedListener<CounterState>() {
            @Override
            public void onStateChanged(StateChangedEventArgs<CounterState> eventArgs) {
                mLocalCount = eventArgs.getState().getCount();
                throw new RuntimeException(TEST_ERROR);
            }

            @Override
            public boolean hasInterestFor(Action action) {
                return true;
            }
        });

        mStore.dispatch(new UpdateCounterAction(UpdateCounterAction.Operation.Increase));

        Assert.assertEquals(1, 1);
        Assert.assertNotNull("mStateChangedListenerError is null", mStateChangedListenerError);
        Assert.assertEquals(RuntimeException.class, mStateChangedListenerError.getClass());
        Assert.assertEquals(TEST_ERROR, mStateChangedListenerError.getMessage());
    }

    @After
    public void after() {
        Assert.assertEquals(mLocalCount, mStore.getState().getCount());
        Assert.assertNotNull("mStateChangedListenerError is not null", mStateChangedListenerError);
        Assert.assertNotNull("mDisposable is null", mDisposable);
        Assert.assertFalse(mDisposable.isDisposed());
        mDisposable.dispose();
    }
}
