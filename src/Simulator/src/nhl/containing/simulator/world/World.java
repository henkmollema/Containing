/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.world;

import nhl.containing.simulator.game.PlatformStorage;
import nhl.containing.simulator.simulation.Behaviour;
import nhl.containing.simulator.simulation.Main;
import nhl.containing.simulator.simulation.Point3;
import nhl.containing.simulator.simulation.Transform;
import nhl.containing.simulator.simulation.Utilities;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;
import java.util.List;
import nhl.containing.simulator.game.AGV;
import nhl.containing.simulator.game.Container;
import nhl.containing.simulator.simulation.Point2;

/**
 *
 * @author sietse
 */
public class World extends Behaviour {
    public static final boolean USE_DIFFUSE = true;
    public static final Point2 STORAGE_SIZE = new Point2(47, 2); // x = containers length per storage; y = storage amount
    
    private static final float WORLD_HEIGHT =  0.0f;
    private static final float WORLD_DEPTH = -150.0f;
    private static final float WATER_LEVEL = - 5.0f;
    private static final float LAND_HEIGHT_EXTEND = 100.0f;
    
    private static final float STORAGE_LENGTH = 1550.0f;
    private static final float STORAGE_WIDTH = 600.0f;
    
    private static final float EXTENDS = 50.0f;
    private static final int SEA_SHIP_CRANE_COUNT = 8;
    private static final int TRAIN_CRANE_COUNT = 4;
    private static final int LORRY_CRANE_COUNT = 20;
    
    private static final int AGV_COUNT = 100;
    private static final float LANE_WIDTH = 10.0f;
    private static final int LANE_COUNT = 4;
    
    
    
    /**
     * Container size
     * @return 
     */
    public static Vector3f containerSize() {
        return new Vector3f(2.438f, 2.438f, 12.192f);
    }
    
    // Main
    private DirectionalLight m_sun;
    
    // World
    private List<PlatformStorage> m_storages = new ArrayList<>(0);
    
    // External
    
    
    @Override
    public void awake() {
        m_sun = LightCreator.createSun(ColorRGBA.White, new Vector3f(-0.5f, -0.5f, -0.5f));
        LightCreator.createAmbient(new ColorRGBA(0.2f, 0.2f, 0.2f, 1.0f));
        Main.camera().createShadowsFiler(m_sun);
        
        createObjects();
        
        // 
        Spatial teapot = Main.assets().loadModel("models/Sietse/Train/Thomas_Train.obj");
        //Material defaultMat = new Material( assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        teapot.setMaterial(MaterialCreator.unshaded("models/Sietse/Train/Thomas_Train.png"));
        Main.root().attachChild(teapot);
        //Time.setFixedTimeScale(0.3f);
        
        Spatial mater = Main.assets().loadModel("models/Sietse/Truck/Mater.obj");
        //Material defaultMat = new Material( assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        mater.setMaterial(MaterialCreator.unshaded("models/Sietse/Truck/mater1_lod0.png"));
        mater.setLocalTranslation(-15.0f, 0, 0);
        Main.root().attachChild(mater);
    }
    
    @Override
    public void update() {
        for(PlatformStorage s : m_storages) s.update();
    }
    private void createObjects() {
        createGround();
        
        // Create storage
        Vector3f offset = new Vector3f(-LANE_WIDTH / 2 - STORAGE_LENGTH / 2, WORLD_HEIGHT, -STORAGE_WIDTH);
        for (int i = 0; i < STORAGE_SIZE.y; ++i) {
            
            if (i == 10) // Adding space for the middle road
                offset.x += LANE_WIDTH * LANE_COUNT;
            
            createStorageCell(offset);
            offset.x += containerSize().x * 6 + 30.0f;
        }
        
        // Create lorry
        
        
        
        AGV agv = new AGV();
        agv.setContainer(new Container(null));
        agv.position(new Vector3f(0.0f, 0.0f, -36.0f));
        m_storages.get(0).getParkingSpot(0).agv(agv);
        
        
        for (int i = 0; i < 3; i++) {
            m_storages.get(0).take(new Point3(4, 4, i), 0);
        }
        m_storages.get(0).place(0, new Point3(4, 5, 1));
        
    }
    private void createStorageCell(Vector3f position) {
        Transform t = new Transform();
        PlatformStorage plat = new PlatformStorage(t, Utilities.zero());
        t.attachChild(plat);
        plat.localPosition(Utilities.zero());
        t.position(position);
        m_storages.add(plat);
    }
    private void createLorryCell(Vector3f position) {
        
    }
    private void createInlandCell(Vector3f position) {
        
    }
    private void createShippingCell(Vector3f position) {
        
    }
    private void createTrainCell(Vector3f position) {
        
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
    
    void runTests() {
        SimulatorTests tests = new SimulatorTests();
        tests.createStorage();
        tests.disposeCreated();
        tests.disposeNull();
        tests.getBox();
    }
}
