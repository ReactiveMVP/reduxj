package li.ruoshi.reduxj;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by ruoshili on 2/16/2017.
 */

public interface Store<TState extends State> {
    TState getState();

    /**
     * 将action分发给 相应的reducer去处理
     *
     * @param action
     * @return true: 改变了状态； false: 没有改变状态
     */
    <TAction extends Action> boolean dispatch(@NonNull TAction action);

    /**
     * 订阅状态变化
     *
     * @param stateChangedListener 监听器
     * @return
     */
    Disposable subscribe(@NonNull StateChangedListener<TState> stateChangedListener);

    /**
     * 获取状态变化的被观察者，用于更加灵活的订阅状态变化
     *
     * @return
     */
    Observable<StateChangedEventArgs<TState>> getObservable();
}
