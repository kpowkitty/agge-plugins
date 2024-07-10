package com.aggeplugins.lib;

import lombok.extern.slf4j.Slf4j;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.awt.FontFormatException;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Slf4j
public class ResourceManager {
    public static Font loadFont(String fh)
    {
        Font font = null;
        try {
            // Load font from resources using ClassLoader
            InputStream stream = ResourceManager.class.getResourceAsStream(fh);
            log.info("Resource: " + ResourceManager.class.getResourceAsStream(fh));
            if (stream != null) {
                font = Font.createFont(Font.TRUETYPE_FONT, stream);
            } else {
                // Handle case where resource is not found
                throw new FileNotFoundException("Font resource not found: " + fh);
            }
        } catch (FontFormatException | IOException e) {
            // Handle exception (e.g., log error, provide default font, etc.)
            e.printStackTrace();
            // Example fallback font (adjust as needed)
            font = new Font("Arial", Font.PLAIN, 12);
        }
        return font;
    }
}
