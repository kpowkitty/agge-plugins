/**
 * @file Util.java
 * @class Util
 * Utility tools.
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-18
 */

package com.aggeplugins.Skiller;

import com.aggeplugins.Skiller.Context;

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

public class Util {
    public static boolean isInventoryReset(Context ctx)
    {
        List<Widget> inventory = Inventory.search().result();
        for (Widget item : inventory) {
            if (!shouldKeep(Text.removeTags(item.getName()), ctx)) { // using our shouldKeep method, we can filter the items here to only include the ones we want to drop.
                return false;
            }
        }
        return true; // we will know that the inventory is reset because the inventory only contains items we want to keep
    }

    public static boolean shouldKeep(String name, Context ctx) 
    {
        List<String> itemsToKeep = new ArrayList<>(List.of(ctx.config.itemsToKeep().split(","))); // split the items listed by comma. and add them to a list.
        itemsToKeep.addAll(List.of(ctx.config.toolsToUse().split(","))); //We must also check if the tools are included in the Inventory, Rather than equipped, so they are added here
        return itemsToKeep.stream()// stream the List using Collection.stream() from java.util
                .anyMatch(i -> Text.removeTags(name.toLowerCase()).contains(i.toLowerCase()));
        // we'll set everything to lowercase as well as remove the html tags that is included (The color of the item in game),
        // and check if the input name contains any of the items in the itemsToKeep list.
        // might seem silly, but this is to allow specific items you want to keep without typing the full name.
        // We also prefer names to ids here, but you can change this if you like.
    }

    public static boolean hasTools(Context ctx) 
    {
        //Updated from https://github.com/moneyprinterbrrr/ImpactPlugins/blob/experimental/src/main/java/com/impact/PowerGather/PowerGatherPlugin.java#L196
        //Big thanks hawkkkkkk
        String[] tools = ctx.config.toolsToUse().split(","); // split the tools listed by comma, no space.

        int numInventoryTools = Inventory.search()
                .filter(item -> isTool(item.getName(), ctx)) // filter inventory by using out isTool method
                .result().size();
        int numEquippedTools = Equipment.search()
                .filter(item -> isTool(item.getName(), ctx)) // filter inventory by using out isTool method
                .result().size();

        return numInventoryTools + numEquippedTools >= tools.length; // if the size of tools and the filtered inventory is the same, we have our tools.
    }

    //public static void setTimeout()
    //{
    //    timeout = RandomUtils.nextInt(SkillerPlugin.config.tickdelayMin(), SkillerPlugin.config.tickDelayMax());
    //}
    public static boolean isTool(String name, Context ctx) 
    {
      String[] tools = ctx.config.toolsToUse().split(","); // split the tools listed by comma, no space.

      return Arrays.stream(tools) // stream the array using Arrays.stream() from java.util
              .anyMatch(i -> name.toLowerCase().contains(i.toLowerCase())); // more likely for user error than the shouldKeep option, but we'll follow the same idea as shouldKeep.
    }
     
}
