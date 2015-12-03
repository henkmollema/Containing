package nhl.containing.simulator.game;

import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;
import nhl.containing.simulator.simulation.Callback;
import nhl.containing.simulator.simulation.Debug;
import nhl.containing.simulator.simulation.Mathf;
import nhl.containing.simulator.simulation.Path;
import nhl.containing.simulator.simulation.Point2;
import nhl.containing.simulator.simulation.Point3;
import nhl.containing.simulator.simulation.Transform;
import nhl.containing.simulator.simulation.Utilities;

/**
 *
 * @author sietse
 */
public abstract class LoadingPlatform extends Platform {
    
    protected Crane m_crane;                                // Crane
    protected ParkingSpot[] m_parkingSpots;                     // AGV parking spots
    
    private List<CraneAction> m_queue = new ArrayList<CraneAction>();   // Action queue
    protected CraneAction m_currentAction;                          // Current action
    private boolean m_firstFrame = true;                        // Is first frame
    
    /**
     * Constructor
     */
    public LoadingPlatform() {
        super();
        m_parkingSpots = parkingSpots();
    }
    /**
     * Constructor
     * @param parent 
     */
    public LoadingPlatform(Transform parent) {
        super(parent);
        m_parkingSpots = parkingSpots();
    }
    
    protected abstract ParkingSpot[] parkingSpots();
    
    /**
     * Set crane path
     * @param to target position
     */
    protected void setCraneTarget(Vector3f to) {
        m_crane.getCranePath().setPath(new Vector3f(to));
    }
    
    /**
     * Update, called every frame
     */
    public void update() {
        if (m_firstFrame) {
            // First frame, init
            if (m_crane != null) {
                m_crane.onTargetCallback = new Callback(this, "onCrane");
                m_firstFrame = false;
            }
        }
        
        if (m_crane != null) {
            // Update crane
            m_crane._update();
        }
    }
    
    /**
     * Create a take action
     * @param p Container carrier container position
     * @param carrier The container carrier (target)
     */
    public void take(Point3 p, int spot) {
        
        // Get container
        Container container = getContainer(p);
        
        if (container == null) {
            // Invalid input
            Debug.error("Null reference: selected container not available! [LoadingPlatform.java] [84]");
            return;
        }
        
        // Check if needed to move containers that lie on top of the target one
        replace(new Point3(p).add(Point3.up()));
        
        // Add the action to queue
        m_queue.add(new CraneAction(container, new Callback(this, "attach2Crane")));
        m_queue.add(new CraneAction(container, spot, new Callback(this, "crane2carrier")));
    }
    /**
     * 
     * @param parkingSpot
     * @param p 
     */
    public void place(int parkingSpot, Point3 p) {
        
        Container container = m_parkingSpots[parkingSpot].agv().getContainer();
        
        if (container == null) {
            // Invalid input
            Debug.error("Null reference: selected container not available! [LoadingPlatform.java] [106]");
            return;
        }
        
        // Set height
        int height = getStackHeight(new Point2(p.x, p.z));
        if (height < p.y || p.y < 0) {
            p.y = height;
        } replace(new Point3(p));
        
        m_queue.add(new CraneAction(container, parkingSpot, new Callback(this, "parking2Crane")));
        m_queue.add(new CraneAction(container, new Callback(this, "crane2Storage")));
    }
    /**
     * Put the container to a new position
     * @param p 
     */
    public void replace(Point3 p) {
        Container container = getContainer(p);
        if (container == null)
            return;
        
        replace(new Point3(p).add(Point3.up()));
        
        // TODO: Do here replacement things
    }
    
    /**
     * On action finished
     */
    public void onCrane() {
        if (m_currentAction != null) {
            if (m_currentAction.finishCallback != null) {
                
                // Run the finish action
                m_currentAction.finishCallback.invoke();
            }
        }
        
        // Get new
        m_currentAction = null;
        if (m_queue.size() > 0) {
            m_queue.get(0).start();
            m_queue.remove(0);
        } else {
            // Default position
            m_crane.setPath();
        }
    }
    
    /**
     * 
     */
    public void crane2Storage() {
        Container c = m_crane.setContainer(null);
        
    }
    /**
     * 
     */
    public void parking2Crane() {
        
    }
    /**
     * Attach container to crane
     */
    public void attach2Crane() {
        m_crane.setContainer(m_currentAction.target);
        replaceContainer(m_currentAction.target, null);
        updateOuter();
    }
    /**
     * Detach from crane, and attach to the AGV
     */
    public void crane2carrier() {
        m_parkingSpots[m_currentAction.parkingSpot].agv().setContainer(m_crane.setContainer(null));
    }
    
    
    
    public ParkingSpot getSpot(int index) {
        return m_parkingSpots[index];
    }
    
    /**
     * 
     * @return 
     */
    private CraneAction baseAction() {
        return null;
    }
    
    /**
     * A crane action
     * Take and place
     */
    private class CraneAction {
        public final int parkingSpot;
        public final Point3 storageSpot;
        public final Container target; // 
        public Callback finishCallback; // Maybe can also without reflection, but for now leave it
        
        public CraneAction(Container target, Callback onFinish) {
            this.target = target;
            this.parkingSpot = -1;
            this.storageSpot = null;
            this.finishCallback = onFinish;
        }
        public CraneAction(Container target, int parkingSpot, Callback onFinish) {
            this.target = target;
            this.parkingSpot = parkingSpot;
            this.storageSpot = null;
            this.finishCallback = onFinish;
        }
        public CraneAction(Container target, Point3 starageSpot, Callback onFinish) {
            this.target = target;
            this.parkingSpot = -1;
            this.storageSpot = starageSpot;
            this.finishCallback = onFinish;
        }
        
        /**
         * On first frame when its <this> turn
         */
        public void start() {
            m_currentAction = this;
            
            if (this.storageSpot != null)
                m_crane.setPath(getSpot(storageSpot).worldPosition());
            else if (parkingSpot < 0)
                m_crane.setPath(target.position());
            else
                m_crane.setPath(m_parkingSpots[parkingSpot].position());
        }
    }
}
