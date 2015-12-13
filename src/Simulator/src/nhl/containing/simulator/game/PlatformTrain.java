/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.game;

import com.jme3.math.Vector3f;
import nhl.containing.simulator.simulation.Point3;
import nhl.containing.simulator.simulation.Transform;
import nhl.containing.simulator.world.WorldCreator;

/**
 *
 * @author sietse
 */
public class PlatformTrain extends PlatformLoading {

    public PlatformTrain(Transform parent, Vector3f _position) {
        super(parent);
        
        createPlatform();
        this.position(_position);
    }
    
    @Override
    protected ParkingSpot[] parkingSpots() {
        return new ParkingSpot[] {
            new ParkingSpot(this, new Vector3f(-5.0f, 0.0f, 0.0f))
        };
    }

    @Override
    protected final void createPlatform() {
        m_crane = WorldCreator.createTrainCrane(this);
        initSpots(new Point3());
        updateOuter();
    }
    
}
