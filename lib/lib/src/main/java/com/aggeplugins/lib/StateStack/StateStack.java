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

package com.aggeplugins.lib.StateStack;

import com.aggeplugins.lib.*;
import com.aggeplugins.lib.StateStack.*;
import com.aggeplugins.lib.export.*;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Supplier;

@Slf4j
public class StateStack {
    public enum Action {
        PUSH,
        POP,
        CLEAR;
    }

    private static class PendingChange {
        Action action;
        StateID stateId;

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

    /**
     * Reverse iterator the StateStack (to operate as a stack). If a State's 
     * run() method returns false, exit and block all other State's from run() 
     * -- give the State complete control.
     */ 
    public void run() 
    {
        //log.info("Running StateStack");
        Iterator<State> it = stack.descendingIterator();
        while (it.hasNext()) {
            if (!it.next().run())
                break;
        }
        applyPendingChanges();
    }

    /**
     * Reverse iterator the StateStack (to operate as a stack). If a State's 
     * handleEvent() method returns false, exit and block all other State's from
     * handleEvent() -- give the State complete control.
     * 
     * @warning Events are not implemented yet!
     */
    public void handleEvent() 
    {
        //log.info("Handling event in StateStack");
        Iterator<State> it = stack.descendingIterator();
        while (it.hasNext()) {
            if (!it.next().handleEvent())
                break;
        }
        applyPendingChanges();
    }

    public void pushState(StateID stateId)
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

    /**
     * Peek at the name of the top State (StateID) of the StateStack. Uses 
     * history to be low-cost.
     * Useful for getting the name of the currently running State.
     *
     * @return String name
     * The name of the State (StateID) at the top of the StateStack.
     */
    public String peekName()
    {
        return history.peekLast().toString();
    }

    /**
     * Returns the size of the StateStack. Uses history to be low-cost.
     * Useful for determining how deep the StateStack is, or special behavior
     * for the bottom State (like more complex names).
     *
     * @return int size
     * The current size of the StateStack.
     */
    public int size()
    {
        return history.size();
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
                    stack.addLast(createState(change.stateId));
                    history.addLast(change.stateId);
                    log.info("StateStack: Pushed " + change.stateId);
                    break;
                case POP:
                    stack.removeLast();
                    history.removeLast();
                    log.info("StateStack: Popped state");
                    break;
                case CLEAR:
                    stack.clear();
                    history.clear();
                    log.info("StateStack: Cleared stack");
                    break;
            }
        }
        pendingList.clear();
    }

    private Deque<State> stack = new ArrayDeque<>();
    private Deque<StateID> history = new ArrayDeque<>();
    private Deque<PendingChange> pendingList = new ArrayDeque<>();
    private Map<StateID, Supplier<State>> factories = new HashMap<>();
    private Context ctx;
}
