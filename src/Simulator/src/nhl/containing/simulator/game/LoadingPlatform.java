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
    
    protected Crane m_crane;
    protected ParkingSpot[] m_parkingSpots;
    
    private List<CraneAction> m_queue = new ArrayList<CraneAction>();
    protected CraneAction m_currentAction;
    private boolean m_firstFrame = true;
    
    public LoadingPlatform() {
        super();
    }
    public LoadingPlatform(Transform parent) {
        super(parent);
    }
    
    protected void setCraneTarget(Vector3f to) {
        m_crane.getCranePath().setPath(new Vector3f(to));
    }
    
    public void update() {
        if (m_firstFrame) {
            if (m_crane != null) {
                m_crane.onTargetCallback = new Callback(this, "onCrane");
                m_firstFrame = false;
            }
        }
        
        if (m_crane != null) {
            m_crane._update();
        }
    }
    
    public void take(Point3 p, int parkingSpot) {
        Container container = getContainer(p);
        
        if (container == null) {
            Debug.error("Null reference: selected container not available!");
            return;
        }
        
        replace(new Point3(p).add(Point3.up()));
    }
    public void place(int parkingSpot, Point3 p) {
        
    }
    
    public void replace(Point3 p) {
        Container container = getContainer(p);
        if (container == null)
            return;
        
        replace(new Point3(p).add(Point3.up()));
        
        // Do here replacement things
    }
    public void onCrane() {
        Debug.log("op 1");
        if (m_currentAction != null) {
            if (m_crane.attachedContainer() == null) {
                attach2Crane();
            } else if (m_crane.attachedContainer() == m_currentAction.target) {
                crane2carrier();
            } else {
                Debug.error("ASDF");
            }
        }
        
        // Get new
        m_currentAction = null;
        if (m_queue.size() > 0) {
            m_queue.get(0).start();
            m_queue.remove(0);
        } else {
            m_crane.setPath();
        }
    }
    private void attach2Crane() {
        m_crane.attachedContainer(m_currentAction.target);
    }
    private void crane2carrier() {
        m_crane.attachedContainer();
    }
    
    private CraneAction baseAction() {
        return null;
    }
    private class CraneAction {
        public final ContainerCarrier holder;
        public final Container target; // 
        
        public CraneAction(Container target) {
            this.target = target;
            this.holder = null;
        }
        
        public void start() {
            m_currentAction = this;
            m_crane.setPath(target.position());
        }
    }
}