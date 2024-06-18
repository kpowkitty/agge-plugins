/**
 * @file AutoQuesterTileOverlay.java
 * @class AutoQuesterTileOverlay
 * Tile overlay - AutoQuest for 10 quest points! 
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-15
 *
 * Special thanks to EthanApi and PiggyPlugins for API, inspiration, and a 
 * source of code at times.
 */

package com.agge.AutoQuester;

import com.example.EthanApiPlugin.Collections.TileObjects;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.awt.*;
import java.util.Optional;

@Slf4j
public class AutoQuesterTileOverlay extends Overlay {
    private final PanelComponent panelComponent = new PanelComponent();
    private final Client client;
    private final AutoQuesterPlugin plugin;

    @Inject
    private AutoQuesterTileOverlay(Client client, AutoQuesterPlugin plugin) {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.UNDER_WIDGETS);

    }

    // XXX THIS IS ALL BROKEN
    @Override
    public Dimension render(Graphics2D graphics) {
    //    if (plugin.GOAL != null) {
    //        // xxx -1 (?), no idea if this will work
    //        LocalPoint lp = LocalPoint.fromWorld(-1, plugin.GOAL);
    //        renderTile(graphics, lp, 
    //                Color.RED, 1.0, new Color(255, 255, 255, 20));
    //        renderTextLocation(graphics, "Goal", 
    //                WorldPoint.fromLocalInstance(client, plugin.GOAL),
    //                Color.RED);
    //    }

        return null;
    }

    ///**
    // * Builds a line component with the given left and right text
    // *
    // * @param left
    // * @param right
    // * @return Returns a built line component with White left text and Yellow right text
    // */
    //private LineComponent buildLine(String left, String right) {
    //    return LineComponent.builder()
    //            .left(left)
    //            .right(right)
    //            .leftColor(Color.WHITE)
    //            .rightColor(Color.YELLOW)
    //            .build();
    //}

    //private void renderTile(Graphics2D graphics, LocalPoint dest, Color color, 
    //        double borderWidth, Color fillColor) {
    //    if (dest != null) {
    //        Polygon poly = Perspective.getCanvasTilePoly(this.client, dest);
    //        if (poly != null) {
    //            OverlayUtil.renderPolygon(graphics, poly, color, fillColor, 
    //                    new BasicStroke((float) borderWidth));
    //        }
    //    }
    //}

    //private void renderTextLocation(Graphics2D graphics, String text, 
    //        LocalPoint lp, Color color) {
    //    if (lp == null) {
    //        return;
    //    }
    //    Point textLocation = Perspective.getCanvasTextLocation(
    //        client, graphics, lp, text, 0);
    //    if (textLocation != null) {
    //        OverlayUtil.renderTextLocation(graphics, textLocation, text, color);
    //    }
    //}
}
