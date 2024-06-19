/**
 * @file Registry.java
 * @class Registry
 * Context struct for passing around context.
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-16
 *
 */

package com.agge.AutoQuester;

import com.agge.AutoQuester.Pathing;
import com.agge.AutoQuester.Instructions;
import com.agge.AutoQuester.Action;

import java.util.Map;

public class Context {
    // Instance context.
    public Map<String, Boolean> cfg;
    public Instructions instructions;
    public Pathing pathing;
    public Action action;

    public Context(Map<String, Boolean> cfg, Instructions instructions, 
            Pathing pathing, Action action)
    {
        this.cfg = cfg;
        this.instructions = instructions;
        this.pathing = pathing;
        this.action = action;
    }
}
