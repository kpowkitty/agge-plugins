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

package com.agge.AutoQuester;

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
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

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
            path = GlobalCollisionMap.findPath(goal);
            fullPath = new ArrayList<>(path);
            Pathing.goal = goal;
            currentPathDestination = null;
            if(path == null){
                return false;
            }
            return true;
        // xxx hack fix for now, just keeping scanning for valid WorldPoint
        } catch (NullPointerException e) {
            log.info("Caught exception with that path!");
            log.info("Repathing to offset...");

            int x = goal.getX();
            int y = goal.getY();

            goal = new WorldPoint(x - 1, y - 1, 0);
            log.info("Path to: " + goal);
            currentPathDestination = null;
            path = GlobalCollisionMap.findPath(goal);
            fullPath = new ArrayList<>(path);
            Pathing.goal = goal;
            currentPathDestination = null;
            if(path == null){
                return false;
            }
            return true;
        }
    }

    public boolean isPathing() {
        log.info("Is pathing...");
        return goal != null && goal.equals(EthanApiPlugin.playerPosition());
    }

    public boolean timeout(int n)
    {
        return true;
    }

//    @Subscribe
//    private void onConfigChanged(ConfigChanged e) {
//        if (e.getGroup().equals("PathingTesting") && e.getKey().equals("run")) {
//            currentPathDestination = null;
//            path = GlobalCollisionMap.findPath(new WorldPoint(config.x(), config.y(), EthanApiPlugin.getClient().getPlane()));
//            fullPath = new ArrayList<>(path);
//            goal = new WorldPoint(config.x(), config.y(), EthanApiPlugin.getClient().getPlane());
//        }
//        if (e.getGroup().equals("PathingTesting") && e.getKey().equals("stop")) {
//            currentPathDestination = null;
//            path = null;
//            fullPath = null;
//            clientThread.invoke(() -> {
//                TileObjects.search().filter(x -> x instanceof WallObject).withAction("Open").nearestToPlayer().ifPresent(
//                        tileObject -> {
////                            WallObject x = (WallObject) tileObject;
////                            System.out.println(x.getWorldLocation());
////                            System.out.println("Open A: " + x.getOrientationA());
////                            System.out.println("Open B: " + x.getOrientationB());
//                        });
//                TileObjects.search().filter(x -> x instanceof WallObject).withAction("Close").nearestToPlayer().ifPresent(
//                        tileObject -> {
////                            WallObject x = (WallObject) tileObject;
////                            System.out.println(x.getWorldLocation());
////                            System.out.println("Close A: " + x.getOrientationA());
////                            System.out.println("Close B: " + x.getOrientationB());
//                        });
//                System.out.println(getTile(EthanApiPlugin.playerPosition()).getWallObject() == null);
//            });
//        }
//    }

    public void run() {
        _ticks++;
        if(goal!=null&&goal.equals(EthanApiPlugin.playerPosition())){
            log.info("Pathing: Reached goal");
            goal = null;
            path = null;
            currentPathDestination = null;
            _ticks = 0; // Arrived, reset tick counter.
            return;
        }
        if (path != null && path.size() >= 1) {
            if(currentPathDestination !=null&&!currentPathDestination.equals(EthanApiPlugin.playerPosition())&&!EthanApiPlugin.isMoving()){
                log.info("stopped walking. clicking destination again");
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(currentPathDestination);
            }
            if (currentPathDestination == null || currentPathDestination.equals(EthanApiPlugin.playerPosition()) || !EthanApiPlugin.isMoving()) {
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
                    WallObject wallObject = getTile(EthanApiPlugin.playerPosition()).getWallObject();
                    if(wallObject == null){
                        wallObject = getTile(path.get(0)).getWallObject();
                    }
                    ObjectPackets.queueObjectAction(wallObject,false,"Open","Close");
                    return;
                }
                step = Math.min(max, path.size() - 1);
                currentPathDestination = path.get(step);
                if (path.indexOf(currentPathDestination) == path.size() - 1) {
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

    private boolean notMoving()
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
