/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.game;

import com.jme3.math.Vector3f;
import nhl.containing.simulator.framework.Transform;

/**
 *
 * @author sietse
 */
public class PlatformInland extends PlatformLoading {

    public PlatformInland(Vector3f offset) {
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
        
    }
    
}
