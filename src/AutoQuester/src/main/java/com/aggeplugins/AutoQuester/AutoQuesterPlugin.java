/**
 * @file AutoQuesterPlugin.java
 * @class AutoQuesterPlugin
 * AutoQuest for 10 quest points! 
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-15
 *
 * Special thanks to EthanApi and PiggyPlugins for API, inspiration, and a 
 * source of code at times.
 */

package com.aggeplugins.AutoQuester;

import com.aggeplugins.AutoQuester.*;
import com.aggeplugins.lib.*;
import com.aggeplugins.MessageBus.*;

import com.piggyplugins.PiggyUtils.API.PlayerUtil;
import com.example.Packets.*;
import com.example.EthanApiPlugin.Collections.ETileItem;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileItems;
import com.example.EthanApiPlugin.Collections.query.TileItemQuery;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.InventoryInteraction;
import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.InteractionApi.NPCInteraction;
import com.example.InteractionApi.ShopInteraction;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.PacketUtils.WidgetInfoExtended;

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
import net.runelite.api.widgets.Widget;
import net.runelite.api.Client;
import net.runelite.client.RuneLite;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@PluginDescriptor(
        name = "<html><font color=\"#73D216\">[A3]</font> AutoQuester</html>",
        description = "AutoQuest for easy quest points!",
        enabledByDefault = false,
        tags = {"agge", "plugin"}
)

@Slf4j
public class AutoQuesterPlugin extends Plugin {
    @Inject
    public PlayerUtil playerUtil;
    @Inject
    public ConfigManager configManager;

    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private AutoQuesterConfig config;
    @Inject
    private AutoQuesterOverlay overlay;
    @Inject
    private AutoQuesterTileOverlay tileOverlay;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    public ItemManager itemManager;
    @Inject
    private Util util;

    public MessageBus messageBus;
    public Message<MessageID, ?> msg;

    @Provides
    private AutoQuesterConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoQuesterConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        keyManager.registerKeyListener(start);
        keyManager.registerKeyListener(skip);
        overlayManager.add(overlay);
        overlayManager.add(tileOverlay);
        
        init();
    }
 
    @Override  
    protected void shutDown() throws Exception
    {
        keyManager.unregisterKeyListener(start);
        keyManager.unregisterKeyListener(skip);
        overlayManager.remove(overlay);
        overlayManager.remove(tileOverlay);

        this.finalizer();
    }

    // Entry, game logic:
    @Subscribe
    private void onGameTick(GameTick event) {
        if (!isStarted())
            return;

        if (playerUtil.isInteracting() || player.getAnimation() == -1)
            idleTicks++;
        else
            idleTicks = 0;

        Action.checkRunEnergy(client);

        instructions.executeInstructions();
            
        // logging
        //log.info("Idle ticks: " + action.getTicks());
        log.info("Curr idx: " + instructions.getIdx());
        log.info("Size: " + instructions.getSize());
        log.info("Curr WorldPoint: " + player.getWorldLocation()); 
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
    public void onConfigChanged(ConfigChanged event) 
    {
        if (!event.getGroup().equals("AutoQuesterConfig"))
            return;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        GameState state = event.getGameState();
        if (state == GameState.HOPPING || state == GameState.LOGGED_IN)
            return;
        // xxx handle stop differently
        //EthanApiPlugin.stopPlugin(this);
    }

    /**
     * Get the current instruction's name.
     * @return String, the current instruction's name or no instructions
     * @note Don't allow direct access to _instructions, return a new 
     * String object.
     */
    public String getInstructionName()
    {
        if (instructions.getSize() == 0)
            return "No instructions!";
        return instructions.getName();
    }

    // Public instance variables.
    public static WorldPoint GOAL = null;
    public Player player = null;
    public boolean started = false;
    public int timeout = 0;
    public int idleTicks = 0;

    private void init()
    {
        // Go through initialization states. Catch errors, if there is -- a lot 
        // of moving pieces.
        initClient();
        initInstance();
        initConfig();
        try {
            this.ctx = new AutoQuesterContext(this, config, client, 
                                              clientThread, _cfg, instructions);
        } catch (NullPointerException e) {
            log.info("Error: Could not create plugin Context, objects not initialized correctly");
        }
        //pathing = new Pathing(_ctx);

        initRegistry();

        messageBus.send(new Message<MessageID, Boolean>(
            MessageID.INSTRUCTIONS, true));
    }

    private void initClient()
    {   
        try {
            this.client = RuneLite.getInjector().getInstance(Client.class);
            this.player = client.getLocalPlayer();
        } catch (NullPointerException e) {
            log.info("Error: Unable to get client instance variables");
        }
    }

    private void initInstance()
    {
        // Get an instance of the MessageBus.
        this.messageBus = messageBus.instance();

        // Instantiate objects that need clean state.
        this.instructions = new Instructions();

        //if (pathing == null) {
        //    log.error("Pathing is not initialized properly");
        //    throw new IllegalStateException(
        //        "Pathing is not initialized properly");
        //}
        if (this.instructions == null) {
            log.error("Instructions is not initialized properly");
            throw new IllegalStateException(
                "Instructions is not initialized properly");
        }
        //if (action == null) {
        //    log.error("Action is not initialized properly");
        //    throw new IllegalStateException(
        //        "Action is not initialized properly");
        //}
    }


    private void initConfig()
    {
        _cfg = new HashMap<>();
        try {
            if (config.testInstructions()) {
                _cfg.put("Test instructions", true);
            } else {
                _cfg.put("Test instructions", false);
            }
            if (config.xMarksTheSpot()) {
                _cfg.put("X Marks the Spot", true);
            } else {
                _cfg.put("X Marks the Spot", false);
            }
            if (config.startedXMarksTheSpot()) {
                _cfg.put("Started X Marks the Spot", true);
            } else {
                _cfg.put("Started X Marks the Spot", false);
            }
            if (config.sheepShearer()) {
                _cfg.put("Sheep Shearer", true);
            } else {
                _cfg.put("Sheep Shearer", false);
            }
            if (config.startedSheepShearer()) {
                _cfg.put("Started Sheep Shearer", true);
            } else {
                _cfg.put("Started Sheep Shearer", false);
            }
            if (config.cooksAssistant()) {
                _cfg.put("Cook's Assistant", true);
            } else {
                _cfg.put("Cook's Assistant", false);
            }
            if (config.startedCooksAssistant()) {
                _cfg.put("Started Cook's Assistant", true);
            } else {
                _cfg.put("Started Cook's Assistant", false);
            }
            if (config.runeMysteries()) {
                _cfg.put("Rune Mysteries", true);
            } else {
                _cfg.put("Rune Mysteries", false);
            }
            if (config.startedRuneMysteries()) {
                _cfg.put("Started Rune Mysteries", true);
            } else {
                _cfg.put("Started Rune Mysteries", false);
            }
            if (config.romeoAndJuliet()) {
                _cfg.put("Romeo and Juliet", true);
            } else {
                _cfg.put("Romeo and Juliet", false);
            }
            if (config.startedRomeoAndJuliet()) {
                _cfg.put("Started Romeo and Juliet", true);
            } else {
                _cfg.put("Started Romeo and Juliet", false);
            }
            if (config.theRestlessGhost()) {
                _cfg.put("The Restless Ghost", true);
            } else {
                _cfg.put("The Restless Ghost", false);
            }
            if (config.startedTheRestlessGhost()) {
                _cfg.put("Started The Restless Ghost", true);
            } else {
                _cfg.put("Started The Restless Ghost", false);
            }
        } catch (NullPointerException e) {
            log.info("Error: Unable to process configuration");
        }
    }

    private void initRegistry()
    {
        // Context guaranteed, SAFE to proceed to Instructions Registry.
        Registry registry = new Registry(ctx);
        try {
            if (_cfg.get("Test instructions")) {
                registry.testInstructions();
                log.info("Registered instructions: Test instructions");
            }
            if (_cfg.get("X Marks the Spot")) {
                registry.xMarksTheSpot();
                log.info("Registered instructions: X Marks the Spot");
            }
            if (_cfg.get("Sheep Shearer")) {
                registry.sheepShearer();
                log.info("Registered instructions: Sheep Shearer");
            }
            if (_cfg.get("Cook's Assistant")) {
                registry.cooksAssistant();
                log.info("Registered instructions: Cook's Assistant");
            }
            if (_cfg.get("Rune Mysteries")) {
                registry.runeMysteries();
                log.info("Registered instructions: Rune Mysteries");
            }
            if (_cfg.get("Romeo and Juliet")) {
                registry.romeoAndJuliet();
                log.info("Registered instructions: Romeo and Juliet");
            }
            if (_cfg.get("The Restless Ghost")) {
                registry.theRestlessGhost();
                log.info("Registered instructions: The Restless Ghost");
            }
        } catch (NullPointerException e) {
            log.info("Error: Unable to register instructions");
        }
    }

    /*
     * Unguarded override to just register all instructions bare. 
     * @warning UNGUARDED!
     */
    private void registerBare()
    {
        try {
          registry.xMarksTheSpot();
          registry.sheepShearer();
          registry.cooksAssistant();
        } catch (NullPointerException e) {
          log.debug("Bare Registry failed!");
        }
    }

    private void finalizer()
    {
        this.started = false;

        // Release client instance variables.
        this.client = null;
        this.player = null;

        // Release resources for everything and hope garbage collector claims 
        // them.
        // Instructions should be cleared. @see class Instructions
        this.instructions.clear();
        this.instructions = null;
        // SAFE to release Context and Registry.
        this.ctx = null;
        this.registry = null;

        // Remove the INSTRUCTIONS Message from the MessageBus, other plugins 
        // are no longer listening for instructions. null the reference
        messageBus.remove(MessageID.INSTRUCTIONS);
        messageBus = null;
        msg = null; // in case it wasn't
    }
 
    private boolean isStarted()
    {
        return client.getGameState() == GameState.LOGGED_IN || started;
    }

    // Key listeners:
    private final HotkeyListener start = new HotkeyListener(() -> 
        config.start()) {
            @Override
            public void hotkeyPressed() {
            start();
            }
    };

    private final HotkeyListener skip = new HotkeyListener(() -> 
        config.skip()) {
            @Override
            public void hotkeyPressed() {
            skip();  
            }
    };

    private void start() {
        if (client.getGameState() != GameState.LOGGED_IN)
            return;
        started = !started;
    }

    private void skip() 
    {
        if (instructions.getSize() == 0) {
            // do nothing    
        } else {
            instructions.skip();
        }
    }

    // Wrapper to return boolean for player.getAnimation()
    //private void isAnimating() {
    //    if (player.getAnimation() == -1)
    //        return true;
    //    return false;
    //}

    // Different random for all instances, more diverse seeds!
    //private Random _rand;

    // Create null reference pointers for needed utilities.
    private Instructions instructions;
    private AutoQuesterContext ctx;
    private Registry registry;

    // Local config. Guarantee that every pointer points to this memory address.
    private Map<String, Boolean> _cfg;

    // k, v for needed locations. xxx not needed!
    private WorldPoint shopkeeper = new WorldPoint(3212, 3246, 0);
    private WorldPoint veos = new WorldPoint(3228, 3242, 0);
}
