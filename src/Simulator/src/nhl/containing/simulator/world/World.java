/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.world;

import nhl.containing.simulator.game.PlatformStorage;
import nhl.containing.simulator.framework.Behaviour;
import nhl.containing.simulator.simulation.Main;
import nhl.containing.simulator.framework.Point3;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.List;
import nhl.containing.simulator.game.AGV;
import nhl.containing.simulator.game.Container;
import nhl.containing.simulator.game.PlatformInland;
import nhl.containing.simulator.game.PlatformLorry;
import nhl.containing.simulator.game.PlatformSea;
import nhl.containing.simulator.game.PlatformTrain;
import nhl.containing.simulator.framework.Point2;

/**
 *
 * @author sietse
 */
public class World extends Behaviour {
    public static final boolean USE_DIFFUSE = true;
    public static final Point2 STORAGE_SIZE = new Point2(/*45*/ 45, 3/*72*/); // x = containers length per storage; y = storage amount
    
    public static final float WORLD_HEIGHT =  0.0f;
    public static final float WORLD_DEPTH = -150.0f;
    public static final float WATER_LEVEL = - 5.0f;
    public static final float LAND_HEIGHT_EXTEND = 100.0f;
    
    public static final int AGV_COUNT = 100;
    public static final float LANE_WIDTH = 10.0f;
    public static final int LANE_COUNT = 4;
    
    public static final float STORAGE_LENGTH = 1550.0f;// - LANE_WIDTH * LANE_COUNT;
    public static final float STORAGE_WIDTH = 600.0f;
    
    public static final float EXTENDS = 100.0f;
    public static final int SEA_SHIP_CRANE_COUNT = 8;
    public static final int TRAIN_CRANE_COUNT = 4;
    public static final int LORRY_CRANE_COUNT = 20;
    
    public static Vector3f containerSize() {
        return new Vector3f(2.438f, 2.438f, 12.192f);
    }
    
    // Main
    private DirectionalLight m_sun;
    
    // World
    private List<PlatformInland > m_inlandCells  = new ArrayList<>(0);
    private List<PlatformLorry  > m_lorryCells   = new ArrayList<>(0);
    private List<PlatformSea    > m_seaCells     = new ArrayList<>(0);
    private List<PlatformStorage> m_storageCells = new ArrayList<>(0);
    private List<PlatformTrain  > m_trainCells   = new ArrayList<>(0);
    
    @Override
    public void awake() {
        m_sun = LightCreator.createSun(ColorRGBA.White, new Vector3f(-0.5f, -0.5f, -0.5f));
        LightCreator.createAmbient(new ColorRGBA(0.2f, 0.2f, 0.2f, 1.0f));
        Main.camera().createShadowsFiler(m_sun);
    }
    
    @Override
    public void start() {
        createGround();
        createInlandCell();
        createLorryCell();
        createSeaCell();
        createStorageCell();
        createTrainCell();
        createAGV();
        Main.getSimClient().Start();
    }
    
    @Override
    public void update() {
        for(PlatformInland  s : m_inlandCells  ) s.update();
        for(PlatformLorry   s : m_lorryCells   ) s.update();
        for(PlatformSea     s : m_seaCells     ) s.update();
        for(PlatformStorage s : m_storageCells ) s.update();
        for(PlatformTrain   s : m_trainCells   ) s.update();
    }
    private void createAGV() {
        Spatial teapot = Main.assets().loadModel("models/Sietse/Train/Thomas_Train.obj");
        teapot.setMaterial(MaterialCreator.unshaded("models/Sietse/Train/Thomas_Train.png"));
        Main.root().attachChild(teapot);
        
        Spatial mater = Main.assets().loadModel("models/Sietse/Truck/Mater.obj");
        mater.setMaterial(MaterialCreator.unshaded("models/Sietse/Truck/mater1_lod0.png"));
        mater.setLocalTranslation(-15.0f, 0, 0);
        Main.root().attachChild(mater);
        
        AGV agv = new AGV();
        agv.setContainer(new Container(null));
        agv.position(new Vector3f(0.0f, 0.0f, -36.0f));
        m_storageCells.get(0).getParkingSpot(0).agv(agv);
        
        
        for (int i = 0; i < 3; i++) {
            m_storageCells.get(0).take(new Point3(4, 4, i), 0);
        }
        m_storageCells.get(0).place(0, new Point3(4, 5, 1));
        
    }
    
    private void createInlandCell() {
        Vector3f offset = new Vector3f(0.0f, WORLD_HEIGHT, STORAGE_WIDTH + EXTENDS);
        for (int i = 0; i < 1; ++i) {
            m_inlandCells.add(new PlatformInland(offset));
            offset.x -= 10.0f;
        }
    }
    private void createLorryCell() {
        Vector3f offset = new Vector3f(STORAGE_LENGTH, WORLD_HEIGHT, STORAGE_WIDTH + EXTENDS);
        for (int i = 0; i < LORRY_CRANE_COUNT; ++i) {
            m_lorryCells.add(new PlatformLorry(offset));
            offset.x -= STORAGE_LENGTH / LORRY_CRANE_COUNT;
        }
    }
    private void createSeaCell() {
        Vector3f offset = new Vector3f(-STORAGE_LENGTH, WORLD_HEIGHT, STORAGE_WIDTH + EXTENDS);
        for (int i = 0; i < 1; ++i) {
            m_seaCells.add(new PlatformSea(offset));
            offset.z -= 10.0f;
        }
    }
    private void createStorageCell() {
        Vector3f offset = new Vector3f(-LANE_WIDTH / 2 - STORAGE_LENGTH, WORLD_HEIGHT, -STORAGE_WIDTH + 50.0f);
        for (int i = 0; i < STORAGE_SIZE.y; ++i) {
            m_storageCells.add(new PlatformStorage(offset));
            
            if (i == 36) // Adding space for the middle road
                offset.x += LANE_WIDTH * LANE_COUNT * 2 + 7.5f;
            offset.x += containerSize().x * 6 + 27.5f;
        }
    }
    private void createTrainCell() {
        Vector3f offset = new Vector3f(0.0f, WORLD_HEIGHT, 0.0f);
        for (int i = 0; i < 0; ++i) {
            m_trainCells.add(new PlatformTrain(offset));
            offset.x -= 10.0f;
        }
    }
    
    private void createGround() {
        createStorageGround();
        createRoadGround();
        createLorryGround();
        createInlandGround();
        createShippingGround();
        createTrainGround();
        createOtherGround();
    }
    private void createStorageGround() {
        // Storage
        Geometry storageEast = WorldCreator.createBox(
                null,                                                           // Parent
                new Vector3f(STORAGE_LENGTH / 2.0f, 1.0f, STORAGE_WIDTH),       // Size
                new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f),                          // Color
                true, false                                                     // Other
        );
        storageEast.setLocalTranslation((LANE_WIDTH * LANE_COUNT) + STORAGE_LENGTH / 2.0f, WORLD_HEIGHT-1.0f, 0.0f);
        
        // 
        Geometry storageWest = WorldCreator.createBox(
                null,                                                           // Parent
                new Vector3f(STORAGE_LENGTH / 2.0f, 1.0f, STORAGE_WIDTH),       // Size
                new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f),                          // Color
                true, false                                                     // Other
        );
        storageWest.setLocalTranslation(-(LANE_WIDTH * LANE_COUNT) - STORAGE_LENGTH / 2.0f, WORLD_HEIGHT-1.0f, 0.0f);
    }
    private void createRoadGround() {
        final ColorRGBA roadColor = new ColorRGBA(0.4f, 0.4f, 0.4f, 1.0f);
        
        // Storage
        Geometry middleRoad = WorldCreator.createBox(
                null,                                                           // Parent
                new Vector3f(LANE_WIDTH * LANE_COUNT, 1.0f, STORAGE_WIDTH),       // Size
                roadColor,                          // Color
                true, false                                                     // Other
        );
        middleRoad.setLocalTranslation(0.0f, WORLD_HEIGHT-1.0f, 0.0f);
        
        // Storage
        Geometry westRoad = WorldCreator.createBox(
                null,                                                           // Parent
                new Vector3f(LANE_WIDTH * LANE_COUNT, 1.0f, STORAGE_WIDTH),       // Size
                roadColor,                          // Color
                true, false                                                     // Other
        );
        westRoad.setLocalTranslation(-STORAGE_LENGTH - LANE_WIDTH * 2 * LANE_COUNT, WORLD_HEIGHT-1.0f, 0.0f);
        
        // Storage
        Geometry eastRoad = WorldCreator.createBox(
                null,                                                           // Parent
                new Vector3f(LANE_WIDTH * LANE_COUNT, 1.0f, STORAGE_WIDTH),       // Size
                roadColor,                          // Color
                true, false                                                     // Other
        );
        eastRoad.setLocalTranslation(STORAGE_LENGTH + LANE_WIDTH * 2 * LANE_COUNT, WORLD_HEIGHT-1.0f, 0.0f);
        
        // Storage
        Geometry northRoad = WorldCreator.createBox(
                null,                                                           // Parent
                new Vector3f(STORAGE_LENGTH + LANE_WIDTH * LANE_COUNT * 3, 1.0f, LANE_WIDTH * LANE_COUNT),       // Size
                roadColor,                          // Color
                true, false                                                     // Other
        );
        northRoad.setLocalTranslation(0, WORLD_HEIGHT-1.0f, STORAGE_WIDTH + LANE_WIDTH * LANE_COUNT);
        
        // Storage
        Geometry southRoad = WorldCreator.createBox(
                null,                                                           // Parent
                new Vector3f(STORAGE_LENGTH + LANE_WIDTH * LANE_COUNT * 3, 1.0f, LANE_WIDTH * LANE_COUNT),       // Size
                roadColor,                          // Color
                true, false                                                     // Other
        );
        southRoad.setLocalTranslation(0, WORLD_HEIGHT-1.0f, -STORAGE_WIDTH - LANE_WIDTH * LANE_COUNT);
    }
    private void createLorryGround() {
        // Storage
        Geometry lorryGround = WorldCreator.createBox(
                null,                                                           // Parent
                new Vector3f(STORAGE_LENGTH / 2 + LANE_WIDTH * LANE_COUNT, 1.0f, EXTENDS),       // Size
                new ColorRGBA(0.5f, 0.6f, 0.8f, 1.0f),                          // Color
                true, false                                                     // Other
        );
        lorryGround.setLocalTranslation(STORAGE_LENGTH / 2 + LANE_WIDTH * LANE_COUNT * 2, WORLD_HEIGHT-1.0f, STORAGE_WIDTH + 2 * LANE_WIDTH * LANE_COUNT + EXTENDS);
    }
    private void createInlandGround() {
        // Storage
        Geometry lorryGround = WorldCreator.createBox(
                null,                                                           // Parent
                new Vector3f(STORAGE_LENGTH / 2 + LANE_WIDTH * LANE_COUNT * 2, 1.0f, EXTENDS),       // Size
                new ColorRGBA(0.6f, 0.8f, 0.5f, 1.0f),                          // Color
                true, false                                                     // Other
        );
        lorryGround.setLocalTranslation(-STORAGE_LENGTH / 2 - LANE_WIDTH * LANE_COUNT , WORLD_HEIGHT-1.0f, STORAGE_WIDTH + 2 * LANE_WIDTH * LANE_COUNT + EXTENDS);
    }
    private void createShippingGround() {
        // Storage
        Geometry lorryGround = WorldCreator.createBox(
                null,                                                           // Parent
                new Vector3f(EXTENDS, 1.0f, STORAGE_WIDTH + LANE_WIDTH * LANE_COUNT * 2 + EXTENDS * 2),       // Size
                new ColorRGBA(0.8f, 0.6f, 0.5f, 1.0f),                          // Color
                true, false                                                     // Other
        );
        lorryGround.setLocalTranslation(-STORAGE_LENGTH - LANE_WIDTH * LANE_COUNT * 3 - EXTENDS, WORLD_HEIGHT-1.0f, 0.0f);
    }
    private void createTrainGround() {
        // Storage
        Geometry lorryGround = WorldCreator.createBox(
                null,                                                           // Parent
                new Vector3f(STORAGE_LENGTH + LANE_WIDTH * LANE_COUNT * 3, 1.0f, EXTENDS),       // Size
                new ColorRGBA(0.6f, 0.8f, 0.5f, 1.0f),                          // Color
                true, false                                                     // Other
        );
        lorryGround.setLocalTranslation(0.0f, WORLD_HEIGHT-1.0f, -STORAGE_WIDTH - 2 * LANE_WIDTH * LANE_COUNT - EXTENDS);
    }
    private void createOtherGround() {
        Geometry belowMainGround = WorldCreator.createBox(
                null,                                                           // Parent
                new Vector3f(
                    STORAGE_LENGTH + LANE_WIDTH * LANE_COUNT * 3 + EXTENDS, 
                    -WORLD_DEPTH, 
                    STORAGE_WIDTH + LANE_WIDTH * LANE_COUNT * 2 + EXTENDS * 2),       // Size
                new ColorRGBA(0.0f, 0.0f, 0.0f, 1.0f),                          // Color
                false, false                                                     // Other
        );
        belowMainGround.setLocalTranslation(-EXTENDS, WORLD_HEIGHT - 1.5f + WORLD_DEPTH, 0.0f);
        
        Geometry aBlock = WorldCreator.createBox(
                null,                                                           // Parent
                new Vector3f(
                    1.0f, 
                    LAND_HEIGHT_EXTEND, 
                    LANE_WIDTH * LANE_COUNT * 2 + STORAGE_WIDTH + EXTENDS * 2.0f),       // Size
                new ColorRGBA(0.0f, 0.0f, 0.0f, 1.0f),                          // Color
                true, false                                                     // Other
        );
        aBlock.setLocalTranslation(STORAGE_LENGTH + LANE_WIDTH * LANE_COUNT * 3, LAND_HEIGHT_EXTEND + WORLD_HEIGHT - 0.5f, 0.0f);
        
        Geometry bBlock = WorldCreator.createBox(
                null,                                                           // Parent
                new Vector3f(
                    STORAGE_LENGTH / 2 + LANE_WIDTH * LANE_COUNT, 
                    LAND_HEIGHT_EXTEND, 
                    1.0f),       // Size
                new ColorRGBA(0.0f, 0.0f, 0.0f, 1.0f),                          // Color
                true, false                                                     // Other
        );
        bBlock.setLocalTranslation(STORAGE_LENGTH / 2 + LANE_WIDTH * LANE_COUNT * 2, LAND_HEIGHT_EXTEND + WORLD_HEIGHT - 0.5f, STORAGE_WIDTH + 2 * LANE_WIDTH * LANE_COUNT + EXTENDS * 2.0f);
    }
}
