package com.aggeplugins.lib;

import com.example.Packets.MousePackets;

import net.runelite.api.Skill;

import java.util.*;
import java.util.stream.Collectors;
import java.lang.reflect.Field;

public class Logger<T> {
    public Logger(T evt)
    {
        this.evt = evt;

        this.init();
    }

    private void init()
    {   
        this.exp = 0;
    }

    private void logEvents()
    {
        // try casting to each event Logger wants to log, catch and throw away
        // if not a correct cast
        //try {
        //    evt = (StateChanged) evt;
        //} catch (e) {
        //    // throw away, wasn't a correct cast
        //}
    }

    /**
     * Utility to log experience gained.
     * 
     * @note Must pass StateChanged event.
     */
    //private int gainedExp(StatChanged event, Skill skill)
    //{
    //    if (event.getSkill() == skill) {
    //        return event.getXp();
    //    }
    //}

    private T evt;
    private int exp;
}
