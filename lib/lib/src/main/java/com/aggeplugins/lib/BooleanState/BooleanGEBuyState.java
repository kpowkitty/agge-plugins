/**
 * @file BooleanGEState.java
 * @class BooleanGEState
 * Grand Exchange state.
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-28
 *
 */

package com.aggeplugins.lib.BooleanState;

import com.aggeplugins.lib.*;
import com.aggeplugins.lib.export.*;
import com.aggeplugins.lib.BooleanState.*;

import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.Collections.query.*;
import com.example.EthanApiPlugin.*;
import com.example.InteractionApi.*;
import com.piggyplugins.PiggyUtils.BreakHandler.ReflectBreakHandler;
import com.example.Packets.*;

import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.coords.WorldArea;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Iterator;

@Slf4j
public class BooleanGEBuyState<T> extends BooleanState<T> {
    public BooleanGEBuyState(Triple<List<Integer>, 
                                    List<Integer>, 
                                    List<Integer>>
        items) 
    {
        super((T) items);

        this.init();
    }

    private void init()
    {
        this.items = (Triple<List<Integer>, 
                             List<Integer>, 
                             List<Integer>>) 
            this.ctx;

        this.buyItems = items.getLeft();
        this.buyAmt = items.getMiddle();
        this.buyPrice = items.getRight();

        this.confirmed = new AtomicBoolean(false);
    }

    @Override
    public boolean run() 
    {
        // State 1: Interacting with GE.
        if (!GEUtil.isOpen()) {
            GEUtil.open();
            log.info("Waiting to finish interacting with GE...");
            return false; // wait to be in GE widget
        }

        // State 2: Buy items.
        // xxx handle not enough coins -- chat message
        Iterator<Integer> i = buyItems.iterator();
        Iterator<Integer> j = buyPrice.iterator();
        Iterator<Integer> k = buyAmt.iterator();

        // These are expected to be in sync, that would be an error in data 
        // entry, but why not check anyway?
        if (i.hasNext() && j.hasNext() && k.hasNext()) {
            Integer item = i.next();
            Integer price = j.next();
            Integer amt = k.next();
            log.info("Buying items...");

            /** @note Control flow:
             * 1. Stay in buying state until confirm Widget.
             * 2. Click confirm Widget.
             * 3. If NOT warning message, remove item and move next item.
             * 4. IF warning message, confirm will not validate.
             * 5. Click warning message on condition entry.
             * 6. Now confirm will validate, back to step 3, remove item and 
             *    move next item.
             */
            if (GEUtil.buy(item, amt, price)) {
                 // confirmed will always be at false in entry, due to overall
                 // control flow of the class.
                confirmed.set(true);
            }
            if (confirmed.get() && !GEUtil.warning()) {
                confirmed.set(false);
                i.remove();
                j.remove();
                k.remove();
            }

            // Break-out; move on to the next buying item.
            return false;       
        }
        
        // Confirm done buying before exiting (return true).
               // These are expected to be in sync, that would be an error in data 
        // entry, but why not check anyway?
        if (i.hasNext() && j.hasNext() && k.hasNext()) {
            Integer item = i.next();
            Integer price = j.next();
            Integer amt = k.next();
            log.info("Buying items...");

            /** @note Control flow:
             * 1. Stay in buying state until confirm Widget.
             * 2. Click confirm Widget.
             * 3. If NOT warning message, remove item and move next item.
             * 4. IF warning message, confirm will not validate.
             * 5. Click warning message on condition entry.
             * 6. Now confirm will validate, back to step 3, remove item and 
             *    move next item.
             */
            if (GEUtil.buy(item, amt, price)) {
                 // confirmed will always be at false in entry, due to overall
                 // control flow of the class.
                confirmed.set(true);
            }
            if (confirmed.get() && !GEUtil.warning()) {
                confirmed.set(false);
                i.remove();
                j.remove();
                k.remove();
            }

            // Break-out; move on to the next buying item.
            return false;       
        }
        
        // Confirm done buying before exiting (return true).
        // All of these should be empty, so confirm everything. 
        return buyItems.isEmpty() && buyPrice.isEmpty() && buyAmt.isEmpty();
    }

    private Triple<List<Integer>, List<Integer>, List<Integer>> items; 
    private List<Integer> buyItems;
    private List<Integer> buyAmt;
    private List<Integer> buyPrice;
    private AtomicBoolean confirmed;
}
