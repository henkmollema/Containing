package nhl.containing.simulator.simulation;

import nhl.containing.simulator.framework.Behaviour;
import nhl.containing.simulator.simulation.Main;
import nhl.containing.simulator.framework.Mathf;
import nhl.containing.simulator.framework.Time;
import nhl.containing.simulator.framework.Utilities;
import nhl.containing.simulator.framework.Transform;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.FogFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;


/**
 * Camera controller:
 * 
 * RTS Controller
 *  Controls (all)
 *      Scroll: zoom
 *      Left Mouse button: select object to follow (when clicked but no valid object, object to follow is null)
 * 
 *  Controls (when no object is selected)
 *      A: move left horizontal
 *      S: move backwards horizontal
 *      D: move right horizontal
 *      W: move forward horizontal
 *      Q: rotate counter clockwhise
 *      R: rotate clockwhise
 *  Controls (when object is selected)
 *      A: rotate around object clockwhise
 *      S: move away from object
 *      D: rotate around object counterclockwhise
 *      W: move towards
 * 
 * Fly Controller
 *  Controls
 *      A: move left
 *      S: move backwards
 *      D: move right
 *      W: move forwards
 *      Q: move down
 *      E: move up
 *      Mousemove: look
 * 
 * @author sietse
 */
public class Camera extends Behaviour {
    
    // 
    private final float CAMERA_RENDER_DISTANCE = 5000.0f;        // The distance how far the camera can render
    
    // Fly
    private final float FLY_CAMERA_SPEED_DEFAULT = 30.0f;       // Default movement speed of the fly camera
    private final float FLY_CAMERA_SPEED_FAST = 180.0f;          // Fast (shift) movement speed of the fly camera
    private final float FLY_CAMERA_SPEED_SLOW = 8.0f;           // Slow (ctrl) movemet speed of the fly camera
    private final float FLY_CAMERA_DAMPING = 20.0f;             // Amount of movement damping
    private Vector3f m_flyVelocity = Utilities.zero();          // Current fly camera velocity
    
    // RTS
    private final float RTS_CAMERA_SPEED_DEFAULT = 120.0f;      // Default movement speed of the RTS camera
    private final float RTS_CAMERA_SPEED_FAST = 280.0f;         // Fast (shift) movement speed of the RTS camera
    private final float RTS_CAMERA_SPEED_SLOW = 40.0f;          // Slow (ctrl) movement speed of the RTS camera
    private final float RTS_CAMERA_ROTATION_SPEED = 70.0f;      // Rotation speed of the RTS camera
    private final float RTS_CAMERA_ZOOM_SPEED = 4.0f;           // Zoom/Scroll speed of the RTS camea
    private final float RTS_CAMERA_SMOOTH = 0.02f;              // Amount of position smoothing of the RTS camera
    private final float RTS_CAMERA_ZOOM_SMOOTH = 0.05f;         // Amout of zoom/scroll smoothing applied to the RTS camera
    private final float RTS_MIN_CAMERA_DISTANCE = 10.0f;        // Minimum amount of distance between the camera and the look at target
    private final float RTS_MAX_CAMERA_DISTANCE = 2000.0f;      // Maximum amount of distance between the camera and the look at target
    private float m_rtsCameraRotation = 0.0f;                   // Current rotation of the RTS camera
    private float m_rtsCameraTargetDistance = 500.0f;           // Desired distance between the RTS camera and the look at target
    private float m_rtsCameraCurrentDistance = 500.0f;          // Current distance betweem the RTS camera and the look at target
    private Float m_rtsCameraDistanceVelocity = 0.0f;           // Current stored smooth velocity of the RTS caemra zoom/scroll value
    private Vector2f m_rtsPositionVelocity = new Vector2f();    // Current stored smooth velocity of the RTS camera movement value
    
    // Shadows
    private final int SHADOW_MAP_RESOLUTION = 2048;             // Resolution of the shadowmap
    private final float SHADOW_INTENSITY = 0.4f;                // Intensity of the shadowmap
    
    // SSAO
    
    // FOG
    private final float FOG_DENSITY = 1.0f;                     // Fog density
    private final ColorRGBA FOG_COLOR =                         // Fog color
            new ColorRGBA(0.6f, 0.7f, 0.9f, 1.0f);
    
    // Bloom
    private final float m_bloomBlurScale = 0.5f;                // Bloom blur scale
    private final float m_bloomExposurePower = 1.0f;            // Bloom exposure power
    private final float m_bloomExposureCutoff = 0.2f;           // Bloom exposure cutoff
    private final float m_bloomIntensity = 0.3f;                // Bloom intensity
    
    // Components
    private Transform m_transform;                              // Camera transform
    private Transform m_target;                                 // Look At Target
    private Vector3f m_previousTargetPosition= Utilities.zero();// Previous Target Position
    private CameraMode m_cameraMode = CameraMode.RTS;           // Camera mode
    private FilterPostProcessor m_postProcessor;                // Image FX
    
    private int m_safeFrameInit = 0;
    
    /**
     * Get Camera Transform
     * @return Camera Transform
     */
    public Transform tranform() {
        return m_transform;
    }
    /**
     * Get Camera Mode (RTS or FLY)
     * @return camera mode
     */
    public CameraMode cameraMode() {
        return m_cameraMode;
    }
    /**
     * Switch between RTS and Fly camera mode
     */
    public void toggleCameraMode() {
        if (m_cameraMode == CameraMode.Fly) {
            // Camera mode is fly, so set to rts
            m_cameraMode = CameraMode.RTS;
            onStartRTS();
        } else {
            // Camera mode is rts, so set to fly
            m_cameraMode = CameraMode.Fly;
            onStartFly();
        }
    }
    /**
     * Get FilerPostProcessor of camera
     * Create one if not existing
     * @return Camera Post Processor
     */
    public FilterPostProcessor postProcessor() {
        
        if (m_postProcessor == null) // Create new if not exist
            m_postProcessor = new FilterPostProcessor(Main.assets());
        
        return m_postProcessor;
    }
    
    /**
     * On create
     */
    @Override
    public void awake() {
        
        // Init transform
        m_transform = new Transform();
        
        // Create image fx
        createSSAO(); 
        createFog();
        createBloom();
    }
    
    public void my_start() {
        
        // Set image fx
        Main.view().addProcessor(postProcessor());
        Main.cam().setLocation(new Vector3f(-500.0f, 50.0f, -300.0f));
        
        // Init camera modes
        onStartRTS();
        onStartFly();
        
        // Set render distance
        Main.cam().setFrustumFar(CAMERA_RENDER_DISTANCE);
    }
    /**
     * On every frame
     */
    @Override
    public void rawUpdate() {
        
        /**
         * JMonkey is F*cked up here
         * When setting the camera position
         * before the world positioning goes wrong.
         * There are no indication of wrong code!
         */
        if (++m_safeFrameInit == 2) {
            my_start();
            return;
        } else if (m_safeFrameInit < 2) {
            return;
        }
        
        // Update camera modes
        onUpdateFly();
        onUpdateRTS();
    }
    
    /**
     * Create shadown
     * @param sun Source
     */
    public void createShadowsFiler(DirectionalLight sun) {
        
        // Create shadow renderer
        DirectionalLightShadowRenderer shadowRenderer = new DirectionalLightShadowRenderer(Main.assets(), SHADOW_MAP_RESOLUTION, 3);
        
        // Init shadows
        shadowRenderer.setLight(sun);
        Main.view().addProcessor(shadowRenderer);
        shadowRenderer.setShadowIntensity(SHADOW_INTENSITY);
        
        // Create shadow filter
        DirectionalLightShadowFilter shadowFilter = new DirectionalLightShadowFilter(Main.assets(), SHADOW_MAP_RESOLUTION, 3);
        shadowFilter.setLight(sun);
        shadowFilter.setEnabled(true);
        
        // Add to camera image FX
        postProcessor().addFilter(shadowFilter);
    }
    /**
     * Create ambient occlusion
     */
    public void createSSAO() {
        SSAOFilter ssaoFilter = new SSAOFilter(6.94f, 10.92f, 0.33f, 0.61f);
        postProcessor().addFilter(ssaoFilter);
    }
    /**
     * Create fog
     */
    public void createFog() {
        FogFilter fogFilter = new FogFilter();
        fogFilter.setEnabled(true);
        fogFilter.setFogColor(FOG_COLOR);
        fogFilter.setFogDensity(FOG_DENSITY);
        postProcessor().addFilter(fogFilter);
    }
    /**
     * Create bloom
     */
    public void createBloom() {
        BloomFilter bloomFilter = new BloomFilter();
        bloomFilter.setDownSamplingFactor(2);
        bloomFilter.setBlurScale(m_bloomBlurScale);
        bloomFilter.setExposurePower(m_bloomExposurePower);
        bloomFilter.setExposureCutOff(m_bloomExposureCutoff);
        bloomFilter.setBloomIntensity(m_bloomIntensity);
        
        postProcessor().addFilter(bloomFilter);
    }
    
    /**
     * Set Lookat Target
     * @param t Target
     */
    public void setTarget(Transform t) {
        
        if (t == null) { // Disable look at
            if (m_target == null)
                return;
            
            // Reset angles
            m_rtsCameraRotation = Main.cam().getRotation().toAngles(null)[1] * Mathf.Rad2Deg;
            m_previousTargetPosition = Utilities.zero();
            
        } else { // Set previous position to current target position
            m_previousTargetPosition = t.position();
        }
        
        // Set target
        m_target = t;
    }
    /**
     * Zoom in for RTS camera
     * @param amount 
     */
    public void zoom(float amount) {
        m_rtsCameraTargetDistance += amount * RTS_CAMERA_ZOOM_SPEED;
        m_rtsCameraTargetDistance = Mathf.clamp(m_rtsCameraTargetDistance, RTS_MIN_CAMERA_DISTANCE, RTS_MAX_CAMERA_DISTANCE);
    }
    /**
     * Init RTS camera
     */
    public void onStartRTS() {
        if (m_cameraMode != CameraMode.RTS)
            return;
        
        // Enable cursor
        Main.inputManager().setCursorVisible(true);
    }
    /**
     * Update RTS camera mode
     */
    public void onUpdateRTS() {
        // Smooth out camera
        m_rtsCameraCurrentDistance = Mathf.smoothdamp(
                m_rtsCameraCurrentDistance, 
                m_rtsCameraTargetDistance, 
                m_rtsCameraDistanceVelocity, 
                RTS_CAMERA_ZOOM_SMOOTH, 
                1000.0f, Time.unscaledDeltaTime());
        
        /**
         * Stop when not in rts mode
         */
        if (m_cameraMode != CameraMode.RTS) {
            return;
        }
        
        /**
         * Get new position
         * Direction * input * Unit_Horizontal
         */
        Vector3f newPosition = new Vector3f();
        newPosition = newPosition.add(Main.cam().getUp().add(Main.cam().getDirection()).mult(Main.input().rawInputAxes().y));
        newPosition = newPosition.add(Main.cam().getLeft().mult(-Main.input().rawInputAxes().x));
        newPosition = Utilities.Horizontal(newPosition);
        
        if (newPosition.lengthSquared() < 0.001f) {
            // No movement
            newPosition = new Vector3f();
        } else {
            // Normalize movement
            newPosition = newPosition.normalize().mult(Time.unscaledDeltaTime());
            
            // Set move speed
            float multiplier = Mathf.log10(m_rtsCameraCurrentDistance - RTS_MIN_CAMERA_DISTANCE);
            if (multiplier < 1.0f)
                multiplier = 1.0f;
            
            // Multiply with speed input shift(fast)/ctrl(slow)/none(default)
            multiplier *=  (Main.input().getButton("Shift").isDown() ? RTS_CAMERA_SPEED_FAST : (Main.input().getButton("Ctrl").isDown() ? RTS_CAMERA_SPEED_SLOW : RTS_CAMERA_SPEED_DEFAULT));
            newPosition = newPosition.mult(multiplier);
        }
        
        // Set new position
        newPosition = newPosition.add(Main.cam().getLocation());
        newPosition.y = m_rtsCameraCurrentDistance;
        
        // Previous and target position
        Vector2f _target = new Vector2f(newPosition.x, newPosition.z);
        Vector2f _previous = new Vector2f(
                Main.cam().getLocation().x,
                Main.cam().getLocation().z);
        
        // Smooth out horizontal position
        _previous = Mathf.smoothdamp(
                _previous, 
                _target, 
                m_rtsPositionVelocity,
                RTS_CAMERA_SMOOTH, 1000.0f, Time.unscaledDeltaTime());
        newPosition = new Vector3f(_previous.x, newPosition.y, _previous.y);
        
        // Rotation
        if (Main.input().getButton("E").isDown())
            m_rtsCameraRotation -= RTS_CAMERA_ROTATION_SPEED * Time.unscaledDeltaTime();
        else if (Main.input().getButton("Q").isDown())
            m_rtsCameraRotation += RTS_CAMERA_ROTATION_SPEED * Time.unscaledDeltaTime();
        
        // Fix rotation
        m_rtsCameraRotation %= 360.0f;
        
        // Set look At
        if (m_target == null) {
            // Set default
            Main.cam().setLocation(newPosition);
            Main.cam().lookAt(Utilities.rotateY(new Vector3f(50.0f, 0.0f, 50.0f), m_rtsCameraRotation).add(Utilities.Horizontal(newPosition)), Vector3f.UNIT_Y);
        } else {
            // Set target
            Main.cam().setLocation(newPosition.add(m_target.position().subtract(m_previousTargetPosition)));
            Main.cam().lookAt(m_target.position(), Utilities.up());
            m_previousTargetPosition = m_target.position();
        }
    }
    
    /**
     * First frame fly camera is enabled
     */
    public void onStartFly() {
        if (m_cameraMode != CameraMode.Fly)
            return;
        
        // Disable cursor
        Main.inputManager().setCursorVisible(false);
    }
    /**
     * Every frame on update
     */
    public void onUpdateFly() {
        
        /**
         * Return when not in fly mode
         */
        if (m_cameraMode != CameraMode.Fly)
            return;
        
        // Get new position
        Vector3f movement = Main.cam().getDirection().mult(Main.input().rawInputAxes().y);
        movement = movement.subtract(Main.cam().getLeft().mult(Main.input().rawInputAxes().x));
        
        // World up/down (up has prior)
        if (Main.input().getButton("E").isDown()) {
            movement = movement.add(Utilities.up());
        } else if (Main.input().getButton("Q").isDown()) {
            movement = movement.add(Utilities.down());
        }
        
        if (movement.lengthSquared() < 0.01f) {
            // No movement
            movement = Utilities.zero();
        } else {
            // Normalize movement
            movement = movement.normalize();
            
            // Set speed
            movement = movement.mult((Main.input().getButton("Shift").isDown() ? FLY_CAMERA_SPEED_FAST : (Main.input().getButton("Ctrl").isDown() ? FLY_CAMERA_SPEED_SLOW : FLY_CAMERA_SPEED_DEFAULT)));
            movement = movement.mult(Time.unscaledDeltaTime());
        }
        
        m_flyVelocity = m_flyVelocity.add(movement);
        m_flyVelocity = m_flyVelocity.divide(1.0f + FLY_CAMERA_DAMPING * Time.unscaledDeltaTime());
        
        
        // Set new position
        Main.cam().setLocation(m_flyVelocity.add(Main.cam().getLocation()));
        
        // Set rotation
        float[] __rot = Main.cam().getRotation().toAngles(null);
        __rot[0] += Main.input().mouseMove().y;
        __rot[1] -= Main.input().mouseMove().x;
        
        // Angles to quaternion
        Quaternion __q = Quaternion.IDENTITY;
        __q = __q.fromAngles(__rot);
        
        Main.cam().setAxes(__q);
    }
}
