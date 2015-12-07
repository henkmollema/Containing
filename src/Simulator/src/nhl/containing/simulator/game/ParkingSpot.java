/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.game;

import com.jme3.math.Vector3f;
import nhl.containing.simulator.simulation.Transform;

/**
 * TODO: create()
 * @author sietse
 */
public class ParkingSpot extends Transform {
    
    private AGV m_agv;
    
    /**
     * Constructor
     * @param parent Platform
     * @param offset Offset to Platform
     */
    public ParkingSpot(Transform parent, Vector3f offset) {
        super(parent);
        this.localPosition(offset);
        
        m_agv = null;
        create();
    }
    
    /**
     * Get AGV
     * @return 
     */
    public AGV agv() {
        return m_agv;
    }
    /**
     * Set AGV
     * @param _agv 
     */
    public void agv(AGV _agv) {
        this.m_agv = _agv;
    }
    /**
     * Visualize the parkingspot
     */
    private void create() {
        // TODO: Maybe shadow caster or decal
    }
}
