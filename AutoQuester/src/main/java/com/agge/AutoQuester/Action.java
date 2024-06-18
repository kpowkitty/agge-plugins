/**
 * @file Action.java
 * @class Action
 * Wrapper for different boolean Action(s) to be performed. 
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-15
 *
 * Special thanks to EthanApi and PiggyPlugins for API, inspiration, and a 
 * source of code at times.
 */

package com.agge.AutoQuester;

import com.agge.AutoQuester.AutoQuesterConfig;
import com.agge.AutoQuester.AutoQuesterOverlay;
import com.agge.AutoQuester.AutoQuesterTileOverlay;
import com.agge.AutoQuester.Util;
import com.agge.AutoQuester.IntPtr;
import com.agge.AutoQuester.Pathing;

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
import com.example.PacketUtils.WidgetInfoExtended;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.callback.ClientThread;
import net.runelite.api.widgets.Widget;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class Action {
    public Action(int max)  
    {
        log.info("Constructing Action!");
        _max = max;
        _ticks = 0;
    }

    public boolean continueDialogue() {
         log.info("Entering cont dialog");
         Optional<Widget> mainContinueOpt = Widgets.search().withTextContains(
            "Click here to continue").first();
         if (mainContinueOpt.isPresent()) {
             MousePackets.queueClickPacket();
             WidgetPackets.queueResumePause(mainContinueOpt.get().getId(), -1);
             _ticks = 0;
             return true;
         }

         // These have yet to be needed.
         Optional<Widget> continue1Opt = Widgets.search().withId(12648448).hiddenState(false).first();
         if (continue1Opt.isPresent()) {
             log.info("continue 1");
             MousePackets.queueClickPacket();
             WidgetPackets.queueResumePause(continue1Opt.get().getId(), 1);
             _ticks = 0;
             return true;
         }
         Optional<Widget> continue2Opt = Widgets.search().withId(41484288).hiddenState(false).first();
         if (continue2Opt.isPresent()) {
             log.info("continue 2");
             MousePackets.queueClickPacket();
             WidgetPackets.queueResumePause(continue2Opt.get().getId(), 1);
             _ticks = 0;
             return true;
         }

         if (_ticks > 1) {
             _ticks = 0;
             return true;
         }
         _ticks++;
         return false;
    }

    public boolean selectDialogue(String str, int choice) {
         log.info("Selecting dialogue");
         Optional<Widget> d = Widgets.search()
                                     .withTextContains(str)
                                     .first();
         if (d.isPresent()) {
             MousePackets.queueClickPacket();
             WidgetPackets.queueResumePause(d.get().getId(), choice);
             _ticks = 0;
             return true;
         }

         // Don't want to timeout here.
         //if (timeout > MAX_TIMEOUT) {
         //    timeout = 0;
         //    return true;
         //}
         //timeout++;
        
         return false;
    }

    public boolean interactNPC(String name, String action) 
    {
         log.info("Interacting with");
         if (NPCInteraction.interact(name, action)) {
             _ticks = 0;
             return true;
         }

         //if (timeout > max) {
         //    timeout = 0;
         //    return true;
         //}
         //timeout++;
        
         return false;
    }

    // xxx widgets are a nightmare
    //public boolean interactMenu()
    //{
    //    Widget menu = plugin.getClient().getWidget(
    //    MousePackets.queueClickPacket();
    //    //interactWidget not implemented yet
    //    WidgetPackets.queueWidgetAction(plugin.getClient().getWidget(
    //        config.item().getWidgetInfo().getPackedId()), "Smith", "Smith set");

    // xxx not needed, but keeping to maybe make generic wrapper (still not 
    // really needed)
    public boolean buyN(String name, int n)
    {
        log.info("Interacting with");
        if (n == 1) {
            return ShopInteraction.buyOne(name);
        }
        return false;
    }
    
    // A better solution would be to use Widget packets, but if it's only SPACE...
    public boolean pressSpace() 
    {
        try {
            KeyEvent keyPress = new KeyEvent(AutoQuesterPlugin.client.getCanvas(), 
                    KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, 
                    KeyEvent.VK_SPACE, KeyEvent.CHAR_UNDEFINED);
            AutoQuesterPlugin.client.getCanvas().dispatchEvent(keyPress);
            KeyEvent keyRelease = new KeyEvent(AutoQuesterPlugin.client.getCanvas(), 
                KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, 
                KeyEvent.VK_SPACE, KeyEvent.CHAR_UNDEFINED);
            AutoQuesterPlugin.client.getCanvas().dispatchEvent(keyRelease);
            KeyEvent keyTyped = new KeyEvent(AutoQuesterPlugin.client.getCanvas(), 
                KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, 
                KeyEvent.VK_SPACE, KeyEvent.CHAR_UNDEFINED);
            AutoQuesterPlugin.client.getCanvas().dispatchEvent(keyTyped);
            return true;
        } catch (IllegalArgumentException e) {
            // Ignore the exception, SPACE executes fine.
            return true;
        }
    }

    public boolean interactTileItem(String name, int actionNo)
    {
        log.info("Trying to interact with: " + name + " " + actionNo);
        AtomicBoolean found = new AtomicBoolean(false);
        TileItems.search()
                 .withName(name)
                 .withinDistance(10) // Assumed to be close.
                 .first().ifPresent(item -> { 
            MousePackets.queueClickPacket();
            TileItemPackets.queueTileItemAction(
                actionNo, item.getTileItem().getId(),
                item.getLocation().getX(), item.getLocation().getY(), false);
            log.info("Interacted with: " + name);
            found.set(true); });
        return found.get();
    }

    /**
     * Block next instruction.
     * @param int ticks
     * How many ticks to block for.
     * @return TRUE when done blocking
     */
    public boolean block(int ticks)
    {
        log.info("Blocking next action!");
        _ticks++;
        log.info("Ticks: " + _ticks);
        if (_ticks > ticks) {
            _ticks = 0;
            return true;
        }
        return false;
    }   

    public void setMax(int max)
    {
        _max = max;
    }

    public int getMax()
    {
        return _max;
    }

    public int getTicks()
    {
        return _ticks;
    }

    public boolean timeout(int n)
    {
        return _ticks > n;
    }
    
    private int _max;
    private int _ticks;
}
