package li.ruoshi.reduxj;

/**
 * Created by ruoshili on 2/17/2017.
 */
public final class StateChangedEventArgs<TState> {
    public final Action action;
    public final TState state;

    public StateChangedEventArgs(Action action, TState state) {
        this.action = action;
        this.state = state;
    }
}
