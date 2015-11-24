/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.game;

import nhl.containing.simulator.simulation.Main;
import nhl.containing.simulator.simulation.Path;
import nhl.containing.simulator.simulation.Transform;
import nhl.containing.simulator.utils.Line3D;
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
    private CraneHook m_hook;
    private Line3D m_rope;
    
    private Spatial m_craneSpatial;
    private Spatial m_hookSpatial;
    
    public Crane(Transform parent, CraneHook craneHook) {
        super(parent);
        init(craneHook, Vector3f.ZERO);
    }
    
    /**
     * Constructor extention for reducing code
     * @param hookHeight
     * @param position
     */
    private void init(CraneHook hook, Vector3f position) {
        m_craneSpatial = Main.assets().loadModel(craneModelPath());
        m_craneSpatial.setMaterial(craneModelMaterial());
        attachChild(m_craneSpatial);
        
        m_hook = hook;
        m_hookSpatial = Main.assets().loadModel(hookModelPath());
        m_hookSpatial.setMaterial(hookModelMaterial());
        m_hook.attachChild(m_hookSpatial);
        attachChild(m_hook);
        
        localPosition(position);
        m_cranePath = getCranePath();
        awake();
    }
    
    public final void _update() {
        if (m_cranePath != null)
            m_cranePath.update();
        if (m_hook != null)
            m_hook._update();
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
