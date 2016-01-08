/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.game;

import com.jme3.math.Vector3f;
import nhl.containing.simulator.framework.Point3;
import nhl.containing.simulator.world.WorldCreator;
import nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.*;
import nhl.containing.simulator.framework.Utilities;

/**
 *
 * @author sietse
 */
public class PlatformLorry extends Platform {

    public PlatformLorry(Vector3f offset,int id) {
        super(offset,id);
        this.register(-1,m_platformid,SimulationItemType.PLATFORM_LORRY);
        initSpots(Point3.one());
    }
    
    @Override
    protected ParkingSpot[] parkingSpots() {
        return new ParkingSpot[] {
            new ParkingSpot(this, Utilities.zero(),m_platformid,SimulationItemType.PARKINGSPOT_LORRY, 0, 1)

        };
    }

    @Override
    protected final void createPlatform() {
        m_crane = WorldCreator.createLorryCrane(this);
        initSpots(new Point3());
        updateOuter();
    }
    
}
