/*
 * Camera cotnroller
 * 
 * RTS and FLY
 */
package nhl.containing.simulator.game;

import nhl.containing.simulator.simulation.Behaviour;
import nhl.containing.simulator.simulation.Main;
import nhl.containing.simulator.simulation.Mathf;
import nhl.containing.simulator.simulation.Time;
import nhl.containing.simulator.simulation.Utilities;
import nhl.containing.simulator.simulation.Transform;
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
    private final float CAMERA_RENDER_DISTANCE = 100.0f;
    
    // Fly
    private final float FLY_CAMERA_SPEED_DEFAULT = 15.0f;
    private final float FLY_CAMERA_SPEED_FAST = 30.0f;
    private final float FLY_CAMERA_SPEED_SLOW = 8.0f;
    
    // RTS
    private final float RTS_CAMERA_SPEED_DEFAULT = 15.0f;
    private final float RTS_CAMERA_SPEED_FAST = 30.0f;
    private final float RTS_CAMERA_SPEED_SLOW = 8.0f;
    private final float RTS_CAMERA_ROTATION_SPEED = 50.0f;
    private final float RTS_CAMERA_ZOOM_SPEED = 4.0f;
    private final float RTS_CAMERA_SMOOTH = 0.02f;
    private final float RTS_CAMERA_ZOOM_SMOOTH = 0.05f;
    private final float RTS_MIN_CAMERA_DISTANCE = 3.0f;
    private final float RTS_MAX_CAMERA_DISTANCE = 80.0f;
    private float m_rtsCameraRotation = 0.0f;
    private float m_rtsCameraTargetDistance = 25.0f;
    private float m_rtsCameraCurrentDistance = 25.0f;
    private Float m_rtsCameraDistanceVelocity = 0.0f;
    private Vector2f m_rtsPositionVelocity = new Vector2f();
    
    // Shadows
    private final int SHADOW_MAP_RESOLUTION = 2048;
    private final float SHADOW_INTENSITY = 0.4f;
    
    // SSAO
    
    // FOG
    private final float FOG_DENSITY = 2.0f;
    private final ColorRGBA FOG_COLOR = new ColorRGBA(0.6f, 0.7f, 0.9f, 1.0f);
    
    // Bloom
    private final float m_bloomBlurScale= 0.5f;
    private final float m_bloomExposurePower = 1.0f;
    private final float m_bloomExposureCutoff = 0.2f;
    private final float m_bloomIntensity = 0.3f;
    
    // Components
    private Transform m_transform;                      // Camera transform
    private Transform m_target;                         // Look At Target
    private Vector3f m_previousTargetPosition = Utilities.zero();   // Previous Target Position
    private CameraMode m_cameraMode = CameraMode.RTS;       // Camera mode
    private FilterPostProcessor m_postProcessor;                // Image FX
    
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
    /**
     * On first frame
     */
    @Override
    public void start() {
        
        // Set image fx
        Main.view().addProcessor(postProcessor());
        
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
        
        // New horizontal position
        Vector3f newPosition = new Vector3f();
        newPosition = newPosition.add(Main.cam().getUp().add(Main.cam().getDirection()).mult(Main.input().rawInputAxis().y));
        newPosition = newPosition.add(Main.cam().getLeft().mult(-Main.input().rawInputAxis().x));
        newPosition = Utilities.Horizontal(newPosition);
        
        if (newPosition.lengthSquared() < 0.001f) {
            // No movement
            newPosition = new Vector3f();
        } else {
            // Normalize movement
            newPosition = newPosition.normalize().mult(Time.unscaledDeltaTime());
            
            // Set move speed
            newPosition = newPosition.mult((Main.input().getButton("Shift").isDown() ? RTS_CAMERA_SPEED_FAST : (Main.input().getButton("Ctrl").isDown() ? RTS_CAMERA_SPEED_SLOW : RTS_CAMERA_SPEED_DEFAULT)));
        }
        
        // Set new position
        newPosition = newPosition.add(Main.cam().getLocation());
        newPosition.y = m_rtsCameraCurrentDistance;
        
        // Previous and target position
        Vector2f __t = new Vector2f(newPosition.x, newPosition.z);
        Vector2f __f = new Vector2f(
                Main.cam().getLocation().x,
                Main.cam().getLocation().z);
        
        // Smooth out horizontal position
        __f = Mathf.smoothdamp(
                __f, 
                __t, 
                m_rtsPositionVelocity,
                RTS_CAMERA_SMOOTH, 1000.0f, Time.unscaledDeltaTime());
        newPosition = new Vector3f(__f.x, newPosition.y, __f.y);
        
        // Rotation
        if (Main.input().getButton("E").isDown())
            m_rtsCameraRotation -= RTS_CAMERA_ROTATION_SPEED * Time.unscaledDeltaTime();
        else if (Main.input().getButton("Q").isDown())
            m_rtsCameraRotation += RTS_CAMERA_ROTATION_SPEED * Time.unscaledDeltaTime();
        
        // Fix rotation
        m_rtsCameraRotation %= 360.0f;
        
        
        if (m_target == null) {
            // Set normal
            Main.cam().setLocation(newPosition);
            Main.cam().lookAt(Utilities.rotateY(new Vector3f(10.0f, -5.0f, 10.0f), m_rtsCameraRotation).add(Utilities.Horizontal(newPosition)), Vector3f.UNIT_Y);
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
     * 
     */
    public void onUpdateFly() {
        
        /**
         * Return when not in fly mode
         */
        if (m_cameraMode != CameraMode.Fly)
            return;
        
        // Get new position
        Vector3f newPosition = Main.cam().getDirection().mult(Main.input().rawInputAxis().y);
        newPosition = newPosition.subtract(Main.cam().getLeft().mult(Main.input().rawInputAxis().x));
        
        // World up/down (up has prior)
        if (Main.input().getButton("E").isDown()) {
            newPosition = newPosition.add(Utilities.up());
        } else if (Main.input().getButton("Q").isDown()) {
            newPosition = newPosition.add(Utilities.down());
        }
        
        if (newPosition.lengthSquared() < 0.01f) {
            // No movement
            newPosition = Utilities.zero();
        } else {
            // Normalize movement
            newPosition = newPosition.normalize();
            
            // Set speed
            newPosition = newPosition.mult(Time.unscaledDeltaTime() * (Main.input().getButton("Shift").isDown() ? FLY_CAMERA_SPEED_FAST : (Main.input().getButton("Ctrl").isDown() ? FLY_CAMERA_SPEED_SLOW : FLY_CAMERA_SPEED_DEFAULT)));
        }
        
        // Set new position
        newPosition = newPosition.add(Main.cam().getLocation());
        Main.cam().setLocation(newPosition);
        
        // Set rotation
        float[] __rot = Main.cam().getRotation().toAngles(null);
        __rot[0] += Main.input().mouseMove().y;
        __rot[1] -= Main.input().mouseMove().x;
        
        Quaternion __q = Quaternion.IDENTITY;
        __q = __q.fromAngles(__rot);
        
        Main.cam().setAxes(__q);
    }
}
