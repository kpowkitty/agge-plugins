package com.aggeplugins.Fighter;

import com.aggeplugins.Fighter.*;
import com.aggeplugins.lib.*;
import com.aggeplugins.lib.export.TaskManager.*;

import com.example.PacketUtils.WidgetInfoExtended;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.example.InteractionApi.NPCInteraction;
import com.example.EthanApiPlugin.Collections.query.NPCQuery;
import com.example.EthanApiPlugin.Collections.*;

import net.runelite.api.NPC;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.widgets.Widget;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Getter
@Slf4j
public class AttackNPC extends AbstractTask<FighterPlugin, FighterConfig> {
    public AttackNPC(FighterPlugin plugin, FighterConfig config) {
        super(plugin, config);
    }

    @Override
    public boolean validate()
    {
        //log.info("Entering attack NPC validation...");
        // Check to make sure we're not already in a time-consuming task.
        return !plugin.inCombat && !plugin.buryingBones &&
               plugin.getLootQueue().isEmpty();
    }

    @Override
    public void execute()
    {
        NPC targetNPC = findNPC(config.npcTarget());
        if (targetNPC != null) {
            log.info("Attacking NPC: {}", config.npcTarget());
            NPCInteraction.interact(targetNPC, "Attack");
        } else {
            log.info("NPC not found: {}", config.npcTarget());
            plugin.currState = "NPC not found: " + config.npcTarget();
        }
    }

    public NPC findNPC(String name) 
    {
        if (plugin.getClient() == null) {
            log.warn("Client not initialized");
            return null;
        }
        List<NPC> npcs = NPCs.search()
                             .nameContains(name)
                             // don't attack other's NPCs
                             .noOneInteractingWith()
                             .alive()
                             .walkable()
                             .result();
        if (config.melee()) {
            npcs = new NPCQuery(npcs).meleeable().result();
        }
        if (config.withinDistance() > 0) {
            npcs = new NPCQuery(npcs).withinWorldArea(new WorldArea(
                plugin.getClient().getLocalPlayer().getWorldLocation(),
                config.withinDistance(),
                config.withinDistance()))
                                     .result();
        }

        // Guard to make sure there's actually NPCs; else return null -- higher
        // up the call-stack can cope with the null.
        if (!npcs.isEmpty()) {
            if (!config.randomize()) {
                return npcs.get(0);  
            } else {
                Random rand = new Random();
                return npcs.get(rand.nextInt(npcs.size()));
            }
        } else {
            return null;
        }

        //return plugin.getClient().getNpcs().stream()
        //        .filter(npc -> npc.getName() != null && npc.getName().contains(npcName))
        //        .findFirst()
        //        .orElse(null);
    }
}
