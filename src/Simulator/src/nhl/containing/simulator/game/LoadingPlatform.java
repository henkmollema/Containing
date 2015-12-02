/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.game;

import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;
import nhl.containing.simulator.simulation.Callback;
import nhl.containing.simulator.simulation.Debug;
import nhl.containing.simulator.simulation.Path;
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
    }
    /**
     * Constructor
     * @param parent 
     */
    public LoadingPlatform(Transform parent) {
        super(parent);
    }
    
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
    public void take(Point3 p, ContainerCarrier carrier) {
        
        // Get container
        Container container = getContainer(p);
        
        if (container == null) {
            // Invalid input
            Debug.error("Null reference: selected container not available!");
            return;
        }
        
        // Check if needed to move containers that lie on top of the target one
        replace(new Point3(p).add(Point3.up()));
        
        // Add the action to queue
        m_queue.add(new CraneAction(container, new Callback(this, "attach2Crane")));
        m_queue.add(new CraneAction(container, carrier, new Callback(this, "crane2carrier")));
    }
    /**
     * 
     * @param parkingSpot
     * @param p 
     */
    public void place(int parkingSpot, Point3 p) {
        // TODO: 
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
        m_currentAction.holder.setContainer(m_crane.setContainer(null));
    }
    
    private CraneAction baseAction() {
        return null;
    }
    
    /**
     * A crane action
     * Take and place
     */
    private class CraneAction {
        public final ContainerCarrier holder;
        public final Container target; // 
        public Callback finishCallback; // Maybe can also without reflection, but for now leave it
        
        public CraneAction(Container target, Callback onFinish) {
            this.target = target;
            this.holder = null;
            this.finishCallback = onFinish;
        }
        public CraneAction(Container target, ContainerCarrier carrier, Callback onFinish) {
            this.target = target;
            this.holder = carrier;
            this.finishCallback = onFinish;
        }
        
        /**
         * On first frame when its <this> turn
         */
        public void start() {
            m_currentAction = this;
            if (holder == null)
                m_crane.setPath(target.position());
            else
                m_crane.setPath(holder.position());
        }
    }
}
