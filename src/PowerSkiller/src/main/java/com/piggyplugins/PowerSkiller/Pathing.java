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

package com.piggyplugins.PowerSkiller;

/* Begin shortest-path. */
import net.runelite.api.Client;
import net.runelite.api.KeyCode;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.SpriteID;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.worldmap.WorldMap;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.JagexColors;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.worldmap.WorldMapOverlay;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;
import shortestpath.pathfinder.CollisionMap;
import shortestpath.pathfinder.Pathfinder;
import shortestpath.pathfinder.PathfinderConfig;
import shortestpath.pathfinder.SplitFlagMap;
import shortestpath.*;
/* End shortest-path. */

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import com.google.inject.Provides;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Pattern;
/* End shortest-path. */

import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.EthanApiPlugin.PathFinding.GlobalCollisionMap;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.ObjectPackets;

import net.runelite.api.ObjectComposition;
import net.runelite.api.Tile;
import net.runelite.api.WallObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.*;
import net.runelite.client.RuneLite;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
public class Pathing {
    public Pathing()
    {
        log.info("Constructing Pathing!");

        _ticks = 0;
        _isDoored = false;
    }

    static List<WorldPoint> path = new ArrayList<>();
    static List<WorldPoint> fullPath = new ArrayList<>();
    static WorldPoint currentPathDestination = null;
    static WorldPoint goal = null;
    
    @Inject
    Random rand = new Random();

    Pathfinder pathfinder;
    SplitFlagMap map = SplitFlagMap.fromResources();
    Map<WorldPoint, List<Transport>> transports = Transport.loadAllFromResources();
    PathfinderConfig pathfinderConfig = new PathfinderConfig(
        map, transports, PowerSkillerPlugin.client);

    //@Inject
    //ClientThread clientThread;
    //@Inject
    //OverlayManager overlayManager;

    //@Override
    //protected void startUp() throws Exception {
    //    currentPathDestination = null;
    //    path = null;
    //    goal = null;
    //    fullPath = null;
    //    overlay = new PathingTestingOverlay(EthanApiPlugin.getClient(), this,config);
    //    overlayManager.add(overlay);
    //}

    //@Override
    //protected void shutDown() throws Exception {
    //    currentPathDestination = null;
    //    path = null;
    //    goal = null;
    //    fullPath = null;
    //    overlayManager.remove(overlay);
    //}
    //
    //
    public boolean isPathingTo(WorldPoint a){
        log.info("Pathing to...");
        return goal != null && goal.equals(a);
    }

    //public static boolean isPathing(){
    //    return goal != null;
    //}

    public boolean pathTo(WorldPoint goal) {
        try {
            log.info("Path to: " + goal);
            currentPathDestination = null;

            // Use shortest-path to calculate the path.
            pathfinder = new Pathfinder(
                pathfinderConfig, 
                PowerSkillerPlugin.client.getLocalPlayer().getWorldLocation(),
                goal);
            path = pathfinder.getPath();
            fullPath = new ArrayList<>(path);

            // null the reference after pathfinder has calculated the path.
            pathfinder = null;
            
            //path = GlobalCollisionMap.findPath(goal);
            //fullPath = new ArrayList<>(path);
            
            Pathing.goal = goal;
            currentPathDestination = null;

            if (path == null) {
                return false;
            }

            return true;

        // xxx hack fix for now, just keeping scanning for a valid WorldPoint
        } catch (NullPointerException e) {
            log.info("Caught exception with that path!");
            log.info("Repathing to offset...");

            int x = goal.getX();
            int y = goal.getY();

            // xxx only offset x, we'll find one eventually...
            goal = new WorldPoint(x - 1, y, 0);
            log.info("Path to: " + goal);

            currentPathDestination = null;

            // Use shortest-path to calculate the path.
            pathfinder = new Pathfinder(
                pathfinderConfig, 
                PowerSkillerPlugin.client.getLocalPlayer().getWorldLocation(),
                goal);
            path = pathfinder.getPath();
            fullPath = new ArrayList<>(path);

            // null the reference after pathfinder has calculated the path.
            pathfinder = null;

            //path = GlobalCollisionMap.findPath(goal);
            //fullPath = new ArrayList<>(path);

            Pathing.goal = goal;
            currentPathDestination = null;

            if (path == null) {
                return false;
            }

            return true;
        }

        return false;
    }

    public boolean isPathing() 
    {
        log.info("Is pathing...");
        return goal != null && goal.equals(EthanApiPlugin.playerPosition());
    }

    public boolean timeout(int n)
    {
        return true;
    }

    public void run() 
    {
        _ticks++;

        if (goal != null && goal.equals(EthanApiPlugin.playerPosition())) {
            log.info("Pathing: Reached goal");
            goal = null;
            path = null;
            currentPathDestination = null;
            _ticks = 0; // Arrived, reset tick counter.
            return;
        }

        if (path != null && path.size() >= 1) {
            if (currentPathDestination != null && 
                !currentPathDestination.equals(EthanApiPlugin.playerPosition()) &&
                !EthanApiPlugin.isMoving()) {

                log.info("stopped walking. clicking destination again");
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(currentPathDestination);
            }
                
            if (currentPathDestination == null || 
                currentPathDestination.equals(EthanApiPlugin.playerPosition()) || 
                !EthanApiPlugin.isMoving()) {

                int step = rand.nextInt((35 - 10) + 1) + 10;
                int max = step;
                for (int i = 0; i < step; i++) {
                    if (path.size() - 2 >= i) {
                        if (isDoored(path.get(i), path.get(i + 1))) {
                            max = i;
                            break;
                        }
                    }
                }

                if (isDoored(EthanApiPlugin.playerPosition(), path.get(0))) {
                    log.info("Door!");
                    WallObject wallObject = getTile(
                        EthanApiPlugin.playerPosition()).getWallObject();

                    if (wallObject == null) {
                        wallObject = getTile(path.get(0)).getWallObject();
                    }

                    ObjectPackets.queueObjectAction(
                        wallObject, false, "Open", "Close");
                    return;
                }

                step = Math.min(max, path.size() - 1);
                currentPathDestination = path.get(step);

                if (path.indexOf(currentPathDestination) == (path.size() - 1)) {
                    path = null;
                } else {
                    path = path.subList(step + 1, path.size());
                }

                if (currentPathDestination.equals(EthanApiPlugin.playerPosition())) {
                    return;
                }

                log.info("Pathing: Taking a step");
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(currentPathDestination);
            }
        }
    }

    private boolean isDoored(WorldPoint a, WorldPoint b) {
        Tile tA = getTile(a);
        Tile tB = getTile(b);
        if (tA == null || tB == null) {
            return false;
        }
        _isDoored = true;
        return isDoored(tA, tB);
    }

    private boolean isDoored(Tile a, Tile b) {
        WallObject wallObject = a.getWallObject();
        if (wallObject != null) {
            ObjectComposition objectComposition = EthanApiPlugin.getClient().getObjectDefinition(wallObject.getId());
            if (objectComposition == null) {
                _isDoored = false;
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
                _isDoored = false;
                return false;
            }
            int orientation = wallObject.getOrientationA();
            if (orientation == 1) {
                //blocks west
                if (a.getWorldLocation().dx(-1).equals(b.getWorldLocation())) {
                    _isDoored = true;
                    return true;
                }
            }
            if (orientation == 4) {
                //blocks east
                if (a.getWorldLocation().dx(+1).equals(b.getWorldLocation())) {
                    _isDoored = true;
                    return true;
                }
            }
            if (orientation == 2) {
                //blocks north
                if (a.getWorldLocation().dy(1).equals(b.getWorldLocation())) {
                    _isDoored = true;
                    return true;
                }
            }
            if (orientation == 8) {
                //blocks south
                _isDoored = true;
                return a.getWorldLocation().dy(-1).equals(b.getWorldLocation());
            }
        }
        WallObject wallObjectb = b.getWallObject();
        if (wallObjectb == null) {
            _isDoored = false;
            return false;
        }
        ObjectComposition objectCompositionb = EthanApiPlugin.getClient().getObjectDefinition(wallObjectb.getId());
        if (objectCompositionb == null) {
            _isDoored = false;
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
            _isDoored = false;
            return false;
        }
        int orientationb = wallObjectb.getOrientationA();
        if (orientationb == 1) {
            //blocks east
            if (b.getWorldLocation().dx(-1).equals(a.getWorldLocation())) {
                _isDoored = true;
                return true;
            }
        }
        if (orientationb == 4) {
            //blocks south
            if (b.getWorldLocation().dx(+1).equals(a.getWorldLocation())) {
                _isDoored = true;
                return true;
            }
        }
        if (orientationb == 2) {
            //blocks south
            if (b.getWorldLocation().dy(+1).equals(a.getWorldLocation())) {
                _isDoored = true;
                return true;
            }
        }
        if (orientationb == 8) {
            //blocks north
            _isDoored = true;
            return b.getWorldLocation().dy(-1).equals(a.getWorldLocation());
        }
        _isDoored = false;
        return false;
    }

    private Tile getTile(WorldPoint point) {
        LocalPoint a = LocalPoint.fromWorld(EthanApiPlugin.getClient(), point);
        if (a == null) {
            return null;
        }
        return EthanApiPlugin.getClient().getScene().getTiles()[point.getPlane()][a.getSceneX()][a.getSceneY()];
    }

    public boolean notMoving()
    {
        if (currentPathDestination != null && 
            !currentPathDestination.equals(EthanApiPlugin.playerPosition())
            && !EthanApiPlugin.isMoving()) {
            _ticks++;
            return true;
        }
        return false;
    }

    private int _ticks;
    private boolean _isDoored;
}
