package nhl.containing.simulator.game;

import nhl.containing.simulator.framework.Point3;
import com.jme3.math.Vector3f;
import nhl.containing.networking.protobuf.SimulationItemProto;
import nhl.containing.simulator.world.World;
import nhl.containing.simulator.world.WorldCreator;

/**
 * Platform extion,
 * This is the main Container storage
 * The thing in the middle
 * @author sietse
 */
public final class PlatformStorage extends Platform {

    private int m_storageid;
    /**
     * Constructor
     * @param id id of the platformstorage
     * @param offset  
     */
    public PlatformStorage(int id,Vector3f offset) {
        super(offset,id);
        this.m_storageid = id;
        this.register(-1,m_storageid,SimulationItemProto.SimulationItem.SimulationItemType.PLATFORM);
    }
    
    /**
     * Create storage platform
     */
    @Override
    protected final void createPlatform() {
        m_crane = WorldCreator.createStorageCrane(this);
        initSpots(new Point3(6, 6, World.STORAGE_SIZE.x));
        updateOuter();
    }

    /**
     * Create parking spots
     * @return 
     */
    @Override
    protected ParkingSpot[] parkingSpots(int id) {
        float y_offset = 0.0f;
        return new ParkingSpot[] {
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 0.0f, y_offset, -World.containerSize().z),id),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 1.0f, y_offset, -World.containerSize().z),id),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 2.0f, y_offset, -World.containerSize().z),id),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 3.0f, y_offset, -World.containerSize().z),id),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 4.0f, y_offset, -World.containerSize().z),id),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 5.0f, y_offset, -World.containerSize().z),id)
        };
    }
}
