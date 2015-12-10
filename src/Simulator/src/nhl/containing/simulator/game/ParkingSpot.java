package nhl.containing.simulator.game;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import nhl.containing.simulator.simulation.Transform;
import nhl.containing.simulator.simulation.Utilities;
import nhl.containing.simulator.world.WorldCreator;

/**
 * TODO: create()
 * @author sietse
 */
public class ParkingSpot extends Transform {
    
    private AGV m_agv;      // Container carrier
    
    /**
     * Constructor
     * @param parent Platform
     * @param offset Offset to Platform
     */
    public ParkingSpot(Transform parent, Vector3f offset) {
        super(parent);
        m_agv = null;
        create();
        this.localPosition(offset);
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
        WorldCreator.createBox(this, Utilities.one(), ColorRGBA.Black);
    }
}
