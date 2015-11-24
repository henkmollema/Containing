/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.game;

import nhl.containing.simulator.simulation.Behaviour;
import nhl.containing.simulator.simulation.Debug;
import nhl.containing.simulator.simulation.Main;
import nhl.containing.simulator.simulation.Mathf;
import nhl.containing.simulator.simulation.Time;
import nhl.containing.simulator.utils.Utilities;
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
        m_target = t;
        m_previousTargetPosition = t.position();
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
    private final float RTS_MIN_CAMERA_DISTANCE = 10.0f;
    private final float RTS_MAX_CAMERA_DISTANCE = 40.0f;
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
    private final float FOG_DENSITY = 10.0f;
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
        //Main.instance().flyCamera().setMoveSpeed(CAMERA_SPEED);
    }
    @Override
    public void update() {
        onUpdateFly();
        onUpdateRTS();
    }
    @Override
    public void fixedUpdate() {
        
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
        Main.instance().showCursor(true);
        
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
        newPosition = newPosition.add(Main.instance().cam().getUp().add(Main.instance().cam().getDirection()).mult(Main.input().rawInputAxis().y));
        newPosition = newPosition.add(Main.instance().cam().getLeft().mult(-Main.input().rawInputAxis().x));
        newPosition = Utilities.Horizontal(newPosition);
        
        if (newPosition.lengthSquared() < 0.001f) {
            newPosition = new Vector3f();
        } else {
            newPosition = newPosition.normalize().mult(Time.unscaledDeltaTime());
            newPosition = newPosition.mult((Main.input().getButton("Shift").isDown() ? RTS_CAMERA_SPEED_FAST : (Main.input().getButton("Ctrl").isDown() ? RTS_CAMERA_SPEED_SLOW : RTS_CAMERA_SPEED_DEFAULT)));
        }
        
        newPosition = newPosition.add(Main.instance().cam().getLocation());
        newPosition.y = m_rtsCameraCurrentDistance;
        
        Vector2f __t = new Vector2f(newPosition.x, newPosition.z);
        Vector2f __f = new Vector2f(
                Main.instance().cam().getLocation().x,
                Main.instance().cam().getLocation().z);
        
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
            Main.instance().cam().setLocation(newPosition);
            Main.instance().cam().lookAt(Utilities.rotateY(new Vector3f(10.0f, -5.0f, 10.0f), m_rtsCameraRotation).add(Utilities.Horizontal(newPosition)), Vector3f.UNIT_Y);
        } else {
            Main.instance().cam().setLocation(newPosition.add(m_target.position().subtract(m_previousTargetPosition)));
            Main.instance().cam().lookAt(m_target.position(), Utilities.up());
            m_previousTargetPosition = m_target.position();
        }
        
        
    }
    
    public void onStartFly() {
        if (m_cameraMode != CameraMode.Fly)
            return;
        Main.instance().showCursor(false);
        
    }
    public void onUpdateFly() {
        
        if (m_cameraMode != CameraMode.Fly)
            return;
            
        Vector3f newPosition = Utilities.zero();
        newPosition = Main.instance().cam().getDirection().mult(Main.input().rawInputAxis().y);
        newPosition = newPosition.subtract(Main.instance().cam().getLeft().mult(Main.input().rawInputAxis().x));
        
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
        
        newPosition = newPosition.add(Main.instance().cam().getLocation());
        Main.instance().cam().setLocation(newPosition);
        
        float[] __rot = Main.instance().cam().getRotation().toAngles(null);
        __rot[0] += Main.input().mouseMove().y;
        __rot[1] -= Main.input().mouseMove().x;
        
        Quaternion __q = Quaternion.IDENTITY;
        __q = __q.fromAngles(__rot);
        
        Main.instance().cam().setAxes(__q);
    }
}
