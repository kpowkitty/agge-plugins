/**
 * @file UsePotion.java
 * @class UsePotion
 * Task - Handle using potions.
 *
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-18
 *
 */

package com.aggeplugins.Fighter;

import com.aggeplugins.Fighter.*;
import com.aggeplugins.lib.*;
import com.aggeplugins.lib.export.TaskManager.*;

import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.TileItemPackets;
import com.example.Packets.WidgetPackets;
import com.example.InteractionApi.InventoryInteraction;

import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.Skill;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class UsePotion extends AbstractTask<FighterPlugin, FighterConfig> {
    public UsePotion(FighterPlugin plugin, FighterConfig config)
    {
        super(plugin, config);
    }

    @Override
    public boolean validate()
    {   
        //log.info("Entering UsePotion task validation");
        // xxx better validation
        return config.useCombatPotion() || config.useRangingPotion() || 
               config.usePrayerPotion();
    }

    @Override
    public void execute()
    {
        // Init Widgets and check for null, interact if not null.
        // xxx might be an expensive procedure, maybe guard better and don't 
        // init each game tick
        Widget prayer = findPrayerPotion();
        Widget ranging = findRangingPotion();
        Widget combat = findCombatPotion();
        Widget strength = findStrengthPotion();

        // xxx these can be done on the same tick? should be fine, maybe 
        // separate better
        // Random, +-5 prayer of config. MAKE SURE TO SET PRAYER LIMIT AT +6.
        if (prayer != null &&
            plugin.getClient().getBoostedSkillLevel(Skill.PRAYER) <= 
           (config.usePrayerPotAt() + (((int) (Math.random() * 11)) - 5))) {
            InventoryInteraction.useItem(prayer, "Drink");
        }
        // Random, +-2
        if (ranging != null &&
            plugin.getClient().getBoostedSkillLevel(Skill.RANGED) <= 
           (config.usePrayerPotAt() + (((int) (Math.random() * 5)) - 2))) {
            InventoryInteraction.useItem(ranging, "Drink");
        }
        // Random, +-2
        if (combat != null &&
            plugin.getClient().getBoostedSkillLevel(Skill.STRENGTH) <= 
           (config.usePrayerPotAt() + (((int) (Math.random() * 5)) - 2))) {
            InventoryInteraction.useItem(combat, "Drink");
        }
        // Random, +-2
        if (strength != null &&   
            plugin.getClient().getBoostedSkillLevel(Skill.STRENGTH) <= 
           (config.usePrayerPotAt() + (((int) (Math.random() * 5)) - 2))) {
            InventoryInteraction.useItem(strength, "Drink");
        }

        // xxx final thoughts: each potion should be split up in it's own task,
        // don't want to do right now.
    }

    /**
     * Finds any unnoted Prayer Potion or Super Restore in the inventory
     *
     * @return The first potion found, or null if none are found
     */
    private Widget findPrayerPotion() {
        Optional<Widget> potion = Inventory.search().onlyUnnoted().withAction("Drink").filter(pot -> {
            String name = pot.getName();
            return name.contains("rayer potion") || name.contains("uper restore");

        }).first();
        return potion.orElse(null);
    }

    /**
     * Finds any unnoted Combat Potion in the inventory
     *
     * @return The first potion found, or null if none are found
     */
    private Widget findRangingPotion() {
        Optional<Widget> potion = Inventory.search().onlyUnnoted().withAction("Drink").nameContains("anging potion").first();
        return potion.orElse(null);
    }

    /**
     * Finds any unnoted Combat Potion in the inventory
     *
     * @return The first potion found, or null if none are found
     */
    private Widget findCombatPotion() {
        Optional<Widget> potion = Inventory.search().onlyUnnoted().withAction("Drink").nameContains("ombat potion").first();
        return potion.orElse(findStrengthPotion());
    }

    /**
     * Finds any unnoted Strength Potion in the inventory
     *
     * @return The first potion found, or null if none are found
     */
    private Widget findStrengthPotion() {
        Optional<Widget> potion = Inventory.search().onlyUnnoted().withAction("Drink").nameContains("trength potion").first();
        return potion.orElse(null);
    }

    /*
     * @enum Potion
     * Type of potion.
     */
    private enum Potion {
        Prayer(1),
        Ranging(2),
        Combat(3),
        Strength(4);

        private final int value;
    
        // Constructor
        Potion(int value) {
            this.value = value;
        }
    
        // Getter method to retrieve the value
        public int getValue() {
            return value;
        }
    }

    /* @note Keeping other good code from AutoCombatv1 below: */
    ///**
    // * Finds any teleport tab in the inventory
    // * @return The first tab found, or null if none are found
    // */
    //public Widget findTeleport() {
    //    Optional<Widget> teleport = Inventory.search().withAction("Break").nameContains("eleport").first();
    //    return teleport.orElse(null);
    //}

    ///**
    // * Finds any edible food in the inventory, based on the SuppliesUtil#foodNames list
    // *
    // * @return The first item found, or null if none are found
    // */
    //public Widget findFood() {
    //    Optional<Widget> food = Inventory.search().withAction("Eat").filter(f -> {
    //        String name = f.getName();
    //        return foodNames.stream().anyMatch(name::contains);
    //    }).first();
    //    return food.orElse(null);
    //}

    //public Widget findBone(){
    //    Optional<Widget> bone = Inventory.search().withAction("Bury").nameContains("one").first();
    //    return bone.orElse(null);
    //}
}
