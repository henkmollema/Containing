/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.game;

import nhl.containing.simulator.simulation.Point3;
import com.jme3.math.Vector3f;
import nhl.containing.simulator.simulation.Debug;
import nhl.containing.simulator.simulation.Transform;
import nhl.containing.simulator.world.World;

/**
 *
 * @author sietse
 */
public final class StoragePlatform extends LoadingPlatform {

    /**
     * Constructor
     * @param position 
     */
    public StoragePlatform(Vector3f position) {
        super();
        createPlatform();
        this.position(position);
    }
    /**
     * Constructor
     * @param parent
     * @param position 
     */
    public StoragePlatform(Transform parent, Vector3f position) {
        super(parent);
        createPlatform();
        this.position(position);
    }
    /**
     * Create storage platform
     */
    @Override
    void createPlatform() {
        m_crane = new RailCrane(this/*, new Vector3f(6.0f, 0.0f, 20.0f)*/);
        initSpots(new Point3(6, 6, World.STORAGE_SIZE.x));
        updateOuter();
    }

    @Override
    protected ParkingSpot[] parkingSpots() {
        float y_offset = 0.0f;
        return new ParkingSpot[] {
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 0.0f, y_offset, -World.containerSize().z)),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 1.0f, y_offset, -World.containerSize().z)),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 2.0f, y_offset, -World.containerSize().z)),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 3.0f, y_offset, -World.containerSize().z)),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 4.0f, y_offset, -World.containerSize().z)),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 5.0f, y_offset, -World.containerSize().z))
        };
    }
}
