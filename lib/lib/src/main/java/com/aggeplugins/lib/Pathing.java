/**
 * @file Pathing.java
 * @class Pathing
 * Pathing - Heavily borrowed and adapted from EthanApi.
 *
 * @author agge3
 * @version 1.0
 * @since 2024-06-15
 *
 * Special thanks to EthanApi and PiggyPlugins for API, inspiration, and a 
 * source of code at times.
 */

package com.aggeplugins.lib;

import com.aggeplugins.lib.*;
import com.aggeplugins.MessageBus.*;

import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.EthanApiPlugin.PathFinding.GlobalCollisionMap;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.ObjectPackets;
import shortestpath.ShortestPathPlugin;
import static shortestpath.ShortestPathPlugin.getPathfinder;

import net.runelite.api.ObjectComposition;
import net.runelite.api.Tile;
import net.runelite.api.WallObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.*;
import net.runelite.client.RuneLite;

import lombok.Getter;
import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

@Slf4j
public class Pathing {
    public enum Type {
        SHORTEST_PATH,
        ETHANS_API;
    }

    public Pathing(Context ctx)
    {
        this.ctx = ctx;
        log.info("Constructing Pathing!");
        init(); // on construction, start with an expected state
    }

    //public static WorldPoint getGoal()
    //{
    //    return goal; 
    //}

    public final boolean calculatingPath()
    {
        if (type == Pathing.Type.SHORTEST_PATH) {
            if (calc.get()) {
                //if (ShortestPathPlugin.getPathfinder() != null) {
                //    if (ShortestPathPlugin.getPathfinder().isDone()) {
                //        log.info("ShortestPath size: " +
                //            ShortestPathPlugin.getPathfinder().getPath().size());
                //        if (ShortestPathPlugin.getPathfinder().getPath().size() == 1) {
                //            // re-calc
                //            //msg = new Message<>("GOAL", goal);
                //            //messageBus.send("PATHING", msg);
                //        } else {
                //            path = ShortestPathPlugin.getPathfinder().getPath();
                //            calc.set(false);
                //        }
                //    }
                //}
                if (messageBus.query(MessageID.SEND_PATH)) {
                    msg = (Message<MessageID, List<WorldPoint>>)
                        messageBus.get(MessageID.SEND_PATH);
                    path = (List<WorldPoint>) msg.getData();
                    goal = path.get(path.size() - 1);
                    msg = null;
                    calc.set(false);

                    // Cleanup the MessageBus.
                    messageBus.remove(MessageID.REQUEST_PATH);
                    messageBus.remove(MessageID.SEND_PATH);
                }
            }
        }
        return calc.get();
    }

    public final boolean setType(Pathing.Type type)
    {
        switch(type) {
        case SHORTEST_PATH:
            this.type = Pathing.Type.SHORTEST_PATH;
            log.info("Pathing type set to: " + this.type);
            return true;
        case ETHANS_API:
            this.type = Pathing.Type.ETHANS_API;
            log.info("Pathing type set to: " + this.type);
            return true;
        default:
            // default is EthansApi, less semantics
            this.type = Pathing.Type.ETHANS_API;
            log.info("Pathing type set to: " + this.type);
            return true;
        }
    }

    public final boolean setPath()
    {
        if (goal != null) {
            // Caller forgot to set pathing type, default correct for them.
            if (type == null)
                this.type = Pathing.Type.SHORTEST_PATH;
            switch(type) {
            case SHORTEST_PATH:
                try {
                    calc.set(true); 
                } catch (NullPointerException e) {
                    log.info("AtomicBoolean has been garbage collected, creating new one...");
                    calc = new AtomicBoolean(true);
                }
                messageBus.send(new Message<MessageID, WorldPoint>(
                    MessageID.REQUEST_PATH, goal));
                //msg = new Message<>("GOAL", goal);
                //messageBus.send("PATHING", msg);
                //msg = null;
            break;
            case ETHANS_API:
                path = GlobalCollisionMap.findPath(goal);
            break;
            }
            return true;
        }
        return false;
    }

    /**
     * Set the pathing goal. If invalid, offset until a valid one is found.
     * @warning Recursive callback to find valid goal, can be expensive. Make
     * sure to set a valid goal to avoid.
     */
    public final boolean setGoal(WorldPoint goal) {
        try {
            log.info("Path to: " + goal);
            this.reset(); // soft-reset, but keep mission-critical state
            this.goal = goal;
            log.info("Final pathing goal: " + this.goal);
            return true;
        } catch (NullPointerException e) {
            // Guard against an incorrectable recursive state. Confirm that the
            // goal is on the same plane as the current plane and block 
            // potential stack overfow. A goal is never going to be found if 
            // this condition is met anyway, we should have exited long ago.
            if (goal.getPlane() != getPos().getPlane() && goal.getX() > 1000) {
                // Recursive call with offset until a valid path exists.
                return setGoal(goal);
            } else {
                // Couldn't find a valid WorldPoint and never will.
                log.info("Could not correct to a valid WorldPoint!");
                return false;
            }
        }
    }

    /**
     * For testing inside a game loop. Always returns TRUE.
     *
     * @return TRUE, ALWAYS
     */
    public boolean test()
    {
        //log.info("Entering test!");
        //if (ShortestPathPlugin.getPathfinder() != null) {
        //    if (ShortestPathPlugin.getPathfinder().isDone()) {
        //        log.info("ShortestPath size: " +
        //            ShortestPathPlugin.getPathfinder().getPath().size());
        //        return false;
        //    }
        //}
        return true;
    }

    public final boolean isPathingTo(WorldPoint goal)
    {
        log.info("Pathing to...");
        return goal != null && goal.equals(goal);
    }

    public final boolean reachedGoal() 
    {
        //return goal != null && 
        //       goal.equals(ctx.client.getLocalPlayer().getWorldLocation());
        return goal != null && goal.equals(getPos()) && 
               !EthanApiPlugin.isMoving();
    }

    public final boolean timeout(int n)
    {
        return true;
    }

    /**
     * A non-final handle obstacles method that can be overwritten if subclasses
     * have specific obstacle behavior. It will block the main run method until 
     * it returns false (done handling obstacles).
     *
     * @remark Default behavior does nothing and simply returns false, allowing 
     * main run method to run freely.
     *
     * @return FALSE, default is there are no obstacles to handle
     */
    public boolean handleObstacles()
    {
        return false;
    }

    // xxx not returning false when done pathing
    public final boolean run() 
    {
        ++ticks;

        if (reachedGoal()) {
            log.info("Reached goal!");
            //if (messageBus.query("INSTRUCTIONS")) { // xxx, finalizer if not
            this.reset();
            return false;
        }

        if (!handleObstacles()) {

            //if (path == null && ticks > 5) {
            //    log.info("Idling with no path. Exiting");
            //    this.finalizer();
            //    return false;
            //}

            if (path != null && path.size() >= 1) {
                //log.info("Current path goal is: " + path.get(path.size() - 1));
                ticks = 0;
                if (currPathDest != null && 
                    !atCurrPathDest() && !EthanApiPlugin.isMoving()) {

                    //log.info("Stopped walking, clicking destination again");
                    MousePackets.queueClickPacket();
                    MovementPackets.queueMovement(currPathDest);
                }
                    
                if (currPathDest == null || 
                    atCurrPathDest() || !EthanApiPlugin.isMoving()) {
                    //log.info("Current path destination is " + currPathDest);

                    int step = rand.nextInt((35 - 10) + 1) + 10;
                    int max = step;
                    for (int i = 0; i < step; i++) {
                        if (path.size() - 2 >= i) {
                            //log.info("Current path is" + path.get(i));
                            if (isDoored(path.get(i), path.get(i + 1))) {
                                max = i;
                                break;
                            }
                        }
                    }

                    if (isDoored(getPos(), path.get(0))) {
                        log.info("Door!");
                        WallObject wallObject = getTile(getPos()).getWallObject();
                        if (wallObject == null) {
                            wallObject = getTile(path.get(0)).getWallObject();
                        }
                        ObjectPackets.queueObjectAction(
                            wallObject, false, "Open", "Close");
                        //log.info("Return TRUE");
                        return true;
                    }

                    step = Math.min(max, path.size() - 1);
                    currPathDest = path.get(step);
                    //log.info("Current path destination is " + currPathDest);

                    if (path.indexOf(currPathDest) == (path.size() - 1)) {
                        log.info("path = null");
                        path = null;
                    } else {
                        path = path.subList(step + 1, path.size());
                        log.info("path.subList(step + 1, path.size())");
                    }

                    if (currPathDest.equals(getPos())) {
                        log.info("currPathDest equals pos -- Return TRUE");
                        return true;
                    }
                   
                    //log.info("Sending mouse packets!");
                    MousePackets.queueClickPacket();
                    MovementPackets.queueMovement(currPathDest);
                }
            }

        }

        //log.info("Reached return TRUE");
        return true;
    }

    private void init()
    {
        try {
            this.rand = new Random();
            this.calc = new AtomicBoolean(false);
            //this.path = new ArrayList<>(); // wait to init the path
            this.currPathDest = null;
            this.goal = null;
            this.ticks = 0;
        } catch (NullPointerException e) {
            log.info("Error: Unable to initialize pathing!");
        }
        // set default pathing type to EthansApi -- less semantics
        this.type = Pathing.Type.ETHANS_API;
        messageBus = messageBus.instance();
    }

    /**
     * Hard finalizer that ensures clean destruction.
     * @warning Can cause unexpected states!
     */
    private void finalizer()
    {
        // do a soft reset
        this.reset();
        // and hard destruct mission-critical, to ensure clean destruction
        this.rand = null;
        this.calc = null;
        this.type = null;
    }

    /**
     * Soft finalizer that resets instance while preserving mission-critical
     * state.
     */
    private void reset()
    {
        this.path = null;
        this.currPathDest = null;
        this.goal = null;
        this.ticks = 0;
    }

    private WorldPoint getPos()
    {
        return ctx.client.getLocalPlayer().getWorldLocation();
    }

    private boolean atCurrPathDest()
    {
        return currPathDest.equals(getPos());
    }
    
    private WorldPoint offset(WorldPoint wp)
    {
        int offset = 1;
        return new WorldPoint(wp.getX() - offset, wp.getY(), wp.getPlane());
    }

    private boolean isDoored(WorldPoint a, WorldPoint b)
    {
        Tile tA = getTile(a);
        Tile tB = getTile(b);
        if (tA == null || tB == null) {
            return false;
        }
        return isDoored(tA, tB);
    }

    private boolean isDoored(Tile a, Tile b)
    {
        WallObject wallObject = a.getWallObject();
        if (wallObject != null) {
            ObjectComposition objectComposition = EthanApiPlugin.getClient().getObjectDefinition(wallObject.getId());
            if (objectComposition == null) {
                return false;
            }
            boolean found = false;
            for (String action : objectComposition.getActions()) {
                if (action != null && action.equals("Open")) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
            int orientation = wallObject.getOrientationA();
            if (orientation == 1) {
                //blocks west
                if (a.getWorldLocation().dx(-1).equals(b.getWorldLocation())) {
                    return true;
                }
            }
            if (orientation == 4) {
                //blocks east
                if (a.getWorldLocation().dx(+1).equals(b.getWorldLocation())) {
                    return true;
                }
            }
            if (orientation == 2) {
                //blocks north
                if (a.getWorldLocation().dy(1).equals(b.getWorldLocation())) {
                    return true;
                }
            }
            if (orientation == 8) {
                //blocks south
                return a.getWorldLocation().dy(-1).equals(b.getWorldLocation());
            }
        }
        WallObject wallObjectb = b.getWallObject();
        if (wallObjectb == null) {
            return false;
        }
        ObjectComposition objectCompositionb = EthanApiPlugin.getClient().getObjectDefinition(wallObjectb.getId());
        if (objectCompositionb == null) {
            return false;
        }
        boolean foundb = false;
        for (String action : objectCompositionb.getActions()) {
            if (action != null && action.equals("Open")) {
                foundb = true;
                break;
            }
        }
        if (!foundb) {
            return false;
        }
        int orientationb = wallObjectb.getOrientationA();
        if (orientationb == 1) {
            //blocks east
            if (b.getWorldLocation().dx(-1).equals(a.getWorldLocation())) {
                return true;
            }
        }
        if (orientationb == 4) {
            //blocks south
            if (b.getWorldLocation().dx(+1).equals(a.getWorldLocation())) {
                return true;
            }
        }
        if (orientationb == 2) {
            //blocks south
            if (b.getWorldLocation().dy(+1).equals(a.getWorldLocation())) {
                return true;
            }
        }
        if (orientationb == 8) {
            //blocks north
            return b.getWorldLocation().dy(-1).equals(a.getWorldLocation());
        }
        return false;
    }

    private Tile getTile(WorldPoint point)
    {
        LocalPoint a = LocalPoint.fromWorld(EthanApiPlugin.getClient(), point);
        if (a == null) {
            return null;
        }
        return EthanApiPlugin.getClient().getScene().getTiles()[point.getPlane()][a.getSceneX()][a.getSceneY()];
    }

    // Pathing instance variables.
    private Random rand;
    private Context ctx;
    private List<WorldPoint> path;
    private WorldPoint currPathDest;
    private WorldPoint goal;
    private AtomicBoolean calc;
    private Pathing.Type type;
    private MessageBus messageBus;
    private Message<MessageID, ?> msg;

    private int ticks;
}
