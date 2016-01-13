package nhl.containing.simulator.game;

import nhl.containing.simulator.framework.Timer;
import nhl.containing.simulator.simulation.Main;
import nhl.containing.simulator.framework.Path;
import nhl.containing.simulator.framework.Transform;
import nhl.containing.simulator.simulation.Line3D;
import nhl.containing.simulator.simulation.Line3DNode;
import nhl.containing.simulator.framework.Mathf;
import nhl.containing.simulator.framework.Utilities;
import nhl.containing.simulator.world.MaterialCreator;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.*;
import nhl.containing.simulator.framework.Callback;
/**
 *
 * Container crane
 * 
 * @author sietse
 */
public final class Crane extends MovingItem {
    private final String MODEL_PATH_BASE = "models/henk/Cranes/";
    public static final int X_AXIS = 0x1;
    public static final int Y_AXIS = 0x2;
    public static final int Z_AXIS = 0x4;
    
    // Components
    private Transform m_frame;                              // Frame that hold the hook
    private Transform m_hook;                               // Hook, that holds the container
    private Line3D m_rope;                                  // Rope between hook and frame
    private Timer m_attachTimer;                            // For attach and detach containers
    
    // Objects
    public Spatial m_frameSpatial;                          // Frame spatial
    public Spatial m_hookSpatial;                           // Hook spatial
    
    // Offsets
    protected Vector3f m_frameOffset = Utilities.zero();    // Local frame offset, from the crane transform
    protected Vector3f m_hookOffset = Utilities.zero();     // Local hook offset, from the frame transform
    protected Vector3f m_ropeOffset = Utilities.zero();     // Rope oofset
    
    // Settings
    private final String m_craneModelName;                  // Name of frame
    private final String m_hookModelName;                   // Name of hook
    private final Material m_craneModelMaterial;            // Material of frame
    private final Material m_hookModelMaterial;             // Material of hook
    private final float m_attachTime;                       // Attack timer
    private final float m_ropeHeight;                       // Heiht of rope
    private final Vector3f m_basePosition;                  // Base position
    public Callback onTargetCallback;                       // Method when arriving at destination
    public boolean paused = false;                          // Is crane waiting
    public int hookMovementAxis = X_AXIS | Y_AXIS;          // Which axis the hook moves
    
    /**
     * Constructor
     * @param parent
     * @param cranePath
     * @param craneModelName
     * @param hookModelName
     * @param craneModelMaterial
     * @param hookModelMaterial
     * @param attachTime
     * @param ropeHeight
     * @param basePosition
     * @param craneOffset
     * @param hookOffset
     * @param ropeOffset
     * @param containerOffset
     * @param craneSpatialOffset
     * @param hookSpatialOffset
     * @param craneScale
     * @param hookScale 
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
            Vector3f ropeOffset, 
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
        
        init(cranePath, craneOffset, hookOffset, ropeOffset, containerOffset, craneSpatialOffset, hookSpatialOffset, craneScale, hookScale);
    }
    
    /**
     * Initialize
     * @param frameOffset
     * @param hookOffset
     * @param containerOffset
     * @param frameSpatialOffset
     * @param hookSpatialOffset 
     */
    private void init(Path cranePath, Vector3f frameOffset, Vector3f hookOffset, Vector3f ropeOffset, Vector3f containerOffset, Vector3f frameSpatialOffset, Vector3f hookSpatialOffset, float craneScale, float hookScale) {
        
        // Init offsets
        m_frameOffset = new Vector3f(frameOffset);
        m_hookOffset = new Vector3f(hookOffset);
        m_ropeOffset = new Vector3f(ropeOffset);
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
    
    /**
     * Get base position
     * @return 
     */
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
        
        if (paused)
            return;
        
        if (path() != null) {
            
            // Update Path
            if (!m_attachTimer.active()) {
                path().update();
            }
            
            // Get path position
            Vector3f pathPos = path().getPosition();
            
            // Crane
            Vector3f cranePos = new Vector3f(m_frameOffset);
            Vector3f hookPos = new Vector3f(m_hookOffset);
            
            // Set X axis
            if ((hookMovementAxis & X_AXIS) == X_AXIS)
                hookPos.x += pathPos.x;
            else
                cranePos.x += pathPos.x;
            
            // Set Y axis
            if ((hookMovementAxis & Y_AXIS) == Y_AXIS)
                hookPos.y += pathPos.y;
            else
                cranePos.y += pathPos.y;
            
            // Set Z axis
            if ((hookMovementAxis & Z_AXIS) == Z_AXIS)
                hookPos.z += pathPos.z;
            else
                cranePos.z += pathPos.z;
            
            // Set
            m_hook.localPosition(hookPos);
            m_frame.localPosition(cranePos);
            
            // Rrope
            m_rope.SetPosition(0, Utilities.Horizontal(m_hook.position()).add(new Vector3f(m_ropeOffset)).add(new Vector3f(0.0f, m_ropeHeight, 0.0f)));
            m_rope.SetPosition(1, m_hook.position().add(m_ropeOffset));
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
    public boolean targetIsLast() {
        return path() == null ? false : path().targetIsLast();
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
     * Check if hook is up
     * @return 
     */
    public boolean isUp() {
        return Mathf.inRange(path().getPosition().y, m_basePosition.y, 0.01f);
    }
    
    /**
     * First frame when setting container
     * @param c 
     */
    @Override
    protected void onSetContainer(Container c) { 
        if (m_hook == null)
            return;
        m_hook.attachChild(c.transform);
        c.transform.localPosition(containerOffset());
    }
}
