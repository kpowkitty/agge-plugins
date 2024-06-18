/**
 * @file AutoLootConfig.java
 * @class AutoLootConfig
 * Config - Modular looting automation. 
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-15
 *
 * Derived in large part from AutoCombat.
 * Majority of credit goes to PiggyPlugins. This is just a refactor with fixes.
 * Thanks PiggyPlugins!
 */

package com.polyplugins.AutoLoot;


import net.runelite.client.config.*;

@ConfigGroup("AutoLootConfig")
public interface AutoLootConfig extends Config {
    @ConfigItem(
            keyName = "Toggle",
            name = "Toggle",
            description = "",
            position = 0
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

    @ConfigSection(
            name = "AutoLoot Configuration",
            description = "Modular looting automation. Don't run with other looting interactions!",
            position = 1,
            closedByDefault = false
    )
    String autoLootConfig = "autoLootConfig";

    @ConfigItem(
            keyName = "lootNames",
            name = "Loot names",
            description = "Items to loot",
            position = 2,
            section = autoLootConfig
    )
    default String lootNames() {
        return "Coins,Bones";
    }

    @ConfigItem(
            keyName = "waitTicks",
            name = "Wait to loot (in ticks)",
            description = "How many ticks?",
            position = 3,
            section = autoLootConfig
    )
    default int waitFor() {
        return 0;
    }

    /* @todo Implement picking up self vs. other's. */
    //@ConfigItem(
    //        keyName = "myPickupNames",
    //        name = "My pickup names",
    //        description = "Items to pickup (my items)",
    //        position = -10,
    //        section = autoLootConfig
    //)
    //default String myPickupNames() {
    //    return "Bronze arrows,Iron arrows";
    //}

    //@ConfigItem(
    //        keyName = "othersPickupNames",
    //        name = "Other's pickup names",
    //        description = "Items to pickup (other's items)",
    //        position = -20,
    //        section = autoLootConfig
    //)
    //default String othersPickupNames() {
    //    return "Coins,Bones";
    //}

    @ConfigItem(
            keyName = "buryBones",
            name = "Bury bones/ashes",
            description = "Will bury ANY bone/ash in your inventory",
            position = 3,
            section = autoLootConfig
    )
    default boolean buryBones() {
        return false;
    }
}
