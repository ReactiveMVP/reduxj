package li.ruoshi.reduxj;

import java.util.List;

/**
 * Created by ruoshili on 2/17/2017.
 */

public interface StateChangedListener2<TState> extends StateChangedListener<TState> {
    /**
     * 返回监听器关心的action类型
     *
     * @return 必须返回不可变列表
     */
    List<Class<? extends Action>> getInterestedActionTypes();
}
