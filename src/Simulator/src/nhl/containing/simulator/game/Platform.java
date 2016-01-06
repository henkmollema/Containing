package nhl.containing.simulator.game;

import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;
import nhl.containing.networking.protocol.InstructionType;
import nhl.containing.simulator.framework.Callback;
import nhl.containing.simulator.framework.Point2;
import nhl.containing.simulator.framework.Point3;
import nhl.containing.simulator.networking.SimulatorClient;
/**
 * TODO: replace() line 193!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * also check line 206
 * 
 * Loading platform, also controls its crane
 * 
 * @author sietse
 */
public abstract class Platform extends ContainerCarrier {
    
    protected Crane m_crane;                                // Crane
    protected ParkingSpot[] m_parkingSpots;                 // AGV parking spots
    protected int m_platformid;
    
    private List<CraneAction> m_queue = new ArrayList<>();  // Action queue
    private CraneAction m_currentAction;                    // Current action
    private boolean m_firstFrame = true;                    // Is first frame
    
    /**
     * Constructor
     */
//    public Platform(Vector3f offset) {
//        super();
//        m_parkingSpots = _parkingSpots(-1);
//        createPlatform();
//        this.position(offset);
//    }
    
    public Platform(Vector3f offset, int id){
        super();
        m_platformid = id;
        m_parkingSpots = _parkingSpots();
        createPlatform();
        this.position(offset);
    }
    
    private ParkingSpot[] _parkingSpots() {
        return parkingSpots();
    }
    
    public ParkingSpot getParkingSpot() {
        int i;
        for (i = 0; i < m_parkingSpots.length; i++) {
            if (m_parkingSpots[i].agv() == null)
                return m_parkingSpots[i];
        }
        
        if (i > 1)
            return m_parkingSpots[i - 1];
        return null;
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
        m_crane.path().setPath(new Vector3f(to));
    }
    /**
     * Init parkingspots
     * @return 
     */
    protected abstract ParkingSpot[] parkingSpots();
    protected abstract void createPlatform();
    
    /**
     * Returns the platform id
     * @return id
     */
    public int getPlatformID(){
        return m_platformid;
    }
    
    public Crane crane() {
        return m_crane;
    }
    /**
     * Update, called every frame
     */
    public void update() {
        
        if (m_crane != null && m_crane.targetIsLast())
            //if (!m_currentAction.isTake())
            //System.out.println("TEST");
            m_crane.paused = !m_currentAction.isTake() && m_currentAction.to.storageSpot == null && m_parkingSpots[m_currentAction.to.parkingSpot].agv() == null;
        
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
     * @param point Container carrier container position
     * @param carrier The container carrier (target)
     */
    public void take(Point3 point, int spot) {
        
        // Get container
        Container _container = getContainer(point);
        
        if (_container == null) {
            // Invalid input
            System.out.println("Null reference: selected container not available!");
            return;
        }
        
        // Add action to queue
        m_queue.add(new CraneAction(_container, new CraneTarget(point), new CraneTarget(spot)));
    }
    /**
     * 
     * @param parkingSpot
     * @param point 
     */
    public void place(int spot, Point3 point) {
        
        Container _container = m_parkingSpots[spot].agv().getContainer();
        
        if (_container == null) {
            // Invalid input
            System.out.println("Null reference: selected container not available!");
            return;
        }
        
        // Add action to queue
        m_queue.add(new CraneAction(_container, new CraneTarget(spot), new CraneTarget(point)));
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
        
        if (m_queue.size() > 0) // Next task position
            m_queue.get(0).start();
        else // Default position
            m_crane.setPath();
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
        
        public boolean isTake() {
            return m_onTargetIndex <= 1;
        }
        /**
         * Constructor
         * NOTE: No input may have "null" value
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
        
        /**
         * Called at first frame
         */
        public void start() {
            if (from.storageSpot != null) {
                
                // Check if need to replace above containers
                if (replace(from.storageSpot)) {
                    getNext();
                    return;
                }
                
                // Check where container need to go
                if (to.storageSpot != null) {
                    //TODO: get new point here !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    to.storageSpot = Point3.zero(); 
                }
            } else if (to.storageSpot != null) {
                
                // Check if need to replace above containers
                if (replace(to.storageSpot)) {
                    getNext();
                    return;
                } else { // Check if any below
                    int _height = getStackHeight(new Point2(to.storageSpot.x, to.storageSpot.z));
                    if (_height < to.storageSpot.y || to.storageSpot.y < 0) {
                        to.storageSpot.y = _height;
                        // Debug.log("Container Y position fixed");
                    }
                }
            }
            
            // Set
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
            CraneTarget _target = (m_onTargetIndex++ <= 0) ? from : to;
            
            if (_target.storageSpot != null)
                
                // To storage
                 m_crane.setPath(getSpot(_target.storageSpot).localPosition); // .worldPosition()
            
            else if (_target.parkingSpot != null)
                
                // To Parking spot
                m_crane.setPath(m_parkingSpots[_target.parkingSpot].localPosition()); //.position()
            
            else
                
                // To container
                m_crane.setPath(container.transform.localPosition()); // .position()
            
            return true;
        }
        /**
         * Calls on action finish
         * Container parents swap
         */
        public void finish() {
            
            if (isTake()) { // Take
                
                if (from.storageSpot != null) {
                    
                    // Storage to crane
                    m_crane.setContainer(container);
                    replaceContainer(container, null);
                    updateOuter();
                } else {
                    
                    // AGV to crane
                    Container _container = m_parkingSpots[from.parkingSpot].agv().getContainer();
                    m_crane.setContainer(_container);
                    m_parkingSpots[from.parkingSpot].agv().setContainer(null);
                }
            } else { // Place
                
                if (to.storageSpot != null) {
                    
                    // Crane to storage
                    Container c = m_crane.setContainer(null);
                    setContainer(to.storageSpot, c);
                    updateOuter();
                    SimulatorClient.sendTaskDone(m_platformid, c.getRFID().id, InstructionType.CRANE_TO_STORAGE_READY, to.storageSpot);
                } else { 
                    // Crane to AGV
                    Container c = m_crane.setContainer(null);
                    m_parkingSpots[to.parkingSpot].agv().setContainer(c);
                    //TODO: Add the right B item
                    SimulatorClient.sendTaskDone((int)m_parkingSpots[to.parkingSpot].agv().id(),c.getRFID().id, InstructionType.CRANE_TO_AGV_READY,from.storageSpot);
                    m_parkingSpots[to.parkingSpot].agv(null);
                }
            }
        }
        
        /**
         * Check if need to replace
         * @param point
         * @return 
         */
        private boolean replace(Point3 point) {
            Point3 _newPoint = point.above();
            Container _container = getContainer(_newPoint);
            if (_container == null)
                return false;
            
            m_queue.add(0, new CraneAction(_container, new CraneTarget(_newPoint), new CraneTarget(Point3.zero())));
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
