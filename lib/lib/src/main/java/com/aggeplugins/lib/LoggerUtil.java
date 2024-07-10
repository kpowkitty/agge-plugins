package com.aggeplugins.lib;

import com.example.Packets.MousePackets;

import net.runelite.api.Skill;
import net.runelite.api.events.StatChanged;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.lang.reflect.Field;

public class LoggerUtil {
    public static Pair<Skill, Integer> gainedExp(StatChanged event) {
        return Pair.of(event.getSkill(), event.getXp());
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
}
