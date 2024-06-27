/**
 * @package lib.export
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-25
 *
 * Agge Plugins export of EthansApi and PiggyAPI. Sometimes unchanged, sometimes 
 * heavily changed. Thank you for the source of code. The diffs represent my 
 * contributions.
 */

package com.aggeplugins.lib.export.TaskManager;

import net.runelite.client.config.Config;
import net.runelite.client.plugins.Plugin;

//do something like this to pass in your specific plugin and config
//extends AbstractTask<StrategySmithPlugin, StrategySmithConfig>
public class ExampleTask extends AbstractTask {
    public ExampleTask( Plugin plugin, Config config) {
        super( plugin, config);
    }

    /**
     * If this returns true, this task will execute
     * @return
     */
    @Override
    public boolean validate() {
        return false;
    }

    /**
     * This is the code that will be executed when validate returns true
     */
    @Override
    public void execute() {

        interactNpc("Goblin", "Attack", true);

    }
}
