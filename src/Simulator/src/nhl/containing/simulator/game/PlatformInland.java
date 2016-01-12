/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.game;

import com.jme3.math.Vector3f;
import nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.*;
import nhl.containing.simulator.framework.Point3;
import nhl.containing.simulator.framework.Utilities;
import nhl.containing.simulator.world.WorldCreator;

/**
 *
 * @author sietse
 */
public class PlatformInland extends Platform {

    /**
     * Constructor
     * @param offset
     * @param id 
     */
    public PlatformInland(Vector3f offset,int id, Vehicle v) {
        super(offset,id, true);
        this.register(-1,m_platformid,SimulationItemType.PLATFORM_INLANDSHIP);
        this.v = v;
    }
    
    /**
     * Parkingsots
     * @return 
     */
    @Override
    protected ParkingSpot[] parkingSpots() {
        return new ParkingSpot[] {
            new ParkingSpot(this, Utilities.zero(),m_platformid,SimulationItemType.PARKINGSPOT_INLANDSHIP, 2, 3, false)
        };
    }

    /**
     * Create its platform
     */
    @Override
    protected void createPlatform() {
        m_crane = WorldCreator.createSeaCrane(this);
        initSpots(Point3.one());
        updateOuter();
    }
    
    private Point3 m_tempPoint;
    private Vehicle v;
    
    public void take(Point3 point) {
        Container c = v.setContainer(point, null);
        setContainer(c);
        super.take(Point3.zero(), 0);
    }
    public void place(Point3 point) {
        m_tempPoint = new Point3(point);
        super.place(0, Point3.zero());
    }
    
    @Override
    public void _onStoragePlace() {
        Container c = setContainer(null);
        v.setContainer(m_tempPoint, c);
    }
}
