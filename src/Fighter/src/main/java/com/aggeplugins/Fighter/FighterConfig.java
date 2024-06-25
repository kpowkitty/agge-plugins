package com.aggeplugins.Fighter;

import net.runelite.client.config.*;

@ConfigGroup("FighterConfig")
public interface FighterConfig extends Config {
    @ConfigItem(
            keyName = "Toggle",
            name = "Toggle",
            description = "",
            position = 0
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "npcTarget",
            name = "Target NPC(s)?",
            description = "NPCs you want to kill",
            position = 1
    )
    default String npcTarget() {
        return "Chicken";
    }

    @ConfigItem(
            keyName = "melee",
            name = "Melee only?",
            description = "Will hard-force meleeable NPCs only",
            position = 2
    )
    default boolean melee()
    {
        return false;
    }
    
    @ConfigItem(
            keyName = "withinDistance",
            name = "Attack NPC(s) within distance?",
            description = "Lock the range of how far from player to look for NPC(s). Set to 0 to disable.",
            position = 3
    )
    default int withinDistance()
    {
        return 0;
    }

    @ConfigItem(
            keyName = "randomize",
            name = "Randomize NPC distance from player?",
            description = "Default NPC is closest to player. Set to randomize in player's search range.",
            position = 4
    )

    default boolean randomize()
    {
        return false;
    }

    @ConfigItem(
            keyName = "shouldLoot",
            name = "Loot?",
            description = "Whether you should loot or not",
            position = 5
    )
    default boolean shouldLoot()
    {
        return true;
    }
    
    @ConfigItem(
            keyName = "loot",
            name = "Items to loot",
            description = "Write a list of item names (Case sensitive) separated by commas.",
            position = 5
    )
    default String loot() {
        return "Bones,Feather";
    }

    @ConfigItem(
            keyName = "buryBones",
            name = "Bury bones/ashes",
            description = "Will bury ANY bone/ash in your inventory",
            position = 6
    )
    default boolean buryBones() {
        return true;
    }

    @ConfigItem(
            keyName = "specEnabled?",
            name = "Use Wep Spec?",
            description = "Use your weapons special?",
            position = 7
    )
    default boolean specEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "shouldEat",
            name = "Eat?",
            description = "Whether you should use food or not",
            position = 8
    )
    default boolean shouldEat()
    {
        return true;
    }

    @ConfigItem(
            keyName = "foodToEat",
            name = "Food: ",
            description = "What food will you use to heal?",
            position = 9
    )
    default String foodToEat() {
        return "Shark";
    }

    @Range(
            min = 6,
            max = 99
    )
    @ConfigItem(
            keyName = "EatAt",
            name = "Eat at",
            description = "",
            position = 9
    )
    default int EatAt() {
        return 15;
    }

    @ConfigItem(
            keyName = "useCombatPotion",
            name = "Combat potions?",
            description = "Uses regular or super combat potions",
            position = 10
    )
    default boolean useCombatPotion() {
        return true;
    }

    @Range(
            min = 3,
            max = 99
    )
    @ConfigItem(
            keyName = "useCombatAt",
            name = "Use at",
            description = "What level to use combat potions at",
            position = 11
    )

    default int useCombatPotAt() {
        return 80;
    }

    @ConfigItem(
            keyName = "useRangingPotion",
            name = "Ranging potions?",
            description = "Uses ranging potions",
            position = 12
    )
    default boolean useRangingPotion() {
        return false;
    }

    @Range(
            min = 3,
            max = 99
    )
    @ConfigItem(
            keyName = "useRangingPotAt",
            name = "Use at",
            description = "What level to use ranging potions at",
            position = 13
    )

    default int useRangingPotAt() {
        return 80;
    }

    @ConfigItem(
            keyName = "usePrayerPotion",
            name = "Prayer potions?",
            description = "Uses prayer potions",
            position = 14
    )
    default boolean usePrayerPotion() {
        return true;
    }

    @Range(
            min = 6,
            max = 99
    )
    @ConfigItem(
            keyName = "usePrayerAt",
            name = "Use at",
            description = "What level to use prayer potions at, prayer or super restore",
            position = 15
    )

    default int usePrayerPotAt() {
        return 20;
    }

    @ConfigItem(
            keyName = "tickDelay",
            name = "Tick Delay",
            description = "Slow down certain actions",
            position = 16
    )
    default int tickDelay() {
        return (int) (Math.random() * 6); // random between 0-5
    }
}
