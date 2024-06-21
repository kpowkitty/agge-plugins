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

import com.aggeplugins.Skiller.State;
import com.aggeplugins.Skiller.StateID;
import com.aggeplugins.Skiller.Context;
import com.aggeplugins.Skiller.StateStack;
import com.aggeplugins.Skiller.Pathing;
import com.aggeplugins.Skiller.BankLocation;
import com.aggeplugins.Skiller.SkillingState;
import com.aggeplugins.Skiller.PathingState;
import com.aggeplugins.Skiller.BankingState;
import com.aggeplugins.Skiller.DroppingState;

import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.Collections.query.TileObjectQuery;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.BankInventoryInteraction;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.NPCInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.piggyplugins.PiggyUtils.BreakHandler.ReflectBreakHandler;

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
    @Inject
    public static Client client;
    @Inject
    PowerSkillerConfig config;
    @Inject
    private KeyManager keyManager;
    @Inject
    private ReflectBreakHandler breakHandler;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private PowerSkillerOverlay overlay;

    public boolean started;

    private int timeout;

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
        stack.push(SKILLING);
    }

    private void initInstance()
    {
        try {
            Context ctx = new Context(this, config);
        } catch (NullPointerException e) {
            log.info("Unable to initialize plugin context!");
        }
        try {
            StateStack stack = new StateStack(ctx);
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

    @Override
    protected void shutDown() throws Exception {
        breakHandler.unregisterPlugin(this);
        keyManager.unregisterKeyListener(toggle);
        this.overlayManager.remove(overlay);

        finalizer();
    }

    private finalizer()
    {
        // null the references, clean state
        client = null;
        ctx = null;
        stack = null;
    }

    @Provides
    private SkillerConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(SkillerConfig.class);
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (!EthanApiPlugin.loggedIn() || 
            !started || 
            breakHandler.isBreakActive(this)) {

            // We do an early return if the user isn't logged in
            return;
        }

        stack.run();

        // (If polling) Poll after to avoid any lag for running the states.
        if (config.pollWp()) {
            log.info("Current WorldPoint: " + 
                client.getLocalPlayer().getWorldLocation());
        }
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
