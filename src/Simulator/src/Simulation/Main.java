package Simulation;

import Game.Camera;
import Utilities.*;
import World.*;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import java.util.Arrays;
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
    
    // Lines
    private static List<Line3D> m_lines;
    
    // Camera
    private Game.Camera m_camera;
    public Game.Camera camera() {
        return m_camera;
    }
    public FlyByCamera flyCamera() {
        return flyCam;
    }
    
    // Input
    public static InputManager inputManager() {
        return instance().inputManager;
    }
    private static float m_previousTimescale = Time.timeScale();
    private Input m_input;
    /** HERE COME ALL BEHAVIOURS
     * 
     * 
     * 
     * 
     */
    private void initBehaviours() {
        
        // Init main behaviours
        m_camera = new Game.Camera();
         m_input = new Input();
        
        // Init all behaviours
        Behaviour[] behaviours = new Behaviour[] {
            m_camera,
            m_input,
            
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
        m_lines = new ArrayList<Line3D>();
        Line3D[] __t = new Line3D[] {
            new Line3D(
                MaterialCreator.diffuse(new ColorRGBA(0.4f, 0.6f, 0.8f, 1.0f)),
                new Line3DNode(new Vector3f(0.0f, 00.0f, 00.0f), 0.1f, ColorRGBA.Blue), 
                new Line3DNode(new Vector3f(0.0f, 10.0f, 00.0f), 1.0f, ColorRGBA.Blue),
                new Line3DNode(new Vector3f(0.0f, 10.0f, 10.0f), 1.0f, ColorRGBA.Blue))
        };
        m_lines.addAll(Arrays.asList(__t));
    }
    private void updateWorld() {
        for (Line3D l : m_lines) {
            l.UpdateMesh();
        }
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
        updateWorld();
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
    
    //Input Listeners   
    public static ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("pause") && !keyPressed) {
                if (Time.timeScale() < 0.001f) {
                    Time.setTimeScale(m_previousTimescale);
                } else {
                    m_previousTimescale = Time.timeScale();
                    Time.setTimeScale(0.0f);
                }
            } else if (name.equals("timescale-lower") && !keyPressed) {
                float __test = 50.0f;
                Time.setTimeScale(Time.timeScale() * (1.0f - __test * Time.unscaledDeltaTime()));
                
            } else if (name.equals("timescale-higher") && !keyPressed) {
                float __test = 50.0f;
                Time.setTimeScale(Time.timeScale() * (1.0f + __test * Time.unscaledDeltaTime()));
                
            } else if (name.equals("timescale-lower") && !keyPressed) {
                float __test = 50.0f;
                Time.setTimeScale(Time.timeScale() * (1.0f - __test * Time.unscaledDeltaTime()));
            } /*else if (name.equals("view-switch") && !keyPressed) {
                Main.instance().camera().viewType(Main.instance().camera().viewType() == Camera.ViewType.Fly ? Camera.ViewType.RTS : Camera.ViewType.Fly);
            }*/
        }
    };
  }