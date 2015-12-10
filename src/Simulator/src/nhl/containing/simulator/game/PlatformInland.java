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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    void createPlatform() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
