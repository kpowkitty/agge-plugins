package com.aggeplugins.MessageBus;

import com.aggeplugins.lib.*;

import net.runelite.api.coords.WorldPoint;
import net.runelite.api.Skill;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public class Instruction {
    private String task;
    private WorldPoint location;
    private BankLocation bank;
    private Map<String, String> info;
    private Pair<Skill, Integer> level;

    /**
     * Constructs an Instruction object.
     *
     * @param task      The task to be performed.
     * @param location  The location where the task should be performed.
     * @param bank      The bank location associated with the task.
     * @param info      Additional information related to the task, stored as key-value pairs.
     * @param level     The level goal for the Instruction.
     */
    public Instruction(String task, WorldPoint location, BankLocation bank, 
                       Map<String, String> info, Pair<Skill, Integer> level) {
        this.task = task;
        this.location = location;
        this.bank = bank;
        this.info = info;
        this.level = level;
    }

    /**
     * Retrieves the task associated with this Instruction.
     *
     * @return The task string.
     */
    public String getTask() {
        return this.task;
    }

    /**
     * Retrieves the location associated with this Instruction.
     *
     * @return The WorldPoint location.
     */
    public WorldPoint getLocation() {
        return this.location;
    }

    /**
     * Retrieves the bank location associated with this Instruction.
     *
     * @return The BankLocation object representing the bank location.
     */
    public BankLocation getBank() {
        return this.bank;
    }

    /**
     * Retrieves additional information associated with this Instruction.
     *
     * @return A map containing additional information.
     */
    public Map<String, String> getInfo() {
        return this.info;
    }

    /**
     * Retrieves the level goal associated with this Instruction.
     *
     * @return Pair<Skill, Integer>
     * The level and goal.
     */
    public Pair<Skill, Integer> getLevel()
    {
        return this.level;
    }
}
