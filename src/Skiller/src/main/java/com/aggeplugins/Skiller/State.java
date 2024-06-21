/**
 * @file State.java
 * @class State
 * Virtual interface for States to inherit.
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-20
 *
 */

package com.aggeplugins.Skiller;

import com.aggeplugins.Skiller.State;
import com.aggeplugins.Skiller.StateID;
import com.aggeplugins.Skiller.Context;
import com.aggeplugins.Skiller.StateStack;

import java.util.*;

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
        this.ctx = ctx;
    }

    protected void requestPushState(StateID stateId) {
        stack.pushState(stateId);
    }

    protected void requestPopState() {
        stack.popState();
    }

    protected void requestClearStates() {
        stack.clearStates();
    }
}
