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

package com.aggeplugins.lib;

import com.aggeplugins.lib.*;
import com.aggeplugins.lib.export.*;

import com.piggyplugins.PiggyUtils.API.*;
import com.piggyplugins.PiggyUtils.*;
import com.example.Packets.*;
import com.example.EthanApiPlugin.*;
import com.example.InteractionApi.*;
import com.example.PacketUtils.*;
import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.Collections.query.*;

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
import java.util.Random;

@Slf4j
public class Action {
    public Action()  
    {
        log.info("Constructing Action!");
        _ticks = 0;
    }

    public static boolean continueDialogue() {
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

    public static boolean selectDialogue(String str, int choice) {
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

    public static boolean interactNPC(String name, String action) 
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
    public static boolean buyN(String name, int n)
    {
        log.info("Interacting with");
        if (n == 1) {
            return ShopInteraction.buyOne(name);
        }
        return false;
    }
    
    // A better solution would be to use Widget packets, but if it's only SPACE...
    public static boolean pressSpace(Client client) 
    {
        try {
            KeyEvent keyPress = new KeyEvent(client.getCanvas(), 
                    KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, 
                    KeyEvent.VK_SPACE, KeyEvent.CHAR_UNDEFINED);
            client.getCanvas().dispatchEvent(keyPress);
            KeyEvent keyRelease = new KeyEvent(client.getCanvas(), 
                KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, 
                KeyEvent.VK_SPACE, KeyEvent.CHAR_UNDEFINED);
            client.getCanvas().dispatchEvent(keyRelease);
            KeyEvent keyTyped = new KeyEvent(client.getCanvas(), 
                KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, 
                KeyEvent.VK_SPACE, KeyEvent.CHAR_UNDEFINED);
            client.getCanvas().dispatchEvent(keyTyped);
            return true;
        } catch (IllegalArgumentException e) {
            // Ignore the exception, SPACE executes fine.
            return true;
        }
    }

    public static boolean interactTileItem(String name, int actionNo)
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
    public static boolean block(int ticks)
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

    public static void checkRunEnergy(Client client) 
    {
        Random rand = new Random();
        if (client.getVarpValue(173) == 0 && 
            client.getEnergy() >= rand.nextInt(50) * 100) { // random 0-50
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1);
        }
    }

    public static boolean isInteractingNPC(Client client)
    {
        return client.getLocalPlayer().isInteracting() && 
               client.getLocalPlayer().getInteracting() != null;
    }

    public static boolean isInteractingTO(Client client)
    {
        return EthanApiPlugin.isMoving() ||
               client.getLocalPlayer().getAnimation() != -1;
    }
    
    public static boolean isInteractingTO(Client client, WorldPoint wp)
    {
        return wp.getPlane() == client.getLocalPlayer()
                                      .getWorldLocation()
                                      .getPlane();
    }

    public static boolean isInteractingTI(int id)
    {
        return Inventory.search().withId(id).onlyUnnoted().empty() &&
               !TileItems.search().withId(id).withinDistance(10).empty();
    }
    
    public static boolean isInteractingTI(String name)
    {
        return Inventory.search().nameContains(name).onlyUnnoted()
                                                    .empty() &&
               !TileItems.search().nameContains(name).withinDistance(10)
                                                     .empty();
    }

    //public static void setMax(int max)
    //{
    //    _max = max;
    //}

    //public static int getMax()
    //{
    //    return _max;
    //}

    public static int getTicks()
    {
        return _ticks;
    }

    public static boolean timeout(int n)
    {
        return _ticks > n;
    }
    
    //private int _max;
    public static int _ticks;
}
