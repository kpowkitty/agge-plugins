/**
 * @file FighterPlugin.java
 * @class FighterPlugin
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-18
 *
 * Derived in large part from Fighter.
 * Original source credit goes to EthanApi and PiggyPlugins. The diffs represent
 * my contributions as part of Agge Plugins.
 */

package com.aggeplugins.Fighter;

import com.aggeplugins.Fighter.*;
import com.aggeplugins.lib.*;
import com.aggeplugins.lib.export.TaskManager.*;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.*;
import static com.example.EthanApiPlugin.EthanApiPlugin.stopPlugin;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.piggyplugins.PiggyUtils.API.PlayerUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemSpawned;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;
import static net.runelite.api.TileItem.OWNERSHIP_SELF;
import static net.runelite.api.TileItem.OWNERSHIP_GROUP;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.GameStateChanged;

import org.apache.commons.lang3.tuple.Pair;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@PluginDescriptor(
        name = "<html><font color=\"#73D216\">[A3]</font> Fighter</html>",
        description = "",
        enabledByDefault = false,
        tags = {"agge", "plugin"}
)
@Slf4j
public class FighterPlugin extends Plugin {
    @Inject
    @Getter
    private Client client;
    @Inject
    private FighterConfig config;
    @Inject
    private FighterOverlay overlay;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    @Getter
    private ItemManager itemManager;
    @Inject
    @Getter
    private ClientThread clientThread;

    public boolean started = false;
    public int timeout = 0;
    public TaskManager taskManager = new TaskManager();
    public boolean inCombat;
    public int idleTicks = 0;
    public LocalPoint npcTile;
    public LocalPoint lootTile;

    /**
     * Flag to prevent finding another NPC if we're already locked on to one.
     */
    public boolean foundNpc;

    /**
     * Flag to block other tasks if we're currently burying bones.
     */
    public boolean buryingBones;

    private int tickDelay;

    @Inject
    PlayerUtil playerUtil;

    @Getter
    private Set<String> lootItems = new HashSet<>();
    @Getter
    private Queue<Pair<TileItem, Tile>> lootQueue = new ConcurrentLinkedQueue<>();

    @Provides
    private FighterConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(FighterConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        // Start when turned on, don't gate behind toggle.
        started = true;

        overlayManager.add(overlay);
        timeout = 0;
        inCombat = false;
        keyManager.registerKeyListener(toggle);

        this.init();
    }

    private void init()
    {
        tickDelay = config.tickDelay();
        addTasks();
    }

    private void addTasks()
    {
        taskManager.addTask(new CheckCombatStatus(this, config));
        taskManager.addTask(new CheckStats(this, config));
        taskManager.addTask(new UsePotion(this, config));
        taskManager.addTask(new LootItems(this, config));
        taskManager.addTask(new BuryBones(this, config));
        taskManager.addTask(new AttackNPC(this, config));
    }

    @Override
    protected void shutDown() throws Exception {
        inCombat = false;
        timeout = 0;
        idleTicks = 0;
        started = false;

        this.finalizer();

        keyManager.unregisterKeyListener(toggle);
        overlayManager.remove(overlay);
    }

    private void finalizer()
    {
        lootQueue.clear();
        taskManager.clearTasks();
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN || !started) {
            return;
        }

        if (playerUtil.isInteracting() || client.getLocalPlayer().getAnimation() == -1) {
            idleTicks++;
        } else {
            idleTicks = 0;
        }

        if (timeout > 0) {
            timeout--;
            log.info("Timeout: {}", timeout);
            return;
        }

        log.info("Game tick observed. Queue size before any operation: {}", lootQueue.size());

        Action.checkRunEnergy(client);

        // Wrap the entire main run loop in a tick delay.
        if (tickDelay-- <= 0) {
            tickDelay = config.tickDelay();
            return;
        }

        //log.info("TaskManager has tasks!");
        for (AbstractTask t : taskManager.getTasks()) {
            if (t.validate()) {
                //log.info("TaskManager validated task!");
                t.execute();
                continue;
            }
        }
        log.info("Game tick processing completed. Queue size after operations: {}", lootQueue.size());
    }

    @Subscribe
    private void onItemSpawned(ItemSpawned e) {
        TileItem item = e.getItem();

        // Don't do any of these procedures if we don't own the item.
        if (item != null && item.getOwnership() == OWNERSHIP_SELF) {
            // This is how you get the Tile from the event.
            Tile tile = e.getTile();

            ItemComposition composition = itemManager.getItemComposition(item.getId());
            if (isLootable(composition.getName())) {
                lootQueue.add(Pair.of(item, tile)); // Store both the TileItem and the Tile
                log.info("Loot added: {} at {}", composition.getName(), tile.getWorldLocation());
            }
        }
    }

    ///**
    // * Get the plugin's loot queue for Tasks.
    // * @return Queue<Pair<TileItem, Tile>> lootQueue
    // * The plugin's loot queue.
    // * @deprecated Lombok does this, but I'm not a fan -- may revisit.
    // */
    //public Queue<Pair<TileItem, Tile>> getLootQueue()
    //{
    //    return lootQueue;
    //}

    private boolean isLootable(String itemName) {
        return config.loot().contains(itemName);
    }

    //private void checkRunEnergy() {
    //    if (runIsOff() && client.getEnergy() >= 30 * 100) {
    //        MousePackets.queueClickPacket();
    //        WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1);
    //    }
    //}

    //private boolean runIsOff() {
    //    return EthanApiPlugin.getClient().getVarpValue(173) == 0;
    //}

    private final HotkeyListener toggle = new HotkeyListener(() -> config.toggle()) {
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
        if (started) {
            this.init();
        } else {
            this.finalizer();
        }
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged event)
    {
        GameState gameState = event.getGameState();

        switch (gameState) {
            case CONNECTION_LOST:
            case LOGIN_SCREEN:
            case LOGIN_SCREEN_AUTHENTICATOR:
                stopPlugin(this);
                break;
            default: //
        }
    }
}
