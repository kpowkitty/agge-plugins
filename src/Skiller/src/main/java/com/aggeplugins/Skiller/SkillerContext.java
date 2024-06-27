/**
 * @file SkillerContext.java
 * @class SkillerContext
 * Context struct for passing around context, extends lib Context to use lib.
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-19
 *
 */

package com.aggeplugins.Skiller;

import com.aggeplugins.lib.*;

import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;

public class SkillerContext extends Context {
    public SkillerPlugin plugin;
    public SkillerConfig config;
    public Client client;
    public ClientThread clientThread;

    public SkillerContext(SkillerPlugin plugin, SkillerConfig config, 
                          Client client, ClientThread clientThread)
    {
        super(plugin, config, client, clientThread);
        this.plugin = plugin;
        this.config = config;
        this.client = client;
        this.clientThread = clientThread;
    }
}
