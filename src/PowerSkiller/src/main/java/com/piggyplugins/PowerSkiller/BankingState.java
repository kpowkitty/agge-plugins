public class BankingState implements State {
    public BankingState(StateStack stack, Context ctx) 
    {
        this.stack = stack;
        this.ctx = ctx;
    }

    @Override
    public boolean run() 
    {
        log.info("Entering BANK State...");

        // xxx deal with bank pin
        //
        if (canDeposit()) {
            // Bank Widget up, can deposit.
            List<Widget> items = BankInventory.search().result();
                for (Widget item : items) {
                    if (!Util.isTool(item.getName().toLowerCase()) && 
                        !shouldKeep(item.getName().toLowerCase())) {
                            BankInventoryInteraction.useItem(
                                item, "Deposit-All");
                    }
                }
        } else if (!canBank()) {
            requestStatePush(PATH);
        } else if (!Inventory.full()) {
            requestStatePop();
        }
        else {
            // do nothing, maybe timeout and state pop to correct
        }

        return false;
    }

    @Override
    public boolean handleEvent()
    {
        // Implement event handling logic
    }

    private boolean canBank()
    {
        AtomicBoolean found = new AtomicBoolean(false);
        TileObjects.search()
                   .withAction("Bank")
                   .nearestToPlayer()
                   .ifPresent(tileObject -> {
                found.set(true);
                TileObjectInteraction.interact(tileObject, "Bank");
        });
        TileObjects.search()
                   .withName("Bank chest")
                   .nearestToPlayer()
                   .ifPresent(tileObject -> {
                found.set(true);
                TileObjectInteraction.interact(tileObject, "Use");
        });
        NPCs.search()
            .withAction("Bank")
            .nearestToPlayer()
            .ifPresent(npc -> {
                found.set(true);
                NPCInteraction.interact(npc, "Bank");
        }); 
        return found.get();
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
    }
    
    private boolean dontBank()
    {
        return !ctx.config.setBank().equals("");
    }
    
    private boolean cantBank()
    {
        return TileObjects.search()
                          .withAction("Bank")
                          .nearestToPlayer()
                          .isEmpty() && 
               NPCs.search()
                   .withAction("Bank")
                   .nearestToPlayer()
                   .isEmpty(); 
    }
}
