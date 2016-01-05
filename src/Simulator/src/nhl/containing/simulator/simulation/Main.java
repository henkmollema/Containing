package nhl.containing.simulator.simulation;

import nhl.containing.simulator.framework.Mathf;
import nhl.containing.simulator.framework.Transform;
import nhl.containing.simulator.framework.Time;
import nhl.containing.simulator.framework.Behaviour;
import nhl.containing.simulator.world.World;
import nhl.containing.simulator.gui.GUI;
import nhl.containing.simulator.networking.InstructionDispatcherSimulator;
import nhl.containing.simulator.networking.SimulatorClient;
import nhl.containing.simulator.game.Container;
import nhl.containing.simulator.framework.Utilities.*;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.input.InputManager;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;
import nhl.containing.simulator.game.AGV;
import nhl.containing.simulator.game.AgvPath;

/**
 * Main class
 * 
 * @author sietse
 */
public class Main extends SimpleApplication {

    // Node Keys
    public static final String TRANSFORM_ID_KEY = "TRANSFORM_KEY";
    
    // Singleton
    private static Main m_instance;
    public static Main instance() {
        return m_instance;
    }
    
    private static ExecutorService m_executor;
    public static ExecutorService executorService() {
        return m_executor;
    }
    
    // Time
    public static final float TIME_SCALE_CHANGE_SPEED = 10.0f;
    public static final float MIN_TIME_SCALE = 0.001f;
    public static final float MAX_TIME_SCALE = 100.0f;
    private float m_previousTimeScale = 1.0f;
    
    // Behaviours
    private static List<Behaviour> m_behaviours;
    private float m_fixedUpdateTimer = 0.0f;
    private float m_rawFixedUpdateTimer = 0.0f;
    private nhl.containing.simulator.simulation.Camera m_camera;
    private Input m_input;
    private GUI m_gui;
    private World _world;
    private AgvPath _agvPath;
    
    //Networking
    private static SimulatorClient _simClient;
    private InstructionDispatcherSimulator _dispatcher;
    
    // Transforms
    private static long m_transformID = 0;
    private static List<Transform> m_transforms = new ArrayList<>();
    private static Map<Long, AGV> m_agvs = new HashMap<>();
    private static Map<Long, Container> m_containers = new HashMap<>();
    
    // Lines
    private static List<Line3D> m_lines = new ArrayList<>();
    
    /**
     * ALL BEHAVIOURS HERE
     *        ||
     *        ||
     *       \||/
     *        \/
     */
    private void initBehaviours() {
        
        m_behaviours = new ArrayList<>();
        
        // Init main behaviours
        m_camera = new nhl.containing.simulator.simulation.Camera();
        m_input = new Input();
        m_gui = new GUI();
        _world = new World();

        // Init all behaviours
        Behaviour[] behaviours = new Behaviour[]{
            m_input,
            m_gui,
            m_camera,
            _dispatcher,
            
            // Non-Main
            _world, // Here we create a new world without any help of "the Creator" ;-)
        };
        
        // Init all behaviours
        for (Behaviour behaviour : behaviours) {
            behaviour._baseInit();
        }
    }
    /**
     * Returns simclient 
     * @return 
     */
    public static SimulatorClient getSimClient(){
        return _simClient;
    }
    
    public World getWorld() {
        return _world;
    }
    
    /**
     * Called at init
     */
    @Override
    public void simpleInitApp() {
        m_instance = this; // init singleton
        m_executor = Executors.newSingleThreadExecutor();
        initBehaviours();
        flyCam.setEnabled(false);
        _agvPath = new AgvPath();
        _agvPath.init();

    }

    /**
     * Called every frame
     *
     * @param tpf deltaTime
     */
    @Override
    public void simpleUpdate(float tpf) {

        if (m_camera != null)
            m_camera.stopChange();
            
        Time._updateTime(tpf);
        updateBehaviours();
        
        // Update lines
        for (Line3D l : m_lines)
            l.UpdateMesh();
        for (AGV agv : m_agvs.values())
            agv.update();
        updateTimescale();
        
        if (m_camera != null)
            m_camera.startChange();
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
     * Register Container
     * @param container
     * @return 
     */
    public static long register(Container container) {
        if (!m_containers.containsValue(container)) {
            m_containers.put(Long.valueOf(m_transformID), container);
            return m_transformID;
        }
        return -1;
    }
    /**
     * Register Container
     * @param container
     * @return 
     */
    public static long register(AGV agv) {
        if (!m_agvs.containsValue(agv)) {
            m_agvs.put(Long.valueOf(m_transformID), agv);
            return m_transformID;
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
     * Unregister Container
     * @param container
     * @return 
     */
    public static boolean unregister(AGV agv) {
        return m_agvs.values().remove(agv);
    }
    /**
     * Unregister Container
     * @param container
     * @return 
     */
    public static boolean unregister(Container container) {
        return m_containers.values().remove(container);
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
     * Get Container by id
     * @param id
     * @return 
     */
    public static Container getContainer(long id) {
        return m_containers.get(id);
    }
    
    public static AGV getAgv(long id){
        return m_agvs.get(id);
    }

    /**
     * Updates all behaviours
     */
    private void updateBehaviours() {
        m_fixedUpdateTimer += Time.deltaTime();
        m_rawFixedUpdateTimer += Time.unscaledDeltaTime();

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
        // RawFixedUpdate
        while (m_rawFixedUpdateTimer >= Time.fixedTimeScale()) {
            m_rawFixedUpdateTimer -= Time.fixedTimeScale();
            for (Behaviour behaviour : m_behaviours) {
                behaviour._baseRawFixedUpdate();
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
        app.setPauseOnLostFocus(false);
        app.showSettings = false;
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1920, 1080);
        settings.setBitsPerPixel(32);
        settings.setFrameRate(120);
        app.setSettings(settings);
        app.start();
        
        //Init networking
        _simClient = new SimulatorClient();
        app._dispatcher = new InstructionDispatcherSimulator(app);
        _simClient.controllerCom().setDispatcher(app._dispatcher);
        Thread networkThread = new Thread(_simClient);
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
        float _timescale = TIME_SCALE_CHANGE_SPEED * Time.unscaledDeltaTime();

        if (m_input.getButton("R").isDown()) {
            _timescale = -_timescale;
        } else if (!m_input.getButton("T").isDown()) {
            return;
        }
        
        // Set new time
        _timescale += Time.timeScale();
        _timescale = Mathf.clamp(_timescale, MIN_TIME_SCALE, MAX_TIME_SCALE);
        Time.setTimeScale(_timescale);
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
    public static nhl.containing.simulator.simulation.Camera camera() {
        return instance().m_camera;
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
}
