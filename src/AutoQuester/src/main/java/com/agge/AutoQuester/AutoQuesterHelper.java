/**
 * @file AutoQuesterHelper.java
 * @class AutoQuesterHelper
 * Helper - AutoQuest for 10 quest points! 
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-15
 *
 * Special thanks to EthanApi and PiggyPlugins for API, inspiration, and a 
 * source of code at times.
 */

package com.agge.AutoQuester;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.query.ItemQuery;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.NPC;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AutoQuesterHelper {
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private AutoQuesterConfig config;

    @Inject
    public ItemManager itemManager;

    ///**
    // * Gets the cached price or wiki price if not yet cached
    // *
    // * @param name Exact name of item
    // * @return
    // */
    //public int getPrice(String name) {
    //    if (lootCache.containsKey(name)) {
    //        return lootCache.get(name);
    //    }
    //    int price = itemManager.search(name).get(0).getWikiPrice();
    //    lootCache.put(name, price);
    //    return price;
    //}
}
