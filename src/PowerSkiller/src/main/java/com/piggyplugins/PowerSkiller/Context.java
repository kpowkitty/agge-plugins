/**
 * @file Context.java
 * @class Context
 * Context struct for passing around context.
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-19
 *
 */

package com.aggeplugins.Skiller;

public class Context {
    public PowerSkillerPlugin plugin;
    public PowerSkillerConfig config;

    public Context(PowerSkillerPlugin plugin, PowerSkillerConfig config)
    {
        this.plugin = plugin;
        this.config = config;
    }
}
