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
import com.aggeplugins.Skiller.SkillerPlugin;
import com.aggeplugins.Fighter.FighterPlugin;

import com.piggyplugins.PiggyUtils.API.*;
import com.piggyplugins.PiggyUtils.*;
import com.example.Packets.*;
import com.example.EthanApiPlugin.Collections.query.*;
import com.example.EthanApiPlugin.*;
import com.example.EthanApiPlugin.Collections.*;
import com.example.InteractionApi.*;
import com.example.PacketUtils.*;
import static com.example.EthanApiPlugin.EthanApiPlugin.stopPlugin;

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
import net.runelite.api.Skill;
import net.runelite.client.plugins.PluginManager;
import net.runelite.api.events.GameStateChanged;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public ItemManager itemManager;
    @Inject
    public PluginManager pluginManager;

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

    // Public instance variables.
    public MessageBus messageBus;
    public Message<MessageID, ?> msg;
    public static WorldPoint GOAL;
    public Player player;
    public boolean started;
    public int timeout;
    public int idleTicks;
    public Logger logger;

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
        
        this.init();
    }
 
    @Override  
    protected void shutDown() throws Exception
    {
        keyManager.unregisterKeyListener(start);
        keyManager.unregisterKeyListener(skip);
        overlayManager.remove(overlay);
        overlayManager.remove(tileOverlay);

        this.finalize();
    }

    // Entry, game logic:
    @Subscribe
    private void onGameTick(GameTick event) {
        if (!isStarted())
            return;

        //if (playerUtil.isInteracting() || player.getAnimation() == -1)
        //    idleTicks++;
        //else
        //    idleTicks = 0;
        
        Action.checkRunEnergy(client);

        instructions.executeInstructions();
            
        // logging
        //log.info("Idle ticks: " + action.getTicks());
        //log.info("Curr idx: " + instructions.getIdx());
        //log.info("Size: " + instructions.getSize());
        //log.info("Curr WorldPoint: " + player.getWorldLocation()); 
    }

    @Subscribe
    public void onChatMessage(ChatMessage e) 
    {
        if (!started)
            return;
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) 
    {
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

    /**
     * Get the current instruction's name.
     * @return String, the current instruction's name or no instructions
     * @note Don't allow direct access to _instructions, return a new 
     * String object.
     */
    public String getInstructionName()
    {
        //try {
            if (instructions.getSize() == 0)
                return "No instructions!";
            return instructions.getName();
        //} catch (NullPointerException e) {
        //    return "Instructions not intialized!";
        //}
    }

    public int getInstructionsSize()
    {
        return instructions.getSize();
    }

    private void init()
    {
        // Go through initialization states. Catch errors, if there is -- a lot 
        // of moving pieces.
        initClient();
        initInstance();
        initRegistry();
        initLogging();

        // Stop all plugins that AccountBuilder might use, it will control them
        // if it's on.
        // xxx stop all plugins, and only start desired
        //pluginManager.stopPlugin(SkillerPlugin);
        //pluginManager.stopPlugin(FighterPlugin);

        // Send a Message that other plugins should be listening for 
        // INSTRUCTIONS
        messageBus.send(new Message<MessageID, Boolean>(
            MessageID.INSTRUCTIONS, true));
    }

    private void initLogging()
    {
        logger = new Logger();
    }

    private void initClient()
    {   
        //try {
            this.client = RuneLite.getInjector().getInstance(Client.class);
            this.player = client.getLocalPlayer();
        //} catch (NullPointerException e) {
        //    log.info("Error: Unable to get client instance variables");
        //}
    }

    private void initInstance()
    {
        // Get an instance of the MessageBus.
        this.messageBus = messageBus.instance();

        // Instantiate object(s) that need clean state.
        this.instructions = new Instructions();

        if (instructions == null) {
            log.error("Instructions is not initialized properly");
            throw new IllegalStateException(
                "Instructions is not initialized properly");
        }

        // Instantiate Context.
        //try {
            this.ctx = new AutoQuesterContext(this, config, client, 
                                              clientThread, instructions);
        //} catch (NullPointerException e) {
        //    log.info("Error: Could not create plugin Context, objects not initialized correctly");
        //}
    
        // Lastly, instantiate public instance variables.
        GOAL = null;
        started = true;
        timeout = 0;
        idleTicks = 0;
        msg = null;
    }

    private void initRegistry()
    {
        // Context guaranteed, SAFE to proceed to Instructions Registry.
        this.registry = new Registry(ctx);
            
        //try {
            // Test instructions.
            if (config.testInstructions()) {
                registry.testInstructions();
                log.info("Registered instructions: Test instructions");
            }

            // Control other plugins.
            if (config.bronzeInstructions())
                registry.bronzeInstructions();
            if (config.mithrilInstructions())
                registry.mithrilInstructions();
            if (config.adamantInstructions())
                registry.adamantInstructions();
            if (config.runeInstructions())
                registry.runeInstructions();

            // Do quests.
            if (config.xMarksTheSpot()) {
                registry.xMarksTheSpot();
                log.info("Registered instructions: X Marks the Spot");
            }
            if (config.sheepShearer()) {
                registry.sheepShearer();
                log.info("Registered instructions: Sheep Shearer");
            }
            if (config.cooksAssistant()) {
                registry.cooksAssistant();
                log.info("Registered instructions: Cook's Assistant");
            }
            if (config.runeMysteries()) {
                registry.runeMysteries();
                log.info("Registered instructions: Rune Mysteries");
            }
            if (config.romeoAndJuliet()) {
                registry.romeoAndJuliet();
                log.info("Registered instructions: Romeo and Juliet");
            }
            if (config.theRestlessGhost()) {
                registry.theRestlessGhost();
                log.info("Registered instructions: The Restless Ghost");
            }
        //} catch (NullPointerException e) {
        //    log.info("Error: Unable to register instructions");
        //}
    }

    /*
     * Unguarded override to just register all instructions bare. 
     * @warning UNGUARDED!
     */
    private void registerBare()
    {
        //try {
          registry.xMarksTheSpot();
          registry.sheepShearer();
          registry.cooksAssistant();
        //} catch (NullPointerException e) {
        //  log.debug("Bare Registry failed!");
        //}
    }

    @Subscribe
    protected void onStatChanged(StatChanged e)
    {
        if (!started) 
            return;
        logger.addExp(e.getSkill(), e.getXp());
    }

    @Override
    public void finalize()
    {
        this.started = false;

        // Release client instance variables.
        this.client = null;
        this.player = null;

        /* Release resources for everything and hope garbage collector claims 
         * them: */
        // Instructions should be cleared. @see class Instructions
        this.instructions.clear();
        this.instructions = null;

        // SAFE to release Context.
        this.ctx = null;

        // SAFE to finalize and release Registry.
        this.registry.finalize();
        this.registry = null;

        // Remove the INSTRUCTIONS Message from the MessageBus; other plugins 
        // are no longer listening for instructions. null the reference.
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

    @Subscribe
    private void onGameStateChanged(GameStateChanged event)
    {
        GameState gameState = event.getGameState();

        switch (gameState) {
            // hard stop gamestates
            case CONNECTION_LOST:
            case LOGGING_IN: // xxx maybe
            case LOGIN_SCREEN:
            case LOGIN_SCREEN_AUTHENTICATOR:
            case STARTING:
            case UNKNOWN: // xxx watch this
                stopPlugin(this);
                break;
            // pause game states
            case HOPPING:
                // xxx pause?
                break;
            case LOGGED_IN:
            case LOADING:
                // normal run
                break;
            default: // normal run
                log.info("GameState case not handled, run by default: " + gameState);
                break;
        }
    }

    // Create null reference pointers for needed utilities.
    private Instructions instructions;
    private AutoQuesterContext ctx;
    private Registry registry;
    //private Util util;
}
