package li.ruoshi.reduxj;

/**
 * Created by ruoshili on 2/16/2017.
 * Redux的动作接口，所有实现此接口的类型必须标记为final
 */
public interface Action {
    /**
     * 获取动作类型的名称
     *
     * @return
     */
    String getActionTypeName();
}
