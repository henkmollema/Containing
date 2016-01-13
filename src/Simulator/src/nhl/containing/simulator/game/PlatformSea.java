package nhl.containing.simulator.game;

import com.jme3.math.Vector3f;
import nhl.containing.simulator.framework.Point3;
import nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.*;
import nhl.containing.simulator.framework.Utilities;
import nhl.containing.simulator.world.WorldCreator;

/**
 * Platform extention for sea platform
 * @author sietse
 */
public class PlatformSea extends Platform {

    private Point3 m_tempPoint;     // Saved point to place a container
    private Vehicle v;              // Target ship
    
    /**
     * Contstructor
     * @param offset
     * @param id
     * @param v 
     */
    public PlatformSea(Vector3f offset,int id,Vehicle v) {
        super(offset,id, false);
        this.register(-1,m_platformid,SimulationItemType.PLATFORM_SEASHIP);
        this.v = v;
    }
    
    /**
     * Get parkingspots
     * @return 
     */
    @Override
    protected ParkingSpot[] parkingSpots() {
        return new ParkingSpot[] {
            new ParkingSpot(this,Utilities.zero(),m_platformid,SimulationItemType.PARKINGSPOT_SEASHIP, 4, 5, true)
        };
    }

    /**
     * Create platform
     */
    @Override
    protected void createPlatform() {
       m_crane = WorldCreator.createSeaCrane(this);
        initSpots(Point3.one());
        updateOuter();
    }
    
    /**
     * Set the target vehicle
     * @param v 
     */
    public void setVehicle(Vehicle v){
        this.v = v;
    }
    
    /**
     * Take from ship
     * @param point 
     */
    public void take(Point3 point) {
        Container c = v.setContainer(point, null);
        setContainer(c);
        super.take(Point3.zero(), 0);
    }
    /**
     * Place on ship
     * @param point 
     */
    public void place(Point3 point) {
        m_tempPoint = new Point3(point);
        super.place(0, Point3.zero());
    }
    
    /**
     * Callen when container is placed
     */
    @Override
    public void _onStoragePlace() {
        Container c = setContainer(null);
        v.setContainer(m_tempPoint, c);
    }
}
