/**
 * @package lib.export
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-25
 *
 * Agge Plugins export of EthansApi and PiggyAPI. Sometimes unchanged, sometimes 
 * heavily changed. Thank you for the source of code. The diffs represent my 
 * contributions.
 */

package com.aggeplugins.lib.export;

import com.example.PacketUtils.WidgetInfoExtended;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;

public class SpellUtil {
    public static WidgetInfoExtended parseStringForWidgetInfoExtended(String input) {
        for (WidgetInfoExtended value : WidgetInfoExtended.values()) {
            if (value.name().equalsIgnoreCase("SPELL_" + input.replace(" ", "_"))) {
                return value;
            }
        }
        return null;
    }

    public static Widget getSpellWidget(Client client, String input) {
        return client.getWidget(parseStringForWidgetInfoExtended(input).getPackedId());
    }
}
