/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import Simulation.Behaviour;
import Simulation.Main;
import Simulation.Mathf;
import Simulation.Time;
import Simulation.Utilities;
import Simulation.Transform;
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
    
    private Transform m_transform;
    public Transform tranform() {
        return m_transform;
    }
    
    private Transform m_target;
    private Vector3f m_previousTargetPosition = Utilities.zero();
    public void setTarget(Transform t) {
        
        if (t == null) {
            if (m_target == null)
                return;
            m_rtsCameraRotation = Main.cam().getRotation().toAngles(null)[1] * Mathf.Rad2Deg;
            m_previousTargetPosition = Utilities.zero();
            
        } else {
            m_previousTargetPosition = t.position();
        }
        
        m_target = t;
    }
    
    private CameraMode m_cameraMode = CameraMode.RTS;
    
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
    
    private FilterPostProcessor m_postProcessor;
    public FilterPostProcessor postProcessor() {
        if (m_postProcessor == null)
            m_postProcessor = new FilterPostProcessor(Main.assets());
        return m_postProcessor;
    }
    @Override
    public void awake() {
        m_transform = new Transform();
        createSSAO();
        createFog();
        createBloom();
    }
    @Override
    public void start() {
        Main.view().addProcessor(postProcessor());
        onStartRTS();
        onStartFly();
    }
    @Override
    public void rawUpdate() {
        onUpdateFly();
        onUpdateRTS();
    }
    
    public void createShadowsFiler(DirectionalLight sun) {
        
        DirectionalLightShadowRenderer shadowRenderer = new DirectionalLightShadowRenderer(Main.assets(), SHADOW_MAP_RESOLUTION, 3);
        shadowRenderer.setLight(sun);
        Main.view().addProcessor(shadowRenderer);
        shadowRenderer.setShadowIntensity(SHADOW_INTENSITY);
        
        DirectionalLightShadowFilter shadowFilter = new DirectionalLightShadowFilter(Main.assets(), SHADOW_MAP_RESOLUTION, 3);
        shadowFilter.setLight(sun);
        shadowFilter.setEnabled(true);
        
        postProcessor().addFilter(shadowFilter);
    }
    
    public void createSSAO() {
        SSAOFilter ssaoFilter = new SSAOFilter(6.94f, 10.92f, 0.33f, 0.61f);
        postProcessor().addFilter(ssaoFilter);
    }
    
    public void createFog() {
        FogFilter fogFilter = new FogFilter();
        fogFilter.setEnabled(true);
        fogFilter.setFogColor(FOG_COLOR);
        fogFilter.setFogDensity(FOG_DENSITY);
        postProcessor().addFilter(fogFilter);
    }
    
    public void createBloom() {
        BloomFilter bloomFilter = new BloomFilter();
        bloomFilter.setDownSamplingFactor(2);
        bloomFilter.setBlurScale(m_bloomBlurScale);
        bloomFilter.setExposurePower(m_bloomExposurePower);
        bloomFilter.setExposureCutOff(m_bloomExposureCutoff);
        bloomFilter.setBloomIntensity(m_bloomIntensity);
        
        postProcessor().addFilter(bloomFilter);
    }
    
    public CameraMode cameraMode() {
        return m_cameraMode;
    }
    public void toggleCameraMode() {
        if (m_cameraMode == CameraMode.Fly) {
            m_cameraMode = CameraMode.RTS;
            onStartRTS();
        } else {
            m_cameraMode = CameraMode.Fly;
            onStartFly();
        }
    }
    public void zoom(float amount) {
        m_rtsCameraTargetDistance += amount * RTS_CAMERA_ZOOM_SPEED;
        m_rtsCameraTargetDistance = Mathf.clamp(m_rtsCameraTargetDistance, RTS_MIN_CAMERA_DISTANCE, RTS_MAX_CAMERA_DISTANCE);
    }
    
    public void onStartRTS() {
        if (m_cameraMode != CameraMode.RTS)
            return;
        Main.inputManager().setCursorVisible(true);
    }
    public void onUpdateRTS() {
        m_rtsCameraCurrentDistance = Mathf.smoothdamp(
                m_rtsCameraCurrentDistance, 
                m_rtsCameraTargetDistance, 
                m_rtsCameraDistanceVelocity, 
                RTS_CAMERA_ZOOM_SMOOTH, 
                1000.0f, Time.unscaledDeltaTime());
        
        
        if (m_cameraMode != CameraMode.RTS) {
            return;
        }
            
        Vector3f newPosition = new Vector3f();
        newPosition = newPosition.add(Main.cam().getUp().add(Main.cam().getDirection()).mult(Main.input().rawInputAxis().y));
        newPosition = newPosition.add(Main.cam().getLeft().mult(-Main.input().rawInputAxis().x));
        newPosition = Utilities.Horizontal(newPosition);
        
        if (newPosition.lengthSquared() < 0.001f) {
            newPosition = new Vector3f();
        } else {
            newPosition = newPosition.normalize().mult(Time.unscaledDeltaTime());
            newPosition = newPosition.mult((Main.input().getButton("Shift").isDown() ? RTS_CAMERA_SPEED_FAST : (Main.input().getButton("Ctrl").isDown() ? RTS_CAMERA_SPEED_SLOW : RTS_CAMERA_SPEED_DEFAULT)));
        }
        
        newPosition = newPosition.add(Main.cam().getLocation());
        newPosition.y = m_rtsCameraCurrentDistance;
        
        Vector2f __t = new Vector2f(newPosition.x, newPosition.z);
        Vector2f __f = new Vector2f(
                Main.cam().getLocation().x,
                Main.cam().getLocation().z);
        
        __f = Mathf.smoothdamp(
                __f, 
                __t, 
                m_rtsPositionVelocity,
                RTS_CAMERA_SMOOTH, 1000.0f, Time.unscaledDeltaTime());
        newPosition = new Vector3f(__f.x, newPosition.y, __f.y);
        
        if (Main.input().getButton("E").isDown())
            m_rtsCameraRotation -= RTS_CAMERA_ROTATION_SPEED * Time.unscaledDeltaTime();
        else if (Main.input().getButton("Q").isDown())
            m_rtsCameraRotation += RTS_CAMERA_ROTATION_SPEED * Time.unscaledDeltaTime();
        m_rtsCameraRotation %= 360.0f;
        
        
        if (m_target == null) {
            Main.cam().setLocation(newPosition);
            Main.cam().lookAt(Utilities.rotateY(new Vector3f(10.0f, -5.0f, 10.0f), m_rtsCameraRotation).add(Utilities.Horizontal(newPosition)), Vector3f.UNIT_Y);
        } else {
            Main.cam().setLocation(newPosition.add(m_target.position().subtract(m_previousTargetPosition)));
            Main.cam().lookAt(m_target.position(), Utilities.up());
            m_previousTargetPosition = m_target.position();
        }
        
        
    }
    
    public void onStartFly() {
        if (m_cameraMode != CameraMode.Fly)
            return;
        Main.inputManager().setCursorVisible(false);
    }
    public void onUpdateFly() {
        
        if (m_cameraMode != CameraMode.Fly)
            return;
            
        Vector3f newPosition = Main.cam().getDirection().mult(Main.input().rawInputAxis().y);
        newPosition = newPosition.subtract(Main.cam().getLeft().mult(Main.input().rawInputAxis().x));
        
        if (Main.input().getButton("E").isDown()) {
            newPosition = newPosition.add(Utilities.up());
        } else if (Main.input().getButton("Q").isDown()) {
            newPosition = newPosition.add(Utilities.down());
        }
        
        if (newPosition.lengthSquared() < 0.01f) {
            newPosition = Utilities.zero();
        } else {
            newPosition = newPosition.normalize();
            newPosition = newPosition.mult(Time.unscaledDeltaTime() * (Main.input().getButton("Shift").isDown() ? FLY_CAMERA_SPEED_FAST : (Main.input().getButton("Ctrl").isDown() ? FLY_CAMERA_SPEED_SLOW : FLY_CAMERA_SPEED_DEFAULT)));
        }
        
        newPosition = newPosition.add(Main.cam().getLocation());
        Main.cam().setLocation(newPosition);
        
        float[] __rot = Main.cam().getRotation().toAngles(null);
        __rot[0] += Main.input().mouseMove().y;
        __rot[1] -= Main.input().mouseMove().x;
        
        Quaternion __q = Quaternion.IDENTITY;
        __q = __q.fromAngles(__rot);
        
        Main.cam().setAxes(__q);
    }
}
