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
    protected CraneAction currentAction;
    
    private List<CraneAction> queue = new ArrayList<CraneAction>();
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
        currentAction = null;
        if (queue.size() > 0) {
            queue.get(0).start();
            queue.remove(0);
        } else {
            m_crane.setPath();
        }
    }
    
    private class CraneAction {
        public final Vector3f target;
        
        public CraneAction(Vector3f target) {
            this.target = target;
        }
        
        public void start() {
            m_crane.setPath(target);
        }
    }
}
