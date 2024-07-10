package com.aggeplugins.lib;

import com.example.Packets.MousePackets;

import net.runelite.api.ItemID;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;
import java.lang.reflect.Field;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Slf4j
public class LibUtil {

   /**
     * Takes a comma-deliminated String and returns a List with trimmed names.
     *
     * @param String str
     * A comma-deliminated String.
     *
     * @return List<String>
     * A List of trimmed String(s), null if unsuccessful.
     */
    public static List<String> stringToList(String str) 
    {
        if (str != null) {
            return Arrays.stream(str.split(","))
                         .map(String::trim)
                         .collect(Collectors.toList());
        }
        return null;
    }

    /**
     * Converts a normal case item name to, first, its ItemID enumeration name;
     * and, finally, its int item ID.
     *
     * @warning Should mostly work! Maybe not always.
     */
    public static int stringToItemId(String str)
    {
        try {
            Class<?> clazz = Class.forName("com.aggeplugins.lib.LocalItemID");
            String fmt = str.toUpperCase().replace(" ", "_");
            log.info("Formatted string: " + fmt);
            Field field = clazz.getField(fmt);
            return field.getInt(null);
        } catch (ClassNotFoundException | IllegalAccessException e) {
            log.info("Unable to load class: LocalItemID");
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            log.info("Error finding ItemID!");
            throw new IllegalArgumentException("Invalid item name: " + str, e);
        }
        return -1;
    }

    public static String itemIdToString(int id) {
        try {
            Class<?> clazz = Class.forName("com.aggeplugins.lib.LocalItemID");
            Field[] fields = clazz.getDeclaredFields();
            
            for (Field field : fields) {
                if (field.getType() == int.class && field.getInt(null) == id) {
                    String name = field.getName().toLowerCase().replace("_", " ");
                    return name.substring(0, 1).toUpperCase() + name.substring(1);
                }
            }
    
            throw new IllegalArgumentException("Invalid item ID: " + id);
        } catch (ClassNotFoundException | IllegalAccessException e) {
            throw new RuntimeException("Error finding item name for ID: " + id, e);
        }
    }

    /**
     * Strip the name of any extra bits but the raw name (looks for characters
     * unlikely in a name).
     */
    public static String stripName(String str)
    {
        Pattern p = Pattern.compile("<[^>]*>");
        Matcher m = p.matcher(str);
        return m.replaceAll("");
    }

    /**
     * Strip a string by matching for a pattern.
     */
    public static String strip(String str, String re)
    {
        Pattern p = Pattern.compile(re);
        Matcher m = p.matcher(str);
        return m.replaceAll("");
    }

    /**
     * Convert an enum to a string first and recieve a human-readable case name.
     */
    public static String unEnumerate(String str)
    {
        String name = str.toLowerCase().replace("_", " ");
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
                    
    // xxx maybe? doesn't work
    //public static int stringToItemId(String str)
    //{
    //    try {
    //        return LocalItemID.valueOf(str.toUpperCase().replace(" ", "_"));
    //    } catch (IllegalArgumentException e) {
    //        throw new IllegalArgumentException("Invalid item name: " + str);
    //    }
    //}

    //public static List<Integer> stringToIntList(List<String> list)
    //{
    //}
    

}
