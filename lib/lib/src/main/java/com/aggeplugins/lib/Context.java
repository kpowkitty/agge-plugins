/**
 * @file Context.java
 * @class Context
 * Abstract Context struct to provide a guarantee that plugin and config exist,
 * but be extended to each class's additional Context needs.
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-25
 *
 */

package com.aggeplugins.lib;

import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;

public abstract class Context {
    public Object plugin;
    public Object config;
    public Client client;
    public ClientThread clientThread;
    public Context(Object plugin, Object config, 
                   Client client, ClientThread clientThread)
    {
        this.plugin = plugin;
        this.config = config;
        this.client = client;
        this.clientThread = clientThread;
    }   
}
