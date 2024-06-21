public class Util {
    @Inject
    PowerSkillerPlugin plugin;
    @Inject
    PowerSkillerConfig config;

    public static boolean isInventoryReset()
    {
        List<Widget> inventory = Inventory.search().result();
        for (Widget item : inventory) {
            if (!shouldKeep(Text.removeTags(item.getName()))) { // using our shouldKeep method, we can filter the items here to only include the ones we want to drop.
                return false;
            }
        }
        return true; // we will know that the inventory is reset because the inventory only contains items we want to keep
    }

    public static boolean shouldKeep(String name) 
    {
        List<String> itemsToKeep = new ArrayList<>(List.of(config.itemsToKeep().split(","))); // split the items listed by comma. and add them to a list.
        itemsToKeep.addAll(List.of(config.toolsToUse().split(","))); //We must also check if the tools are included in the Inventory, Rather than equipped, so they are added here
        return itemsToKeep.stream()// stream the List using Collection.stream() from java.util
                .anyMatch(i -> Text.removeTags(name.toLowerCase()).contains(i.toLowerCase()));
        // we'll set everything to lowercase as well as remove the html tags that is included (The color of the item in game),
        // and check if the input name contains any of the items in the itemsToKeep list.
        // might seem silly, but this is to allow specific items you want to keep without typing the full name.
        // We also prefer names to ids here, but you can change this if you like.
    }

    public static boolean hasTools() 
    {
        //Updated from https://github.com/moneyprinterbrrr/ImpactPlugins/blob/experimental/src/main/java/com/impact/PowerGather/PowerGatherPlugin.java#L196
        //Big thanks hawkkkkkk
        String[] tools = config.toolsToUse().split(","); // split the tools listed by comma, no space.

        int numInventoryTools = Inventory.search()
                .filter(item -> isTool(item.getName())) // filter inventory by using out isTool method
                .result().size();
        int numEquippedTools = Equipment.search()
                .filter(item -> isTool(item.getName())) // filter inventory by using out isTool method
                .result().size();

        return numInventoryTools + numEquippedTools >= tools.length; // if the size of tools and the filtered inventory is the same, we have our tools.
    }

    public static void setTimeout()
    {
        timeout = RandomUtils.nextInt(config.tickdelayMin(), config.tickDelayMax());
    }
}
