/**
 * @file BooleanBankingState.java
 * @class BooleanBankingState
 * Banking state. Pass null to either bank items or keep items to exclude one or
 * the other.
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
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.*;
import com.piggyplugins.PiggyUtils.BreakHandler.ReflectBreakHandler;
import com.example.Packets.*;

import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.coords.WorldArea;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Iterator;

@Slf4j
public class BooleanBankingState<T> extends BooleanState<T> {
    public BooleanBankingState(Pair<List<Integer>, List<Integer>> items) 
    {
        super((T) items);
        this.init();
    }

    private void init()
    {
        this.items = (Pair<List<Integer>, List<Integer>>) this.ctx;

        if (this.items.getLeft() == null)
            this.bankItems = Collections.<Integer>emptyList();
        else
            this.bankItems = items.getLeft();

        if (this.items.getRight() == null) {
            this.keepItems = Collections.<Integer>emptyList();
            this.keepItemsStr = Collections.<String>emptyList();
        } else {
            this.keepItems = items.getRight();
            this.keepItemsStr = new ArrayList<>();
            for (Integer e : this.keepItems) {
                String str = LibUtil.itemIdToString(e);
                if (str != null) {
                    str = LibUtil.stripName(str);
                    this.keepItemsStr.add(str);
                } else {
                    log.info("Error converting (item ID:) " + e + " to string!");
                }
            }
        }

        // can't init this before being in bank!!
        //this.bankInventory = BankInventory.search().result();

        banked = false;

        this.equipment = Equipment.search().result();
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

        // Guard the BankInventory to only be seen once, so items can be 
        // properly removed from it.
        if (!banked) {
            bankInventory = BankInventory.search().result();
            log.info("Bank inventory size: " + bankInventory.size());
            banked = true;
        }

        // State 2: Deposit all items.
        // xxx and equipment (?)
        Iterator<Widget> i = bankInventory.iterator();

        if (i.hasNext()) {
            Widget item = i.next();
            // name is html tagged, strip it
            String name = LibUtil.stripName(item.getName());
            log.info("Keep items ID: " + keepItems + "; Bank item ID: " + item.getItemId());
            log.info("Keep items name: " + keepItemsStr + "; Bank item name: " + name);
            // Sometimes ID doesn't always work, check String too.
            if (keepItems.contains(item.getItemId()) ||
                keepItemsStr.contains(name)) {
                log.info("Keeping item: " + item.getItemId());
                // don't deposit, just remove from the deposit list
                i.remove();
                return false; // break-out and re-enter
            } else {
                log.info("Depositing item: " + item.getId());
                BankInventoryInteraction.useItem(item, "Deposit-All");
                i.remove();
                return false; // break-out and re-enter
            }
        }

        // State 3: Withdraw desired items.
        Iterator<Integer> j = bankItems.iterator();

        log.info("Items size: " + bankItems.size());
        if (bankItems.size() > 0) {
            log.info("Items head: " + bankItems.get(0));
        }

        if (j.hasNext()) {
            Integer item = j.next();
            log.info("Trying to withdraw: " + item);

            // If item to withdraw is in keep items: remove, break-out, and 
            // move next.
            //if (keepItems.contains(item)) {
            //    j.remove();
            //    return false;
            //} else {
            // Guard bank action on whether it's already in inventory or not
            // (it shouldn't be).

            // xxx if it's kept it should be in our inventory
            if (!Inventory.search()
                         .withId(item)
                         .first().isPresent()) {
                Bank.search()
                    .withId(item)
                    .first().ifPresent(w -> {
                        //MousePackets.queueClickPacket();
                        //WidgetPackets.queueWidgetAction(i, action);
                        BankInteraction.withdrawX(w, 1);
                });
                return false; // break-out and re-enter with new state
            }

            // Safe to remove, it's in our inventory.
            j.remove();
            
            //}
        }

        // close the bank screen before exiting
        EthanApiPlugin.invoke(11, 786434, MenuAction.CC_OP.getId(), 1, -1, "", "", -1, -1);

        log.info("Bank items size: " + bankItems.size());
        // both should be true
        return banked && bankItems.isEmpty();
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

    private Pair<List<Integer>, List<Integer>> items;
    private List<Integer> bankItems;
    private List<Integer> keepItems;
    private List<String> keepItemsStr;
    private List<Widget> bankInventory;
    boolean banked;

    private List<EquipmentItemWidget> equipment;
}
