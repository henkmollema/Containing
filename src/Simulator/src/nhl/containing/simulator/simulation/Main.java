package nhl.containing.simulator.simulation;

import nhl.containing.simulator.world.World;
import nhl.containing.simulator.gui.GUI;
import nhl.containing.simulator.networking.InstructionDispatcherSimulator;
import nhl.containing.simulator.networking.SimulatorClient;
import nhl.containing.simulator.simulation.Utilities.*;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.input.InputManager;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

/**
 *
 * 
 * @author sietse
 */
public class Main extends SimpleApplication {

    public static final String TRANSFORM_ID_KEY = "TRANSFORM_KEY";
    
    // Singleton
    private static Main m_instance;
    public static Main instance() {
        return m_instance;
    }
    
    // Time
    public static final float TIME_SCALE_CHANGE_SPEED = 10.0f;
    public static final float MIN_TIME_SCALE = 0.001f;
    public static final float MAX_TIME_SCALE = 100.0f;
    private float m_previousTimeScale = 1.0f;
    
    // Behaviours
    private static List<Behaviour> m_behaviours;
    private float m_fixedUpdateTimer = 0.0f;
    private nhl.containing.simulator.game.Camera m_camera;
    private Input m_input;
    private GUI m_gui;
    
    //Networking
    private SimulatorClient _simClient;
    private InstructionDispatcherSimulator _dispatcher;
    
    // Transforms
    private static long m_transformID = 0;
    private static List<Transform> m_transforms = new ArrayList<Transform>();
    
    // Lines
    private static List<Line3D> m_lines = new ArrayList<Line3D>();
    
    /**
     * Get JMonkey Camera
     * @return 
     */
    public static com.jme3.renderer.Camera cam() {
        return instance().cam;
    }
    /**
     * Get Sietse Camera
     * @return 
     */
    public static nhl.containing.simulator.game.Camera camera() {
        return instance().m_camera;
    }
    /**
     * Get settings
     * @return 
     */
    public static AppSettings settings() {
        return instance().settings;
    }
    /**
     * Get simulator client
     * @return 
     */
    public SimulatorClient simClient() {
        return _simClient;
    }
    /**
     * Get input manager
     * @return 
     */
    public static InputManager inputManager() {
        return instance().inputManager;
    }
    /**
     * Get asset manager
     * @return 
     */
    public static AssetManager assets() {
        return instance().assetManager;
    }
    /**
     * Get GUI font
     * @return 
     */
    public static BitmapFont guiFont() {
        return instance().guiFont;
    }
    /**
     * Set GUI font
     * @param font
     * @return 
     */
    public static BitmapFont guiFont(BitmapFont font) {
        return instance().guiFont = font;
    }
    /**
     * Get Sietse input
     * @return 
     */
    public static Input input() {
        return instance().m_input;
    }
    /**
     * Get root node
     * @return 
     */
    public static Node root() {
        return instance().rootNode;
    }
    /**
     * Get GUI root node
     * @return 
     */
    public static Node guiRoot() {
        return instance().guiNode;
    }
    /**
     * Get renderer manager
     * @return 
     */
    public static RenderManager renderer() {
        return instance().renderManager;
    }
    /**
     * Get viewport
     * @return 
     */
    public static ViewPort view() {
        return instance().viewPort;
    }
    
    
    /**
     * HERE COME ALL BEHAVIOURS
     *
     *
     *
     *
     */
    private void initBehaviours() {
        
        m_behaviours = new ArrayList<Behaviour>();
        
        // Init main behaviours
        m_camera = new nhl.containing.simulator.game.Camera();
        m_input = new Input();
        m_gui = new GUI();

        // Init all behaviours
        Behaviour[] behaviours = new Behaviour[]{
            m_input,
            m_gui,
            m_camera,
            
            // Non-Main
            new World(), // Here we create a new world without any help of "the Creator" ;-)
        };
        
        // Init all behaviours
        for (Behaviour behaviour : behaviours) {
            behaviour._baseInit();
        }
    }
    /**
     * Called at init
     */
    @Override
    public void simpleInitApp() {
        m_instance = this; // init singleton
        initBehaviours();
        flyCam.setEnabled(false);
    }

    /**
     * Called every frame
     *
     * @param tpf deltaTime
     */
    @Override
    public void simpleUpdate(float tpf) {

        Time._updateTime(tpf);
        updateBehaviours();
        
        // Update lines
        for (Line3D l : m_lines) {
            l.UpdateMesh();
        }
        
        updateTimescale();
    }

    /**
     * Called on render
     *
     * @param rm Renderer
     */
    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    /**
     * Register behaviour
     *
     * @param behaviour Behaviour to register
     * @return Register success
     */
    public static boolean register(Behaviour behaviour) {
        if (!m_behaviours.contains(behaviour)) {
            m_behaviours.add(behaviour);
            behaviour.awake();
            return true;
        }
        return false;
    }
    /**
     * Register Transform
     * @param transform
     * @return 
     */
    public static long register(Transform transform) {
        if (!m_transforms.contains(transform)) {
            m_transforms.add(transform);
            return m_transformID++;
        }
        return -1;
    }
    /**
     * Register line
     * @param line 
     */
    public static boolean register(Line3D line) {
        if (!m_lines.contains(line)) {
            m_lines.add(line);
            return true;
        }
        return false;
    }
    
    /**
     * Unregister behaviour
     * @param behaviour behaviour to unregister
     * @return unregister success
     */
    public static boolean unregister(Behaviour behaviour) {
        return m_behaviours.remove(behaviour);
    }
    /**
     * Unregister transform
     * @param transform
     * @return 
     */
    public static boolean unregister(Transform transform) {
        return m_transforms.remove(transform);
    }
    /**
     * Unregister line
     * @param line
     * @return 
     */
    public static boolean unregister(Line3D line) {
        return m_lines.remove(line);
    }
    
    /**
     * Get transform by id
     * @param id
     * @return 
     */
    public static Transform getTransform(long id) {
        for (Transform t : m_transforms) {
            if (t.id() == id)
                return t;
        }
        return null;
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
        while (m_fixedUpdateTimer >= Time.fixedTimeScale()) {
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
    /**
     * Main, do not touch,
     * unless you know what you are doing
     * @param args 
     */
    public static void main(String[] args) {     
        Logger.getLogger("").setLevel(Level.SEVERE);
        Main app = new Main();
        app.showSettings = false;
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1920, 1080);
        settings.setBitsPerPixel(32);
        settings.setFrameRate(120);
        app.setSettings(settings);
        app.start();
        
        //Init networking
        app._simClient = new SimulatorClient();
        app._dispatcher = new InstructionDispatcherSimulator(app);
        app._simClient.controllerCom().setDispatcher(app._dispatcher);
        Thread networkThread = new Thread(app._simClient);
        networkThread.setDaemon(true);
        networkThread.setName("Network Simulator");
        networkThread.start();
    }
    
    /**
     * Pause/Unpause
     */
    public void togglePause() {
        if (Time.timeScale() < 0.001f) {
            // unpause
            Time.setTimeScale(m_previousTimeScale);
        } else {
            // Pause
            m_previousTimeScale = Time.timeScale();
            Time.setTimeScale(0.0f);
        }
    }
    
    /**
     * Update timescale
     */
    public void updateTimescale() {
        float __temp = TIME_SCALE_CHANGE_SPEED * Time.deltaTime();

        if (m_input.getButton("R").isDown()) {
            __temp = -__temp;
        } else if (!m_input.getButton("T").isDown()) {
            return;
        }
        
        // Set new time
        __temp += Time.timeScale();
        __temp = Mathf.clamp(__temp, MIN_TIME_SCALE, MAX_TIME_SCALE);
        Time.setTimeScale(__temp);
    }
    /**
     * Set timescale to default value (1.0)
     */
    public void resetTimescale() {
        Time.setTimeScale(1.0f);
    }
    /**
     * Exit app
     */
    public void exit() {
        instance().stop();
    }
}
