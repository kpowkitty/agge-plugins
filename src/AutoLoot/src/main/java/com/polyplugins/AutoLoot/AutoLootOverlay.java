/**
 * @file AutoLootPlugin.java
 * @class AutoLootPlugin
 * Overlay - Modular looting automation. 
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-15
 *
 * Derived in large part from AutoCombat.
 * Majority of credit goes to PiggyPlugins. This is just a refactor with fixes.
 * Thanks PiggyPlugins!
 */

package com.polyplugins.AutoLoot;

import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.google.common.base.Strings;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;


import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.lang.model.type.ArrayType;
import javax.sound.sampled.Line;
import java.awt.*;
import java.util.Arrays;
import java.util.Optional;


public class AutoLootOverlay extends OverlayPanel {
    @Override
    public Dimension render(Graphics2D graphics)
    {
        panelComponent.getChildren().clear();

        panelComponent.getChildren().add(TitleComponent.builder()
                .text("AutoLoot")
                .color(new Color(255, 157, 249))
                .build());
        panelComponent.getChildren().add(TitleComponent.builder()
                .text(_plugin.started ? "STARTED" : "STOPPED")
                .color(_plugin.started ? Color.GREEN : Color.RED)
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Loot queue: ")
                .leftColor(new Color(255, 157, 249))
                .right(Integer.toString(_plugin.lootQueue.size()))
                .rightColor(Color.WHITE)
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Waiting: ")
                .leftColor(new Color(255, 157, 249))
                .right(Integer.toString(_plugin.ticks.get()) + " ticks")
                .rightColor(Color.WHITE)
                .build());

        return super.render(graphics);
    }

    @Inject
    private AutoLootOverlay(AutoLootPlugin plugin)
    {
        super(plugin);
        _plugin = plugin;
        setPosition(OverlayPosition.BOTTOM_LEFT);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setDragTargetable(true);
    }

    private final AutoLootPlugin _plugin;
}
