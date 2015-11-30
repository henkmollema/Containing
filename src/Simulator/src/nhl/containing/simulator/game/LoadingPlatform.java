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
    
    public void take(Point3 p, ContainerCarrier carrier) {
        
        Container container = getContainer(p);
        
        if (container == null) {
            Debug.error("Null reference: selected container not available!");
            return;
        }
        
        //replace(new Point3(p).add(Point3.up()));
        m_queue.add(new CraneAction(container, new Callback(this, "attach2Crane")));
        m_queue.add(new CraneAction(container, carrier, new Callback(this, "crane2carrier")));
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
        if (m_currentAction != null) {
            if (m_currentAction.finishCallback != null)
                m_currentAction.finishCallback.invoke();
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
        m_crane.setContainer(m_currentAction.target);
    }
    private void crane2carrier() {
        m_currentAction.holder.setContainer(m_crane.setContainer(null));
    }
    
    private CraneAction baseAction() {
        return null;
    }
    private class CraneAction {
        public final ContainerCarrier holder;
        public final Container target; // 
        public final Callback finishCallback;
        
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
        
        public void start() {
            m_currentAction = this;
            if (holder == null)
                m_crane.setPath(target.position());
            else
                m_crane.setPath(holder.position());
        }
    }
}
