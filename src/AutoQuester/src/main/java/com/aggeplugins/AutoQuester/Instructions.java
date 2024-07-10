/**
 * @file Instructions.java
 * @class Instructions
 * Provides functionality to register, execute, and clear boolean instructions. 
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-15
 */

package com.aggeplugins.AutoQuester;

import com.aggeplugins.AutoQuester.*;
import com.aggeplugins.lib.*;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.query.NPCQuery;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MovementPackets;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import com.agge.AutoQuester.IntPtr;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.BiConsumer;
import java.lang.Runnable;
import java.util.Vector;
import java.util.ArrayList;

@Slf4j
public class Instructions {
    // xxx make this a singleton, it's better fit that way
    public Instructions()
    {
        log.info("Constructing Instructions!");

        _instructions = new ArrayList<>();
        _names = new ArrayList<>();
        //_goals = new ArrayList<>();
        _idx = 0;
    }

    /**
     * Executes current instruction.
     * @return TRUE if instruction executed, FALSE if still executing
     */
    public boolean execute()
    {
        if (_idx < _instructions.size() && 
            _instructions.get(_idx).getAsBoolean()) {
            _instructions.remove(_idx);
            _names.remove(_idx);
            // Do not increment index since the current index is now pointing 
            // to the next instruction.
            return true;
        }
        return false;
    }

    /** 
     * Executes all registered instructions.
     * @return TRUE if all instructions executed, FALSE if still executing
     */
    public boolean executeInstructions()
    {
        // Execute the current instruction.
        boolean executed = execute();
        // Check if there are more instructions left after executing the current 
        // one.
        return executed && _idx >= _instructions.size();
    }

    /**
     * Register instructions into this instance's instruction vector.
     * @param BooleanSupplier instruction
     * A functional boolean instruction.
     * TRUE if instruction has executed, FALSE if still executing
     * @param String name
     * Provide a human-readable name for the instruction.
     * @param Optional<Integer> n
     * Add n instructions. RECOMMENDED: Seed Random.
     */
    public void register(BooleanSupplier instruction, String name, 
            Optional<Integer> n) 
    {
        if (!n.isPresent()) {
            _instructions.add(instruction);
            _names.add(name);
        } else {
            for (int i = 0; i < n.get(); i++) {
                _instructions.add(instruction);
                _names.add(name);
            }
        }
    }

    /**
     * Register instructions into this instance's instruction vector.
     * @param BooleanSupplier instruction
     * A functional boolean instruction.
     * TRUE if instruction has executed, FALSE if still executing
     * @param String name
     * Provide a human-readable name for the instruction.
     */
    public void register(BooleanSupplier instruction, String name) 
    {
        _instructions.add(instruction);
        _names.add(name);
    }

    /**
     * Register a BiConsumer as a BooleanSupplier step with wildcard parameters.
     * @param f The BiConsumer to register.
     * @param arg1 The first argument for the BiConsumer.
     * @param arg2 The second argument for the BiConsumer.
     * @param name The name of the step.
     * @param <T> The type of the first argument of the BiConsumer.
     * @param <U> The type of the second argument of the BiConsumer.
     */
    public <T, U> void registerAsBoolean(
            BiConsumer<? super T, ? super U> f, T t, U u, String name) {
        this.register(() -> {
            f.accept(t, u);
            return true;
        }, name);
    }

    /**
     * Register a Consumer as a BooleanSupplier step with wildcard parameter.
     * @param f The Consumer to register.
     * @param arg The argument for the Consumer.
     * @param name The name of the step.
     * @param <T> The type of the Consumer argument.
     */
    public <T> void registerAsBoolean(
            Consumer<? super T> f, T t, String name)
    {
        this.register(() -> {
            f.accept(t);
            return true;
            }, name
        );
    }

    /**
     * Register a Runnable as a BooleanSupplier step.
     * @param f The Runnable to register.
     * @param name The name of the step.
     */
    public void registerAsBoolean(Runnable f, String name)
    {
        this.register(() -> {
            f.run();
            return true;
            }, name
        );
    }

    /**
     * Returns the size of the instruction list.
     * @return The size of the instruction list
     */
    public int getSize() 
    {
        return _instructions.size();
    }

    /**
     * Get the index of the executing instruction.
     * @return The current index
     */
    public int getIdx() 
    {
        return _idx;
    }

    /**
     * Clears all instructions.
     */
    public void clear() 
    {
        _instructions.clear();
        _names.clear();
        _idx = 0; // Reset index.
    }

    /**
     * Get a human-readable name for the current instruction.
     * @return String instruction
     * The instruction's human-readable name.
     */
    public String getName()
    {
        // Need to have idx + 1 to see the proper instruction. xxx maybe not?
        try {
            return _names.get(_idx);
        } catch (Exception e) {
            // Just catch the out-of-bounds exception and return a message.
            return "Last instruction!";
        }
    }

    /**
     * Skip the current instruction.
     * @note Useful if stuck/broken.
     * @warning Is a destructive procedure! Can't be undone!
     */
    public boolean skip()
    {
        _instructions.remove(_idx);
        return true;
    }

    /**
     * Get the current instruction's WorldPoint goal.
     * @return WorldPoint wp
     * The instruction's WorldPoint goal.
     * @todo UNIMPLEMENTED, requires changing the interface. Using a static 
     * global instead.
     */
    //public WorldPoint getGoal()
    //{
    //    return _goals.get(_idx);
    //}

    private List<BooleanSupplier> _instructions;
    private List<String> _names; 
    //private List<WorldPoint> _goals;
    private int _idx;
}
