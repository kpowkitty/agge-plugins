/**
 * @file StateStack.java
 * @class StateStack
 * Stack to control the active State.
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
import java.util.function.Supplier;

public class StateStack {
    public enum Action {
        PUSH,
        POP,
        CLEAR
    }

    private static class PendingChange {
        Action action;
        States stateId;

        PendingChange(Action action, StateID stateId)
        {
            this.action = action;
            this.stateId = stateId;
        }
    }

    public StateStack(Context ctx)
    {
        this.ctx = ctx;
    }

    public <T extends State> void registerState(StateID stateId, 
                                                Supplier<T> stateSupplier) 
    {
        factories.put(stateId, stateSupplier::get);
    }

    public void run() 
    {
        for (State state : stack) {
            state.run();
        }
    }

    public void handleEvent() 
    {
        for (State state : stack) {
            state.handleEvent(event);
        }
        applyPendingChanges();
    }

    public void pushState(StateID stateID)
    {
        pendingList.add(new PendingChange(Action.PUSH, stateId));
    }

    public void popState()
    {
        pendingList.add(new PendingChange(Action.POP, StateID.NONE));
    }

    public void clearStates()
    {
        pendingList.add(new PendingChange(Action.CLEAR, StateID.NONE));
    }

    public boolean isEmpty()
    {
        return stack.isEmpty();
    }

    /**
     * Return a copy of the history, so States can create a copy that they can
     * manipulate freely.
     * Mainly useful for PathingState knowing where it should be pathing to 
     * (i.e., what was the last State), or other context sensitive States.
     *
     * @return ArrayDeque<StateID> history
     * A copy of the history.
     */
    public Deque<StateID> getHistory()
    {
        return new ArrayDeque<StateID>(history);
    }

    private State createState(StateID stateId)
    {
        Supplier<State> factory = factories.get(stateId);
        return factory != null ? factory.get() : null;
    }

    private void applyPendingChanges()
    {
        for (PendingChange change : pendingList) {
            switch (change.action) {
                case PUSH:
                    stack.addFirst(createState(change.stateId));
                    history.addFirst(change.stateId);
                    break;
                case POP:
                    stack.removeFirst();
                    history.removeFirst();
                    break;
                case CLEAR:
                    stack.clear();
                    history.clear();
                    break;
            }
        }
        pendingList.clear();
    }

    private final Deque<State> stack = new ArrayDeque<>();
    private final Deque<StateID> history = new ArrayDeque<>();
    private final Deque<PendingChange> pendingList = new ArrayDeque<>();
    private final Map<States, Supplier<State>> factories = new HashMap<>();
    private Context ctx;
}
