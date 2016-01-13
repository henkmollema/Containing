package nhl.containing.simulator.game;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.SimulationItemType;
import nhl.containing.simulator.framework.Transform;
import nhl.containing.simulator.framework.Utilities;
import nhl.containing.simulator.simulation.Main;
import nhl.containing.simulator.world.WorldCreator;

/**
 * TODO: create()
 * @author sietse
 */
public class ParkingSpot extends Transform {
    
    private AGV m_agv;                  // Container carrier
    private int m_arrivalPathID;        // Node where to arrive to get to this
    private int m_departPathID;         // Node where to depart get get away from this
    private boolean m_snap2X = false;   // Snap to X (flase -> Snap to Z)
    public AGV future_agv;
    
     /**
      * Constructor
      * @param parent
      * @param offset
      * @param id
      * @param type
      * @param arrival
      * @param depart
      * @param snap2x 
      */
    public ParkingSpot(Transform parent,Vector3f offset,int id,SimulationItemType type, int arrival, int depart, boolean snap2x){
        super(parent);
        m_agv = null;
        create();
        this.localPosition(offset);
        this.register(id,type,arrival,depart);
        Main.register(this);
        m_arrivalPathID = arrival;
        m_departPathID = depart;
        m_snap2X = snap2x;
    }
    
    /**
     * Constructor
     * @param parent
     * @param offset
     * @param type
     * @param arrival
     * @param depart
     * @param snap2x 
     */
    public ParkingSpot(Transform parent, Vector3f offset, SimulationItemType type, int arrival, int depart, boolean snap2x) {
        super(parent);
        m_agv = null;
        create();
        this.localPosition(offset);
        this.register(type,arrival,depart);
        Main.register(this);
        m_arrivalPathID = arrival;
        m_departPathID = depart;
        m_snap2X = snap2x;
    }
    
    /**
     * Get arrival node id
     * @return 
     */
    public int arrivalID() {
        return m_arrivalPathID;
    }
    /**
     * Get depart node id
     * @return 
     */
    public int departID() {
        return m_departPathID;
    }
    /**
     * Set arrival id
     * @param i 
     */
    public void arrivalID(int i) {
        m_arrivalPathID = i;
    }
    /**
     * Set depart id
     * @param i 
     */
    public void departID(int i) {
        m_departPathID = i;
    }
    /**
     * Is napped to X axis
     * if false it is snapped
     * to Z axis
     * @return 
     */
    public boolean snap2x() {
        return m_snap2X;
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
        if(_agv == null)
        {
            //System.err.println(this.id() + " left by AGV: " + m_agv.id());
        }
        
        this.m_agv = _agv;
        
    }
    /**
     * Visualize the parkingspot
     */
    private void create() {
        // OPTIONAL: Maybe shadow caster or decal or something with stencils
        WorldCreator.createBox(this, Utilities.one(), ColorRGBA.Black);
    }
}
