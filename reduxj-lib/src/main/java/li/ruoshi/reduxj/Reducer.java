package li.ruoshi.reduxj;


import io.reactivex.annotations.NonNull;

/**
 * Created by ruoshili on 2/16/2017.
 */

public interface Reducer<TState, TAction extends Action> {

    /**
     * 返回该Reducer可以处理的Action类型
     *
     * @return 可以处理的Action类型，不然不为null
     */
    @NonNull
    Class<TAction> getActionClass();

    /**
     * 根据Action
     *
     * @param action
     * @param originalState
     * @return 新的状态，必然不为null
     */
    @NonNull
    TState reduce(TAction action, TState originalState);

}
