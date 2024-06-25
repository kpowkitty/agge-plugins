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

package com.aggeplugins.lib.export;

import net.runelite.api.coords.WorldPoint;

import java.util.concurrent.ThreadLocalRandom;

public class MathUtil {

    public static int random(int min, int max){
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    /**
     * Returns a random tile +/- rX and +/- rY from the given tile
     * @param wp
     * @param rX
     * @param rY
     * @return
     */
    public static WorldPoint randomizeTile(WorldPoint wp, int rX, int rY) {
        return wp.dx(random(-rX, rX + 1)).dy(random(-rY, rY + 1));
    }

    public static WorldPoint randomizeTile2(WorldPoint wp, int rX, int rY) {
        return wp.dx(random(rX, rX + 1)).dy(random(rY, rY + 1));
    }

}
