package io.github.reactivemvp.reduxj.action;

import io.github.reactivemvp.reduxj.Action;

/**
 * Created by ruoshili on 4/12/2017.
 */

public final class IncreaseCounterAction implements Action {
    @Override
    public String getActionTypeName() {
        return "io.github.reactivemvp.model.action.IncreaseCounterAction";
    }
}
