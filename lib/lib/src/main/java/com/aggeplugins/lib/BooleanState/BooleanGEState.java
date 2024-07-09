/**
 * @file BooleanGEState.java
 * @class BooleanGEState
 * Grand Exchange state.
 *
 * @note Takes a Pair instead of a more straight-foward data structure, to allow
 * for many different types of inheritence from BooleanState. Results in a very 
 * nasty constructor, but unravels nicely.
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
public class BooleanGEState<T> extends BooleanState<T> {
    public BooleanGEState(
        Pair<
        Triple<List<Integer>, List<Integer>, List<Integer>>,
        Pair<List<Integer>, List<Integer>>
        > items) 
    {
        super((T) items);
        this.init();
    }

    private void init()
    {
        this.items = (Pair<
            Triple<List<Integer>, List<Integer>, List<Integer>>,
            Pair<List<Integer>, List<Integer>>
            >) this.ctx;

        this.buyTriple = items.getLeft();
        this.buyItems = buyTriple.getLeft();
        this.buyAmt = buyTriple.getMiddle();
        this.buyPrice = buyTriple.getRight();

        this.sellPair = items.getRight();
        this.sellItems = sellPair.getLeft();
        this.sellPrice = sellPair.getRight();

        this.confirmed = new AtomicBoolean(false);
        this.delay = RandomUtil.randTicks();

        // xxx for testing Strings
        //this.sell = new ArrayList<>(Arrays.asList(
        //    "Bronze pickaxe", "Bronze axe", "Bread", "Wooden shield", 
        //    "Bronze arrow"
        //));
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

        // State 2: Sell items.
        Iterator<Integer> i = sellItems.iterator();
        Iterator<Integer> j = sellPrice.iterator();

        // These are expected to be in sync, that would be an error in data 
        // entry, but why not check anyway?
        if (i.hasNext() && j.hasNext()) {
            Integer item = i.next();
            Integer price = j.next();
            log.info("Selling items...");

            /** @note Control flow:
             * 1. Stay in selling state until confirm Widget.
             * 2. Click confirm Widget.
             * 3. If NOT warning message, remove item and move next item.
             * 4. IF warning message, confirm will not validate.
             * 5. Click warning message on condition entry.
             * 6. Now confirm will validate, back to step 3, remove item and 
             *    move next item.
             */
            if (GEUtil.sell(item, price)) {
                confirmed.set(true);
            }
            if (confirmed.get() && !GEUtil.warning()) {
                confirmed.set(false);
                i.remove();
                j.remove();
            }

            // Break-out; be done selling before moving on to buying.
            return false;
        }

        // State 3: Buy items.
        // xxx handle not enough coins -- chat message
        i = buyItems.iterator();
        j = buyPrice.iterator();
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
        
        // Confirm done buying and selling before exiting (return true).
        // All of these should be empty, so confirm everything.
        return buyItems.isEmpty()  && buyPrice.isEmpty()  && buyAmt.isEmpty() &&
               sellItems.isEmpty() && sellPrice.isEmpty();
    }

    /**
     * Same as normal run() procedure, but overloaded to allow an optional tick
     * delay (GE interaction seems VERY fast).
     *
     * @todo UNIMPLEMENTED
     */
    //public boolean run(boolean delay) 
    //{
    //    /* Guard everything behind a tick delay. */
    //    
    //    // If no delay, just call non-overloaded procedure -- is designed 
    //    // without delay.
    //    if (!delay) {
    //        this.run();
    //    }

    //    // Exit until delay has counted down.
    //    if (this.delay-- > 0) {
    //        log.info("Waiting {} ticks", this.delay);
    //        return false;
    //    }

    //    // State 1: Interacting with GE.
    //    if (!GEUtil.isOpen()) {
    //        GEUtil.open();
    //        log.info("Waiting to finish interacting with GE...");
    //        // Get new delay and reiterate.
    //        this.delay = RandomUtil.randTicks();
    //        return false; // wait to be in GE widget
    //    }

    //    // State 2: Sell items.
    //    if (!sell.isEmpty()) {
    //        Integer item = sell.get(0);
    //        log.info("Selling items...");
    //        int price = 1;

    //        /** @note Control flow:
    //         * 1. Stay in selling state until confirm Widget.
    //         * 2. Click confirm Widget.
    //         * 3. If NOT warning message, remove item and move next item.
    //         * 4. IF warning message, confirm will not validate.
    //         * 5. Click warning message on condition entry.
    //         * 6. Now confirm will validate, back to step 3, remove item and 
    //         *    move next item.
    //         */
    //        if (GEUtil.sell(item, price)) {
    //            confirmed.set(true);
    //        }
    //        if (confirmed.get() && !GEUtil.warning()) {
    //            confirmed.set(false);
    //            sell.remove(0);
    //        }
    //        // Get new delay and reiterate.
    //        this.delay = RandomUtil.randTicks();
    //        // Break-out; be done selling before moving on to buying.
    //        return false;
    //    }

    //    // State 3: Buy items.
    //    if (!buy.isEmpty()) {
    //        Integer item = buy.get(0);
    //        log.info("Buying items...");
    //        int amount = 1;
    //        int price = 1000;

    //        if (GEUtil.buy(item, amount, price)) {
    //             // confirmed will always be at false in entry, due to overall
    //             // control flow of class.
    //            confirmed.set(true);
    //        }
    //        // xxx set up this way to handle price too high Widget, not 
    //        // implemented!
    //        if (confirmed.get()) {
    //            confirmed.set(false);
    //            sell.remove(0);
    //        }
    //        // Get new delay and reiterate.
    //        this.delay = RandomUtil.randTicks();
    //        // Break-out; be done buying before moving on.
    //        return false;
    //    }
    //    
    //    // Confirm done buying and selling before exiting (return true).
    //    return buy.isEmpty() && sell.isEmpty();
    //}

    private Pair<
            Triple<List<Integer>, List<Integer>, List<Integer>>,
            Pair<List<Integer>, List<Integer>>
            > items; 

    private Triple<List<Integer>, List<Integer>, List<Integer>> buyTriple;
    private List<Integer> buyItems;
    private List<Integer> buyPrice;
    private List<Integer> buyAmt;

    private Pair<List<Integer>, List<Integer>> sellPair;
    private List<Integer> sellItems;
    private List<Integer> sellPrice;

    private AtomicBoolean confirmed;
    private int delay;

    //private List<String> sell;
}
