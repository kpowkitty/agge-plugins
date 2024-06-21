/**
 * @file DroppingState.java
 * @class DroppingState
 * Dropping items state.
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-20
 *
 */

package com.aggeplugins.Skiller;

import com.aggeplugins.Skiller.State;
import com.aggeplugins.Skiller.StateID;
import com.aggeplugins.Skiller.Context;
import com.aggeplugins.Skiller.StateStack;
import com.aggeplugins.Skiller.Pathing;

import net.runelite.api.coord.WorldPoint;

import java.util.*;

public class DroppingState implements State {
    private final StateStack stack;
    private final Context ctxt;


    public DroppingState(StateStack stack, Context ctx) 
    {
        this.stack = state;
        this.ctx = cxt;
    }

    @Override
    public boolean run() 
    {
        if (!isDropping())
            requestStatePop();
        return false;
    }

    @Override
    public void handleEvent()
    {
        // Implement event handling logic
    }
    
    //if ((isDroppingItems() && !isInventoryReset()) || !shouldBank() && Inventory.full()) {
    //    // if the user should be dropping items, we'll check if they're done
    //    // should sit at this state til it's finished.
    //    return State.DROP_ITEMS;
    //}

    private void isDropping() {
        // filter the inventory to only get the items we want to drop
        List<Widget> itemsToDrop = Inventory.search()
                                            .filter(item -> 
            !shouldKeep(item.getName()) && 
            !isTool(item.getName())).result();

        for (int i = 0; i < Math.min(itemsToDrop.size(), 
                            RandomUtils.nextInt(config.dropPerTickOne(), 
                                                config.dropPerTickTwo())); 
             i++) {
            // we'll loop through this at a max of 10 times.  can make this a config options.  drops x items per tick (x = 10 in this example)
            InventoryInteraction.useItem(itemsToDrop.get(i), "Drop");
        }
        return itemstoDrop != 0; // if not empty, return true
    }
}
