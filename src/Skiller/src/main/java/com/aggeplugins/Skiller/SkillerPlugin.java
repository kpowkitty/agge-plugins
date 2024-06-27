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
import com.example.EthanApiPlugin.Collections.query.TileObjectQuery;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.BankInventoryInteraction;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.NPCInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.piggyplugins.PiggyUtils.BreakHandler.ReflectBreakHandler;
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

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import com.google.inject.Provides;
import com.google.inject.Inject;
import org.apache.commons.lang3.RandomUtils;

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

        if (messageBus.query(MessageID.INSTRUCTIONS)) {
            // Don't run and listen for instructions if being controlled by
            // AccountBuilder.
            return;
        }

        // For current WorldPoint polling (to find skilling location 
        // WorldPoint(s)).
        if (config.pollWp()) {
            log.info("Current WorldPoint: " + 
                client.getLocalPlayer().getWorldLocation());
        }

        // Handle run energy.
        Action.checkRunEnergy(client);

        // Let states control everything else.
        stack.run();
        // Set the current overlay State name to the top of the StateStack 
        // (after running the State to not interupt the run procedure).
        if (stack.size() > 1) // let skilling control its own name
            currState = stack.peekName();
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
}
