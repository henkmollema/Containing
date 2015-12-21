/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.game;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import nhl.containing.simulator.framework.Callback;
import nhl.containing.simulator.framework.LoopMode;
import nhl.containing.simulator.framework.Path;
import nhl.containing.simulator.framework.Point3;
import nhl.containing.simulator.framework.Utilities;
import nhl.containing.simulator.simulation.Main;
import nhl.containing.simulator.world.MaterialCreator;

/**
 *
 * @author sietse
 */
public class Vehicle extends MovingItem {
    
    public enum VehicleState {
        Disposed,
        Waiting,
        ToLoad,
        ToOut
    }
    
    private boolean m_initialized = false;
    private final static String BASE_MODEL_PATH = "models/";
    
    public Spatial m_frontSpatial;
    
    public Material m_frontMaterial;
    
    private Vector3f m_frontOffset;
    
    public Vector3f[] from;
    public Vector3f[] to;
    
    private VehicleState m_currentState;
    
    
    public Vehicle(Point3 size, float speed, String frontModel, float frontScale, Vector3f frontOffset) {
        super(size, speed);
        init(frontModel, frontScale, frontOffset);
    }
    
    private void init(String frontModel, float frontScale, Vector3f frontOffset) {
        m_frontOffset = frontOffset == null ? Utilities.zero() : frontOffset;
        
        if (!Utilities.nullOrEmpty(frontModel)) {
            if (m_frontMaterial == null)
                m_frontMaterial = MaterialCreator.unshadedRandom();
            
            m_frontSpatial = Main.assets().loadModel(BASE_MODEL_PATH + frontModel);
            m_frontSpatial.setMaterial(m_frontMaterial);
            m_frontSpatial.scale(frontScale);
            this.attachChild(m_frontSpatial);
            m_frontSpatial.setLocalTranslation(m_frontOffset);
        }
        
        // Init path
        path(new Path());
        path().m_callback = new Callback(this, "onVehicle");
        path().m_loopMode = LoopMode.Once;
        path().setSpeed(m_loadedSpeed);
        path().m_waitTime = 0.0f;
        path().m_useTimeInsteadOfSpeed = false;
    }
    
    public void onVehicle() {
        switch (m_currentState) {
            case ToLoad:
                if (path().atLast()) {
                    state(VehicleState.Waiting);
                } else if (path().atFirst()) {
                    
                }
                
                // todo: moet hier ook geen update?
                //path().update();
                break;
            case ToOut:
                if (path().atLast()) {
                    state(VehicleState.Disposed);
                } else if (path().atFirst()) {
                    
                }
                
                path().update();
                break;
        }
    }
    
    public void update() {
        if (!m_initialized) {
            m_initialized = true;
            return;
        }
        
        switch (m_currentState) {
            case ToLoad:
                path().update();
                position(path().getPosition());
                break;
            case ToOut:
                path().update();
                position(path().getPosition());
                break;
        }
        
    }
    
    public void state(VehicleState state) {
        if (m_currentState == state)
            return;
        
        switch (state) {
            case Disposed:
                this.setCullHint(CullHint.Always);
                break;
            case ToLoad:
                this.setCullHint(CullHint.Dynamic);
                path().setPathf(from[0], from);
                break;
            case ToOut:
                path().setPathf(to[0], to);
                break;
            case Waiting:
                break;
        }
        m_currentState = state;
    }
    public VehicleState state() {
        return m_currentState;
    }
    
}
