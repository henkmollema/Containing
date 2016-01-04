package nhl.containing.simulator.game;


import nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.*;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import nhl.containing.simulator.framework.EaseType;
import nhl.containing.simulator.framework.Interpolate;
import nhl.containing.simulator.framework.LoopMode;
import nhl.containing.simulator.framework.Path;
import nhl.containing.simulator.framework.Point3;
import nhl.containing.simulator.framework.Time;
import nhl.containing.simulator.framework.Utilities;
import nhl.containing.simulator.simulation.Main;
import nhl.containing.simulator.world.MaterialCreator;

/**
 *
 * @author sietse
 */
public final class AGV extends MovingItem {
    
    
    
    // Constants
    private static final Vector3f startPosition = new Vector3f(0.0f, 0.0f, 0.0f);
    private static final Vector3f transformOffset = new Vector3f(0.0f, 2.0f, 0.0f);
    private static final Vector3f spatialOffset = new Vector3f(0.0f, 0.0f, 0.0f);
    private static final float spatialScale = 2.2f;
    private static final Vector3f containerOffset = new Vector3f(-1.2f, 0.4f, -16.0f);
    private static final String model = "models/henk/AGV.obj";
    private static final Material material = MaterialCreator.diffuse(new ColorRGBA(0.7f, 0.9f, 0.56f, 1.0f));
    private static final float loadedSpeed = 10.0f;
    private static final float unloadedSpeed = 20.0f;
    
    // Fields
    private static float m_distance = 20.0f;
    private static float m_rotationSpeed = 0.1f / 1000.0f;
    
    // Members
    private boolean m_waiting = false;
    private Vector3f m_previousPosition = null;
    private Vector3f m_previousDirection = Utilities.zero();
    
    // Components
    private Spatial m_spatial;
    
    
    public AGV() {
        super(null, loadedSpeed, unloadedSpeed);
        init();
        this.register(SimulationItemType.AGV);
        initSpots(Point3.one());
        Main.register(this);
        path().setPath(
                new Vector3f(40.0f, 0.0f, 0.0f),
                new Vector3f(40.0f, 0.0f, 40.0f),
                new Vector3f(80.0f, 0.0f, 40.0f),
                new Vector3f(80.0f, 0.0f, 0.0f),
                new Vector3f(40.0f, 0.0f, 0.0f)
        );
        
    }
    
    public void update() {
        path().update();
        position(path().getPosition().add(transformOffset));
        lookDirection(spatialOffset);
        
        if (m_previousPosition != null) {
            Vector3f change = position().subtract(m_previousPosition);
            float length = change.length();
            
            if (length > 0.001f) {
                Vector3f n = change.divide(length);
                float dist = m_previousDirection.subtract(n).length();
                n = Interpolate.ease(EaseType.Linear, m_previousDirection, n, m_rotationSpeed * path().m_speed / (dist * Time.deltaTime()));
                m_previousDirection = n;
                
                lookDirection(n);
            }
        }
        m_previousPosition = position();
    }
    
    private void init() {
        
        // 
        position(Utilities.zero());
        m_spatial = Main.assets().loadModel(model);
        m_spatial.setMaterial(material);
        m_spatial.scale(spatialScale);
        attachChild(m_spatial);
        m_spatial.setLocalTranslation(new Vector3f(spatialOffset));
        
        //
        containerOffset(new Vector3f(containerOffset));
        
        // Init path
        Path path = new Path();
        path.m_useTimeInsteadOfSpeed = false;
        path.m_speed = unloadedSpeed;
        path.m_waitTime = 0.0f;
        path.m_loopMode = LoopMode.Once;
        path.m_previousPosition = new Vector3f(startPosition);
        path(path);
    }
}
