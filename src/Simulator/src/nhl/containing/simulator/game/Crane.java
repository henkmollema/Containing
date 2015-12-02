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
import nhl.containing.simulator.world.WorldCreator;

/**
 *
 * @author sietse
 */
public abstract class Crane extends MovingItem {
    private final String MODEL_PATH_BASE = "models/henk/Cranes/";
    
    // Components
    private Transform m_frame;
    private Transform m_hook;
    private Line3D m_rope;
    private Timer m_attachTimer = new Timer(attachTime());
    
    // Objects
    private Spatial m_craneSpatial;
    private Spatial m_hookSpatial;
    
    // Offsets
    protected Vector3f m_frameOffset = Utilities.zero();
    protected Vector3f m_hookOffset = Utilities.zero();
    
    // Other
    public Callback onTargetCallback;
    
    /**
     * Constructor
     * @param parent 
     */
    public Crane(Transform parent) {
        super(parent);
        init(Utilities.zero(), Utilities.zero(), Utilities.zero(), Utilities.zero(), Utilities.zero());
    }
    /**
     * Constructor
     * @param parent
     * @param basePosition
     * @param craneOffset
     * @param hookOffset
     * @param containerOffset
     * @param craneSpatialOffset
     * @param hookSpatialOffset 
     */
    public Crane(Transform parent, Vector3f craneOffset, Vector3f hookOffset, Vector3f containerOffset, Vector3f craneSpatialOffset, Vector3f hookSpatialOffset) {
        super(parent);
        init(craneOffset, hookOffset, containerOffset, craneSpatialOffset, hookSpatialOffset);
    }
    
    /**
     * 
     * @param frameOffset
     * @param hookOffset
     * @param containerOffset
     * @param frameSpatialOffset
     * @param hookSpatialOffset 
     */
    private void init(Vector3f frameOffset, Vector3f hookOffset, Vector3f containerOffset, Vector3f frameSpatialOffset, Vector3f hookSpatialOffset) {
        
        // Init offsets
        m_frameOffset = new Vector3f(frameOffset);
        m_hookOffset = new Vector3f(hookOffset);
        this.containerOffset(containerOffset);
        
        // Init transforms (Platform -> Crane -> Frame -> Hook)
        m_frame = new Transform(this);
        m_hook = new Transform(m_frame);
        
        // Create frame
        m_craneSpatial = Main.assets().loadModel(craneModelPath());
        m_craneSpatial.setMaterial(craneModelMaterial());
        m_craneSpatial.rotate(0.0f, 90.0f * Mathf.Deg2Rad, 0.0f);
        m_craneSpatial.scale(1.5f);
        m_frame.attachChild(m_craneSpatial);
        
        // Create hook
        m_hookSpatial = Main.assets().loadModel(hookModelPath());
        m_hookSpatial.setMaterial(hookModelMaterial());
        m_hookSpatial.rotate(0.0f, 90.0f * Mathf.Deg2Rad, 0.0f);
        m_hook.attachChild(m_hookSpatial);
        
        // Spatial offsets
        m_craneSpatial.setLocalTranslation(frameSpatialOffset);
        m_hookSpatial.setLocalTranslation(hookSpatialOffset);
        
        // Line
        m_rope = new Line3D(MaterialCreator.rope(), new Line3DNode(Utilities.zero(), 0.2f), new Line3DNode(Utilities.zero(), 0.2f));
        Main.register(m_rope);
        
        // Path
        path(getCranePath());
        path().setPosition(basePosition());
        path().setCallback(new Callback(this, "_onCrane"));
        
        // Run aditional inhereted awake
        awake();
    }
    
    private String craneModelPath() {
        return MODEL_PATH_BASE + craneModelName();
    }
    private String hookModelPath() {
        return MODEL_PATH_BASE + hookModelName();
    }
    
    public final void _update() {
        
        if (path() != null) {
            
            // Update Path
            if (!m_attachTimer.active()) {
                path().update();
            }
            
            // Get path position
            Vector3f pathPos = path().getPosition();
            
            // Crane
            Vector3f cranePos = new Vector3f(m_frameOffset);
            cranePos.z += pathPos.z;
            m_frame.localPosition(cranePos);
            
            // Hook
            Vector3f hookPos = new Vector3f(m_hookOffset);
            hookPos.x += pathPos.x;
            hookPos.y += pathPos.y;
            m_hook.localPosition(hookPos);
            
            // Container
            /*if (getSpot().container != null) {
                if (getSpot().container.getParent() != m_hook) {
                    m_hook.attachChild(getSpot().container);
                    getSpot().container.localPosition(containerOffset());
                }
            }*/
            
            // Rrope
            m_rope.SetPosition(0, Utilities.Horizontal(m_hook.position()).add(new Vector3f(0.0f, ropeHeight(), 0.0f)));
            m_rope.SetPosition(1, m_hook.position());
        }
        
        if (m_attachTimer.finished(true)) {
            if (onTargetCallback != null) {
                onTargetCallback.invoke();
            }
        } else if (!m_attachTimer.active() && path().atLast()) {
            if (onTargetCallback != null) {
                onTargetCallback.invoke();
            }
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
    protected abstract float ropeHeight();
    protected abstract Vector3f basePosition();
    
    public void _onCrane() {
        if (path().atLast()) {
            m_attachTimer.reset();
        }
    }
    public void setPath() {
        setPath(basePosition());
    }
    public void setPath(Vector3f pos) {
        
        // Get poins
        Vector3f p = new Vector3f(pos);
        Vector3f b = basePosition();
        Vector3f c = path().getPosition();
        
        // Create path
        Vector3f[] newPath = new Vector3f[] {
            new Vector3f(c.x, b.y, c.z),    // Go up
            new Vector3f(c.x, b.y, p.z),   // Go forward
            new Vector3f(p.x, b.y, p.z),   // Go side
            new Vector3f(p.x, p.y, p.z)    // Go down
        };
        
        path().setPath(newPath);
    }
    
    @Override
    protected void onSetContainer(Container c) { 
        if (m_hook == null)
            return;
        m_hook.attachChild(c);
        c.localPosition(containerOffset());
    }
}
