package com.aggeplugins.Skiller;

import net.runelite.client.config.*;

@ConfigGroup("Skiller")
public interface SkillerConfig extends Config {
    @ConfigSection(
            name = "Tick Delays",
            description = "Configuration for delays added to skilling activities",
            position = 3

    )
    String tickDelaySection = "Tick Delays";

    @ConfigSection(
            name = "Drop Config",
            description = "Configuration for amount of items to drop",
            position = 5

    )
    String dropConfigSection = "Drop Config";

    @ConfigItem(
            keyName = "toggle",
            name = "Toggle",
            description = "",
            position = -2
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            name = "Should Bank?",
            keyName = "shouldBank",
            description = "Search for a bank in the nearby area, if found bank when full",
            position = -3
    )
    default boolean shouldBank() {
        return false;
    }

    @ConfigItem(
            name = "Poll Current WorldPoint",
            keyName = "pollWp",
            description = "Toggle to display the current WorldPoint in logs",
            position = -4
    )
    default boolean pollWp() {
        return true;
    }

    @ConfigItem(
            name = "Set Bank",
            keyName = "setBank",
            description = "Takes a direct enumerated name, see BankLocation.java",
            position = -5
    )
    default String setBank() {
        return "";
    }

    @ConfigItem(
            name = "Set Skilling x-coordinate",
            keyName = "skillingX",
            description = "The target skilling location x-coordinate, poll current WorldPoint to find",
            position = -8
    )
    default int skillingX() {
        return 0;
    }

    @ConfigItem(
            name = "Set Skilling y-coordinate",
            keyName = "skillingY",
            description = "The target skilling location y-coordinate, poll current WorldPoint to find",
            position = -7
    )
    default int skillingY() {
        return 0;
    }

    @ConfigItem(
            name = "Set Skilling z-coordinate",
            keyName = "skillingZ",
            description = "The target skilling location z-coordinate, poll current WorldPoint to find",
            position = -6
    )
    default int skillingZ() {
        return 0;
    }

    @ConfigItem(
            keyName = "searchNpc",
            name = "Search NPCs (for fishing, etc)",
            description = "For things like fishing spots",
            position = -1
    )
    default boolean searchNpc() {
        return false;
    }

    @ConfigItem(
            name = "Object",
            keyName = "objectToInteract",
            description = "Game obejct you will be interacting with",
            position = 0
    )
    default String objectToInteract() {
        return "Tree";
    }

    @ConfigItem(
            name = "Expected Action",
            keyName = "expectedAction",
            description = "The action you wish to do on the object",
            position = 1
    )
    default String expectedAction() {
        return "Chop";
    }

    @ConfigItem(
            name = "Tool(s)",
            keyName = "toolsToUse",
            description = "Tools required to act with your object, can type ` axe` or ` pickaxe` to ignore the type",
            position = 2
    )
    default String toolsToUse() {
        return " axe";
    }

    @ConfigItem(
            name = "Keep Items",
            keyName = "itemToKeep",
            description = "Items you don't want dropped. Separate items by comma,no space. Good for UIM",
            position = 3
    )
    default String itemsToKeep() {
        return "coins,rune pouch,divine rune pouch,looting bag,clue scroll";
    }


    @ConfigItem(
            name = "Tick Delay Min",
            keyName = "tickDelayMin",
            description = "Lower bound of tick delay, can set both to 0 to remove delay",
            position = 4,
            section = tickDelaySection
    )
    default int tickdelayMin() {
        return 0;
    }

    @Range(

    )
    @ConfigItem(
            name = "Tick Delay Max",
            keyName = "tickDelayMax",
            description = "Upper bound of tick delay, can set both to 0 to remove delay",
            position = 5,
            section = tickDelaySection
    )
    default int tickDelayMax() {
        return 3;
    }

    @Range(
            max = 9
    )
    @ConfigItem(
            name = "Drop Per Tick Min",
            keyName = "numToDrop1",
            description = "Minimum amount of items dropped per tick",
            position = 6,
            section = dropConfigSection
    )
    default int dropPerTickOne() {
        return 1;
    }

    @Range(
            max = 9
    )
    @ConfigItem(
            name = "Drop Per Tick Max",
            keyName = "numToDrop2",
            description = "Maximum amount of items dropped per tick",
            position = 7,
            section = dropConfigSection
    )
    default int dropPerTickTwo() {
        return 3;
    }

    @ConfigItem(
            name = "Forestry Tree",
            keyName = "dropItems",
            description = "Object w most players,UNCHECK IF NOT WC",
            position = 5
    )
    default boolean useForestryTreeNotClosest() {
        return false;
    }

//    Artifact from when hutch wrote everything for dirty UIMs. we keep cuz its funny.
//    @ConfigItem(
//            name = "Empty slots",
//            keyName = "emptySlots",
//            description = "Amount of empty slots you have to skill with, mostly a UIM feature lol",
//            position = 3
//    )
//    default int emptySlots() {
//        return 28;
//    }
}
