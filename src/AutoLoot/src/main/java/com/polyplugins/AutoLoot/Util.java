/**
 * @file Util.java
 * @class Util
 * Utility tools (mainly tick utilities and tick reference interface).
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-15
 *
 */

package com.polyplugins.AutoLoot;

import com.polyplugins.AutoLoot.AutoLootConfig;
import com.polyplugins.AutoLoot.IntPtr;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.query.NPCQuery;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MovementPackets;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class Util {
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;

    /**
     * Has the player waited the configuration wait duration?
     * @note Not dealing with dependency injection, passing a context.
     */
    public boolean hasWaited(IntPtr ticks, AutoLootConfig cfg) 
    {
        if (ticks.get() > cfg.waitFor()) {
            ticks.set(0);
            reset();
        }
        return !_flag;
        
    }

    public boolean isWaiting(IntPtr ticks) 
    {
        if (_flag) {

            // ticks++
            int tmp = ticks.get();
            tmp++;
            ticks.set(tmp);
            return true;
        }
        return false;
    }   

    public void shouldWait() 
    {
        if (!_deny)
            _flag = true;
    }

    /**
     * Reset the state of Util.
     */
    public void reset() 
    {
        _flag = false;
        _deny = false;
    }

    /**
     * Block all waiting procedures.
     */
    public Boolean denyWait(Boolean flag)
    { 
        if (flag != null) {
            _deny = flag;
            return _deny;
        }
        return null;
    }
            
    private boolean _flag = false;
    private boolean _deny = false;
}