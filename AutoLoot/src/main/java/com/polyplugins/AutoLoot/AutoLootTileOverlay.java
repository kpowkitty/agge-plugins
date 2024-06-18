/**
 * @file AutoLootPlugin.java
 * @class AutoLootPlugin
 * Tile overlay - Modular looting automation. 
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


import com.example.EthanApiPlugin.Collections.TileObjects;
import com.google.common.base.Strings;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.awt.*;
import java.util.Optional;

@Slf4j
public class AutoLootTileOverlay extends Overlay {
    private final PanelComponent panelComponent = new PanelComponent();
    private final Client client;
    private final AutoLootPlugin plugin;

    @Inject
    private AutoLootTileOverlay(Client client, AutoLootPlugin plugin) {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.UNDER_WIDGETS);

    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (plugin.lootTile != null) {
            renderTile(graphics, plugin.lootTile, 
                    Color.RED, 1.0, new Color(255, 255, 255, 20));
            renderTextLocation(graphics, "Loot", 
                    WorldPoint.fromLocalInstance(client, plugin.lootTile),
                    Color.RED);
        }

        return null;
    }

    /**
     * Builds a line component with the given left and right text
     *
     * @param left
     * @param right
     * @return Returns a built line component with White left text and Yellow right text
     */
    private LineComponent buildLine(String left, String right) {
        return LineComponent.builder()
                .left(left)
                .right(right)
                .leftColor(Color.WHITE)
                .rightColor(Color.YELLOW)
                .build();
    }

    private void renderTile(Graphics2D graphics, LocalPoint dest, Color color, 
            double borderWidth, Color fillColor) {
        if (dest != null) {
            Polygon poly = Perspective.getCanvasTilePoly(this.client, dest);
            if (poly != null) {
                OverlayUtil.renderPolygon(graphics, poly, color, fillColor, 
                        new BasicStroke((float) borderWidth));
            }
        }
    }

    private void renderTextLocation(Graphics2D graphics, String text, 
            WorldPoint worldPoint, Color color) {
        LocalPoint point = LocalPoint.fromWorld(client, worldPoint);
        if (point == null) {
            return;
        }
        Point textLocation = Perspective.getCanvasTextLocation(
            client, graphics, point, text, 0);
        if (textLocation != null) {
            OverlayUtil.renderTextLocation(graphics, textLocation, text, color);
        }
    }
}
