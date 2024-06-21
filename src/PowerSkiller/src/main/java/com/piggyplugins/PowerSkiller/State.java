public abstract class State {
    /**
     * All virtual methods are boolean, so States can block other States.
     */
    public abstract boolean run();
    public abstract boolean handleEvent();

    protected StateStack stack;
    protected Context ctx;

    public State(StateStack stack, Context ctx) {
        this.stack = stack;
        this.context = context;
    }

    protected void requestPushState(StateID stateId) {
        stack.pushState(stateId);
    }

    protected void requestPopState() {
        stack.popState();
    }

    protected StateID requestPreviousState() {
        return stack.getPreviousState();
    }

    protected void requestClearStates() {
        stack.clearStates();
    }

    protected void getHistory
}
