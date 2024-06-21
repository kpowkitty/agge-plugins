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

import com.aggeplugins.Skiller.State;
import com.aggeplugins.Skiller.StateID;
import com.aggeplugins.Skiller.Context;
import com.aggeplugins.Skiller.StateStack;
import com.aggeplugins.Skiller.Util;

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

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class SkillingState extends State {
    public SkillingState(StateStack stack, Context ctx)
    {
        super(stack, ctx);
        init();
    }

    private void init()
    {
        ctx.plugin.currState = "SKILLING";
    }

    @Override
    public boolean run()
    {
        ctx.plugin.currState = "ANIMATING";

        // self-explanatory, we just return a State if the conditions are met.
        if (EthanApiPlugin.isMoving() || 
            ctx.client.getLocalPlayer().getAnimation() != -1) {

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

        return false; // keep previous state from running
    }

    @Override
    public boolean handleEvent()
    {
        return false; // keep previous state from handling events
    }

    private boolean findObject() {
        AtomicBoolean found = new AtomicBoolean(false);
        String objectName = ctx.config.objectToInteract();
        if (ctx.config.useForestryTreeNotClosest() && ctx.config.expectedAction().equalsIgnoreCase("chop")) {
            TileObjects.search().withName(objectName).nearestToPoint(getObjectWMostPlayers()).ifPresent(tileObject -> {
                ObjectComposition comp = TileObjectQuery.getObjectComposition(tileObject);
                TileObjectInteraction.interact(tileObject, comp.getActions()[0]);
                found.set(true);
            });
        } else {
            TileObjects.search().withName(objectName).nearestToPlayer().ifPresent(tileObject -> {
                ObjectComposition comp = TileObjectQuery.getObjectComposition(tileObject);
                TileObjectInteraction.interact(tileObject, comp.getActions()[0]); // find the object we're looking for.  this specific example will only work if the first Action the object has is the one that interacts with it.
                // don't *always* do this, you can manually type the possible actions. eg. "Mine", "Chop", "Cook", "Climb".
                found.set(true);
            });
        }
        return found.get();
    }

    private boolean findNpc() 
    {
        AtomicBoolean found = new AtomicBoolean(false);
        String npcName = ctx.config.objectToInteract();
        NPCs.search().withName(npcName).nearestToPlayer().ifPresent(npc -> {
            NPCComposition comp = ctx.client.getNpcDefinition(npc.getId());
            if (Arrays.stream(comp.getActions())
                    .filter(Objects::nonNull)
                    .anyMatch(action -> action.equalsIgnoreCase(ctx.config.expectedAction()))) {
                NPCInteraction.interact(npc, ctx.config.expectedAction()); // For fishing spots ?
                found.set(true);
            } else {
                NPCInteraction.interact(npc, comp.getActions()[0]);
                found.set(true);
            }
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
}
