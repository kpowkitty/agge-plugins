/**
 * @file TimeoutState.java
 * @class TimeoutState
 * Various timeout checks that attempt to fix a timeout. MESSY GOES HERE.
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-20
 *
 */

package com.aggeplugins.Skiller;

import com.aggeplugins.Skiller.*;
import com.aggeplugins.lib.*;
import com.aggeplugins.lib.StateStack.*;

import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.Collections.query.*;
import com.example.EthanApiPlugin.*;
import com.example.InteractionApi.*;
import com.piggyplugins.PiggyUtils.BreakHandler.ReflectBreakHandler;

import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.coords.WorldPoint;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

//@Slf4j
//public class TimeoutState extends State {
//    public TimeoutState(StateStack stack, Context ctx) {
//        super(stack, ctx);
//        this.prev = stack.getHistory().peekLast();
//        init();
//    }
//
//    @Override
//    public boolean run()
//    {
//        if (prev == SKILLING) {
//
//        }
//
//        else if (prev == BANKING) {
//
//        }
//
//        else if (prev == PATHING) {
//
//        }
//
//        else {
//            requestClearStack(); // just hard reset
//
//        return false;
//    }
//
//    @Override
//    public boolean handleEvent() 
//    {
//        return false;  
//    }
// 
//
//    /**
//     * Initialization procedure for PathingState. Validates and prepares to
//     * path, or handles an invalid state.
//     */
//    private void init()
//    {
//        this.setGoal();
//        pathing.setType(Pathing.Type.SHORTEST_PATH);
//        pathing.setGoal(goal);
//        pathing.setPath();
//        ctx.plugin.currState = "PATHING";
//    }
