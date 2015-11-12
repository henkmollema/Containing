package Simulation;

import Utilities.Utilities;
import World.*;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.input.FlyByCamera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import java.util.List;

/**
 * test
 * @author sietse
 */
public class Main extends SimpleApplication {

    // Singleton
    private static Main m_instance;
    public static Main instance() {
        return m_instance;
    }

    // Something
    public static AssetManager assets() {
        return instance().assetManager;
    }
    public static Node root() {
        return instance().rootNode;
    }
    public static ViewPort view() {
        return instance().viewPort;
    }
    
    // Behaviours
    private static List<Behaviour> m_behaviours;
    private float m_fixedUpdateTimer = 0.0f;
    
    // Main Behaviours
    private Game.Camera m_camera;
    
    public Game.Camera camera() {
        return m_camera;
    }
    public FlyByCamera flyCamera() {
        return flyCam;
    }
    
    /** HERE COME ALL BEHAVIOURS
     * 
     * 
     * 
     * 
     */
    private void initBehaviours() {
        // Init main behaviours
        m_camera = new Game.Camera();
        
        // Init all behaviours
        Behaviour[] behaviours = new Behaviour[] {
            // Main
            m_camera,
            
            // Non-Main
            new World(),
            new TestBehaviour()
        };
        // Init all behaviours
        for (Behaviour behaviour : behaviours) {
            behaviour._baseInit();
        }
    }
    /**
     * Create world here
     */
    private void initWorld() {
        
    }
    
    /**
     * Called at init
     */
    @Override
    public void simpleInitApp() {
        m_instance = this;
        m_behaviours = new ArrayList<Behaviour>();
        initBehaviours();
        initWorld();
    }
    /**
     * Called every frame
     * @param tpf deltaTime
     */
    @Override
    public void simpleUpdate(float tpf) {
        
        Time._updateTime(tpf);
        updateBehaviours();
    }
    /**
     * Called on render
     * @param rm Renderer
     */
    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    /**
     * Register behaviour
     * @param behaviour Behaviour to register
     * @return Register success
     */
    public static boolean Register(Behaviour behaviour) {
        if (!m_behaviours.contains(behaviour)) {
            m_behaviours.add(behaviour);
            behaviour.awake();
            return true;
        }
        return false;
    }
    /**
     * Unregister behaviour
     * @param behaviour behaviour to unregister
     * @return unregister success
     */
    public static boolean Unregister(Behaviour behaviour) {
        return m_behaviours.remove(behaviour);
    }
    /**
     * Updates all behaviours
     */
    private void updateBehaviours() {
        m_fixedUpdateTimer += Time.deltaTime();
        
        // Update
        for (Behaviour behaviour : m_behaviours) {
            behaviour._baseUpdate();
        }
        
        // FixedUpdate
        while(m_fixedUpdateTimer >= Time.fixedTimeScale()) {
            m_fixedUpdateTimer -= Time.fixedTimeScale();
            for (Behaviour behaviour : m_behaviours) {
                behaviour._baseFixedUpdate();
            }
        }
        
        // LateUpdate
        for (Behaviour behaviour : m_behaviours) {
            behaviour._baseLateUpdate();
        }
    }
    
    public static void main(String[] args) {
        Main app = new Main();
        app.showSettings = false;
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1280, 600);
        settings.setBitsPerPixel(32);
        app.setSettings(settings);
        app.start();
    }
}