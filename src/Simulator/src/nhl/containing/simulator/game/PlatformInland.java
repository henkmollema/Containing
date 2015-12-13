/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.game;

import com.jme3.math.Vector3f;
import nhl.containing.simulator.simulation.Transform;

/**
 *
 * @author sietse
 */
public class PlatformInland extends PlatformLoading {

    public PlatformInland(Transform parent, Vector3f _position) {
        super(parent);
        
        createPlatform();
        this.position(_position);
    }
    
    @Override
    protected ParkingSpot[] parkingSpots() {
        return new ParkingSpot[] {
            new ParkingSpot(this, Vector3f.ZERO)
        };
    }

    @Override
    void createPlatform() {
        
    }
    
}
