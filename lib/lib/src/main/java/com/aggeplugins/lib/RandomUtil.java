package com.aggeplugins.lib;

import com.example.Packets.MousePackets;

import java.util.*;

public class RandomUtil {
    
    public static int randOffset(int range)
    {
        return (int) ((Math.random() * 2 * range++) - range);
    }

    public static int randTicks(int min, int max)
    {
        // Ensure min is less than or equal to max
        if (min > max) {
            throw new IllegalArgumentException("min should be less than or equal to max");
        }
        
        // Generate a random integer between min and max (inclusive)
        return min + (int)(Math.random() * ((max - min) + 1));
    }
    
    /**
     * Generic random tick delay that can be re-used in many things, but
     * changed once -- here.
     */
    public static int randTicks()
    {
        /** @note Change minimum/maximum for all plugins here: */
        final int MIN = 0;
        final int MAX = 7;

        // Ensure min is less than or equal to max
        if (MIN > MAX) {
            throw new IllegalArgumentException("min should be less than or equal to max");
        }
        
        // Generate a random integer between min and max (inclusive)
        return MIN + (int)(Math.random() * ((MAX - MIN) + 1));
    }

    public static void randLambda(Runnable lambda)
    {
        Random rand = new Random();
        int n = rand.nextInt(3) + 1; // random between 1-3
        for (int i = 0; i < n; i++) {
            lambda.run();
        }
    }
    
    public static void randLambda(Runnable lambda, int max)
    {
        Random rand = new Random();
        int n = rand.nextInt(max) + 1; // random between 1-max
        for (int i = 0; i < n; i++) {
            lambda.run();
        }
    }
    
    public static void randLambda(Runnable lambda, int min, int max)
    {
        Random rand = new Random();
        int n = rand.nextInt(max) + min; // random between min-max
        for (int i = 0; i < n; i++) {
            lambda.run();
        }
    }

    public static void randMousePackets()
    {
        Random rand = new Random();
        int n = rand.nextInt(3) + 1; // random between 1-3
        for (int i = 0; i < n; i++) {
            MousePackets.queueClickPacket();
        }
    }
    
    public static void randMousePackets(int max)
    {
        Random rand = new Random();
        int n = rand.nextInt(max) + 1; // random between 1-max
        for (int i = 0; i < n; i++) {
            MousePackets.queueClickPacket();
        }
    }
    
    public static void randMousePackets(int min, int max)
    {
        Random rand = new Random();
        int n = rand.nextInt(max) + min; // random between min-max
        for (int i = 0; i < n; i++) {
            MousePackets.queueClickPacket();
        }
    }

    //public static int randIdx(List<?> l)
    //{
    //    return random.nextInt(l.size());
    //}
}
