/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.world;

import nhl.containing.simulator.game.StoragePlatform;
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
    public static final Point2 STORAGE_SIZE = new Point2(22, 1); // x = containers length per storage; y = storage amount
    
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
    private List<StoragePlatform> m_storages = new ArrayList<StoragePlatform>(0);
    
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
        for(StoragePlatform s : m_storages) s.update();
    }
    private void createObjects() {
        Vector3f offset = Utilities.up();
        for (int i = 0; i < STORAGE_SIZE.y; ++i) {
            createStorage(offset);
            offset.x += containerSize().x * 6 + 15.0f;
        }
        
        
        
        AGV agv = new AGV();
        agv.setContainer(new Container(null));
        agv.position(new Vector3f(0.0f, 0.0f, -36.0f));
        m_storages.get(0).getParkingSpot(0).agv(agv);
        
        
        for (int i = 0; i < 3; i++) {
            m_storages.get(0).take(new Point3(4, 4, i), 0);
        }
        m_storages.get(0).place(0, new Point3(4, 5, 1));
        
        
        Geometry g = WorldCreator.createBox(null, new Vector3f(500.0f, 1.0f, 500.0f));
        g.setLocalTranslation(0.0f, -1.0f, 0.0f);
    }
    private void createStorage(Vector3f position) {
        Transform t = new Transform();
        StoragePlatform plat = new StoragePlatform(t, Utilities.zero());
        t.attachChild(plat);
        plat.localPosition(Utilities.zero());
        t.position(position);
        m_storages.add(plat);
    }
    
    void runTests() {
        SimulatorTests tests = new SimulatorTests();
        tests.createStorage();
        tests.disposeCreated();
        tests.disposeNull();
        tests.getBox();
    }
}
