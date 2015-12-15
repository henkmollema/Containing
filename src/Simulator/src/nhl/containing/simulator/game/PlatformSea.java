/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.game;

import com.jme3.math.Vector3f;
import nhl.containing.simulator.framework.Point3;
import nhl.containing.simulator.framework.Transform;
import nhl.containing.simulator.world.WorldCreator;

/**
 *
 * @author sietse
 */
public class PlatformSea extends PlatformLoading {

    public PlatformSea(Vector3f offset) {
        super(offset);
    }
    
    @Override
    protected ParkingSpot[] parkingSpots() {
        return new ParkingSpot[] {
            new ParkingSpot(this, Vector3f.ZERO)
        };
    }

    @Override
    protected void createPlatform() {
       m_crane = WorldCreator.createSeaCrane(this);
        initSpots(new Point3());
        updateOuter();
    }
    
}
