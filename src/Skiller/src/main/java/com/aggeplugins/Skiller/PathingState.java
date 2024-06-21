/**
 * @file PathingState.java
 * @class PathingState
 * Pathing state.
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
import com.aggeplugins.Skiller.Pathing;
import com.aggeplugins.Skiller.BankLocation;

import net.runelite.api.coord.WorldPoint;

import java.util.*;

public class PathingState implements State {
    private final stack stack;
    private final Context ctx;

    public PathingState(StateStack stack, Context ctx) {
        this.stack = stack;
        this.ctx = ctx;
        this.prev = stack.getHistory().removeFirst();
        this.pathing = new Pathing();
        init();
    }

    @Override
    public boolean run()
    {
        pathing.run();
        if (!pathing.isPathing()) {
            finalizer();
            requestStatePop();
        }
        return false;
    }

    @Override
    public boolean handleEvent() 
    {
        // Implement event handling logic
    }

    /**
     * Initialization procedure for PathingState. Validates and prepares to
     * path, or handles an invalid state.
     */
    private void init()
    {
        if (prev == StateID.BANKING) {
            try {
                goal = BankLocation.fromString(ctx.config.setBank());
                log.info("Valid bank WorldPoint);
                pathing.pathTo(goal);
                log.info("Found a path! Pathing...");
        } else if (prev == StateID.SKILLING)
            try {
                // User can provide a skilling location, optional WorldPoint 
                // poll in logs.
                goal = new WorldPoint(ctx.config.skillingX(), 
                                      ctx.config.skillingY(),
                                      ctx.config.skillingZ());
                log.info("Valid skilling WorldPoint");
                pathing.pathTo(goal);
                log.info("Found a path! Pathing...");
            } catch (IllegalArgumentException e) {
                log.info(e.getMessage());
            }
        } else {
            log.info("Pathing has no goal! Reverting to previous state...");
            finalizer();
            requestStatePop();
        }
    }

    /**
    * Finalizer procedure for PathingState. Make sure to call!
    * @remark PathingState DOES have a finalizer procedure (force clean state
    * and null references).
    */
    private void finalizer()
    {
        pathing = null;
        goal = null;
    }

    private StateID prev;
    private Pathing pathing;
    private WorldPoint goal;
}
