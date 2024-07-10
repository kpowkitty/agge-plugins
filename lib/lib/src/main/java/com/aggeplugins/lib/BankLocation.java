/**
 * @file BankLocation.java
 * @class BankLocation
 * Enumerate all banks and their WorldArea(s).
 * (Can use toWorldPoint() to retrieve WorldPoint(s))
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-18
 *
 */

package com.aggeplugins.lib;

import com.aggeplugins.lib.*;
import com.aggeplugins.lib.export.*;

import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

/* 
 * @note WorldArea(x, y, width, height, plane)
 * So, WorldPoint x, y are the first two parameters and z is the last parameter.
 */
public enum BankLocation {
    NONE(new WorldArea(0, 0, 0, 0, 0)),
    LUMBRIDGE(new WorldArea(3207, 3215, 4, 8, 2)),
    VARROCK_WEST(new WorldArea(3180, 3433, 6, 15, 0)),
    VARROCK_WEST1(new WorldArea(3185, 3436, 1, 1, 0)),
    VARROCK_WEST2(new WorldArea(3185, 3438, 1, 1, 0)),
    VARROCK_WEST3(new WorldArea(3185, 3440, 1, 1, 0)),
    VARROCK_WEST4(new WorldArea(3185, 3442, 1, 1, 0)),
    VARROCK_WEST5(new WorldArea(3185, 3444, 1, 1, 0)),
    VARROCK_EAST(new WorldArea(3250, 3419, 8, 6, 0)),
    GRAND_EXCHANGE(new WorldArea(3154, 3480, 22, 22, 0)),
    EDGEVILLE(new WorldArea(3091, 3488, 8, 12, 0)),
    FALADOR_EAST(new WorldArea(3009, 3355, 13, 4, 0)),
    FALADOR_WEST(new WorldArea(2943, 3368, 7, 6, 0)),
    DRAYNOR(new WorldArea(3092, 3240, 6, 7, 0)),
    DUEL_ARENA(new WorldArea(3380, 3267, 5, 7, 0)),
    SHANTAY_PASS(new WorldArea(3299, 3118, 11, 10, 0)),
    AL_KHARID(new WorldArea(3269, 3161, 4, 13, 0)),
    CATHERBY(new WorldArea(2806, 3438, 7, 4, 0)),
    SEERS_VILLAGE(new WorldArea(2721, 3487, 10, 7, 0)),
    ARDOUGNE_NORTH(new WorldArea(2612, 3330, 10, 6, 0)),
    ARDOUGNE_SOUTH(new WorldArea(2649, 3280, 7, 8, 0)),
    PORT_KHAZARD(new WorldArea(2658, 3156, 7, 9, 0)),
    YANILLE(new WorldArea(2609, 3088, 6, 10, 0)),
    CORSAIR_COVE(new WorldArea(2567, 2862, 7, 7, 0)),
    CASTLE_WARS(new WorldArea(2435, 3081, 12, 18, 0)),
    LLETYA(new WorldArea(2349, 3160, 8, 7, 0)),
    GRAND_TREE_WEST(new WorldArea(2436, 3484, 9, 8, 1)),
    GRAND_TREE_SOUTH(new WorldArea(2448, 3476, 8, 8, 1)),
    TREE_GNOME_STRONGHOLD(new WorldArea(2441, 3414, 11, 23, 1)),
    SHILO_VILLAGE(new WorldArea(2842, 2951, 20, 8, 0)),
    NEITIZNOT(new WorldArea(2334, 3805, 6, 2, 0)),
    JATIZSO(new WorldArea(2413, 3798, 7, 7, 0)),
    BARBARIAN_OUTPOST(new WorldArea(2532, 3570, 6, 10, 0)),
    //ETCETERIA_BANK(new WorldArea(2618, 3893, 4, 4, 0)), has quest requirements
    DARKMEYER(new WorldArea(3601, 3365, 9, 3, 0)),
    CHARCOAL_BURNERS(new WorldArea(1711, 3460, 14, 10, 0)),
    HOSIDIUS(new WorldArea(1748, 3594, 5, 8, 0)),
    PORT_PISCARILIUS(new WorldArea(1794, 3784, 18, 7, 0)),
    // HALLOWED_SEPULCHRE_BANK(new WorldArea(2393, 5975, 15, 15, 0)), has quest requirements
    CANIFIS(new WorldArea(3508, 3474, 6, 10, 0)),
    // MOTHERLODE_MINE_BANK(new WorldArea(3754, 5664, 4, 3, 0)), has pickaxe requirement
    BURGH_DE_ROTT(new WorldArea(3492, 3208, 10, 6, 0)),
    TOB(new WorldArea(3646, 3204, 10, 13, 0)),
    FEROX_ENCLAVE(new WorldArea(3127, 3627, 10, 6, 0));

    BankLocation(WorldArea wa)
    {
        this.wa = wa;
    }

    public WorldArea getWorldArea()
    {
        return this.wa;
    }

    public WorldPoint getWp()
    {
        return this.wa.toWorldPoint();
    }
    
    /* 
     * @note An implementation that doesn't preemptively do the WorldPoint 
     * conversion:
     */
    //// Method to convert string to enum
    //public static BankLocation fromString(String text) {
    //    try {
    //        return BankLocation.valueOf(text);
    //    } catch (IllegalArgumentException e) {
    //        throw new IllegalArgumentException("Unknown bank location: " + text);
    //    }
    //}

    // Method to convert string to enum and then to WorldPoint
    public static WorldPoint fromString(String text)
    {
        try {
            BankLocation location = BankLocation.valueOf(text);
            return location.getWorldArea().toWorldPoint();
        } catch (IllegalArgumentException e) {
            //log.info("Invalid BankLocation, returning null");
            // xxx maybe return a default wp, like the ge
            return null;
        }
    }

    private final WorldArea wa;

    /* @note RuneLite API has this, keeping in case a wrapper is needed. */
    //public WorldPoint toWorldPoint()
    //{
    //    // xxx could choose a random point in the wa
    //    return new WorldPoint(x, y, plane);
    //}
}
