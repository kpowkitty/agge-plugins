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

//package com.aggeplugins.lib.BooleanState;
//
//import com.aggeplugins.lib.*;
//import com.aggeplugins.lib.export.*;
//import com.aggeplugins.lib.BooleanState.*;
//
//import com.example.EthanApiPlugin.Collections.*;
//import com.example.EthanApiPlugin.Collections.query.*;
//import com.example.EthanApiPlugin.*;
//import com.example.InteractionApi.*;
//import com.piggyplugins.PiggyUtils.BreakHandler.ReflectBreakHandler;
//import com.example.Packets.*;
//
//import net.runelite.api.*;
//import net.runelite.api.widgets.Widget;
//import net.runelite.api.coords.WorldPoint;
//import net.runelite.api.coords.WorldArea;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.tuple.Pair;
//
//import java.util.*;
//import java.util.Optional;
//import java.util.stream.Collectors;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.Iterator;
//
//@Slf4j
//public class BooleanGESellState<T> extends BooleanState<T> {
//    public BooleanGESellState(Pair<List<Integer>, Integer> items) 
//    {
//        super((T) items);
//        this.init();
//    }
//
//    private void init()
//    {
//        this.items = (Pair<List<Integer>, Integer>) this.ctx;
//        this.sell = items.getLeft();
//        this.price = items.getRight();
//    }
//
//    @Override
//    public boolean run() 
//    {
//        // State 1: Interacting with GE.
//        if (GEUtil.isOpen()) {
//            GEUtil.open();
//            log.info("Waiting to finish interacting with GE...");
//            return false; // wait to be in GE widget
//        }
//
//        // State 2: Buy items.
//        for (Iterator<Integer> it = sell.iterator(); it.hasNext(); ) {
//            log.info("Selling items...");
//            Integer item = it.next();
//            GEUtil.sellItem(item, price);
//            log.info("Trying to sell: " + item);
//            it.remove();
//        }
//            
//        return sell.isEmpty();
//    }
//
//    private List<Integer> sell;
//    private Integer price;
//    private Pair<List<Integer>, Integer> items; 
//}
