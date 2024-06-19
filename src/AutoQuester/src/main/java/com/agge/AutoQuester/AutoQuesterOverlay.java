/**
 * @file AutoQuesterOverlay.java
 * @class AutoQuesterOverlay
 * Overlay - AutoQuest for 10 quest points! 
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-15
 *
 * Special thanks to EthanApi and PiggyPlugins for API, inspiration, and a 
 * source of code at times.
 */

package com.agge.AutoQuester;

import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileObjects;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import com.google.common.base.Strings;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.lang.model.type.ArrayType;
import javax.sound.sampled.Line;
import java.awt.*;
import java.util.Arrays;
import java.util.Optional;

public class AutoQuesterOverlay extends OverlayPanel {
    @Override
    public Dimension render(Graphics2D graphics)
    {
        panelComponent.getChildren().clear();

        panelComponent.setPreferredSize(new Dimension(200, 480));
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("AutoQuester")
                .color(new Color(255, 157, 249))
                .build());
        panelComponent.getChildren().add(TitleComponent.builder()
                .text(_plugin.started ? "STARTED" : "STOPPED")
                .color(_plugin.started ? Color.GREEN : Color.RED)
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Current instruction: ")
                .leftColor(new Color(255, 157, 249))
                .right(_plugin.getInstructionName())
                .rightColor(Color.WHITE)
                .build());

        return super.render(graphics);
    }

    @Inject
    private AutoQuesterOverlay(AutoQuesterPlugin plugin)
    {
        super(plugin);
        _plugin = plugin;
        setPosition(OverlayPosition.BOTTOM_LEFT);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setDragTargetable(true);
    }

    private final AutoQuesterPlugin _plugin;
}
