package com.aggeplugins.Fighter;

import com.aggeplugins.Fighter.*;
import com.aggeplugins.lib.*;
import com.aggeplugins.lib.export.TaskManager.*;

import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.TileItemPackets;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Optional;
import java.util.List;

@Slf4j
public class LootItems extends AbstractTask<FighterPlugin, FighterConfig> {

    public LootItems(FighterPlugin plugin, FighterConfig config) {
        super(plugin, config);
        this.clicked = false;
    }

    @Override
    public boolean validate() {
        //log.info("Entering LootItems task validation");
        return config.shouldLoot() && !plugin.inCombat &&
               !plugin.getLootQueue().isEmpty();
    }

    @Override
    public void execute() {
        // Only peek, there's a procedure for removing.
        Pair<TileItem, Tile> lootPair = plugin.getLootQueue().peek();
        if (lootPair != null) {
            TileItem loot = lootPair.getLeft();
            Tile lootTile = lootPair.getRight();
            plugin.lootTile = lootTile.getLocalLocation();

            // See if we've actually picked up the loot before removing. If not,
            // send another command to pick-up (timeout guards spam); if yes,
            // remove from the loot queue and re-enter with a new queue head. 
            // Done this way to make sure all loot is picked-up!
            List<TileItem> l = lootTile.getGroundItems();
            if (!l.contains(loot)) {
                log.info("Removing loot!");
                plugin.getLootQueue().remove();
                plugin.lootTile = null;
                clicked = false;
                return; // exit, we've picked up this loot
            }

            log.info("Processing loot: {} at {}", plugin.getItemManager().getItemComposition(loot.getId()).getName(), lootTile.getWorldLocation());
                if(!Inventory.full() && !clicked) {
                    log.info("Sending mouse clicks to pick-up loot!");
                    RandomUtil.randMousePackets();
                    clicked = true;
                    TileItemPackets.queueTileItemAction(new ETileItem(lootTile.getWorldLocation(), loot), false);
                    // Random 0-5 tick timeout, inclusive.
                    //plugin.timeout = RandomUtil.randTicks(0, 5);
                }
        }
    }

    private boolean clicked;
}
