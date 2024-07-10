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

package com.aggeplugins.AutoQuester;

import com.aggeplugins.AutoQuester.*;
import com.aggeplugins.lib.ui.*;

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
import java.time.Duration;

public class AutoQuesterOverlay extends OverlayPanel {
    // Import the common theme.
    Theme theme = new Theme();
   
    @Override
    public Dimension render(Graphics2D graphics)
    {
        panelComponent.getChildren().clear();

        panelComponent.setPreferredSize(new Dimension(435, 435));
        panelComponent.setBackgroundColor(theme.background);
        //panelComponent.setBorder(new Rectangle())

        panelComponent.getChildren().add(LineComponent.builder()
                .left("AccountBuilder")
                //.leftFont(theme.sourceSansBlack)
                .leftColor(theme.title)
                .right(plugin.started ? "STARTED" : "STOPPED")
                //.rightFont(theme.sourceSans)
                .rightColor(plugin.started ? theme.green : theme.off)
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Current instruction: ")
                //.leftFont(theme.sourceCodePro)
                .leftColor(theme.red)
                .right(plugin.getInstructionName())
                //.rightFont(theme.sourceCodePro)
                .rightColor(theme.text)
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Instructions remaining: ")
                //.leftFont(theme.sourceCodePro)
                .leftColor(theme.red)
                .right(String.valueOf(plugin.getInstructionsSize()))
                //.rightFont(theme.sourceCodePro)
                .rightColor(theme.text)
                .build());
        // xxx throw a null pointer, ensure a clean init
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Time elapsed: ")
                //.leftFont(theme.sourceCodePro)
                .leftColor(theme.red)
                .right(plugin.logger.getFormattedTime())
                //.rightFont(theme.sourceCodePro)
                .rightColor(theme.text)
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Exp gained: ")
                //.leftFont(theme.sourceCodePro)
                .leftColor(theme.red)
                .right(String.valueOf(plugin.logger.getTotalExp()))
                //.rightFont(theme.sourceCodePro)
                .rightColor(theme.text)
                .build());

        return super.render(graphics);
    }

    @Inject
    private AutoQuesterOverlay(AutoQuesterPlugin plugin)
    {
        super(plugin);
        this.plugin = plugin;
        setPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setDragTargetable(true);
    }

    private final AutoQuesterPlugin plugin;
}
