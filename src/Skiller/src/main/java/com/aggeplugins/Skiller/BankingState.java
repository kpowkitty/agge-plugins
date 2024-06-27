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
import net.runelite.api.coords.WorldArea;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class BankingState extends State {
    public BankingState(StateStack stack, SkillerContext ctx) 
    {
        super(stack, ctx);
        this.ctx = ctx;
        init();
    }

    /**
     * Public override to set the max distance to look for banking objects from 
     * outside the state.
     */
    public void setMaxDistance(int max)
    {
        MAX_DISTANCE = max;
    }

    private void init()
    {
    }

    @Override
    public boolean run() 
    {
        log.info("In banking state");

        // xxx deal with bank pin
       
        if (canDeposit()) {
            // Bank Widget up, can deposit.
            log.info("Can deposit items");
            List<Widget> items = BankInventory.search().result();
                for (Widget item : items) {
                    if (!Util.isTool(item.getName().toLowerCase(), ctx) && 
                        !Util.shouldKeep(item.getName().toLowerCase(), ctx)) {
                            BankInventoryInteraction.useItem(
                                item, "Deposit-All");
                    }
                }
            requestPopState();
        } else if (!canBank()) {
            log.info("Cannot bank, pushing Pathing state");
            requestPushState(StateID.PATHING);
        } else if (!Inventory.full()) {
            log.info("Inventory not full, popping state");
            requestPopState();
        }
        else {
            log.info("No action taken");
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
                   .withinDistance(MAX_DISTANCE)
                   .nearestToPlayer()
                   .ifPresent(to -> {
                found.set(true);
                log.info("Bank found");
                TileObjectInteraction.interact(to, "Bank");
        });
        TileObjects.search()
                   .withName("Bank chest")
                   .withinDistance(MAX_DISTANCE)
                   .nearestToPlayer()
                   .ifPresent(to -> {
                found.set(true);
                log.info("Bank chest found");
                TileObjectInteraction.interact(to, "Use");
        });
        NPCs.search()
            .withAction("Bank")
            .withinWorldArea(new WorldArea(ctx.client.getLocalPlayer()
                                                     .getWorldLocation(),
                                           MAX_DISTANCE, MAX_DISTANCE))
            .nearestToPlayer()
            .ifPresent(npc -> {
                log.info("Bank NPC found");
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

    /**
     * Magic number constant that ensures the player doesn't try to skill from
     * too far away, and properly enters the pathing state.
     */
    private int MAX_DISTANCE = 5;

    private SkillerContext ctx;
}
