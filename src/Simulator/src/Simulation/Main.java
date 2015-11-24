package Simulation;

import World.MaterialCreator;
import GUI.GUI;
import Networking.InstructionDispatcherSimulator;
import Networking.SimulatorClient;
import Simulation.Utilities.*;
import World.*;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.*;

/**
 * test
 *
 * 
 * @author sietse
 */
public class Main extends SimpleApplication {

    public static final String TRANSFORM_ID_KEY = "TRANSFORM_KEY";
    public static final float MIN_TIME_SCALE = 0.001f;
    public static final float MAX_TIME_SCALE = 100.0f;
    
    // Singleton
    private static Main m_instance;
    public static Main instance() {
        return m_instance;
    }
    
    // 
    private float m_previousTimeScale = 1.0f;
    // Behaviours
    private static List<Behaviour> m_behaviours;
    private float m_fixedUpdateTimer = 0.0f;
    // Transforms
    private static long m_transformID = 0;
    private static List<Transform> m_transforms = new ArrayList<Transform>();
    // Lines
    private static List<Line3D> m_lines = new ArrayList<Line3D>();
    private Input m_input;
    private GUI m_gui;
    // Camera
    private Game.Camera m_camera;

    public static com.jme3.renderer.Camera cam() {
        return instance().cam;
    }

    public static Game.Camera camera() {
        return instance().m_camera;
    }

    // 
    public static AppSettings settings() {
        return instance().settings;
	}
    //Networking
    SimulatorClient _simClient;
    InstructionDispatcherSimulator _dispatcher;
    
    public SimulatorClient simClient()
    {
        return _simClient;
    }
    
    // Input
    public static InputManager inputManager() {
        return instance().inputManager;
    }
    public static AssetManager assets() {
        return instance().assetManager;
    }
    public static BitmapFont guiFont() {
        return instance().guiFont;
    }
    public static BitmapFont guiFont(BitmapFont font) {
        return instance().guiFont = font;
    }
    public static Input input() {
        return instance().m_input;
    }
    public static Node root() {
        return instance().rootNode;
    }
    public static Node guiRoot() {
        return instance().guiNode;
    }
    public static RenderManager renderer() {
        return instance().renderManager;
    }
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
        // Init main behaviours
        m_camera = new Game.Camera();
        m_input = new Input();
        m_gui = new GUI();

        // Init all behaviours
        Behaviour[] behaviours = new Behaviour[]{
            m_input,
            m_gui,
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
        
        //m_lines = new ArrayList<Line3D>();
        /*Line3D[] __t = new Line3D[]{
            new Line3D(
            MaterialCreator.diffuse(new ColorRGBA(0.4f, 0.6f, 0.8f, 1.0f)),
            new Line3DNode(new Vector3f(0.0f, 00.0f, 00.0f), 0.1f, ColorRGBA.Blue),
            new Line3DNode(new Vector3f(0.0f, 10.0f, 00.0f), 1.0f, ColorRGBA.Blue),
            new Line3DNode(new Vector3f(0.0f, 10.0f, 10.0f), 1.0f, ColorRGBA.Blue))
        };
        m_lines.addAll(Arrays.asList(__t));
        * */
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
        updateWorld();
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

    public static long register(Transform transform) {
        if (!m_transforms.contains(transform)) {
            m_transforms.add(transform);
            return m_transformID++;
        }
        return -1;
    }
    
    public static void register(Line3D line) {
        m_lines.add(line);
    }
    
    /**
     * Unregister behaviour
     * @param behaviour behaviour to unregister
     * @return unregister success
     */
    public static boolean unregister(Behaviour behaviour) {
        return m_behaviours.remove(behaviour);
    }
    public static boolean unregister(Transform transform) {
        return m_transforms.remove(transform);
    }
    
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

    public static void main(String[] args) {     
        Logger.getLogger("").setLevel(Level.SEVERE);
        Main app = new Main();
        app.showSettings = false;
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1920, 1080);
        settings.setBitsPerPixel(32);
        app.setSettings(settings);
        app.start();
        
        //Init networking
        app._simClient = new SimulatorClient();
        app._dispatcher = new InstructionDispatcherSimulator(app);
        app._simClient.getComProtocol().setDispatcher(app._dispatcher);
        new Thread(app._simClient).start();
    }

    public void togglePause() {
        if (Time.timeScale() < 0.001f) {
            // unpause
            Time.setTimeScale(m_previousTimeScale);
        } else {
            m_previousTimeScale = Time.timeScale();
            Time.setTimeScale(0.0f);
        }
    }

    public void updateTimescale() {
        float __temp = 10.0f * Time.deltaTime();

        if (m_input.getButton("R").isDown()) {
            __temp = -__temp;
        } else if (!m_input.getButton("T").isDown()) {
            return;
        }

        __temp += Time.timeScale();
        __temp = Mathf.clamp(__temp, MIN_TIME_SCALE, MAX_TIME_SCALE);
        Time.setTimeScale(__temp);
    }

    public void resetTimescale() {
        Time.setTimeScale(1.0f);
    }

    public void exit() {
        
        instance().stop();
    }
}
