/**
 * @file SkillerPlugin.java
 * @class SkillerPlugin
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-18
 *
 * Derived in large part from PowerSkiller.
 * Original source credit goes to EthanApi and PiggyPlugins. The diffs represent 
 * my contributions as part of Agge Plugins.
 */

package com.aggeplugins.Skiller;

import com.aggeplugins.Skiller.*;
import com.aggeplugins.lib.*;
import com.aggeplugins.lib.StateStack.*;
import com.aggeplugins.MessageBus.*;

import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.Collections.query.*;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.BankInventoryInteraction;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.NPCInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.piggyplugins.PiggyUtils.BreakHandler.ReflectBreakHandler;
import static com.example.EthanApiPlugin.EthanApiPlugin.stopPlugin;
//import shortestpath.ShortestPathPlugin;
//import static shortestpath.ShortestPathPlugin.getPathfinder;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;
import net.runelite.client.util.Text;
import net.runelite.api.Client;
import net.runelite.client.RuneLite;
import net.runelite.client.callback.ClientThread;
import net.runelite.api.Skill;
import net.runelite.api.events.GameStateChanged;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import com.google.inject.Provides;
import com.google.inject.Inject;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@PluginDescriptor(
        name = "<html><font color=\"#73D216\">[A3]</font> Skiller</html>",
        description = "Will interact with an object and drop or bank all items when inventory is full",
        enabledByDefault = false,
        tags = {"agge", "plugin", "skilling"}
)
@Slf4j
public class SkillerPlugin extends Plugin {
    // RuneLite injections.
    @Inject
    private static Client client;
    @Getter
    @Inject
    public ClientThread clientThread;
    @Inject
    private KeyManager keyManager;
    @Inject
    private ReflectBreakHandler breakHandler;
    @Inject
    private OverlayManager overlayManager;

    // Lastly, inject RuneLite-expected plugin interfaces as a co-dependency.
    @Inject
    public SkillerConfig config;
    @Inject
    public SkillerOverlay overlay;

    // Context handles all domain-specific plugin context from here.
    private StateStack stack;
    private SkillerContext ctx;
    private int timeout;
    private MessageBus messageBus;
    private Message<MessageID, ?> msg;
    private Pair<Skill, Integer> pair;
    private boolean block;

    public boolean started;
    public String currState = "";

    @Override
    protected void startUp() throws Exception {
        // Start when turned on, don't gate behind toggle.
        started = true;

        breakHandler.registerPlugin(this);
        keyManager.registerKeyListener(toggle);
        this.overlayManager.add(overlay);

        init();
    }

    /**
     * Default initialization. Go through all the initialization procedures and
     * push the default State.
     * @remark StateStack (and States) will manage the correct State from there.
     */
    private void init()
    {
        initClient();
        initInstance();
        registerStates();

        stack.pushState(StateID.SKILLING);
        messageBus = messageBus.instance();
        messageBus.instance();

        // Random delay 0-7 between actions.
        timeout = RandomUtil.randTicks();    

        block = true; // start blocking by default
    }

    private void initInstance()
    {
        try {
            ctx = new SkillerContext(this, config, client, clientThread);
        } catch (NullPointerException e) {
            log.info("Unable to initialize plugin context!");
        }
        try {
            stack = new StateStack(ctx);
        } catch (NullPointerException e) {
            log.info("Unable to initialize state stack!");
        }
    }

    private void initClient()
    {   
        try {
            client = RuneLite.getInjector().getInstance(Client.class);
        } catch (NullPointerException e) {
            log.info("Error: Unable to get client instance variables");
        }
    }

    private void registerStates()
    {
        try {
            stack.registerState(StateID.SKILLING, () -> 
                new SkillingState(stack, ctx));
        } catch (NullPointerException e) {
            log.info("Error: Enable to register skilling state!");
        }
        try {
            stack.registerState(StateID.PATHING, () -> 
                new PathingState(stack, ctx));
        } catch (NullPointerException e) {
            log.info("Error: Enable to register pathing state!");
        }
        try {
            stack.registerState(StateID.BANKING, () -> 
            new BankingState(stack, ctx));

        } catch (NullPointerException e) {
            log.info("Error: Enable to register banking state!");
        }
        try {
            stack.registerState(StateID.DROPPING, () -> 
                new DroppingState(stack, ctx));
        } catch (NullPointerException e) {
            log.info("Error: Enable to register dropping state!");
        }
    }

    @Override
    protected void shutDown() throws Exception {
        breakHandler.unregisterPlugin(this);
        keyManager.unregisterKeyListener(toggle);
        this.overlayManager.remove(overlay);

        this.finalizer();
    }

    private void finalizer()
    {
        if (stack != null)
            stack.clearStates();

        // null the references, clean state
        client = null;
        ctx = null;
        stack = null;
        messageBus = null;

        timeout = 0;
    }

    @Provides
    private SkillerConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(SkillerConfig.class);
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (!EthanApiPlugin.loggedIn() || 
            !started) {
            //breakHandler.isBreakActive(this)) {

            // We do an early return if the user isn't logged in
            return;
        }
       
        // If there's instructions, let AccountBuilder control -- skip to entry
        // if there's not.
        if (messageBus.query(MessageID.INSTRUCTIONS)) {
            // If we've listened for a request, then handle the request.
            //log.info("Listening for a skilling request...");
            if (!messageBus.query(MessageID.REQUEST_SKILLING)) {
                // Break-out and block everything else until recieving a 
                // request.
                //log.info("No skilling request! Blocking...");
                currState = "Waiting for instructions";
                return;
            } else {
                //log.info("Handling instructions...");
                handleInstructions();
            }
        }

        /* Entry: */
        log.info("Skiller is not being blocked!");

        // Set the current overlay State name to the top of the StateStack.
        if (stack.size() > 1) // let skilling control its own name
            currState = stack.peekName();

        // For current WorldPoint polling (to find skilling location 
        // WorldPoint(s)).
        if (config.pollWp()) {
            log.info("Current WorldPoint: " + 
                client.getLocalPlayer().getWorldLocation());
        }

        // (For listed States) Block all actions behind a random delay.
        // xxx have this on state change too?
        StateID active = stack.peekId();
        if ((active == StateID.SKILLING ||
            active == StateID.PATHING) &&
            timeout-- > 0) {
            //log.info("Delaying actions: {}", timeout);
            currState = "Tick delay: " + timeout;
            return;
        }

        /* Proceed with core actions after random delay: */

        // Handle run energy.
        Action.checkRunEnergy(client);

        // Let states control everything else.
        stack.run();

        // Reset random delay to block actions again.
        timeout = RandomUtil.randTicks();
    }

    private void setTimeout() {
        timeout = RandomUtils.nextInt(config.tickdelayMin(), config.tickDelayMax());
    }

    private final HotkeyListener toggle = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            toggle();
        }
    };

    private boolean listen()
    {
        if (!messageBus.query(MessageID.REQUEST_SKILLING)) {
            //log.info("No skilling request!");
            currState = "Waiting for instructions";
            return false;
        }

        return true;
    }

    private void handleInstructions()
    {
        if (msg == null) {
            msg = (Message<MessageID, Pair<Skill, Integer>>)
                messageBus.get(MessageID.REQUEST_SKILLING);
            pair = (Pair<Skill, Integer>) msg.getData();
        // xxx also wait for inventory to be full, otherwise is abrubt!
        } else if (!Action.checkLevelUp(client, pair.getLeft(),
                                                pair.getRight()) &&
                   Inventory.full()) {
            log.info("Done skilling! Releasing control...");
            messageBus.send(new Message<MessageID, Boolean>(
                MessageID.DONE_SKILLING, true));

            // cleanup
            msg = null;
            pair = null;
        }
    }
    
    public void toggle() {
        if (!EthanApiPlugin.loggedIn()) {
            return;
        }
        started = !started;
        if (!started) {
            breakHandler.stopPlugin(this);
        } else {
            breakHandler.startPlugin(this);
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
}
