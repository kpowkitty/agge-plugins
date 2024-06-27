/**
 * @file SkillingState.java
 * @class SkillingState
 * Skilling state, default state.
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
import com.example.EthanApiPlugin.Collections.query.TileObjectQuery;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.NPCInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.piggyplugins.PiggyUtils.BreakHandler.ReflectBreakHandler;

import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.coords.WorldArea;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class SkillingState extends State {
    public SkillingState(StateStack stack, SkillerContext ctx)
    {
        super(stack, ctx);
        this.ctx = ctx;
        init();
    }

    /**
     * Public override to set the max distance to look for skilling objects 
     * from outside the state.
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
        // Make sure the player isn't moving or in an animation before skilling,
        // otherwise animating.
        if (EthanApiPlugin.isMoving() || 
            ctx.client.getLocalPlayer().getAnimation() != -1) {
            ctx.plugin.currState = "ANIMATING";
        } else {
            ctx.plugin.currState = "SKILLING";

            if (Util.hasTools(ctx)) {
                // xxx timeout
                
                // Core action loop:
                if (ctx.config.searchNpc()) {
                    if (!findNpc())
                        requestPushState(StateID.PATHING);
                } else {
                    if (!findObject())
                        requestPushState(StateID.PATHING);
                }

                // xxx setTimeout();

                if (Inventory.full()) {
                    if (ctx.config.shouldBank())
                        requestPushState(StateID.BANKING);
                    else
                        requestPushState(StateID.DROPPING);
                }
            }
        }
        
        //log.info("In skilling state");

        return false; // keep previous state from running
    }

    @Override
    public boolean handleEvent()
    {
        return false; // keep previous state from handling events
    }

    private boolean findObject() {
        AtomicBoolean found = new AtomicBoolean(false);

        if (ctx.config.useForestryTreeNotClosest() && 
            ctx.config.expectedAction().equalsIgnoreCase("chop")) {
            
            TileObjects.search()
                       .nameContains(
                            ctx.config.objectToInteract())
                       .withinDistance(MAX_DISTANCE)
                       .nearestToPoint(getObjectWMostPlayers())
                       .ifPresent(to -> {
                found.set(true);
                ctx.plugin.currState = "Found forestry tree";
                log.info("Forestry tree found");
                ObjectComposition comp = 
                    TileObjectQuery.getObjectComposition(to);
                TileObjectInteraction.interact(to, comp.getActions()[0]);
            });
        } else {
            // Should consistently grab the nearest TileObject to where the 
            // player's currently standing.
            TileObjects.search()
                       .nameContains(
                            ctx.config.objectToInteract())
                       .withAction(
                            ctx.config.expectedAction())
                       .withinDistance(MAX_DISTANCE)
                       .nearestToPoint(
                            ctx.client.getLocalPlayer()
                                      .getWorldLocation())
                       .ifPresent(to -> {
                found.set(true);
                ctx.plugin.currState = "Found skilling object";
                log.info("Skilling object found");
                TileObjectInteraction.interact(to, ctx.config.expectedAction());
            });
        }
        return found.get();
    }

    /**
     * @note Useful for fishing spots.
     */
    private boolean findNpc() 
    {
        AtomicBoolean found = new AtomicBoolean(false);
        // Should consistently grab the nearest NPC to where the player's 
        // currently standing.
        NPCs.search()
            .nameContains(
                ctx.config.objectToInteract())
            .withAction(
                ctx.config.expectedAction())
            .withinWorldArea(new WorldArea(ctx.client.getLocalPlayer()
                                                     .getWorldLocation(),
                                           MAX_DISTANCE, MAX_DISTANCE))
            .nearestToPoint(
                ctx.client.getLocalPlayer()
                          .getWorldLocation())
            .ifPresent(npc -> {
                found.set(true);
                ctx.plugin.currState = "Found skilling NPC";
                //log.info("Found skilling NPC at ");
                NPCInteraction.interact(npc, ctx.config.expectedAction()); 
        });
        return found.get();
    }

    /**
     * Tile w most players on it within 2 tiles of the object we're looking for
     *
     * @return That tile or the player's tile if failed(such as doing forestry option when alone by trees)
     */
    public WorldPoint getObjectWMostPlayers() 
    {
        String objectName = ctx.config.objectToInteract();
        Map<WorldPoint, Integer> playerCounts = new HashMap<>();
        WorldPoint mostPlayersTile = null;
        int highestCount = 0;
        List<TileObject> objects = TileObjects.search().withName(objectName).result();

        List<Player> players = Players.search().notLocalPlayer().result();

        for (TileObject object : objects) {
            for (Player player : players) {
                if (player.getWorldLocation().distanceTo(object.getWorldLocation()) <= 2) {
                    WorldPoint playerTile = player.getWorldLocation();
                    playerCounts.put(playerTile, playerCounts.getOrDefault(playerTile, 0) + 1);
                    if (playerCounts.get(playerTile) > highestCount) {
                        highestCount = playerCounts.get(playerTile);
                        mostPlayersTile = playerTile;
                    }
                }
            }
        }

        return mostPlayersTile == null ? ctx.client.getLocalPlayer().getWorldLocation() : mostPlayersTile;
    }

    /**
     * Magic number constant that ensures the player doesn't try to skill from
     * too far away, and properly enters the pathing state.
     */
    private int MAX_DISTANCE = 10;

    private SkillerContext ctx;
}
