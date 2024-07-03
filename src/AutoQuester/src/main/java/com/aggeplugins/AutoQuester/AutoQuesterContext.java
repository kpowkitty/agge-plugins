/**
 * @file AutoQuesterContext.java
 * @class AutoQuesterContext
 * Context struct for passing around context, extends lib Context to use lib.
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-16
 *
 */

package com.aggeplugins.AutoQuester;

import com.aggeplugins.AutoQuester.*;
import com.aggeplugins.lib.*;

import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;

import java.util.Map;

public class AutoQuesterContext extends Context {
    public AutoQuesterPlugin plugin;
    public AutoQuesterConfig config;
    public Client client;
    public ClientThread clientThread;

    // Instance context.
    public Instructions instructions;

    public AutoQuesterContext(AutoQuesterPlugin plugin, 
                              AutoQuesterConfig config,
                              Client client, ClientThread clientThread,
                              Instructions instructions)
    {
        super(plugin, config, client, clientThread);
        this.plugin = plugin;
        this.config = config;
        this.client = client;
        this.clientThread = clientThread;
        this.instructions = instructions;
    }
}
