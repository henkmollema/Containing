/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.game;

import nhl.containing.simulator.simulation.Main;
import nhl.containing.simulator.simulation.Path;
import nhl.containing.simulator.simulation.Transform;
import nhl.containing.simulator.simulation.Line3D;
import nhl.containing.simulator.simulation.Line3DNode;
import nhl.containing.simulator.simulation.Mathf;
import nhl.containing.simulator.simulation.Utilities;
import nhl.containing.simulator.world.MaterialCreator;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.*;
import java.util.ArrayList;
import java.util.List;

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
    public Crane(Transform parent, CraneHook craneHook, Vector3f offset) {
        super(parent);
        init(craneHook, offset);
    }
    
    /**
     * Constructor extention for reducing code
     * @param hookHeight
     * @param position
     */
    private void init(CraneHook hook, Vector3f position) {
        m_craneSpatial = Main.assets().loadModel(craneModelPath());
        m_craneSpatial.setMaterial(craneModelMaterial());
        m_craneSpatial.rotate(0.0f, 90.0f * Mathf.Deg2Rad, 0.0f);
        m_craneSpatial.scale(1.5f);
        attachChild(m_craneSpatial);
        
        m_hook = hook;
        m_hookSpatial = Main.assets().loadModel(hookModelPath());
        m_hookSpatial.setMaterial(hookModelMaterial());
        m_hookSpatial.rotate(0.0f, 90.0f * Mathf.Deg2Rad, 0.0f);
        m_hook.attachChild(m_hookSpatial);
        m_hook.setLocalTranslation(0.0f, 0.0f, 0.0f);
        attachChild(m_hook);
        
        List<Line3DNode> lineNodes = new ArrayList<Line3DNode>(0);
        lineNodes.add(new Line3DNode(Utilities.zero(), 1.0f, ColorRGBA.White));
        lineNodes.add(new Line3DNode(Utilities.zero(), 1.0f, ColorRGBA.White));
        
        m_rope = new Line3D(lineNodes, MaterialCreator.rope());
        m_rope.SetPosition(0, m_hook.position());
        m_rope.SetPosition(1, this.position().add(new Vector3f(0.0f, 5.0f, 0.0f)));
        Main.register(m_rope);
        
        
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
