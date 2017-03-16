package li.ruoshi.reduxj.store;


import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import li.ruoshi.reduxj.Action;
import li.ruoshi.reduxj.Middleware;
import li.ruoshi.reduxj.Reducer;
import li.ruoshi.reduxj.StateChangedEventArgs;
import li.ruoshi.reduxj.StateChangedListener;
import li.ruoshi.reduxj.StateChangedListener2;
import li.ruoshi.reduxj.Store;

/**
 * Created by ruoshili on 2/16/2017.
 * <p>
 * Store的泛型实现，已经实现了所有的Store的功能，它的子类只需要继承它，并特化TState即可
 */
public abstract class AbstractStore<TState extends State> implements Store<TState> {
    private TState mState;
    private List<Reducer<TState, ? extends Action>> mReducers = Collections.emptyList();
    private List<Middleware<? extends Action>> mMiddlewareList = Collections.emptyList();


    protected final Object mReduceSyncRoot = new Object();
    protected final Object mMiddlewareSyncRoot = new Object();

    @Override
    public TState getState() {
        return mState;
    }

    private final Relay<StateChangedEventArgs<TState>> mActionRelay = PublishRelay.create();

    private final Consumer<Throwable> mOnError = new Consumer<Throwable>() {
        @Override
        public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
        }
    };


    /**
     * 分发动作。在这里寻找合适的reducer处理action
     *
     * @param action
     * @param <TAction>
     * @return 参数中的action是否被处理了，如果没有找到相应的reducer，则说明未被处理
     */
    @Override
    public <TAction extends Action> boolean dispatch(@NonNull final TAction action) {
        if (executeMiddleware(action)) {
            return true;
        }

        // 目前由于reducer做的事情比较简单，只是创建对象和修改对象状态，就在调用者线程执行了
        final boolean shouldFireStateChangedEvent;
        final TState newState;
        synchronized (mReduceSyncRoot) {
            final TState originalState = mState;

            // Reducer与Action类型必须1:1
            for (Reducer<TState, ? extends Action> r : mReducers) {
                // 对比类型，如果匹配才调用
                if (action.getClass().equals(r.getActionClass())) {
                    //noinspection unchecked
                    mState = ((Reducer<TState, TAction>) r).reduce(action, mState);
                    //noinspection ConstantConditions
                    if (mState == null) {
                        mState = originalState;
                    }
                    break;
                }
            }
            // 只有状态发生了改变才发出通知
            shouldFireStateChangedEvent = originalState != mState;
            // 在同步区域内，保证mState不会被其他线程意外的改变
            newState = mState;
        }
        if (shouldFireStateChangedEvent) {
            mActionRelay.accept(new StateChangedEventArgs<>(action, newState));
        }
        return shouldFireStateChangedEvent;
    }

    private <TAction extends Action> boolean executeMiddleware(@NonNull final TAction action) {
        if (mMiddlewareList.size() > 0) {
            // 使用 middleware 处理副作用，Middleware需要自己处理异步
            synchronized (mMiddlewareSyncRoot) {
                for (final Middleware<? extends Action> middleware : mMiddlewareList) {
                    if (action.getClass().equals(middleware.getActionClass())) {
                        //noinspection unchecked
                        ((Middleware<TAction>) middleware)
                                .process(action)
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
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected void onMiddlewareError(@NonNull Throwable throwable) {

    }

    protected void onStateChangedListenerError(@NonNull Throwable throwable) {

    }

    @Override
    public Observable<StateChangedEventArgs<TState>> getObservable() {
        return mActionRelay.toSerialized();
    }

    @Override
    public Disposable subscribe(@NonNull final StateChangedListener<TState> stateChangedListener) {
        Observable<StateChangedEventArgs<TState>> observable = mActionRelay;

        if (stateChangedListener instanceof StateChangedListener2) {
            StateChangedListener2<TState> listener2 = (StateChangedListener2<TState>) stateChangedListener;

            final List<Class<? extends Action>> interestedActionTypes = listener2.getInterestedActionTypes();

            if (interestedActionTypes != null && interestedActionTypes.size() > 0) {
                observable = observable.filter(new Predicate<StateChangedEventArgs<TState>>() {
                    @Override
                    public boolean test(@io.reactivex.annotations.NonNull
                                                StateChangedEventArgs<TState> eventArgs) throws Exception {
                        return interestedActionTypes.contains(eventArgs.action.getClass());
                    }
                });
            }
        }
        return observable.subscribe(new Consumer<StateChangedEventArgs<TState>>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull
                                       StateChangedEventArgs<TState> eventArgs) throws Exception {
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
                           final Reducer<TState, ? extends Action>... reducers) {
        init(initState,
                Collections.<Middleware<? extends Action>>emptyList(),
                Arrays.asList(reducers));
    }

    public final void init(@NonNull final TState initState,
                           final List<Middleware<? extends Action>> middlewareList,
                           final List<Reducer<TState, ? extends Action>> reducers) {
        //noinspection ConstantConditions
        if (initState == null) {
            throw new NullPointerException("initState is null");
        }
        mState = initState;
        mMiddlewareList = Collections.unmodifiableList(middlewareList);
        mReducers = Collections.unmodifiableList(reducers);
    }

}
