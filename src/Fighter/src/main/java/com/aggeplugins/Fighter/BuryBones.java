/**
 * @file BuryBones.java
 * @class BuryBones
 * Task - Handle burying bones.
 *
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-18
 *
 */

package com.aggeplugins.Fighter;

import com.aggeplugins.Fighter.*;
import com.aggeplugins.lib.*;
import com.aggeplugins.lib.export.TaskManager.*;

import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.TileItemPackets;
import com.example.Packets.WidgetPackets;
import com.example.InteractionApi.InventoryInteraction;

import net.runelite.api.*;
import net.runelite.api.widgets.Widget;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class BuryBones extends AbstractTask<FighterPlugin, FighterConfig> {
    public BuryBones(FighterPlugin plugin, FighterConfig config)
    {
        super(plugin, config);
    }

    @Override
    public boolean validate()
    {   
        //log.info("Entering BuryBones task validation");
        return config.buryBones() && !plugin.inCombat &&
               plugin.getLootQueue().isEmpty();
    }

    @Override
    public void execute()
    {
        Optional<Widget> bones = Inventory.search()
                                          .onlyUnnoted()
                                          .withAction("Bury")
                                          .first();
        if (bones.isPresent()) {
            Widget b = bones.get();
            plugin.buryingBones = true;
            plugin.timeout = RandomUtil.randTicks(1, 3);
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(b, "Bury");
            return; // exit early, we've found bones
        }
        bones = Inventory.search()
                         .onlyUnnoted()
                         .withAction("Scatter")
                         .first();
        if (bones.isPresent()) {
            Widget b = bones.get();
            plugin.buryingBones = true;
            plugin.timeout = RandomUtil.randTicks(1, 3);
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(b, "Scatter");
            return; // exit early, we've found ashes
        }
        plugin.buryingBones = false;
    }

    /* Old code: */
    //Inventory.search().onlyUnnoted().withAction("Bury").filter(b -> 
    //    config.buryBones()).first().ifPresent(bone -> {
    //        MousePackets.queueClickPacket();
    //        WidgetPackets.queueWidgetAction(bone, "Bury");
    //        // Random 2-4, inclusive.
    //        plugin.timeout = 2 + (int) (Math.random() * 2);
    //});

    //Inventory.search().onlyUnnoted().withAction("Scatter").filter(b -> 
    //    config.buryBones()).first().ifPresent(bone -> {
    //        MousePackets.queueClickPacket();
    //        WidgetPackets.queueWidgetAction(bone, "Scatter");
    //        // Random 2-4, inclusive.
    //        plugin.timeout = 2 + (int) (Math.random() * 2);
    //});
}
