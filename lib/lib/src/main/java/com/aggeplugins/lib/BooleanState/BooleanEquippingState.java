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

        block = false;
    }

    private boolean canEquip(int id)
    {
        return !Inventory.search().withId(id).withAction("Equip").empty() ||
               !Inventory.search().withId(id).withAction("Wear").empty() ||
               !Inventory.search().withId(id).withAction("Wield").empty();
    }

    private boolean inInventory(int id)
    {
        return Inventory.search().withId(id).first().isPresent();
    }

    @Override
    public boolean run()
    {
        // State 1: Equipping items and logging equipment
        equipping: {
            Iterator<Integer> it = items.iterator();
            if (it.hasNext()) {
                Integer item = it.next();
                log.info("Item ID: " + item);

                if (!canEquip(item) && !block) {
                    // just remove and move next, we can't equip this item
                    removeSlot(item);
                    it.remove();
                    break equipping;
                }

                log.info("Able to equip item ID: " + item);
                InventoryInteraction.useItem(item, "Equip", "Wear", "Wield");
                block = true;

                // Guard until item is not in inventory, to safely remove and 
                // not lose items in the game tick logic.
                if (!inInventory(item)) {
                    removeSlot(item);
                    log.info("Sucessfully equipped item ID: " + item);
                    // Safe to remove the item now, properly guarded and all 
                    // procedures done.
                    it.remove();
                    block = false; // release block
                    break equipping;
                }

                break equipping;
            }
        }

        // State 2: Unequipping all items left in equipment
        unequipping: {
            slotIt.reset();
            if (slotIt.hasNext()) {
                EquipmentSlotEx slot = slotIt.next();

                EquipmentUtilEx.getItemInSlot(slot).ifPresent(w -> {
                    w.interact("Remove");
                    log.info("Successfully removed item ID: " + w.getEquipmentItemId());
                });

                // Guards against missing actions due to game tick update.
                if (!EquipmentUtilEx.getItemInSlot(slot).isPresent()) {
                    //log.info("Entering equipment slot removal routine...");
                    slotIt.remove();
                    break unequipping;
                }

                break unequipping; // keep trying to unequip until we're done
            }
        }

        // Guard the return true, both of these should be empty.
        return items.isEmpty() && slotIt.isEmpty();
    }

    private boolean removeSlot(int id) 
    {
        // Make sure iterator is reset before looping all slots.
        slotIt.reset();

        // Loop over all equipment slots and remove the equipped item.
        while (slotIt.hasNext()) {
            EquipmentSlotEx slot = slotIt.next();
            log.info("Equipment slots remaining: " + slotIt.size());

            //log.info("On equipment slot: " + slot.getIdx());
            Optional<EquipmentItemWidget> widget = 
                EquipmentUtilEx.getItemInSlot(slot);

            if (widget.isPresent()) {
                log.info("Equipment slot match found!\n" +
                    "Equipment slot item ID: " + widget.get().getEquipmentItemId() + "\n" +
                    "Item to equip ID: " + id);
                if (widget.get().getEquipmentItemId() == id) {
                    log.info("Equipment slot ID matches item ID. Removing from equipment slot list...");
                    slotIt.remove();
                    return true; // release control
                }
            }
        }

        return false;
    }

    private List<Integer> items;
    private EquipmentSlotExIterator slotIt;
    private boolean block;
}
