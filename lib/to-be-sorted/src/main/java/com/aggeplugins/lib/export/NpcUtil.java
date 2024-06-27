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

import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.query.NPCQuery;
import net.runelite.api.NPC;

import java.util.Optional;

public class NpcUtil {

    public static Optional<NPC> getNpc(String name, boolean caseSensitive) {
        if (caseSensitive) {
            return NPCs.search().withName(name).nearestToPlayer();
        } else {
            return nameContainsNoCase(name).nearestToPlayer();
        }
    }

    public static NPCQuery nameContainsNoCase(String name) {
        return NPCs.search().filter(npcs -> npcs.getName() != null && npcs.getName().toLowerCase().contains(name.toLowerCase()));
    }

}
