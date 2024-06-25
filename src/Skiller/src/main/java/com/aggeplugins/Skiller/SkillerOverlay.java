package com.aggeplugins.Skiller;

import com.aggeplugins.Skiller.Context;

import com.google.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import java.awt.*;

public class SkillerOverlay extends OverlayPanel {
    @Inject
    private SkillerOverlay(SkillerPlugin plugin)
    {
        super(plugin);
        this.plugin = plugin;
        setPosition(OverlayPosition.BOTTOM_LEFT);
        setPreferredSize(new Dimension(160, 160));
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        Color purple = new Color(84, 72, 122);

        panelComponent.setPreferredSize(new Dimension(200, 320));
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Agge Skiller")
                .color(purple)
                .build());
        panelComponent.getChildren().add(TitleComponent.builder()
                .text(plugin.started ? "Running" : "Paused")
                .color(plugin.started ? Color.GREEN : Color.RED)
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("State: ")
                .leftColor(purple)
                .right(!plugin.started ? "STOPPED" : plugin.currState)
                .rightColor(Color.WHITE)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Trying to: ")
                .leftColor(purple)
                .right(plugin.config.expectedAction() + " " + plugin.config.objectToInteract() )
                .rightColor(Color.WHITE)
                .build());

        return super.render(graphics);
    }

    private final SkillerPlugin plugin;
}
