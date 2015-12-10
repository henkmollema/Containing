package nhl.containing.simulator.game;

import nhl.containing.simulator.simulation.Point3;
import com.jme3.math.Vector3f;
import nhl.containing.simulator.simulation.Transform;
import nhl.containing.simulator.world.World;
import nhl.containing.simulator.world.WorldCreator;

/**
 * Platform extion,
 * This is the main Container storage
 * The thing in the middle
 * @author sietse
 */
public final class PlatformStorage extends PlatformLoading {

    /**
     * Constructor
     * @param position 
     */
    public PlatformStorage(Vector3f position) {
        super();
        createPlatform();
        this.position(position);
    }
    /**
     * Constructor
     * @param parent
     * @param position 
     */
    public PlatformStorage(Transform parent, Vector3f position) {
        super(parent);
        createPlatform();
        this.position(position);
    }
    /**
     * Create storage platform
     */
    @Override
    void createPlatform() {
        m_crane = WorldCreator.createStorageCrane(this);
        initSpots(new Point3(6, 6, World.STORAGE_SIZE.x));
        updateOuter();
    }

    /**
     * Create parking spots
     * @return 
     */
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
