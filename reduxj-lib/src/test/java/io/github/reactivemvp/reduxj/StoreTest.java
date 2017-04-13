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
import io.reactivex.functions.Consumer;

public class StoreTest {
    private CounterStore mStore;
    private Throwable mError;
    private int mLocalCount;
    private Disposable mDisposable;

    @Before
    public void before() {
        mStore = new CounterStore();
        mStore.init(
                new CounterState.Builder().build(),
                new UpdateCounterReducer());

        mDisposable = mStore.getObservable()
                .subscribe(new Consumer<StateChangedEventArgs<CounterState>>() {
                    @Override
                    public void accept(@NonNull StateChangedEventArgs<CounterState> args) throws Exception {
                        mLocalCount = args.getState().getCount();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        mError = throwable;
                    }
                });

        mLocalCount = mStore.getState().getCount();
    }

    @Test
    public void testNullAction() {
        final int originalCount = mStore.getState().getCount();
        mStore.dispatch(null);
        Assert.assertEquals(originalCount, mStore.getState().getCount());
    }


    @Test
    public void testInc() {
        final int originalCount = mStore.getState().getCount();
        mStore.dispatch(new UpdateCounterAction(UpdateCounterAction.Operation.Increase));
        Assert.assertEquals(originalCount + 1, mStore.getState().getCount());
    }

    @Test
    public void testDec() {
        final int originalCount = mStore.getState().getCount();
        mStore.dispatch(new UpdateCounterAction(UpdateCounterAction.Operation.Decrease));
        Assert.assertEquals(originalCount - 1, mStore.getState().getCount());
    }


    @After
    public void after() {
        Assert.assertEquals(mLocalCount, mStore.getState().getCount());
        Assert.assertNull("mError is not null", mError);
        Assert.assertNotNull("mDisposable is null", mDisposable);
        Assert.assertFalse(mDisposable.isDisposed());
        mDisposable.dispose();
    }

}
