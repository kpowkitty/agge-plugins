/**
 * Thanks to EthanApi and PiggyPlugins for a source of code.
 */

package com.aggeplugins.lib;

import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.Collections.query.*;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.*;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class GEUtil
{
	private static final int F2P_SLOTS = 3;
	private static final int P2P_SLOTS = 8;

	private static final int PRICE_VARBIT = 4398;
	private static final int QUANTITY_VARBIT = 4396;
	private static final int OFFER_VARBIT = 4439;

	public static boolean isOpen()
	{
		return Widgets.search()
                      .withId(
                       WidgetInfo.GRAND_EXCHANGE_INVENTORY_ITEMS_CONTAINER
                      .getId())
                      .first().isPresent();
	}

	public static void open()
	{
		NPCs.search()
            .withAction("Exchange")
            .nearestToPlayer()
            .ifPresent(x -> NPCInteraction.interact(x, "Exchange"));
	}

	public static boolean isSelling()
	{
		return EthanApiPlugin.getClient()
                             .getVarbitValue(OFFER_VARBIT) != 0 && 
               EthanApiPlugin.getClient()
                             .getVarbitValue(
                              Varbits.GE_OFFER_CREATION_TYPE) != 0;
	}

	public static boolean isBuying()
	{
		return EthanApiPlugin.getClient()
                             .getVarbitValue(OFFER_VARBIT) != 0 && 
               EthanApiPlugin.getClient()
                             .getVarbitValue(
                              Varbits.GE_OFFER_CREATION_TYPE) == 0;
	}

	public static int getItemId()
	{
		return EthanApiPlugin.getClient()
                             .getVarpValue(VarPlayer.CURRENT_GE_ITEM);
	}

	public static void setItem(int id)
	{
		EthanApiPlugin.getClient().runScript(754, id, 84);
	}

	public static int getPrice()
	{
		return EthanApiPlugin.getClient().getVarbitValue(PRICE_VARBIT);
	}

	public static int getQuantity()
	{
		return EthanApiPlugin.getClient().getVarbitValue(QUANTITY_VARBIT);
	}

	public static boolean isFull()
	{
		boolean isMember = EthanApiPlugin.getClient()
                                         .getWorldType()
                                         .contains(WorldType.MEMBERS);
		return getOffers().size() > 
               (isMember ? (P2P_SLOTS - 1) : (F2P_SLOTS - 1));
	}

	public static List<GrandExchangeOffer> getOffers()
	{
		return Arrays.stream(
                      EthanApiPlugin.getClient()
                                    .getGrandExchangeOffers())
                                    .filter(x -> 
            x.getItemId() > 0).collect(Collectors.toList());
	}

	public static void collect()
	{
		Widgets.search()
               .hiddenState(false)
               .withText("Collect")
               .first().ifPresent(x -> 
            widgetAction(1, x.getId(), -1, 0));
	}

	public static void buyItem(int id, int amount, int price)
	{
		if (!isOpen() || isFull()) {
			return;
		}

		if (!isBuying()) {
			Optional<Widget> buyWidget = 
                Widgets.search()
                       .hiddenState(false)
                       .withAction("Create <col=ff9040>Buy</col> offer")
                       .first();

			if (buyWidget.isPresent()) {
				MousePackets.queueClickPacket();
				WidgetPackets.queueWidgetAction(buyWidget.get(),
                                                "Create Buy offer");
				return;
			}

			return;
		}

		if (getItemId() != id) {
			setItem(id);
			return;
		}

		if (getQuantity() != amount) {
			Widgets.search()
                   .hiddenState(false)
                   .withId(
                    WidgetInfo.CHATBOX_FULL_INPUT
                   .getId())
                   .first().ifPresentOrElse(
                x -> enterAmount(amount), 
                () -> widgetAction(1, 30474265, -1, 7));
			return;
		}

		if (getPrice() != price)
		{
			Widgets.search()
                   .hiddenState(false)
                   .withId(
                    WidgetInfo.CHATBOX_FULL_INPUT
                   .getId())
                   .first().ifPresentOrElse(x -> 
                enterAmount(price), () -> widgetAction(1, 30474265, -1, 12));
			return;
		}

		Widgets.search()
               .withAction("Confirm")
               .first().ifPresent(x -> 
            widgetAction(1, x.getId(), -1, -1));
	}



    public static boolean sellItem(String name, int price)
	{
		if (!isOpen()) {
            log.info("GE not opened!");
			return false;
		}

        if (isFull()) {
            log.info("Offers are full! Trying to collect...");
            collectAll();
            if (isFull()) {
                // If still full after collecting offers, hard exit.
                return true;
            }
        }

		//Optional<Widget> itemToSell = GrandExchangeInventory.search().withId(GrandExchangeInventory.search().withId(id).first().isEmpty() ? getNotedId(id) : id).first();

		if (!isSelling()) {
            log.info("Trying to sell an item");
			Optional<Widget> buyWidget = 
                Widgets.search()
                       .hiddenState(false)
                       .withAction("Create <col=ff9040>Sell</col> offer")
                       .first();

			if (buyWidget.isPresent()) {
                log.info("Found buy Widget, creating sell offer");
				MousePackets.queueClickPacket();
				WidgetPackets.queueWidgetAction(buyWidget.get(), 
                                                "Create Sell offer");
				return false; // break-out, continue selling procedure
			}

			//widgetAction(1, getFreeSlot(), -1, 4);
			return false; // break-out, continue selling procedure
		}

        GeInventoryInteraction.offerItem(name);

		//if (getItemId() != id) {
        //    log.info("Item ID matched correctly");
        //    GeInventoryInteraction.offerItem(item.get());
        //    log.info("Item offered!");

        //    //widgetAction(1, WidgetInfo.GRAND_EXCHANGE_INVENTORY_ITEMS_CONTAINER.getId(), x.getItemId(), x.getIndex());
        //    
		//	return false; // break-out, continue selling procedure
		//}

		if (getPrice() != price) {
            log.info("Entering price logic");
			Widgets.search()
                   .hiddenState(false)
                   .withId(WidgetInfo.CHATBOX_FULL_INPUT.getId()).first()
                   .ifPresentOrElse(x -> 
                enterAmount(price), () -> widgetAction(1, 30474265, -1, 12));
			return false; // break-out, continue selling procedure
		}

		widgetAction(1, 30474265, -1, 6);

        log.info("Confirming trade");
		Widgets.search()
               .withAction("Confirm")
               .first().ifPresent(x -> 
            widgetAction(1, x.getId(), -1, -1));

        log.info("Exiting");

        return true;
	}

	private static void enterAmount(int amount)
	{
		EthanApiPlugin.getClient()
                      .setVarcStrValue(VarClientStr.INPUT_TEXT, 
                                       String.valueOf(amount));
		EthanApiPlugin.getClient().runScript(681);
	}


	private static void widgetAction(int actionFieldNo, int widgetId, 
                                     int itemId, int childId)
	{
        log.info("Trying to perform Widget action!");
		MousePackets.queueClickPacket();
		WidgetPackets.queueWidgetActionPacket(actionFieldNo, widgetId, 
                                              itemId, childId);
	}

	private static int getNotedId(int id)
	{
		return EthanApiPlugin.getClient()
                             .getItemDefinition(id)
                             .getLinkedNoteId();
	}

    public static boolean sell(int id, int price) {
		if (!isOpen()) {
            log.info("GE not opened!");
			return false;
		}

        if (isFull()) {
            log.info("Offers are full! Trying to collect...");
            collectAll();
            if (isFull()) {
                // If still full after collecting offers, hard exit.
                return true;
            }
        }

        //log.info("Selling item: " + id);

        //Optional<Widget> itemToSell = GrandExchangeInventory.search().withId(GrandExchangeInventory.search().withId(id).first().isEmpty() ? getNotedId(id) : id).first();

        // Search for item in GE inventory; exit and move next if not found.
        Optional<Widget> item = GrandExchangeInventory.search()
                                                      .withId(id)
                                                      .first();
        if (!item.isPresent()) {
            log.info("Item not found! Searching for noted...");
            item = GrandExchangeInventory.search()
                                         .withId(getNotedId(id))
                                         .first();
            if (!item.isPresent()) {
                log.info("Noted item not found! Exiting...");
                return true;
            } else {
                log.info("Found noted item to sell!");
            }
        } else {
            log.info("Found item to sell!");
        }

        if (!isSelling()) {
            widgetAction(1, getFreeSlot(), -1, 4);
            log.info("Is trying to sell");
            return false;
        }

	    //if (!isSelling()) {
        //      log.info("Trying to sell an item");
	    //	Optional<Widget> buyWidget = 
        //          Widgets.search()
        //                 .hiddenState(false)
        //                 .withAction("Create <col=ff9040>Sell</col> offer")
        //                 .first();
        //      if (buyWidget.isPresent()) 

	    //	if (buyWidget.isPresent()) {
        //          log.info("Found buy Widget, creating sell offer");
	    //		MousePackets.queueClickPacket();
	    //		WidgetPackets.queueWidgetAction(buyWidget.get(), 
        //                                          "Create Sell offer");
	    //		return false; // break-out, continue selling procedure
	    //	}

	    //	//widgetAction(1, getFreeSlot(), -1, 4);
	    //	return false; // break-out, continue selling procedure
	    //}

        if (getItemId() != id) {
            item.ifPresent(x -> widgetAction(1, WidgetInfo.GRAND_EXCHANGE_INVENTORY_ITEMS_CONTAINER.getId(), x.getItemId(), x.getIndex()));
            //log.info("Converting to GE item ID");
            return false;
        }

	    //if (getItemId() != id) {
        //    log.info("Item ID matched correctly");
        //    GeInventoryInteraction.offerItem(item.get());
        //    log.info("Item offered!");

        //    widgetAction(1, WidgetInfo.GRAND_EXCHANGE_INVENTORY_ITEMS_CONTAINER.getId(), x.getItemId(), x.getIndex());
        //    
	    //	return false; // break-out, continue selling procedure
	    //}

        if (getPrice() != price) {
            Widgets.search().hiddenState(false).withId(WidgetInfo.CHATBOX_FULL_INPUT.getId()).first().ifPresentOrElse(x -> enterAmount(price), () -> widgetAction(1, 30474265, -1, 12));
            //log.info("Setting price");
            return false;
        }

	    //if (getPrice() != price) {
        //      log.info("Entering price logic");
	    //	Widgets.search()
        //             .hiddenState(false)
        //             .withId(WidgetInfo.CHATBOX_FULL_INPUT.getId()).first()
        //             .ifPresentOrElse(x -> 
        //          enterAmount(price), () -> widgetAction(1, 30474265, -1, 12));
	    //	return false; // break-out, continue selling procedure
	    //}
          
        //log.info("Reached second to end");
        widgetAction(1, 30474265, -1, 6); // xxx 12 (?)

        return confirm();
        
        //if (confirm()) {
        //    // if warning(), return another confirm(); else, exit
        //    return warning() ? confirm() : true;
        //}

        //if (confirm()) {
        //    log.info("Confirmed! Checking for warning...");
        //    if (warning()) {
        //        log.info("Found warning! Clicking yes...");
        //        return confirm();
        //    }
        //    return true;
        //}

        //return confirm();

        //return false; // something went wrong
    }

    public static boolean confirm()
    {
        if (Widgets.search()
                   .withId(30474269)
                   .withAction("Confirm")
                   .first().isPresent()) {
            log.info("Found \"Confirm\" button!");
            Widgets.search()
                   .withId(30474269)
                   .withAction("Confirm")
                   .first().ifPresent(w -> {
                widgetAction(1, w.getId(), -1, -1);
            });
            return true;
        }
        return false;
    }

    public static boolean warning()
    {
        if (Widgets.search()
                   .withId(18939908)
                   //.withTextContains("Warning!")
                   .first().isPresent()) {
            log.info("Warning for too low of offer is present!");
            Widgets.search()
                   //.withId(18939912)
                   //.withTextContains("<col=0dc10d>Yes</col>")
                   .withAction("Yes")
                   //.hiddenState(false)
                   .first().ifPresent(w -> {
                log.info("Found \"Yes\" Widget, confirming...");
                //invoke is param0,param1,menuentryOpcode,identifier,itemid,"option","target",canvasX,canvasY
                EthanApiPlugin.invoke(-1, 18939912, MenuAction.CC_OP.getId(), 1, -1, "", "", -1, -1);

            });
            return true;
        }
        return false;
    }

    private static int getFreeSlot()
    {
        return Widgets.search()
                      .hiddenState(false)
                      .filter(x -> 
            OFFER_SLOTS.contains(x.getId()) && x.getChild(3) != null && 
                                 !x.getChild(3)
                                   .isHidden())
                                   .first().map(Widget::getId)
                                   .orElse(-1);
    }

    public static List<GrandExchangeOffer> getCompletedOffers() {
        return Arrays.stream(EthanApiPlugin.getClient().getGrandExchangeOffers()).filter(x -> x.getItemId() > 0 && COMPLETED_OFFER_STATES.contains(x.getState())).collect(Collectors.toList());
    }

    public static void collectAll() {
        Widgets.search().hiddenState(false).withText("Collect").first().ifPresent(x -> widgetAction(1, x.getId(), -1, 0));
    }

    private static final List<Integer> OFFER_SLOTS = List.of(30474247, 30474248, 30474249);
    // xxx members
    //private static final List<Integer> OFFER_SLOTS = List.of(30474247, 30474248, 30474249, 30474250, 304744251, 30474252, 304744253, 304742454);
    private static final List<GrandExchangeOfferState> COMPLETED_OFFER_STATES = List.of(GrandExchangeOfferState.BOUGHT, GrandExchangeOfferState.SOLD);

    public static boolean buy(int id, int amount, int price) {
        if (!isOpen()) {
            return false;
        }

        if (isFull()) {
            log.info("Offers are full! Trying to collect...");
            collectAll();
            if (isFull()) {
                // If still full after collecting offers, hard exit.
                return true;
            }
        }

        if (!isBuying()) {
            widgetAction(1, getFreeSlot(), -1, 3);
            return false;
        }

        if (getItemId() != id) {
            setItem(id);
            return false;
        }

        if (getQuantity() != amount) {
            Widgets.search()
                   .hiddenState(false)
                   .withId(WidgetInfo.CHATBOX_FULL_INPUT.getId())
                   .first().ifPresentOrElse(
                x -> enterAmount(amount), 
                () -> widgetAction(1, 30474265, -1, 7));
            return false;
        }

        // xxx hard exit (return true) if not enough coins Widget
        if (getPrice() != price) {
            Widgets.search()
                   .hiddenState(false)
                   .withId(WidgetInfo.CHATBOX_FULL_INPUT.getId()).first()
                   .ifPresentOrElse(
                x -> enterAmount(price), 
                () -> widgetAction(1, 30474265, -1, 12));
            return false;
        }

        return confirm();

        // xxx price too high Widget
    }
}
