/*
 * Crane
 * 
 * NOTE:
 * Manual updating required
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
import com.jme3.math.Vector3f;
import com.jme3.scene.*;
import nhl.containing.simulator.simulation.Callback;
/**
 *
 * @author sietse
 */
public final class Crane extends MovingItem {
    private final String MODEL_PATH_BASE = "models/henk/Cranes/";
    
    // Components
    private Transform m_frame;                              // Frame that hold the hook
    private Transform m_hook;                               // Hook, that holds the container
    private Line3D m_rope;                                  // Rope between hook and frame
    private Timer m_attachTimer;  // For attach and detach containers
    
    // Objects
    private Spatial m_frameSpatial;                         // Frame spatial
    private Spatial m_hookSpatial;                          // Hook spatial
    
    // Offsets
    protected Vector3f m_frameOffset = Utilities.zero();    // Local frame offset, from the crane transform
    protected Vector3f m_hookOffset = Utilities.zero();     // Local hook offset, from the frame transform
    
    // Settings
    private final String m_craneModelName;
    private final String m_hookModelName;
    private final Material m_craneModelMaterial;
    private final Material m_hookModelMaterial;
    private final float m_attachTime;
    private final float m_ropeHeight;
    private final Vector3f m_basePosition;
    public Callback onTargetCallback;                       // Method when arriving at destination
    
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
    public Crane(
            Transform parent,
            Path cranePath,
            String craneModelName,
            String hookModelName,
            Material craneModelMaterial,
            Material hookModelMaterial,
            float attachTime,
            float ropeHeight,
            Vector3f basePosition,
            Vector3f craneOffset, 
            Vector3f hookOffset, 
            Vector3f containerOffset, 
            Vector3f craneSpatialOffset, 
            Vector3f hookSpatialOffset,
            float craneScale,
            float hookScale) {
        
        super(parent);
        
        this.m_craneModelName       = craneModelName;
        this.m_hookModelName        = hookModelName;
        this.m_craneModelMaterial   = craneModelMaterial;
        this.m_hookModelMaterial    = hookModelMaterial;
        
        this.m_attachTime           = attachTime;
        this.m_ropeHeight           = ropeHeight;
        this.m_basePosition         = basePosition;
        this.m_attachTimer          = new Timer(m_attachTime);
        
        init(cranePath, craneOffset, hookOffset, containerOffset, craneSpatialOffset, hookSpatialOffset, craneScale, hookScale);
    }
    
    /**
     * Initialize
     * @param frameOffset
     * @param hookOffset
     * @param containerOffset
     * @param frameSpatialOffset
     * @param hookSpatialOffset 
     */
    private void init(Path cranePath, Vector3f frameOffset, Vector3f hookOffset, Vector3f containerOffset, Vector3f frameSpatialOffset, Vector3f hookSpatialOffset, float craneScale, float hookScale) {
        
        // Init offsets
        m_frameOffset = new Vector3f(frameOffset);
        m_hookOffset = new Vector3f(hookOffset);
        this.containerOffset(containerOffset);
        
        // Init transforms (Platform -> Crane -> Frame -> Hook)
        m_frame = new Transform(this);
        m_hook = new Transform(m_frame);
        
        // Create frame
        m_frameSpatial = Main.assets().loadModel(frameModelPath());
        m_frameSpatial.setMaterial(m_craneModelMaterial);
        m_frameSpatial.rotate(0.0f, 90.0f * Mathf.Deg2Rad, 0.0f);
        m_frameSpatial.scale(craneScale);
        m_frame.attachChild(m_frameSpatial);
        
        // Create hook
        m_hookSpatial = Main.assets().loadModel(hookModelPath());
        m_hookSpatial.setMaterial(m_hookModelMaterial);
        m_hookSpatial.rotate(0.0f, 90.0f * Mathf.Deg2Rad, 0.0f);
        m_hookSpatial.scale(hookScale);
        m_hook.attachChild(m_hookSpatial);
        
        // Spatial offsets
        m_frameSpatial.setLocalTranslation(frameSpatialOffset);
        m_hookSpatial.setLocalTranslation(hookSpatialOffset);
        
        // Line
        m_rope = new Line3D(MaterialCreator.rope(), new Line3DNode(Utilities.zero(), 0.2f), new Line3DNode(Utilities.zero(), 0.2f));
        Main.register(m_rope);
        
        // Path
        path(cranePath);
        path().setPosition(m_basePosition);
        path().setCallback(new Callback(this, "_onCrane"));
        
        // Run aditional inhereted awake
        awake();
    }
    
    
    public Vector3f basePosition(){
        return new Vector3f(m_basePosition);
    }
    /**
     * Frame model path
     * @return 
     */
    private String frameModelPath() {
        return MODEL_PATH_BASE + m_craneModelName;
    }
    /**
     * Hook model path
     * @return 
     */
    private String hookModelPath() {
        return MODEL_PATH_BASE + m_hookModelName;
    }
    /**
     * update
     */
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
            
            // Rrope
            m_rope.SetPosition(0, Utilities.Horizontal(m_hook.position()).add(new Vector3f(0.0f, m_ropeHeight, 0.0f)));
            m_rope.SetPosition(1, m_hook.position());
        }
        
        // Check if on target
        if (m_attachTimer.finished(true)) {
            if (onTargetCallback != null)
                onTargetCallback.invoke();
        } else if (!m_attachTimer.active() && path().atLast()) {
            if (onTargetCallback != null)
                onTargetCallback.invoke();
        }
        
        // Child update
        update();
    }
    /**
     * On create
     */
    protected void awake() { }
    /**
     * On update
     */
    protected void update() { }
    
    /**
     * Called when arrived at a node
     */
    public void _onCrane() {
        if (path().atLast()) {
            // Called when arrived at the target (last) node
            m_attachTimer.reset();
        }
    }
    /**
     * Set path to default destination
     */
    public void setPath() {
        setPath(m_basePosition);
    }
    /**
     * Set path to custom destination
     * @param pos position
     */
    public void setPath(Vector3f pos) {
        
        // Get poins
        Vector3f p = new Vector3f(pos);             // Target
        Vector3f b = new Vector3f(m_basePosition);  // Base
        Vector3f c = path().getPosition();          // Current
        
        // Create path
        Vector3f[] newPath = new Vector3f[] {
            new Vector3f(c.x, b.y, c.z),            // Go up
            new Vector3f(c.x, b.y, p.z),            // Go forward
            new Vector3f(p.x, b.y, p.z),            // Go side
            new Vector3f(p.x, p.y, p.z)             // Go down
        };
        
        // Set path
        path().setPath(newPath);
    }
    
    /**
     * 
     * @param c 
     */
    @Override
    protected void onSetContainer(Container c) { 
        if (m_hook == null)
            return;
        m_hook.attachChild(c);
        c.localPosition(containerOffset());
    }
}
