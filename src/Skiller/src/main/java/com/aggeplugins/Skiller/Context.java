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

import net.runelite.api.Client;

public class Context {
    public SkillerPlugin plugin;
    public SkillerConfig config;
    public Client client;

    public Context(SkillerPlugin plugin, SkillerConfig config, Client client)
    {
        this.plugin = plugin;
        this.config = config;
        this.client = client;
    }
}
