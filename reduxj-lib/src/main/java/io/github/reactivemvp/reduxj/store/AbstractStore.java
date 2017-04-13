package io.github.reactivemvp.reduxj.store;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.github.reactivemvp.reduxj.Action;
import io.github.reactivemvp.reduxj.Middleware;
import io.github.reactivemvp.reduxj.Reducer;
import io.github.reactivemvp.reduxj.State;
import io.github.reactivemvp.reduxj.StateChangedEventArgs;
import io.github.reactivemvp.reduxj.StateChangedListener;
import io.github.reactivemvp.reduxj.Store;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * Created by ruoshi on 4/11/17.
 */

public abstract class AbstractStore<TState extends State> implements Store<TState> {
    private final Object mReduceSyncRoot = new Object();
    private final Object mMiddlewareSyncRoot = new Object();

    private final Relay<StateChangedEventArgs<TState>> mStateChangedRelay;
    private final Observable<StateChangedEventArgs<TState>> mStateChangedObservable;

    public AbstractStore() {
        mStateChangedRelay = PublishRelay.create();
        mStateChangedObservable = mStateChangedRelay.serialize();
    }

    private final Consumer<Throwable> mOnError = new Consumer<Throwable>() {
        @Override
        public void accept(@NonNull Throwable throwable) throws Exception {
            onStateChangedListenerError(throwable);
        }
    };
    private TState mState;
    private List<Reducer<TState>> mReducers = Collections.emptyList();
    private List<Middleware> mMiddlewareList = Collections.emptyList();

    @Override
    public TState getState() {
        return mState;
    }

    @Override
    public final <TAction extends Action> TAction dispatch(@NonNull final TAction action) {
        executeMiddleware(action);
        executeReducers(action);
        return action;
    }

    private <TAction extends Action> void executeReducers(@NonNull TAction action) {
        final boolean shouldFireStateChangedEvent;
        final TState newState;
        synchronized (mReduceSyncRoot) {
            final TState originalState = mState;

            for (Reducer<TState> r : mReducers) {
                mState = r.reduce(mState, action);
                if (mState == null) {
                    mState = originalState;
                }
            }
            // fire state changed event only if the state was really changed
            shouldFireStateChangedEvent = originalState != mState;
            // ensure that mState can not be changed by other thread
            newState = mState;
        }
        if (shouldFireStateChangedEvent) {
            mStateChangedRelay.accept(new StateChangedEventArgs<>(action, newState));
        }
    }

    private <TAction extends Action> void executeMiddleware(@NonNull final TAction action) {
        if (mMiddlewareList.size() > 0) {
            synchronized (mMiddlewareSyncRoot) {
                Observable.fromIterable(mMiddlewareList)
                        .flatMap(new Function<Middleware, ObservableSource<? extends Action>>() {
                            @Override
                            public ObservableSource<? extends Action> apply(@NonNull Middleware middleware) throws Exception {
                                return middleware.process(action);
                            }
                        })
                        .subscribe(new Consumer<Action>() {
                                       @Override
                                       public void accept(@NonNull Action resultAction) throws Exception {
                                           dispatch(resultAction);
                                       }
                                   },
                                new Consumer<Throwable>() {
                                    @Override
                                    public void accept(@NonNull Throwable throwable) throws Exception {
                                        onMiddlewareError(throwable);
                                    }
                                });
            }
        }
    }

    protected void onMiddlewareError(@NonNull Throwable throwable) {
    }

    protected void onStateChangedListenerError(@NonNull Throwable throwable) {
    }

    @Override
    public Observable<StateChangedEventArgs<TState>> getObservable() {
        return mStateChangedObservable;
    }

    @Override
    public Disposable subscribe(@NonNull final StateChangedListener<TState> stateChangedListener) {
        return mStateChangedRelay.filter(new Predicate<StateChangedEventArgs<TState>>() {
            @Override
            public boolean test(@NonNull StateChangedEventArgs<TState> eventArgs) throws Exception {
                return stateChangedListener.hasInterestFor(eventArgs.getAction());
            }
        }).subscribe(new Consumer<StateChangedEventArgs<TState>>() {
            @Override
            public void accept(@NonNull StateChangedEventArgs<TState> eventArgs) throws Exception {
                try {
                    stateChangedListener.onStateChanged(eventArgs);
                } catch (Throwable ignore) {
                    onStateChangedListenerError(ignore);
                }
            }
        }, mOnError);
    }

    @SafeVarargs
    public final void init(@NonNull final TState initState,
                           final Reducer<TState>... reducers) {
        init(initState, Collections.<Middleware>emptyList(), Arrays.asList(reducers));
    }

    public final void init(@NonNull final TState initState,
                           final List<Middleware> middlewareList,
                           final List<Reducer<TState>> reducers) {
        if (initState == null) {
            throw new NullPointerException("initState is null");
        }
        mState = initState;
        mMiddlewareList = Collections.unmodifiableList(middlewareList);
        mReducers = Collections.unmodifiableList(reducers);
    }

}
