/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.game;

import com.jme3.math.Vector3f;
import nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.*;
import nhl.containing.simulator.framework.Point3;
import nhl.containing.simulator.world.WorldCreator;

/**
 *
 * @author sietse
 */
public class PlatformTrain extends Platform {

    /**
     * Constructor
     * @param offset
     * @param id 
     */
    public PlatformTrain(Vector3f offset,int id) {
        super(offset,id, true);
        this.register(-1,m_platformid,SimulationItemType.PLATFORM_TRAIN);
        initSpots(new Point3(1, 1, 1));
    }
    
    /**
     * Get all parkingspots (new)
     * @return 
     */
    @Override
    protected ParkingSpot[] parkingSpots() {
        return new ParkingSpot[] {
            new ParkingSpot(this, new Vector3f(-5.0f, 0.0f, 0.0f),m_platformid,SimulationItemType.PARKINGSPOT_TRAIN, 6, 7, false)
        };
    }

    /**
     * create platform
     */
    @Override
    protected final void createPlatform() {
        m_crane = WorldCreator.createTrainCrane(this);
        initSpots(new Point3());
        updateOuter();
    }
    
}
