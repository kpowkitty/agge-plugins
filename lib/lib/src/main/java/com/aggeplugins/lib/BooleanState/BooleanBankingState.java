/**
 * @file BooleanBankingState.java
 * @class BooleanBankingState
 * Banking state.
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

import java.util.*;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Iterator;

@Slf4j
public class BooleanBankingState<T> extends BooleanState<T> {
    public BooleanBankingState(List<Integer> items) 
    {
        super((T) items);
        this.init();
    }

    private void init()
    {
        if (this.ctx == null)
            this.items = Collections.<Integer>emptyList();
        else 
            this.items = (List<Integer>) this.ctx;

        this.deposited = false;
        this.withdrew = false;
        this.banking = false;
        this.clicked = new AtomicBoolean(false);
    }

    /**
     * Deposit all items (except excluded) and withdraw items specified in 
     * BooleanBankingState's constructor.
     *
     * @remark Pass null to BooleanBankingState's constructor to only deposit 
     * items.
     */
    @Override
    public boolean run() 
    {
        // xxx handle if inventory is already empty or (effectively) empty due
        // to items present that don't want to be deposited
        // xxx also handle if item is already in inventory, and don't want to
        // re-withdraw

        // State 1: Interacting with bank.
        if (!canDeposit()) {
            canBank();
            log.info("Waiting to finish interacting with bank...");
            return false; // wait to be in bank widget
        }

        // State 2: Deposit all items.
        if (!inventoryEmpty() && !deposited) {
            log.info("Depositing items...");
            List<Widget> l = BankInventory.search().result();
                for (Widget item : l) {
                    log.info("Trying to deposit: " + item.getName());
                    BankInventoryInteraction.useItem(item, "Deposit-All");
                }
            return false; // break-out until finished depositing
        }
        deposited = true; // done depositing

        // State 3: Withdraw desired items.
        Iterator<Integer> it = items.iterator();

        if (it.hasNext()) {
            Integer item = it.next();
            log.info("Trying to withdraw: " + item);

            // Guard bank action on whether it's already in inventory or not
            // (it shouldn't be).
            if (!Inventory.search()
                         .withId(item)
                         .first().isPresent()) {
                Bank.search()
                    .withId(item)
                    .first().ifPresent(i -> {
                        //MousePackets.queueClickPacket();
                        //WidgetPackets.queueWidgetAction(i, action);
                        BankInteraction.withdrawX(i, 1);
                });
                return false; // break-out and re-enter with new state
            }

            // Safe to remove, it's in our inventory.
            it.remove();
        }

        return items.isEmpty();
    }

    private boolean canBank()
    {
        AtomicBoolean found = new AtomicBoolean(false);
        TileObjects.search()
                   .withAction("Bank")
                   .first().ifPresent(to -> {
                found.set(true);
                log.info("Bank found");
                TileObjectInteraction.interact(to, "Bank");
        });
        TileObjects.search()
                   .withName("Bank chest")
                   .first().ifPresent(to -> {
                found.set(true);
                log.info("Bank chest found");
                TileObjectInteraction.interact(to, "Use");
        });
        NPCs.search()
            .withAction("Bank")
            .first().ifPresent(npc -> {
                log.info("Bank NPC found");
                found.set(true);
                NPCInteraction.interact(npc, "Bank");
        }); 
        return found.get();
    }

    private List<String> itemsToList(String items)
    {
        return LibUtil.stringToList(items);
    }

    private boolean inventoryEmpty()
    {
        return Inventory.getEmptySlots() == 28;
    }

    //private boolean canNpcBank()
    //{
    //    AutomicBoolean found = new AtomicBoolean(false);
    //    NPCs.search()
    //        .withAction("Bank")
    //        .nearestToPlayer()
    //        .ifPresent(npc -> {
    //            found.set(true);
    //            NPCInteraction.interact(npc, "Bank");
    //    }); 
    //    return found.get();
    //}

    private boolean canDeposit()
    {
        // If the bank widget is not found, return false.
        return !Widgets.search().withId(786445).first().isEmpty();
    }

    private boolean pin()
    {
        if (Widgets.search().withId(13959169).first().isPresent()) {
            log.info("Unable to continue: Bank pin");
            return true;
        }
        return false;
    }

    private List<Integer> items;
    private boolean deposited;
    private boolean withdrew;
    private boolean banking;
    private AtomicBoolean clicked;
}
