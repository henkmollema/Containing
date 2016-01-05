/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.game;

import com.jme3.math.Vector3f;
import nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.*;
import nhl.containing.simulator.framework.Utilities;

/**
 *
 * @author sietse
 */
public class PlatformInland extends Platform {

    public PlatformInland(Vector3f offset,int id) {
        super(offset,id);
        this.register(-1,m_platformid,SimulationItemType.PLATFORM_INLANDSHIP);
    }
    @Override
    protected ParkingSpot[] parkingSpots() {
        return new ParkingSpot[] {
            new ParkingSpot(this, Utilities.zero(),m_platformid,SimulationItemType.PARKINGSPOT_INLANDSHIP, 2, 3)
        };
    }

    @Override
    protected void createPlatform() {
        
    }
    
}
