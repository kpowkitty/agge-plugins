/**
 * @file Util.java
 * @class Util
 * Useful utility tools.
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-15
 */

package com.agge.AutoQuester;

import com.agge.AutoQuester.IntPtr;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.query.NPCQuery;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MovementPackets;

import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class Util {
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private AutoQuesterConfig config;

    //public boolean hasWaited(IntPtr ticks) {
    //    if (ticks.get() > config.waitFor()) {
    //        ticks.set(0);
    //        flag = false;
    //    }
    //    return !flag;
    //}

    //public boolean isWaiting(IntPtr ticks) {
    //    if (flag) {
    //        // ticks++
    //        int tmp = ticks.get();
    //        tmp++;
    //        ticks.set(tmp);
    //        return true;
    //    }
    //    return false;
    //}   

    public void shouldWait() {
        _flag = true;
    }

    private boolean _flag = false;
}
