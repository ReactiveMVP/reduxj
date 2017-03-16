package li.ruoshi.reduxj;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;

/**
 * Created by ruoshili on 3/15/2017.
 * <p>
 * 中间件负责处理action中有副作用的部分，
 * 例如进行IO请求，Middle可以通过pipe line方式进行编排，
 * Middleware的功能是把一个action转换为一个或者多个其他的action，
 * 例如根据输入的action发起http请求，然后把结果转换为新的action再次投递到store
 * Middleware不可以对State进行修改
 */
public interface Middleware<TAction extends Action> {

    /**
     * 返回该Reducer可以处理的Action类型
     *
     * @return 可以处理的Action类型，不然不为null
     */
    @NonNull
    Class<TAction> getActionClass();

    /**
     * 如果action是有副作用的，在这里进行处理，
     * 然后生成一个或者多个新的action再次投递到store
     *
     * @param action
     * @return action的执行结果
     */
    @NonNull
    Observable<? extends Action> process(TAction action);

}
