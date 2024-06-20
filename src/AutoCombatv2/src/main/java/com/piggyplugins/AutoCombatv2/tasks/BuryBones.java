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

package com.piggyplugins.AutoCombatv2.tasks;

import com.piggyplugins.AutoCombatv2.AutoCombatv2Plugin;
import com.piggyplugins.AutoCombatv2.AutoCombatv2Config;

import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.TileItemPackets;
import com.example.Packets.WidgetPackets;
import com.example.InteractionApi.InventoryInteraction;
import com.piggyplugins.PiggyUtils.strategy.AbstractTask;

import net.runelite.api.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BuryBones extends AbstractTask<AutoCombatv2Plugin, AutoCombatv2Config> {
    public BuryBones(AutoCombatv2Plugin plugin, AutoCombatv2Config config)
    {
        super(plugin, config);
    }

    @Override
    public boolean validate()
    {   
        // xxx better validation
        return config.buryBones();
    }

    @Override
    public void execute()
    {
        Inventory.search().onlyUnnoted().withAction("Bury").filter(b -> 
            config.buryBones()).first().ifPresent(bone -> {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetAction(bone, "Bury");
                // Random 3-6, inclusive.
                plugin.timeout = 3 + (int) (Math.random() * 4);
        });

        Inventory.search().onlyUnnoted().withAction("Scatter").filter(b -> 
            config.buryBones()).first().ifPresent(bone -> {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetAction(bone, "Scatter");
                // Random 3-6, inclusive.
                plugin.timeout = 3 + (int) (Math.random() * 4);
        });
    }
}
