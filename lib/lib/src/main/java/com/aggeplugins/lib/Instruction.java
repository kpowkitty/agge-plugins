package com.aggeplugins.MessageBus;

import com.aggeplugins.lib.*;

import net.runelite.api.coords.WorldPoint;

public class Instruction {
    private String topic;
    private WorldPoint goal;
    private BankLocation bank;
    private Object cfg;
    private Object etc;
    public Instruction(String topic, WorldPoint goal, BankLocation bank, 
                       Object cfg, Object etc)
    {
        this.topic = topic;
        this.goal = goal;
        this.bank = bank;
        this.cfg = cfg;
        this.etc = etc;
    }
    public String getTopic()
    {
        return this.topic;
    }
    public WorldPoint getGoal()
    {
        return this.goal;
    }
    public BankLocation getBank()
    {
        return this.bank; // xxx
    }
    public Object getCfg()
    {
        return this.cfg;
    }
    public Object getEtc()
    {
        return this.etc;
    }
}
