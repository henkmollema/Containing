/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.game;

import nhl.containing.simulator.simulation.Timer;
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
import nhl.containing.simulator.simulation.Callback;
import nhl.containing.simulator.simulation.Debug;

/**
 *
 * @author sietse
 */
public abstract class Crane extends MovingItem {
    private final String MODEL_PATH_BASE = "models/henk/Cranes/";
    
    private Line3D m_rope;
    private Transform m_hook;
    private Container m_container;
    private Timer m_attachTimer = new Timer(attachTime());
    private float m_ropeHeight = 22.0f;
    
    protected Vector3f m_offset = Utilities.zero();
    protected Vector3f m_hookOffset = Utilities.zero();
    protected Vector3f m_containerOffset =  Utilities.zero();
    protected Vector3f m_base = Utilities.zero();
    
    public Callback onTargetCallback;
    
    private Spatial m_craneSpatial;
    private Spatial m_hookSpatial;
    
    public Crane(Transform parent) {
        super(parent);
        init(Utilities.zero(), Utilities.zero(), Utilities.zero(), Utilities.zero(), Utilities.zero(), Utilities.zero());
    }
    public Crane(Transform parent, Vector3f offset) {
        super(parent);
        init(offset, Utilities.zero(), Utilities.zero(), Utilities.zero(), Utilities.zero(), Utilities.zero());
    }
    public Crane(Transform parent, Vector3f basePosition, Vector3f craneOffset, Vector3f hookOffset, Vector3f containerOffset, Vector3f craneSpatialOffset, Vector3f hookSpatialOffset) {
        super(parent);
        init(basePosition, craneOffset, hookOffset, containerOffset, craneSpatialOffset, hookSpatialOffset);
    }
    
    /**
     * Constructor extention for reducing code
     * @param hookHeight
     * @param position
     */
    private void init(Vector3f basePosition, Vector3f craneOffset, Vector3f hookOffset, Vector3f containerOffset, Vector3f craneSpatialOffset, Vector3f hookSpatialOffset) {
        
        m_offset = new Vector3f(craneOffset);
        m_hookOffset = new Vector3f(hookOffset);
        m_base = new Vector3f(basePosition);
        m_containerOffset = new Vector3f(containerOffset);
        
        m_craneSpatial = Main.assets().loadModel(craneModelPath());
        m_craneSpatial.setMaterial(craneModelMaterial());
        m_craneSpatial.rotate(0.0f, 90.0f * Mathf.Deg2Rad, 0.0f);
        m_craneSpatial.scale(1.5f);
        attachChild(m_craneSpatial);
        m_craneSpatial.setLocalTranslation(craneSpatialOffset);
        
        m_hook = new Transform();
        m_hookSpatial = Main.assets().loadModel(hookModelPath());
        m_hookSpatial.setMaterial(hookModelMaterial());
        m_hookSpatial.rotate(0.0f, 90.0f * Mathf.Deg2Rad, 0.0f);
        m_hook.attachChild(m_hookSpatial);
        m_hookSpatial.setLocalTranslation(hookSpatialOffset);
        m_hook.setLocalTranslation(0.0f, 0.0f, 0.0f);
        m_hook.localPosition(m_hookOffset);
        attachChild(m_hook);
        
        List<Line3DNode> lineNodes = new ArrayList<Line3DNode>(0);
        lineNodes.add(new Line3DNode(Utilities.zero(), 0.2f, ColorRGBA.White));
        lineNodes.add(new Line3DNode(Utilities.zero(), 0.2f, ColorRGBA.White));
        
        m_rope = new Line3D(lineNodes, MaterialCreator.rope());
        Main.register(m_rope);
        
        localPosition(m_offset);
        path(getCranePath());
        path().setCallback(new Callback(this, "_onCrane"));
        awake();
    }
    
    
    private String craneModelPath() {
        return MODEL_PATH_BASE + craneModelName();
    }
    private String hookModelPath() {
        return MODEL_PATH_BASE + hookModelName();
    }
    
    public final void _update() {
        m_rope.SetPosition(1, m_hook.position());
        m_rope.SetPosition(0, Utilities.Horizontal(m_hook.position()).add(new Vector3f(0.0f, m_ropeHeight, 0.0f)));
        
        if (path() != null) {
            if (!m_attachTimer.active())
                path().update();
            
            // Hook
            Vector3f newPos = new Vector3f(path().getPosition());
            float _z = newPos.z;
            newPos.z = 0.0f;
            newPos = newPos.add(m_hookOffset);
            m_hook.localPosition(newPos);
            
            // Container
            if (m_container != null) {
                m_container.position(m_hook.position().add(m_hookOffset));
            }
            
            // Crane
            newPos = new Vector3f(m_base);
            newPos.z += _z;
            localPosition(newPos);
            
            // Line
            
        }
        
        if (m_attachTimer.finished(true)) {
            if (onTargetCallback != null) {
                onTargetCallback.invoke();
            }
            if (onTargetCallback != null)
                onTargetCallback.invoke();
        } else if (!m_attachTimer.active() && path().atLast()) {
            if (onTargetCallback != null)
                onTargetCallback.invoke();
        }
        
        update();
    }
    protected void awake() { }
    protected void update() { }
    
    protected abstract String craneModelName();
    protected abstract String hookModelName();
    protected abstract Material craneModelMaterial();
    protected abstract Material hookModelMaterial();
    protected abstract Path getCranePath();
    protected abstract float attachTime();
    
    public void _onCrane() {
        if (path().atLast()) {
            m_attachTimer.reset();
        }
    }
    public void setPath() {
        setPath(m_base);
    }
    public void setPath(Vector3f path) {
        Vector3f cur = path().getPosition();
        Vector3f[] newPath = new Vector3f[] {
            new Vector3f(cur.x, m_base.y, cur.x),    // Go up
            new Vector3f(cur.x,  m_base.y, path.z),   // Go forward
            new Vector3f(path.x, m_base.y, path.z),   // Go side
            new Vector3f(path.x, path.y, path.z)    // Go down
        };
        path().setPath(newPath);
    }
    
    public Container attachedContainer() {
        return m_container;
    }
    public void attachedContainer(Container c) {
        m_container = c;
        c.localPosition(m_containerOffset);
    }
}
