package com.aggeplugins.lib;

import com.example.Packets.MousePackets;

import net.runelite.api.ItemID;

import java.util.*;
import java.util.stream.Collectors;
import java.lang.reflect.Field;

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
            Field field = ItemID.class.getField(str.toUpperCase()
                                                   .replace(" ", "_"));
            return field.getInt(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException("Invalid item name: " + str, e);
        }
    }

    //public static List<Integer> stringToIntList(List<String> list)
    //{
    //}
}
