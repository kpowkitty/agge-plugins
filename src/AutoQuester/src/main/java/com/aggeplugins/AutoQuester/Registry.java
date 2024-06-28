/**
 * @file Registry.java
 * @class Registry
 * Master instruction registry. Instructions go here!
 * The purpose of the registry is: (1) have a localized registry of
 * instructions, (2) have simple-call wrapper functions that shouldn't polute
 * the global namespace, (3) allow conditional call of registered instruction
 * sets in the global namespace.
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-16
 *
 */

package com.aggeplugins.AutoQuester;

import com.aggeplugins.AutoQuester.*;
import com.aggeplugins.lib.*;
import com.aggeplugins.MessageBus.*;
import com.aggeplugins.Skiller.*;

import com.piggyplugins.PiggyUtils.API.*;
import com.piggyplugins.PiggyUtils.*;
import com.example.Packets.*;
import com.example.EthanApiPlugin.*;
import com.example.InteractionApi.*;
import com.example.PacketUtils.*;
import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.Collections.query.*;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.Client;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.client.config.*;

import com.example.Packets.*;
import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.Optional;

@Slf4j
public class Registry {
    /**
     * Instantiate Plugin Context for the Registry.
     * Initialize new Random seed each Registry instance.
     */
    public Registry(AutoQuesterContext ctx)
    {
        log.info("Constructing Registry!");

        this.ctx = ctx;

        // Instance context.
        _cfg = ctx.cfg;
        this.instructions = ctx.instructions;
        this.pathing = new Pathing(this.ctx);
        this.pathing.setType(Pathing.Type.SHORTEST_PATH);

        this.rand = new Random();
        // Random between 1 and 3 (inclusive).
        minWait = 1 + this.rand.nextInt(3);
        // Random between 2 and 4 (inclusive).
        shortCont = 2 + this.rand.nextInt(3);
        // Random between 3 and 6 (inclusive).
        medCont = 3 + this.rand.nextInt(4);
        // Random between 7 and 12 (inclusive).
        longCont = 7 + this.rand.nextInt(6);
        // Random between 10 and 15 (inclusive).
        shortWait = 10 + this.rand.nextInt(6);
        // Random between 20 and 35 (inclusive).
        medWait = 20 + this.rand.nextInt(16);
        // Random between 50 and 120 (inclusive).
        longWait = 50 + this.rand.nextInt(71);
    }

    //private Map<InstructionID, Pair<BooleanSupplier, Instruction> instructionMap 
    //    = new HashMap<>(
    //        InstructionID.15_MINING_TIN,
    //    new Pair<>(
    //        miningInstruction(),
    //    new Instruction(
    //        Mining,
    //        new WorldPoint(3172, 3364, 0),
    //        BankLocation.VARROCK_WEST1)
    //    ))

    //private enum InstructionID {
    //    15_MINING_TIN,
    //    20_ATTACK_CHICKENS,
    //    10_STRENGTH_CHICKENS,
    //    60_COMBAT_FLESH_CRAWLERS;
    //}



    // Register all the instructions, these will return TRUE when they should
    // be removed. Then move on to the next instruction.
    
    public void testInstructions() 
    {
        //messageBus.send(new Message<MessageID, Instruction>(
        //    MessageID.REQUEST_SKILLING, 

        //SkillerConfig conf = 
        //    ctx.plugin.configManager.getConfig(SkillerPlugin.class);
        //ConfigDescriptor descriptor =
        //    ctx.plugin.configManager.getConfigDescriptor(conf);
        //ctx.plugin.configManager.setConfiguration(
        //    descriptor, "setBank", "VARROCK_EAST");
        ctx.plugin.configManager.setConfiguration("Skiller", "setBank", "VARROCK_EAST");
    }

    //private void setConfig(String group, String key, T val)
    //{
    //    ctx.plugin.configManager.setConfiguration(group, key, val);
    //}

    //public void miningInstruction(Instruction instr)
    //{
    //    ctx.plugin.configManager.setConfiguration(
    //        "Skiller", "setBank", instr.getBank().toString());
    //    ctx.plugin.configManager.setConfiguration(
    //        "Skiller", "skillingX", instr.getGoal().getX());
    //    ctx.plugin.configManager.setConfiguration(
    //        "Skiller", "skillingY", instr.getGoal().getY());
    //    ctx.plugin.configManager.setConfiguration(
    //        "Skiller", "skillingZ", instr.getGoal().getPlane());
    //    ctx.plugin.configManager.setConfiguration(
    //        "Skiller", "skillingX", instr.getGoal.getX());
    //    ctx.plugin.configManager.setConfiguration(
    //        "Skiller", "expectedAction", instr.getTopic());

    //    ctx.plugin.configManager.setConfiguration(
    //        "Skiller", "expectedAction", instr.getSupplies());


    //}











    //public void tutorialIsland()
    //{
        //10747973 // normal dialogue        

        //block(longWait);

        //interact("Fishing spot", "Net", TILE_OBJECT);
        //block(shortWait);

        //// xxx widget
        //block(longWait); 
        //
        //block(medCont);

        //talk("Survival Expert");
        //cshort();

        //interact("Tree", "Chop down", TILE_OBJECT);
        //block(shortWait);

        //interact("Logs", "Use", INVENTORY);
        //interact("Tinderbox", "Use", INVENTORY);
        //block(shortWait);

        //interact("Raw shrimps", "Use", INVENTORY);
        //interact("Fire", "Use", TILE_OBJECT);
        //block(shortWait);

        //path(3090, 3092);
        //interact("Gate", "Open", TILE_OBJECT);
        //block(shortCont);

        //path(3079, 3084);
        //interact("Door", "Open", TILE_OBJECT);
        //block(shortWait);

        //talk("Master Chef");
        //cshort();

        //interact("Pot of flour", "Use", INVENTORY);
        //interact("Bucket of water", "Use", INVENTORY);
        //interact("Bread dough", "Use", INVENTORY);
        //interact("Range", "Use", TILE_OBJECT);
        //block(shortWait);

        //path(3073, 3090);
        //block(shortCont);

        //path(3086, 3124);
        //interact("Door", "Open", TILE_OBJECT);
        //block(shortCont);
        //talk("Quest Guide");
        //cont();

        //// widget
        //block(longWait);
        //
        //talk("Quest Guide");
        //cmed();
        //interact("Ladder", "Climb-down", TILE_OBJECT);
        //block(shortWait);

        //talk("Mining Instructor");
        //cmed();

        //interact("Tin rocks", "Mine", TILE_OBJECT);
        //block(medWait);
        //interact("Copper rocks", "Mine", TILE_OBJECT);
        //block(medWait);

        //interact("Furnace", "Use", TILE_OBJECT);
        //block(medWait);

        //talk("Mining Instruction");
        //cshort();

        //// widget
        //block(longWait);
        //
        //path(3093, 9502);
        //interact("Gate", "Open", TILE_OBJECT);
        //talk("Combat Instructor");
        //cshort();
        //
        //// widget
        //block(longWait);
        //
        //interact("Bronze dagger", "Wield", INVENTORY);
        //talk("Combat Instructor");
        //cshort();
        //interact("Bronze sword", "Wield", INVENTORY);
        //interact("Wooden shield", "Wield", INVENTORY);

        //// widget
        //block(longWait);
        //
        //path(3111, 9518);
        //interact("Gate", "Open", TILE_OBJECT);

        //interact("Giant rat", "Attack", NPC);
        //block(medWait);
        //path(3110, 9518);
        //interact("Gate", "Open", TILE_OBJECT);
        //block(medCont);
        //talk("Combat Instructor");
        //cshort();

        //// widget
        //block(longWait);
        //
        //interact("Shortbow", "Wield", INVENTORY);
        //interact("Bronze arrow", "Wield", INVENTORY);
        //interact("Giant rat", "Attack", NPC);

        //path(3111, 9525);
        //interact("Ladder", "Climb-up", TILE_OBJECT);
        //block(shortCont);
        //block(shortWait);

        //path(3122, 3123);
        //interact("Bank both", "Use", TILE_OBJECT);
        //block(shortCont);

        //// widget
        //block(longWait);

        //path(3120, 3121);
        //interact("Poll both", "Use", TILE_OBJECT);
        //block(shortCont);
        //cshort();

        //// widget
        //block(longWait);

        //path(3124, 3124);
        //interact("Door", "Open", TILE_OBJECT);
        //block(shortCont);
        //talk("Account Guide");
        //cshort();

        //// widget
        //block(longWait);

        //talk("Account Guide");
        //clong();
        //path(3129, 3124);
        //interact("Door", "Open", TILE_OBJECT);
        //block(shortCont);

        //path(3125, 3107);
        //talk("Brother Brace");
        //cshort();

        //// widget
        //block(longWait);

        //talk("Brother Brace");
        //cshort();

        //// widget
        //block(longWait);


        //talk("Brother Brace");
        //cshort();

        //path(3122, 3103);
        //interact("Door", "Use", TILE_OBJECT);
        //block(shortCont);

        //path(3141, 3088);
        //talk("Magic Instructor");
        //cshort();

        //// widget
        //block(longWait);

        //talk("Magic Instructor");

        //// widget
        //block(longWait);

        //interact("Chicken", "Cast", NPC);
        //block(medCont);
        //talk("Magic Instructor");
        //cont();
        //dialogue("Yes", 1);
        //cont();

        //// random
        //// num = need to parse for num (regex?)
        //int num = 3;
        //dialogue("No, I'm not planning to do that.", num);
        //clong();
        //block(medWait);
        //cont();
    //}

    public void xMarksTheSpot()
    {
        if (!_cfg.get("Started X Marks the Spot")) {
            log.info("Already started X Marks the Spot!");

            // Starting the quest.
            path(3228, 3242);

            // Full Veos dialogue.
            talk("Veos");
            cshort();
            dialogue("I'm looking for a quest.", 2);
            cmed();
            dialogue("Yes", 1);
            block(medCont);
            cmed();
            dialogue("Okay, thanks Veos.", 1);
            cmed();
        }

        // Shop keeper
        path(3112, 3246);
        trade("Shop keeper");

        // Using class Action and class XXXInteraction interchangably, whatever
        // makes the most sense.
        register(() -> ShopInteraction.buyOne("Spade"), null);

        // Dig 1
        path(3230, 3209);
        interact("Spade", "Dig", INVENTORY);
        // Dig 2
        path(3203, 3212);
        interact("Spade", "Dig", INVENTORY);
        // Dig 3
        path(3109, 3264);
        interact("Spade", "Dig", INVENTORY);

        // Dig 4
        // xxx or just hard force the gate? if it's already open, will break!
        path(3077, 3257);
        path(3078, 3261);

        path(3078, 3259);
        interact("Spade", "Dig", INVENTORY);

        // Final Veos
        path(3054, 3245);
        talk("Veos");
        clong();
        // xxx deal with different continue

        // 2 here, to make sure
        clong();
        // Start pathing
        path(3054, 3245);
        // Will get caught in another dialogue
        clong();
    }

    public void sheepShearer()
    {
        // xxx handle if it's started already or not
        if (!_cfg.get("Started Sheep Shearer")) {
            log.info("Already started Sheep Shearer!");
        }

        // Fred the Farmer, pickup shears
        path(3191, 3272);
        interact("Shears", TAKE, TILE_ITEM);

        // Go to sheep pen
        path(3196, 3277);
        interact(12982, "Climb-over", TILE_OBJECT); // stile
        register(() -> !Action.isInteractingTO(ctx.client));

        // Collect 20 wool.
        for (int i = 0; i < 3; i++) {
            interact(NpcID.SHEEP_2786, "Shear", NPC);   // 1
            register(() -> !Action.isInteractingNPC(ctx.client));
            //block(longCont);
            interact(NpcID.SHEEP_2699, "Shear", NPC);   // 2
            register(() -> !Action.isInteractingNPC(ctx.client));
            //block(longCont);
            interact(NpcID.SHEEP_2787, "Shear", NPC);   // 3
            register(() -> !Action.isInteractingNPC(ctx.client));
            //block(longCont);
            interact(NpcID.SHEEP_2693, "Shear", NPC);   // 4
            register(() -> !Action.isInteractingNPC(ctx.client));
            //block(longCont);
            interact(NpcID.SHEEP_2694, "Shear", NPC);   // 5
            register(() -> !Action.isInteractingNPC(ctx.client));
            //block(longCont);
            interact(NpcID.SHEEP_2699, "Shear", NPC);   // 6
            register(() -> !Action.isInteractingNPC(ctx.client));
            //block(longCont);
            interact(NpcID.SHEEP_2695, "Shear", NPC);   // 7
            register(() -> !Action.isInteractingNPC(ctx.client));
            //block(medWait);
        }                                               // = 21
        
        pathDoor(3212, 3262);

        // Go to Lumbridge Castle staircase
        //path(new WorldPoint(3206, 3208, 0));
        //interact(ObjectID.STAIRCASE_16671, "Climb-up", TILE_OBJECT);
        //block(longCont);

        pathTO(3206, 3208, ObjectID.STAIRCASE_16671, "Climb-up");
        pathTO(3209, 3213, 1, "Spinning wheel", "Spin");
        block(minWait);
        register(() -> Action.pressSpace(ctx.client));
        register(() -> !Action.isInteractingTO(ctx.client)); // xxx might work
        block(shortWait); // or Action.isInteractingTO(ctx.client);

        //path(new WorldPoint(3209, 3213, 1));
        //interact("Spinning wheel", "Spin", TILE_OBJECT);

        //register(() -> Action.pressSpace(ctx.client), null);
        //block(medWait);

        // Climb-down stairs
        pathTO(3206, 3214, 1, "Staircase", "Climb-down");
        //path(new WorldPoint(3206, 3214, 1));
        //path(new WorldPoint(3205, 3209, 1));
        //interact("Staircase", "Climb-down", TILE_OBJECT);

        // Go back to Fred
        path(3190, 3273);
        talk("Fred the Farmer");

        //cont();
        cshort();
        dialogue("I'm looking for a quest.", 1);
        clong();
        cmed();
        dialogue("Yes.", 1);
        clong();
        clong();
    }

    public void cooksAssistant()
    {
        // Not a good way to avoid pathing here, whether it's started or not...
        path(new WorldPoint(3208, 3216, 0));

        if (!_cfg.get("Started Cook's Assistant")) {
            log.info("Already started Cook's Assistant!");

            talk("Cook");
            register(() -> Action.continueDialogue(), null);
            register(() -> Action.selectDialogue(
                "You don't look very happy.", 3), null);
            register(() -> Action.continueDialogue(), shortCont);
            register(() -> Action.selectDialogue(
                "What's wrong?", 1), null);
            register(() -> Action.continueDialogue(), medCont);
            dialogue("Yes", 1);
            cshort();
            dialogue("Actually, I know where to find this stuff", 4);
            cont();
        }

        interact("Pot", TAKE, TILE_ITEM);
        block(medCont);

        // Trapdoor ID = 14880
        interact(14880, "Climb-down", TILE_OBJECT);
        block(longCont);
        register(() -> Action.interactTileItem(
            "Bucket", Integer.valueOf(TAKE)), null);
        register(() -> Action.block(shortWait), null);
        interact(17385, "Climb-up", TILE_OBJECT);
        block(medCont);

        path(3252, 3266);
        path(3254, 3271);
        interact(ObjectID.DAIRY_COW, "Milk", TILE_OBJECT);
        block(longCont);
        path(3163, 3288);
        path(3162, 3292);
        interact("Wheat", "Pick", TILE_OBJECT);
        block(shortWait);
        path(3164, 3306);
        interact(12964, "Climb-up", TILE_OBJECT);
        block(medCont);

        interact(12965, "Climb-up", TILE_OBJECT);
        block(medCont);
        interact(ObjectID.HOPPER_24961, "Fill", TILE_OBJECT);
        block(medCont);
        interact(ObjectID.HOPPER_CONTROLS_24964, "Operate", TILE_OBJECT);
        block(shortWait);
        interact(12966, "Climb-down", TILE_OBJECT);
        block(medCont);
        interact(12965, "Climb-down", TILE_OBJECT);
        block(medCont);
        interact(1781, "Empty", TILE_OBJECT);
        block(medCont);

        // xxx to maybe guarantee door? can break!!
        //path(3167, 3303);
        //interact(1524, "Open", TILE_OBJECT);

        path(3186, 3278);
        interact("Egg", TAKE, TILE_ITEM);
        block(medCont);
        path(new WorldPoint(3208, 3216, 0));
        talk("Cook");
        clong();
    }

    public void runeMysteries()
    {
        if (!_cfg.get("Started Rune Mysteries")) {
            log.info("Already started Rune Mysteries!");

            // not started
            path(3205, 3209);
            interact(16671, "Climb-up", TILE_OBJECT);
            block(shortCont);
            path(3210, 3224, 1);
            talk("Duke Horacio");
            cont();
            dialogue("Have you any quests for me?", 1);
            clong();
            dialogue("Yes", 1);
            cmed();
            path(3205, 3209, 1);
            interact(16672, "Climb-down", TILE_OBJECT);
            block(shortCont);
        }

        // Going to Wizard's Tower.
        path(3105, 3162); // xxx there's probably a better wp
        interact(2147, "Climb-down", TILE_OBJECT);
        block(medCont); // xxx too long?
        path(3109, 9570, 0); // xxx better wp
        path(3103, 9571, 0); // xxx better wp
        // @note from here on out: "wp" = "xxx better wp"
        talk("Archmage Sedridor");
        cmed();
        dialogue("Okay, here you are.", 1);
        clong();
        cmed();
        dialogue("Go ahead.", 1);
        clong();
        cmed(); // xxx is this needed? LONG dialogue
        dialogue("Yes, certainly.", 1);
        cmed();
        path(3109, 9570, 0);
        path(3104, 9587, 0);
        interact(2148, "Climb-up", TILE_OBJECT);
        block(medCont); // xxx too long?
        path(3108, 3163); // wp

        // Going to Varrock.
        // xxx broken, different path!
        path(3252, 3402);
        talk("Aubury");
        cont();
        dialogue("I've been sent here with a package for you.", 2);
        clong();
        cmed(); // xxx

        // Back to Wizard's Tower.
        path(3103, 3162); // xxx there's probably a better wp
        interact(2147, "Climb-down", TILE_OBJECT);
        block(medCont); // xxx too long?
        path(3109, 9570, 0); // xxx better wp
        path(3103, 9571, 0); // xxx better wp
        // @note from here on out: "wp" = "xxx better wp"
        talk("Archmage Sedridor");
        clong();

        // Leave Wizard's Tower.
        path(3109, 9570, 0);
        path(3104, 9587, 0);
        interact(2148, "Climb-up", TILE_OBJECT);
        block(medCont); // xxx too long?
        path(3108, 3163); // wp
    }

    public void romeoAndJuliet()
    {
        if (!_cfg.get("Started Romeo and Juliet")) {
            log.info("Already started Rune Mysteries!");

            // not started
            path(3213, 3428);
            talk("Romeo");
            cont();
            dialogue("Yes, I have seen her actually!", 1);
            clong();
            cmed();
            dialogue("Yes.", 1);
            cshort();
            dialogue("Ok, thanks.", 3);
            cont();
        }

        // To Juliet.
        //path(3159, 3436);
        //interact(11797, "Climb-up", TILE_OBJECT);
        //block(shortCont);
        //path(3158, 3425, 1);
        //talk("Juliet");
        //clong();
        //path(3157, 3429, 1); // wp
        //path(3155, 3436, 1);
        //interact(11799, "Climb-down", TILE_OBJECT);
        //block(shortCont);

        //// Back to Romeo.
        //path(3213, 3428);
        //talk("Romeo");
        //clong();
        //clong();
        //dialogue("Ok, thanks.", 4);

        //// To Father Lawrence.
        //path(3255, 3482);
        //talk("Father Lawrence");
        //cmed();
        //block(medWait); // cutscene
        //clong();
        //clong();
        //clong(); // xxx may be too long? but it made it work lol

        //// varrock east mine 3x iron: (3286, 3388, 0)

        //// Cadava berries.
        //path(3270, 3370);

        //// bush1 = 23635, bush2 = 23625, bush3 = 33183
        //// random select
        //int[] a = {23635, 23625, 33183};
        //interact(rand(a), "Pick-from", TILE_OBJECT);
        //block(shortWait);

        path(3195, 3404);
        talk("Apothecary");
        cont();
        dialogue("Talk about something else.", 2);
        dialogue("Talk about Romeo & Juliet.", 1);
        cmed();
        block(shortWait); // animation
        register(() -> Action.pressSpace(ctx.client), null);
        clong();
        // Final chat is him giving you potion, is this needed v ? check!
        block(shortWait);
        register(() -> Action.pressSpace(ctx.client), null);

        // To Juliet.
        path(3159, 3436);
        interact(11797, "Climb-up", TILE_OBJECT);
        block(shortCont);
        path(3158, 3425, 1);
        talk("Juliet");
        clong();
        block(shortWait); // cutscene
        cmed();
        block(medCont); // xxx needed?
        cont();
        block(medCont); // xxx needed?
        cmed();
        block(longCont); // animation
        cshort();
        block(longCont); // animation

        // Leave Juliet.
        path(3157, 3429, 1); // wp
        path(3155, 3436, 1);
        interact(11799, "Climb-down", TILE_OBJECT);
        block(shortCont);

        // To Romeo.
        path(3213, 3428);
        talk("Romeo");
        cmed();
        block(medCont); // cutscene
        cshort();
        block(longCont); // cutscene
        cont();
        block(shortWait); // cutscene
        cshort();
        block(shortWait); // cutscene
        cmed();
        block(shortWait); // finishing cutscene
    }

    public void theRestlessGhost()
    {
        if (!_cfg.get("Started The Restless Ghost")) {
            log.info("Already started The Restless Ghost!");
            // not started
            path(3243, 3208);
            talk("Father Aereck");
            cont();
            dialogue("I'm looking for a quest!", 3);
            cshort();
            dialogue("Yes", 1);
            clong();
        }

        // Go to Father Urhney
        path(3145, 3175);
        talk("Father Urhney");
        cont();
        dialogue("Father Aereck sent me to talk to you.", 2);
        cshort();
        dialogue("He's got a ghost haunting his graveyard.", 1);
        clong();
        interact("Ghostspeak amulet", "Wear", INVENTORY); // xxx might break

        // To Wizard's Tower.
        path(3103, 3162); // xxx there's probably a better wp
        interact(2147, "Climb-down", TILE_OBJECT);
        block(medCont); // xxx too long?

        // Go to coffin.
        path(3248, 3193);
        interact(2145, "Open", TILE_OBJECT);
        block(shortWait);
        talk("Restless ghost");
        cmed();
        dialogue("Yep, now tell me what the problem is.", 1);
        clong();

        // Go to Skeleton.
        path(3107, 9558, 0);
        path(3114, 9561, 0);

        // xxx could also just interact and block, but want to minimize damage
        path(3120, 9565, 0);
        interact(2146, "Search", TILE_OBJECT);
        block(minWait);
        path(3114, 9561, 0);
        path(3107, 9558, 0);

        path(3104, 9576, 0);
        interact(2148, "Climb-up", TILE_OBJECT);
        block(medCont); // xxx too long?
        path(3108, 3163); // wp

        // Go to coffin.
        path(3248, 3193);
        interact(2145, "Open", TILE_OBJECT);
        block(longCont);
        // xxx might have to do different way
        interact("Ghost's skull", "Use", INVENTORY);
        interact(2145, "Use", TILE_OBJECT);

        block(medWait); // cutscene
    }

    public void tutorialIsland()
    {
        talk("Gielinor Guide");
        interact("Door", "Open", TILE_OBJECT);
        block(longCont);

        talk("Survial Expect");
        block(medWait);
        cshort();

        // widget
        block(longWait);

        interact("Fishing spot", "Net", TILE_OBJECT);
        block(shortWait);

        // xxx widget
        block(longWait);

        block(medCont);

        talk("Survival Expert");
        cshort();

        interact("Tree", "Chop down", TILE_OBJECT);
        block(shortWait);

        interact("Logs", "Use", INVENTORY);
        interact("Tinderbox", "Use", INVENTORY);
        block(shortWait);

        interact("Raw shrimps", "Use", INVENTORY);
        interact("Fire", "Use", TILE_OBJECT);
        block(shortWait);

        path(3090, 3092);
        interact("Gate", "Open", TILE_OBJECT);
        block(shortCont);

        path(3079, 3084);
        interact("Door", "Open", TILE_OBJECT);
        block(shortWait);

        talk("Master Chef");
        cshort();

        interact("Pot of flour", "Use", INVENTORY);
        interact("Bucket of water", "Use", INVENTORY);
        interact("Bread dough", "Use", INVENTORY);
        interact("Range", "Use", TILE_OBJECT);
        block(shortWait);

        path(3073, 3090);
        block(shortCont);

        path(3086, 3124);
        interact("Door", "Open", TILE_OBJECT);
        block(shortCont);
        talk("Quest Guide");
        cont();

        // widget
        block(longWait);

        talk("Quest Guide");
        cmed();
        interact("Ladder", "Climb-down", TILE_OBJECT);
        block(shortWait);

        talk("Mining Instructor");
        cmed();

        interact("Tin rocks", "Mine", TILE_OBJECT);
        block(medWait);
        interact("Copper rocks", "Mine", TILE_OBJECT);
        block(medWait);

        interact("Furnace", "Use", TILE_OBJECT);
        block(medWait);

        talk("Mining Instruction");
        cshort();

        // widget
        block(longWait);

        path(3093, 9502);
        interact("Gate", "Open", TILE_OBJECT);
        talk("Combat Instructor");
        cshort();

        // widget
        block(longWait);

        interact("Bronze dagger", "Wield", INVENTORY);
        talk("Combat Instructor");
        cshort();
        interact("Bronze sword", "Wield", INVENTORY);
        interact("Wooden shield", "Wield", INVENTORY);

        // widget
        block(longWait);

        path(3111, 9518);
        interact("Gate", "Open", TILE_OBJECT);

        interact("Giant rat", "Attack", NPC);
        block(medWait);
        path(3110, 9518);
        interact("Gate", "Open", TILE_OBJECT);
        block(medCont);
        talk("Combat Instructor");
        cshort();

        // widget
        block(longWait);

        interact("Shortbow", "Wield", INVENTORY);
        interact("Bronze arrow", "Wield", INVENTORY);
        interact("Giant rat", "Attack", NPC);

        path(3111, 9525);
        interact("Ladder", "Climb-up", TILE_OBJECT);
        block(shortCont);
        block(shortWait);

        path(3122, 3123);
        interact("Bank both", "Use", TILE_OBJECT);
        block(shortCont);

        // widget
        block(longWait);

        path(3120, 3121);
        interact("Poll both", "Use", TILE_OBJECT);
        block(shortCont);
        cshort();

        // widget
        block(longWait);

        path(3124, 3124);
        interact("Door", "Open", TILE_OBJECT);
        block(shortCont);
        talk("Account Guide");
        cshort();

        // widget
        block(longWait);

        talk("Account Guide");
        clong();
        path(3129, 3124);
        interact("Door", "Open", TILE_OBJECT);
        block(shortCont);

        path(3125, 3107);
        talk("Brother Brace");
        cshort();

        // widget
        block(longWait);

        talk("Brother Brace");
        cshort();

        // widget
        block(longWait);


        talk("Brother Brace");
        cshort();

        path(3122, 3103);
        interact("Door", "Use", TILE_OBJECT);
        block(shortCont);

        path(3141, 3088);
        talk("Magic Instructor");
        cshort();

        // widget
        block(longWait);

        talk("Magic Instructor");

        // widget
        block(longWait);

        interact("Chicken", "Cast", NPC);
        block(medCont);
        talk("Magic Instructor");
        cont();
        dialogue("Yes", 1);
        cont();

        // random
        // num = need to parse for num (regex?)
        int num = 3;
        dialogue("No, I'm not planning to do that.", num);
        clong();
        block(medWait);
        cont();
    }

    /**
     * Utility functions specific to the registry.
     */
    // Local macros to register common instructions less verbosely.
    private void register(BooleanSupplier f, Integer n)
    {
        if (n == null)
            instructions.register(f, "Undefined instruction", Optional.empty());
        else
            instructions.register(f, "Undefined instruction", Optional.of(n));
    }

    private void register(BooleanSupplier f)
    {
        instructions.register(f, "Undefined instruction");
    }

    private void path(WorldPoint wp)
    {
        instructions.register(() -> pathing.setType(Pathing.Type.SHORTEST_PATH), 
            "Setting path type");
        instructions.register(() -> pathing.setGoal(wp), "Path to: " + wp);
        instructions.register(() -> pathing.setPath(), "Setting path");
        instructions.register(() -> !pathing.calculatingPath(), 
            "Calculating path...");
        instructions.register(() -> !pathing.run(), "Pathing to: " + wp);
    }

    private void path(int x, int y)
    {
        WorldPoint wp = new WorldPoint(x, y, 0);
        instructions.register(() -> pathing.setType(Pathing.Type.SHORTEST_PATH), 
            "Setting path type");
        instructions.register(() -> pathing.setGoal(wp), "Path to: " + wp);
        instructions.register(() -> pathing.setPath(), "Setting path");
        instructions.register(() -> !pathing.calculatingPath(), 
            "Calculating path...");
        instructions.register(() -> !pathing.run(), "Pathing to: " + wp);
    }

    private void path(int x, int y, int z)
    {
        WorldPoint wp = new WorldPoint(x, y, z);
        instructions.register(() -> pathing.setType(Pathing.Type.SHORTEST_PATH), 
            "Setting path type");
        instructions.register(() -> pathing.setGoal(wp), "Path to: " + wp);
        instructions.register(() -> pathing.setPath(), "Setting path");
        instructions.register(() -> !pathing.calculatingPath(), 
            "Calculating path...");
        instructions.register(() -> !pathing.run(), "Pathing to: " + wp);
    }

    private void pathDoor(int x, int y)
    {
        path(x, y);
        if (canOpenDoor(1)) {
            interact("Door", "Open", TILE_OBJECT);
            instructions.register(() -> !Action.isInteractingTO(ctx.client),
                                  "Opening door");
        }
        else if (canOpenGate(1)) {
            interact("Gate", "Open", TILE_OBJECT);
            instructions.register(() -> !Action.isInteractingTO(ctx.client),
                                        "Opening gate");
        }
    }

    private void openDoor()
    {
        if (canOpenDoor(MAX_DISTANCE)) {
            interact("Door", "Open", TILE_OBJECT);
            instructions.register(() -> !Action.isInteractingTO(ctx.client),
                                        "Opening door");
        } else if (canOpenGate(MAX_DISTANCE)) {
            interact("Gate", "Open", TILE_OBJECT);
            instructions.register(() -> !Action.isInteractingTO(ctx.client),
                                        "Opening gate");
        } else {
            log.info("Tried to open door, but no door to open!");
        }
    }

    private void pathTO(int x, int y, int id, String action)
    {
        path(x, y);
        interact(id, action, TILE_OBJECT);
        instructions.register(() -> !Action.isInteractingTO(ctx.client),
                                    action + "ing " + id);
    }
    
    private void pathTO(int x, int y, int z, int id, String action)
    {
        path(x, y, z);
        interact(id, action, TILE_OBJECT);
        instructions.register(() -> !Action.isInteractingTO(ctx.client),
                                    action + "ing " + id);
    }

    private void pathTO(int x, int y, String name, String action)
    {
        path(x, y);
        interact(name, action, TILE_OBJECT);
        instructions.register(() -> !Action.isInteractingTO(ctx.client),
                                    action + "ing " + name);
    }
    
    private void pathTO(int x, int y, int z, String name, String action)
    {
        path(x, y, z);
        interact(name, action, TILE_OBJECT);
        instructions.register(() -> !Action.isInteractingTO(ctx.client),
                                    action + "ing " + name);
    }

    private void talk(String name)
    {
        instructions.register(() -> Action.interactNPC(name, "Talk-to"),
                "Talk to: " + name);
    }

    private void trade(String name)
    {
        instructions.register(() -> Action.interactNPC(name, "Trade"),
                "Trade: " + name);
    }

    private void sup()
    {
        interact("Staircase", "Climb-up", TILE_OBJECT);
    }

    private void sdown()
    {
        interact("Staircase", "Climb-up", TILE_OBJECT);
    }

    private void block(int ticks)
    {
        instructions.register(() -> Action.block(ticks),
                "Blocking next instruction: " + ticks + " ticks");
    }

    private void debug()
    {
        instructions.register(() -> {
            log.info("Completed last instruction");
            return true; },
            "Debug: Completed last instruction");
    }

    private void interact(String name, String action, int type) {
        switch(type) {
        case TILE_OBJECT:
            instructions.register(() ->
                TileObjectInteraction.interact(name, action),
                "Tile object interaction: " + action + " " + name);
            break;
        case INVENTORY:
            instructions.register(() ->
                InventoryInteraction.useItem(name, action),
                "Inventory interaction: " + action + " " + name);
            break;
        case NPC:
            instructions.register(() ->
                NPCInteraction.interact(name, action),
                "NPC interaction: " + action + " " + name);
            break;
        case TILE_ITEM:
            int a = Integer.valueOf(action);
            instructions.register(() -> Action.interactTileItem(name, a),
                    "Tile item interaction: " + action + " " + name);
            break;
        default:
            throw new IllegalArgumentException(
                "Invalid interaction type: " + type);
        }
    }
    private void interact(int id, String action, int type) {
        switch(type) {
        case TILE_OBJECT:
            instructions.register(() ->
                TileObjectInteraction.interact(id, action),
                "Tile object interaction: " + action + " " + id);
            break;
        case INVENTORY:
            instructions.register(() ->
                InventoryInteraction.useItem(id, action),
                "Inventory interaction: " + action + " " + id);
            break;
        case NPC:
            instructions.register(() ->
                NPCInteraction.interact(id, action),
                "NPC interaction: " + action + " " + id);
            break;
        default:
            throw new IllegalArgumentException(
                "Invalid interaction type: " + type);
        }
    }

    private boolean canOpenDoor(int distance)
    {
        return !TileObjects.search()
                           .nameContains("Door").withAction("Open")
                           .withinDistance(distance)
                           .empty();
    }

    private boolean canOpenGate(int distance)
    {
        return !TileObjects.search()
                           .nameContains("Gate").withAction("Open")
                           .withinDistance(distance)
                           .empty();
    }

    // xxx likely will fix timers with npcs, also look into way for overall 
    // animations
    //// Check both if the player is interacting and if there is an interaction target
    //boolean currentlyInCombat = plugin.getClient().getLocalPlayer().isInteracting() &&
    //        plugin.getClient().getLocalPlayer().getInteracting() != null;

    /** 
     a Common dialogue helper macros.
     */
    private void cont()
    {
        instructions.register(() -> Action.continueDialogue(),
                "Continue dialogue: 0 times", Optional.empty());
    }

    private void cshort()
    {
        instructions.register(() -> Action.continueDialogue(),
                "Continue dialogue: " + shortCont + " times",
                Optional.of(shortCont));
    }

    private void cmed()
    {
        // random select
        instructions.register(() -> Action.continueDialogue(),
                "Continue dialogue: " + medCont + " times",
                Optional.of(medCont));
    }

    private void clong()
    {
        instructions.register(() -> Action.continueDialogue(),
                "Continue dialogue: " + longCont + " times",
                Optional.of(longCont));
    }

    private void dialogue(String str, int choice)
    {
        instructions.register(() -> Action.selectDialogue(str, choice),
                "Dialogue: " + str + " (" + choice + ")");
    }

    /**
     * @warning NO guardrails.
     */
    private int rand(int[] a)
    {
        return a[rand.nextInt(a.length)];
    }

    /**
     * Local enum for simplifying common calls.
     * @warning Treated as unsigned int, be careful not to index to 32nd bit!
     */
    private final int DEFAULT           = 0;
    private final int BANK_INVENTORY    = 1;
    private final int SHOP_INVENTORY    = 1 << 1;
    private final int PLAYER            = 1 << 2;
    private final int GE                = 1 << 3;
    private final int TILE_OBJECT       = 1 << 4;
    private final int INVENTORY         = 1 << 5;
    private final int PRAYER            = 1 << 6;
    private final int BANK              = 1 << 7;
    private final int SHOP              = 1 << 8;
    private final int NPC               = 1 << 9;
    private final int TILE_ITEM         = 1 << 10;

    /**
     * Action field number IDs.
     * @note To keep Action.interactTileItem() open to other action field number
     * IDs, but enumerate common ones.
     */
    private final String TAKE = "3";

    private int MAX_DISTANCE = 10;

    /**
     * Seeded random variables for the registry.
     */
    public int minWait;
    public int shortCont;
    public int medCont ;
    public int longCont;
    public int shortWait;
    public int medWait;
    public int longWait;
    private Random rand;

    /*
     * Plugin Context for the Registry.
     */
    private Map<String, Boolean> _cfg;
    private Instructions instructions;
    private Pathing pathing;
    private AutoQuesterContext ctx;
}
