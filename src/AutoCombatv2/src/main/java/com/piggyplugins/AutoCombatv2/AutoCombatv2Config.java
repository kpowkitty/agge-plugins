package com.piggyplugins.AutoCombatv2;


import net.runelite.client.config.*;

@ConfigGroup("AutoCombatv2Config")
public interface AutoCombatv2Config extends Config {
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
            name = "npcTarget",
            description = "NPCs you want to kill",
            position = 1
    )
    default String npcTarget() {
        return "Chicken";
    }

    @ConfigItem(
            keyName = "loot",
            name = "Items to loot",
            description = "Write a list of item names (Case sensitive) separated by commas.",
            position = 2
    )
    default String loot() {
        return "Bones,Feather";
    }

    @ConfigItem(
            keyName = "specEnabled?",
            name = "Use Wep Spec?",
            description = "Use your weapons special?",
            position = 1
    )
    default boolean specEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "foodToEat",
            name = "Food: ",
            description = "What food will you use to heal?",
            position = 1
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
            position = 3
    )
    default int EatAt() {
        return 15;
    }

    @ConfigItem(
            keyName = "tickDelay",
            name = "Tick Delay",
            description = "Slow down certain actions",
            position = 3
    )
    default int tickDelay() {
        return (int) (Math.random() * 8); // random between 0-7
    }

    @ConfigItem(
            keyName = "buryBones",
            name = "Bury bones/ashes",
            description = "Will bury ANY bone/ash in your inventory",
            position = 4
    )
    default boolean buryBones() {
        return true;
    }

   @ConfigItem(
            keyName = "useCombatPotion",
            name = "Combat potions?",
            description = "Uses regular or super combat potions",
            position = 5
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
            position = 6
    )

    default int useCombatPotAt() {
        return 80;
    }

    @ConfigItem(
            keyName = "useRangingPotion",
            name = "Ranging potions?",
            description = "Uses ranging potions",
            position = 7
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
            position = 8
    )

    default int useRangingPotAt() {
        return 80;
    }

    @ConfigItem(
            keyName = "usePrayerPotion",
            name = "Prayer potions?",
            description = "Uses prayer potions",
            position = 9
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
            position = 10
    )

    default int usePrayerPotAt() {
        return 20;
    }
}
