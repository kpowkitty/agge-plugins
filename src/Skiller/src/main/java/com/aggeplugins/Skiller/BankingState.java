/**
 * @file BankingState.java
 * @class BankingState
 * Banking state.
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
import com.aggeplugins.Skiller.Util;

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
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class BankingState extends State {
    public BankingState(StateStack stack, Context ctx) 
    {
        super(stack, ctx);
        init();
    }

    private void init()
    {
        ctx.plugin.currState = "BANKING";
    }

    @Override
    public boolean run() 
    {
        log.info("Entering BANK State...");

        // xxx deal with bank pin
       
        if (canDeposit()) {
            // Bank Widget up, can deposit.
            List<Widget> items = BankInventory.search().result();
                for (Widget item : items) {
                    if (!Util.isTool(item.getName().toLowerCase(), ctx) && 
                        !Util.shouldKeep(item.getName().toLowerCase(), ctx)) {
                            BankInventoryInteraction.useItem(
                                item, "Deposit-All");
                    }
                }
        } else if (!canBank()) {
            requestPushState(StateID.PATHING);
        } else if (!Inventory.full()) {
            requestPopState();
        }
        else {
            // do nothing, maybe timeout and state pop to correct
        }

        return false;
    }

    @Override
    public boolean handleEvent()
    {
        return false;
    }

    private boolean canBank()
    {
        AtomicBoolean found = new AtomicBoolean(false);
        TileObjects.search()
                   .withAction("Bank")
                   .nearestToPlayer()
                   .ifPresent(tileObject -> {
                found.set(true);
                TileObjectInteraction.interact(tileObject, "Bank");
        });
        TileObjects.search()
                   .withName("Bank chest")
                   .nearestToPlayer()
                   .ifPresent(tileObject -> {
                found.set(true);
                TileObjectInteraction.interact(tileObject, "Use");
        });
        NPCs.search()
            .withAction("Bank")
            .nearestToPlayer()
            .ifPresent(npc -> {
                found.set(true);
                NPCInteraction.interact(npc, "Bank");
        }); 
        return found.get();
    }

    //private boolean canNpcBank()
    //{
    //    AutomicBoolean found = new AtomicBoolean(false);
    //    NPCs.search()
    //        .withAction("Bank")
    //        .nearestToPlayer()
    //        .ifPresent(npc -> {
    //            found.set(true);
    //            NPCInteraction.interact(npc, "Bank");
    //    }); 
    //    return found.get();
    //}

    private boolean canDeposit()
    {
        // If the bank widget is not found, return false.
        return !Widgets.search().withId(786445).first().isEmpty();
    }

    private boolean pin()
    {
        if (Widgets.search().withId(13959169).first().isPresent()) {
            log.info("Unable to continue: Bank pin");
            return true;
        }
        return false;
    }
    
    private boolean dontBank()
    {
        return !ctx.config.setBank().equals("");
    }
    
    private boolean cantBank()
    {
        return TileObjects.search()
                          .withAction("Bank")
                          .nearestToPlayer()
                          .isEmpty() && 
               NPCs.search()
                   .withAction("Bank")
                   .nearestToPlayer()
                   .isEmpty(); 
    }
}
