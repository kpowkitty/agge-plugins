/**
 * @file AutoQuesterConfig.java
 * @class AutoQuesterConfig
 * Config - AutoQuest for 10 quest points! 
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-15
 *
 * Special thanks to EthanApi and PiggyPlugins for API, inspiration, and a 
 * source of code at times.
 */

package com.agge.AutoQuester;

import net.runelite.client.config.*;

@ConfigGroup("AutoQuesterConfig")
public interface AutoQuesterConfig extends Config {
    @ConfigItem(
            keyName = "start",
            name = "Start/Stop",
            description = "Keybind to start and stop",
            position = 0
    )
    default Keybind start() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "skip",
            name = "Skip current instruction",
            description = "Keybind to skip the current instruction",
            //"(Useful to manually fix a broken instruction)"
            position = 1
    )
    default Keybind skip() {
        return Keybind.NOT_SET;
    }

    @ConfigSection(
            name = "AutoQuester Configuration",
            description = "Select quests and other useful things",
            position = 2,
            closedByDefault = false
    )
    String autoQuesterConfig = "autoQuesterConfig";

    @ConfigItem(
            keyName = "testInstructions",
            name = "Test",
            description = "For testing your own quest instructions!",
            position = 3,
            section = autoQuesterConfig
    )
    default boolean testInstructions() 
    {
        return false;
    }

    @ConfigItem(
            keyName = "xMarksTheSpot",
            name = "X Marks the Spot",
            description = "Toggle",
            position = 4,
            section = autoQuesterConfig
    )
    default boolean xMarksTheSpot() 
    {
        return false;
    }

    @ConfigItem(
            keyName = "startedXMarksTheSpot",
            name = "X Marks the Spot already started?",
            description = "Toggle",
            position = 5,
            section = autoQuesterConfig
    )
    default boolean startedXMarksTheSpot() 
    {
        return false;
    }

    @ConfigItem(
            keyName = "sheepShearer",
            name = "Sheep Shearer",
            description = "Toggle",
            position = 6,
            section = autoQuesterConfig
    )
    default boolean sheepShearer() 
    {
        return false;
    }

    @ConfigItem(
            keyName = "startedSheepShearer",
            name = "Sheep Shearer already started?",
            description = "Toggle",
            position = 7,
            section = autoQuesterConfig
    )
    default boolean startedSheepShearer() 
    {
        return false;
    }

    @ConfigItem(
            keyName = "cooksAssistant",
            name = "Cook's Assistant",
            description = "Toggle",
            position = 8,
            section = autoQuesterConfig
    )
    default boolean cooksAssistant() 
    {
        return false;
    }

    @ConfigItem(
            keyName = "startedCooksAssistant",
            name = "Cook's Assistant already started?",
            description = "Toggle",
            position = 9,
            section = autoQuesterConfig
    )
    default boolean startedCooksAssistant() 
    {
        return false;
    }

    @ConfigItem(
            keyName = "runeMysteries",
            name = "Rune Mysteries",
            description = "Toggle",
            position = 10,
            section = autoQuesterConfig
    )
    default boolean runeMysteries() 
    {
        return false;
    }

    @ConfigItem(
            keyName = "startedRuneMysteries",
            name = "Rune Mysteries already started?",
            description = "Toggle",
            position = 11,
            section = autoQuesterConfig
    )
    default boolean startedRuneMysteries() 
    {
        return false;
    }

    @ConfigItem(
            keyName = "romeoAndJuliet",
            name = "Romeo and Juliet",
            description = "Toggle",
            position = 12,
            section = autoQuesterConfig
    )
    default boolean romeoAndJuliet() 
    {
        return false;
    }

    @ConfigItem(
            keyName = "startedRomeoAndJuliet",
            name = "Romeo and Juliet already started?",
            description = "Toggle",
            position = 13,
            section = autoQuesterConfig
    )
    default boolean startedRomeoAndJuliet() 
    {
        return false;
    }   

    @ConfigItem(
            keyName = "theRestlessGhost",
            name = "The Restless Ghost",
            description = "Toggle",
            position = 14,
            section = autoQuesterConfig
    )
    default boolean theRestlessGhost() 
    {
        return false;
    }

    @ConfigItem(
            keyName = "startedtheRestlessGhost",
            name = "The Restless Ghost already started?",
            description = "Toggle",
            position = 15,
            section = autoQuesterConfig
    )
    default boolean startedTheRestlessGhost() 
    {
        return false;
    }

    //@ConfigSection(
    //        name = "Debug",
    //        description = "Options to debug/fix the plugin",
    //        position = 10,
    //        closedByDefault = false
    //)
    //String autoQuesterDebug = "autoQuesterDebug";
    //
    //@ConfigItem(
    //        keyName = "skipInstruction",
    //        name = "Skip the current instruction",
    //        description = "Toggle on, then off -- will skip the instruction",
    //        position = 11,
    //        section = autoQuesterDebug
    //)
    //default boolean skipInstruction() 
    //{
    //    return false;
    //}

    //@ConfigItem(
    //        keyName = "saveInstructions",
    //        name = "Save registered instructions",
    //        description = "Don't hard reset, save the current instruction index",
    //        position = 12,
    //        section = autoQuesterDebug
    //)
    //default boolean saveInstructions() 
    //{
    //    return false;
    //}

    //@ConfigItem(
    //        keyName = "skipNInstructions",
    //        name = "Skip n instructions",
    //        description = "Immediately set to 0 again, or will keep skiping n!",
    //        position = 13,
    //        section = autoQuesterDebug
    //)
    //default int skipNInstructions() 
    //{
    //    return 0;
    //}
}
