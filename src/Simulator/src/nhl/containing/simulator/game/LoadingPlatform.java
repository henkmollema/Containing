package nhl.containing.simulator.game;

import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;
import nhl.containing.simulator.simulation.Callback;
import nhl.containing.simulator.simulation.Debug;
import nhl.containing.simulator.simulation.Point2;
import nhl.containing.simulator.simulation.Point3;
import nhl.containing.simulator.simulation.Transform;

/**
 * TODO: replace() line 193!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * also check line 206
 * @author sietse
 */
public abstract class LoadingPlatform extends Platform {
    
    protected Crane m_crane;                                            // Crane
    protected ParkingSpot[] m_parkingSpots;                             // AGV parking spots
    
    private List<CraneAction> m_queue = new ArrayList<CraneAction>();   // Action queue
    private CraneAction m_currentAction;                                // Current action
    private boolean m_firstFrame = true;                                // Is first frame
    
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
    /**
     * Get parking spot
     * @param index
     * @return 
     */
    public ParkingSpot getParkingSpot(int index) {
        return m_parkingSpots[index];
    }
    
    /**
     * Set crane path
     * @param to target position
     */
    protected void setCraneTarget(Vector3f to) {
        m_crane.getCranePath().setPath(new Vector3f(to));
    }
    /**
     * Init parkingspots
     * @return 
     */
    protected abstract ParkingSpot[] parkingSpots();
    
    /**
     * Update, called every frame
     */
    public void update() {
        
        // Init
        if (m_firstFrame) {
            // First frame, init
            if (m_crane != null) {
                m_crane.onTargetCallback = new Callback(this, "onCrane");
                m_firstFrame = false;
            }
        }
        
        // Update crane
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
            Debug.error("Null reference: selected container not available!");
            return;
        }
        
        // Add action to queue
        m_queue.add(new CraneAction(container, new CraneTarget(p), new CraneTarget(spot)));
    }
    /**
     * 
     * @param parkingSpot
     * @param p 
     */
    public void place(int spot, Point3 p) {
        
        Container container = m_parkingSpots[spot].agv().getContainer();
        
        if (container == null) {
            // Invalid input
            Debug.error("Null reference: selected container not available!");
            return;
        }
        
        // Add action to queue
        m_queue.add(new CraneAction(container, new CraneTarget(spot), new CraneTarget(p)));
    }
    
    /**
     * On crane action finished
     */
    public void onCrane() {
        if (m_currentAction != null) 
        {
            // Finish the action
            m_currentAction.finish();
            
            // Check if need to do the placing
            if (m_currentAction.setPath()) {
                return;
            }
        } getNext();
    }
    /**
     * Get the next action in queue
     */
    public void getNext() {
        // Get new
        m_currentAction = null;
        
        if (m_queue.size() > 0) {
            m_queue.get(0).start();
        } else {
            // Default position
            m_crane.setPath();
        }
    }
    
    /**
     * A crane action
     * Take and place
     */
    private class CraneAction {
        public final Container   container;     // Target Container
        public final CraneTarget from;          // Taking place
        public final CraneTarget to;            // Placing place
        private int m_onTargetIndex = 0;        // Index to check if need to take or place
        
        /**
         * Constructor
         * @param container
         * @param from
         * @param to 
         */
        public CraneAction(Container container, CraneTarget from, CraneTarget to) {
            if (container == null) {
                throw new IllegalArgumentException("Container may not be NULL");
            } if (from == null) {
                throw new IllegalArgumentException("From may not be NULL");
            } if (to == null) {
                throw new IllegalArgumentException("To may not be NULL");
            }
            
            this.container = container;
            this.from      = from;
            this.to        = to;
        }
        
        public void start() {
            if (from.storageSpot != null) {
                
                // Check if need to replace above containers
                if (replace(from.storageSpot)) {
                    getNext();
                    return;
                }
                
                // Check where container need to go
                if (to.storageSpot != null) {
                    // TODO: get new point here !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    to.storageSpot = Point3.zero(); 
                }
            } else if (to.storageSpot != null) {
                
                // Check if need to replace above containers
                if (replace(to.storageSpot)) {
                    getNext();
                    return;
                } else { // Check if any below
                    int height = getStackHeight(new Point2(to.storageSpot.x, to.storageSpot.z));
                    if (height < to.storageSpot.y || to.storageSpot.y < 0) {
                        to.storageSpot.y = height;
                        // Debug.log("Container Y position fixed");
                    }
                }
            }
            
            m_currentAction = this;
            m_queue.remove(0);
            setPath();
        }
        /**
         * Set new Crane target path
         * @return 
         */
        public boolean setPath() {
            if (m_onTargetIndex > 1)
                return false;
                
            // Check if from or to
            CraneTarget _t = (m_onTargetIndex++ <= 0) ? from : to;
            
            if (_t.storageSpot != null)
                
                // To storage
                 m_crane.setPath(getSpot(_t.storageSpot).worldPosition());
            
            else if (_t.parkingSpot != null)
                
                // To Parking spot
                m_crane.setPath(m_parkingSpots[_t.parkingSpot].position());
            
            else
                
                // To container
                m_crane.setPath(container.position());
            
            return true;
        }
        /**
         * Calls on finish
         * Container parents swap
         */
        public void finish() {
            
            if (m_onTargetIndex <= 1) { // Take
                
                if (from.storageSpot != null) {
                    
                    // Storage to crane
                    m_crane.setContainer(container);
                    replaceContainer(container, null);
                    updateOuter();
                } else {
                    
                    // AGV to crane
                    Container c = m_parkingSpots[from.parkingSpot].agv().getContainer();
                    m_crane.setContainer(c);
                    m_parkingSpots[from.parkingSpot].agv().setContainer(null);
                }
            } else { // Place
                
                if (to.storageSpot != null) {
                    
                    // Crane to storage
                    Container c = m_crane.setContainer(null);
                    setContainer(to.storageSpot, c);
                    updateOuter();
                } else {
                    
                    // Crane to AGV
                    m_parkingSpots[to.parkingSpot].agv().setContainer(m_crane.setContainer(null));
                }
            }
        }
        /**
         * Check if need to replace
         * @param p
         * @return 
         */
        private boolean replace(Point3 p) {
            Point3 _np = p.above();
            Container _c = getContainer(_np);
            if (_c == null)
                return false;
            
            m_queue.add(0, new CraneAction(_c, new CraneTarget(_np), new CraneTarget(Point3.zero())));
            return true;
        }
    }
    /**
     * A crane target position
     * Storage or AGV
     */
    private class CraneTarget {
        public Point3 storageSpot;
        public final Integer parkingSpot;
        
        /**
         * Constructor
         * @param p Storage Position
         */
        public CraneTarget(Point3 p) {
            storageSpot = p;
            parkingSpot = null;
        }
        /**
         * Constructor
         * @param spot Parking Spot Index
         */
        public CraneTarget(int spot) {
            storageSpot = null;
            parkingSpot = spot + 0;
        }
    }
}
