/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import Simulation.Main;
import Simulation.Path;
import Simulation.Transform;
import Utilities.Line3D;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.*;

/**
 *
 * @author sietse
 */
public abstract class Crane extends MovingItem {
    private final String MODEL_PATH_BASE = "models/henk/Cranes/";
    
    private Path m_cranePath;
    private float m_hookHeightUp;
    
    private Spatial m_craneSpatial;
    private Spatial m_hookSpatial;
    
    public Crane(Transform parent, float hookHeightUp) {
        super(parent);
    }
    
    /**
     * Constructor extention for reducing code
     * @param hookHeight
     * @param position
     */
    private void init(float hookHeight, Vector3f position) {
        m_craneSpatial = Main.assets().loadModel(craneModelPath());
        m_craneSpatial.setMaterial(craneModelMaterial());
        attachChild(m_craneSpatial);
        
        m_hookSpatial = Main.assets().loadModel(hookModelPath());
        m_hookSpatial.setMaterial(hookModelMaterial());
        attachChild(m_hookSpatial);
        
        localPosition(position);
        m_cranePath = getCranePath();
        awake();
    }
    
    public final void _update() {
        update();
    }
    
    
    private String craneModelPath() {
        return MODEL_PATH_BASE + craneModelName();
    }
    private String hookModelPath() {
        return MODEL_PATH_BASE + hookModelName();
    }
    
    protected void awake() { }
    protected void update() { }
    
    protected abstract String craneModelName();
    protected abstract String hookModelName();
    protected abstract Material craneModelMaterial();
    protected abstract Material hookModelMaterial();
    protected abstract Path getCranePath();
}
