/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.game;

import com.jme3.math.Vector3f;
import nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.*;

/**
 *
 * @author sietse
 */
public class PlatformInland extends Platform {

    public PlatformInland(Vector3f offset,int id) {
        super(offset,id);
    }
    @Override
    protected ParkingSpot[] parkingSpots() {
        return new ParkingSpot[] {
            new ParkingSpot(this, Vector3f.ZERO,SimulationItemType.PARKINGSPOT_INLANDSHIP)
        };
    }

    @Override
    protected void createPlatform() {
        
    }
    
}
