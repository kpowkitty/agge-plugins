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
import com.aggeplugins.Skiller.Pathing;

import net.runelite.api.coord.WorldPoint;

import java.util.*;

public class SkillingState implements State {
    private final StateStack stateStack;
    private final Context context;

    public SkillingState(StateStack stack, Context ctx)
    {
        this.stack = stack;
        this.ctx = ctx;
    }

    @Override
    public boolean run()
    {
        // self-explanatory, we just return a State if the conditions are met.
        if (EthanApiPlugin.isMoving() || 
            client.getLocalPlayer().getAnimation() != -1) {

            if (hasTools()) {
                // xxx timeout
                
                // Core action loop:
                if (config.searchNpc()) {
                    if (!findNpc())
                        requestStatePush(PATH);
                } else {
                    if (!findObject())
                        requestStatePush(PATH);
                }

                // xxx setTimeout();

                if (Inventory.full()) {
                    if (ctx.config.shouldBank())
                        requestStatePush(BANK);
                    else
                        requestStatePush(DROP);
                }
            }
        }
        return false; // keep previous state from running
    }

    @Override
    public boolean handleEvent() {
        // Implement event handling logic
    }

    private boolean findObject() {
        AtomicBoolean found = new AtomicBoolean(false);
        String objectName = config.objectToInteract();
        if (config.useForestryTreeNotClosest() && config.expectedAction().equalsIgnoreCase("chop")) {
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
        String npcName = config.objectToInteract();
        NPCs.search().withName(npcName).nearestToPlayer().ifPresent(npc -> {
            NPCComposition comp = client.getNpcDefinition(npc.getId());
            if (Arrays.stream(comp.getActions())
                    .filter(Objects::nonNull)
                    .anyMatch(action -> action.equalsIgnoreCase(config.expectedAction()))) {
                NPCInteraction.interact(npc, config.expectedAction()); // For fishing spots ?
                found.set(true);
            } else {
                NPCInteraction.interact(npc, comp.getActions()[0]);
                found.set(true);
            }
        });
        return found.get();
    }

    private boolean hasTools() 
    {
        //Updated from https://github.com/moneyprinterbrrr/ImpactPlugins/blob/experimental/src/main/java/com/impact/PowerGather/PowerGatherPlugin.java#L196
        //Big thanks hawkkkkkk
        String[] tools = config.toolsToUse().split(","); // split the tools listed by comma, no space.

        int numInventoryTools = Inventory.search()
                .filter(item -> isTool(item.getName())) // filter inventory by using out isTool method
                .result().size();
        int numEquippedTools = Equipment.search()
                .filter(item -> isTool(item.getName())) // filter inventory by using out isTool method
                .result().size();

        return numInventoryTools + numEquippedTools >= tools.length; // if the size of tools and the filtered inventory is the same, we have our tools.
    }

    private boolean isTool(String name) 
    {
        String[] tools = config.toolsToUse().split(","); // split the tools listed by comma, no space.

        return Arrays.stream(tools) // stream the array using Arrays.stream() from java.util
                .anyMatch(i -> name.toLowerCase().contains(i.toLowerCase())); // more likely for user error than the shouldKeep option, but we'll follow the same idea as shouldKeep.
    }

    /**
     * Tile w most players on it within 2 tiles of the object we're looking for
     *
     * @return That tile or the player's tile if failed(such as doing forestry option when alone by trees)
     */
    public WorldPoint getObjectWMostPlayers() 
    {
        String objectName = config.objectToInteract();
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

        return mostPlayersTile == null ? client.getLocalPlayer().getWorldLocation() : mostPlayersTile;
    }
}
