/**
 * @file BooleanEquipingState.java
 * @class BooleanEquipingState
 * Boolean equipping state; return true when done equipping.
 *
 * @remark Default behavior is to equip the items constructed with and unequip
 * all other items. Construct with null to (instead of equipping) remove all 
 * equipped items. 
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-30
 *
 */

package com.aggeplugins.lib.BooleanState;

import com.aggeplugins.lib.*;
import com.aggeplugins.lib.BooleanState.*;
import com.aggeplugins.lib.export.*;
import com.aggeplugins.lib.export.EquipmentSlotEx.EquipmentSlotExIterator;

import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.Collections.query.*;
import com.example.EthanApiPlugin.*;
import com.example.InteractionApi.*;
import com.piggyplugins.PiggyUtils.BreakHandler.ReflectBreakHandler;
import com.example.Packets.*;

import net.runelite.api.*;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Iterator;

@Slf4j
public class BooleanEquippingState<T> extends BooleanState<T> {
    public BooleanEquippingState(List<Integer> items)
    {
        super((T) items);
        this.init();
    }

    private void init()
    {   
        if (this.ctx == null) {
            this.items = Collections.<Integer>emptyList();
        } else {
            this.items = (List<Integer>) this.ctx;
        }

        this.slotIt = new EquipmentSlotExIterator();
    }

    @Override
    public boolean run()
    {
        // State 1: Equipping items and logging equipment
        Iterator<Integer> it = items.iterator();

        if (it.hasNext()) {
            Integer item = it.next();

            InventoryInteraction.useItem(item, "Equip", "Wear", "Wield");

            // Guard until item is not in inventory, to safely remove and not
            // lose items in the game tick logic.
            if (!Inventory.search()
                          .withId(item)
                          .first().isPresent()) {

                // Loop over all equipment slots and remove the equipped item.
                while (slotIt.hasNext()) {
                    EquipmentSlotEx slot = slotIt.next();
                    EquipmentUtilEx.getItemInSlot(slot).ifPresent(i -> {
                        if (i.equals(item)) {
                            slotIt.remove();
                        }
                    });
                }

                // Looped through all equipment slots, reset iterator for next
                // game loop.
                slotIt.reset();

                // Safe to remove the item now, properly guarded and all
                // procedures done.
                it.remove();
            }

            // Break-out, until equipped all items.
            return false;        
        }

        // State 2: Unequipping all items left in equipment
        if (slotIt.hasNext()) {
            EquipmentSlotEx slot = slotIt.next();

            EquipmentUtilEx.getItemInSlot(slot).ifPresent(i -> {
                i.interact("Remove");
            });

            // Guards against missing actions due to game tick update.
            if (!EquipmentUtilEx.getItemInSlot(slot).isPresent()) {
                slotIt.remove();
            }

            // Break-out, until done unequipping items.
            return false;
        }

        // Guard the return true, both of these should be empty.
        return items.isEmpty() && slotIt.isEmpty();
    }

    private List<Integer> items;
    private EquipmentSlotExIterator slotIt;
}
