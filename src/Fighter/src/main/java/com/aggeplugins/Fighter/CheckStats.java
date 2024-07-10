package com.aggeplugins.Fighter;

import com.aggeplugins.Fighter.*;
import com.aggeplugins.lib.*;
import com.aggeplugins.lib.export.TaskManager.*;

import com.example.EthanApiPlugin.Collections.Bank;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.InteractionApi.InventoryInteraction;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;

import java.util.Optional;

@Slf4j
public class CheckStats extends AbstractTask<FighterPlugin, FighterConfig> {

    public CheckStats(FighterPlugin plugin, FighterConfig config) {
        super(plugin, config);
    }

    @Override
    public boolean validate()
    {
        //log.info("Entering CheckStates task validation");
        return config.shouldEat();
    }

    @Override
    public void execute() {
        consumeFood();
    }

    public void consumeFood() {
        // Random, +-5 hp of config. MAKE SURE TO SET HP LIMIT AT +6.
        if (plugin.getClient().getBoostedSkillLevel(Skill.HITPOINTS) <= 
           (config.EatAt() + randOffset(5))) {
            Widget food = findFood();
            if (food != null)
            log.info("Found food! Attempting to consume food for health recovery...");
            InventoryInteraction.useItem(food, "Eat");
            // timeout 1 or 2 ticks
            plugin.timeout = 1 + (int) Math.random() * 2;
            return; // exit
        } else {
            log.info("Failed to eat food: {}", config.foodToEat());
            return; // exit
        }
    }

    public Widget findFood()
    {
        Optional<Widget> food = Inventory.search()
                .onlyUnnoted()
                .withAction("Eat")
                .matchesWildCardNoCase(config.foodToEat().toLowerCase())
                .first();

        return food.orElse(null);
    }

    private int randOffset(int range)
    {
        return (int) ((Math.random() * 2 * range++) - range);
    }
}
