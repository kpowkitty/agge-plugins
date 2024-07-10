/**
 * Extended to add a font builder to TitleComponent.
 */

//package com.aggeplugins.lib.ui;
//
//import com.aggeplugins.lib.ui.*;
//
//import net.runelite.client.ui.overlay.components.*;
//
//import lombok.Setter;
//import lombok.Builder;
//import lombok.Getter;
//
//import java.awt.*;
//
//public class TitleComponentEx extends TitleComponent {
//    private Font font;
//
//    public TitleComponentEx(String text, Color color, Point preferredLocation, 
//                            Dimension preferredSize, Rectangle bounds)
//    {
//        super(text, color, preferredLocation, preferredSize, bounds);
//    }
//
//    public void setFont(Font font)
//    {
//        this.font = font;
//    }
//
//    @Override
//    public Dimension render(Graphics2D graphics) 
//    {
//        if (font != null) {
//            graphics.setFont(font);
//        }
//        return super.render(graphics);
//    }
//}
