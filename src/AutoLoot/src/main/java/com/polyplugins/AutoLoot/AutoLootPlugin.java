/**
 * @file AutoLootPlugin.java
 * @class AutoLootPlugin
 * Modular looting automation. 
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-15
 *
 * Derived in large part from AutoCombat.
 * Majority of credit goes to PiggyPlugins. This is just a refactor with fixes.
 * Thanks PiggyPlugins!
 */

package com.polyplugins.AutoLoot;

import com.example.EthanApiPlugin.Collections.ETileItem;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileItems;
import com.example.EthanApiPlugin.Collections.query.TileItemQuery;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.InventoryInteraction;
import com.example.Packets.*;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.piggyplugins.PiggyUtils.API.PlayerUtil;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;
import static net.runelite.api.TileItem.OWNERSHIP_SELF;
import static net.runelite.api.TileItem.OWNERSHIP_GROUP;
import com.polyplugins.AutoLoot.AutoLootConfig;
import com.polyplugins.AutoLoot.AutoLootOverlay;
import com.polyplugins.AutoLoot.AutoLootTileOverlay;
import com.polyplugins.AutoLoot.Util;
import com.polyplugins.AutoLoot.IntPtr;

import java.util.*;

@PluginDescriptor(
        name = "<html><font color=\"#73D216\">[A3]</font> AutoLoot</html>",
        description = "Modular looting automation",
        enabledByDefault = false,
        tags = {"agge", "plugin"}
)
@Slf4j

public class AutoLootPlugin extends Plugin {
    @Inject
    public PlayerUtil playerUtil;
    @Inject
    public AutoLootHelper lootHelper; 

    @Inject
    private Client client;
    @Inject
    private AutoLootConfig config;
    @Inject
    private AutoLootOverlay overlay;
    @Inject
    private AutoLootTileOverlay tileOverlay;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    public ItemManager itemManager;
    @Inject
    private ClientThread clientThread;

    @Provides
    private AutoLootConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoLootConfig.class);
    }
  
    public Player player;
    public LocalPoint lootTile; 
    
    public IntPtr ticks;
    public Queue<ETileItem> lootQueue;
  
    public boolean started = false;
    public int timeout = 0;
    public int idleTicks = 0;

    private Util util;
    private boolean hasBones = false;
    private boolean looting = false;

    @Override
    protected void startUp() throws Exception {
        keyManager.registerKeyListener(toggle);
        overlayManager.add(overlay);
        overlayManager.add(tileOverlay);

        init();
    }

    private void init() {
        util = new Util();
        ticks = new IntPtr(0);
        lootQueue = new LinkedList<ETileItem>();
        player = client.getLocalPlayer();
    }

    @Override  
    protected void shutDown() throws Exception {
        keyManager.unregisterKeyListener(toggle);
        overlayManager.remove(overlay);
        overlayManager.remove(tileOverlay);

        resetEverything();
      
        // Clear the references.
        ticks = null;
        lootQueue = null;
        util = null;
    }

    private void resetEverything() {
        player = null;
        lootTile = null;

        // Don't lose the reference, this is not a re-init, just clear what we
        // have.
        ticks.set(0);
        lootQueue.clear();
        util.reset();

        started = false;
        timeout = 0;
        idleTicks = 0;
        hasBones = false;
        looting = false;
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN || 
            EthanApiPlugin.isMoving() || 
            !started) {
            return;
        }


        // Will only wait if loot queue flags to wait. 
        if (util.isWaiting(ticks)); // Do nothing for now, catch the return. 

        if (lootQueue.isEmpty()) {
            looting = false;
        }

        if (!lootQueue.isEmpty() && util.hasWaited(ticks, config)) {
            if (!Inventory.full()) {
                log.info("Entering looting");
                looting = true;
                ETileItem eti = lootQueue.peek();
                eti.interact(false);
                lootQueue.remove();
                lootTile = null;
                return; // Block bone-burying until looting is done.
            } else {
                lootQueue.clear();
                EthanApiPlugin.sendClientMessage(
                        "Inventory full, stopping. May handle in future update");
                EthanApiPlugin.stopPlugin(this);
            }
        }

        Inventory.search().onlyUnnoted().withAction("Bury").filter(
            b -> config.buryBones()).first().ifPresent(
                bone -> {
                    MousePackets.queueClickPacket();
                    WidgetPackets.queueWidgetAction(bone, "Bury");
                    timeout = 1;
        });

        Inventory.search().onlyUnnoted().withAction("Scatter").filter(
            b -> config.buryBones()).first().ifPresent(
                bone -> {
                    MousePackets.queueClickPacket();
                    WidgetPackets.queueWidgetAction(bone, "Scatter");
                    timeout = 1;
        });
    }

    @Subscribe  
	public void onItemSpawned(ItemSpawned itemSpawned) { 
        if (!started) 
            return;
        final TileItem item = itemSpawned.getItem();
        
        // Don't do any of these procedures if we don't own the item.
        /* @note Could add OWNERSHIP_GROUP, but want to avoid breakage. */
        if (item.getOwnership() == OWNERSHIP_SELF) {
		    final Tile tile = itemSpawned.getTile();
		    ETileItem eti = new ETileItem(tile.getWorldLocation(), item);
            for (String str : lootHelper.getLootNames()) {
                TileItems.search()
                         .withId(item.getId())
                         .withName(str)
                         .first().ifPresent(i -> { lootQueue.add(i); });
                log.info("Added "  + str + " to loot queue!");

                    // Don't wait to re-wait!
                    if (!util.isWaiting(ticks))
                        util.shouldWait();
                }
            }

            /* 
             * @note An alternative way with name-matching the loot string:
             * For name matching to string.
             * ItemComposition comp = itemManager.getItemComposition(item.getId());
             * String name = comp.getName();
             * for (String str : lootHelper.getLootNames()) {
             *    if (str.equals(name)) {
             *        lootTile = tile.getLocalLocation();
             *        lootQueue.add(eti);
             *        if (!util.isWaiting(ticks)) // Don't want to re-wait!
             *            util.shouldWait();
             *    }
             * }
             */
	}

    @Subscribe
    public void onStatChanged(StatChanged event) {
        if (!started) 
            return;
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (!started)
            return;
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if (!started) 
            return;
        int bid = event.getVarbitId();
        int pid = event.getVarpId();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals("AutoLootConfig"))
            return;
        if (event.getKey().equals("lootNames")) {
            lootHelper.setLootNames(null);
            lootHelper.getLootNames();
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        GameState state = event.getGameState();
        if (state == GameState.HOPPING || state == GameState.LOGGED_IN) return;
        EthanApiPlugin.stopPlugin(this);
    }

    /* @note Not handling run energy in AutoLoot. */
    //private void checkRunEnergy() {
    //    if (runIsOff() && playerUtil.runEnergy() >= 30) {
    //        MousePackets.queueClickPacket();
    //        WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1);
    //    }
    //}

    //private boolean runIsOff() {
    //    return EthanApiPlugin.getClient().getVarpValue(173) == 0;
    //}

    private final HotkeyListener toggle = new HotkeyListener(
        () -> config.toggle()) {
            @Override
            public void hotkeyPressed() {
            toggle();
            }
    };

    public void toggle() {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        started = !started;
    }
}
