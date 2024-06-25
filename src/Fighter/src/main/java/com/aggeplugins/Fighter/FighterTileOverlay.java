/**
 * @file FighterTileOverlay.java
 * @class FighterTileOverlay
 * Tile overlay - Fighter, AIO combat.
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-25
 */

package com.aggeplugins.Fighter;

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
public class FighterTileOverlay extends Overlay {
    private final PanelComponent panelComponent = new PanelComponent();
    private final Client client;
    private final FighterPlugin plugin;

    Color red = new Color(235, 0, 40);
    Color purple = new Color(81, 39, 82);

    @Inject
    private FighterTileOverlay(Client client, FighterPlugin plugin) {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.UNDER_WIDGETS);

    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (plugin.npcTile != null) {
            renderTile(graphics, plugin.npcTile, red, 1.0,
                    new Color(235, 0, 40, 20));
            renderTextLocation(graphics, "Target", 
                    WorldPoint.fromLocalInstance(client, plugin.npcTile),
                    Color.WHITE);
        }
        if (plugin.lootTile != null) {
            renderTile(graphics, plugin.lootTile, purple, 1.0, 
                    new Color(81, 39, 82, 20));
            renderTextLocation(graphics, "Loot", 
                    WorldPoint.fromLocalInstance(client, plugin.lootTile),
                    Color.WHITE);
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
