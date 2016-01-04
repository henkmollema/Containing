package nhl.containing.simulator.game;

import nhl.containing.simulator.framework.Point3;
import com.jme3.math.Vector3f;
import nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.*;
import nhl.containing.simulator.world.World;
import nhl.containing.simulator.world.WorldCreator;

/**
 * Platform extion,
 * This is the main Container storage
 * The thing in the middle
 * @author sietse
 */
public final class PlatformStorage extends Platform {

    /**
     * Constructor
     * @param id id of the platformstorage
     * @param offset  
     */
    public PlatformStorage(Vector3f offset,int id) {
        super(offset,id);
        this.register(-1,m_platformid,SimulationItemType.PLATFORM_STORAGE);
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
    protected ParkingSpot[] parkingSpots() {
        float y_offset = 0.0f;
        float z_offset = 200.0f;
        
        return new ParkingSpot[] {
            
            // 
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 0.0f, y_offset, -World.containerSize().z),m_platformid,SimulationItemType.PARKINGSPOT_STORAGE, -1, -1),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 1.0f, y_offset, -World.containerSize().z),m_platformid,SimulationItemType.PARKINGSPOT_STORAGE, -1, -1),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 2.0f, y_offset, -World.containerSize().z),m_platformid,SimulationItemType.PARKINGSPOT_STORAGE, -1, -1),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 3.0f, y_offset, -World.containerSize().z),m_platformid,SimulationItemType.PARKINGSPOT_STORAGE, -1, -1),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 4.0f, y_offset, -World.containerSize().z),m_platformid,SimulationItemType.PARKINGSPOT_STORAGE, -1, -1),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 5.0f, y_offset, -World.containerSize().z),m_platformid,SimulationItemType.PARKINGSPOT_STORAGE, -1, -1),
            
            // 
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 0.0f, y_offset, -World.containerSize().z - z_offset + World.STORAGE_WIDTH * 2),m_platformid,SimulationItemType.PARKINGSPOT_STORAGE, -1, -1),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 1.0f, y_offset, -World.containerSize().z - z_offset + World.STORAGE_WIDTH * 2),m_platformid,SimulationItemType.PARKINGSPOT_STORAGE, -1, -1),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 2.0f, y_offset, -World.containerSize().z - z_offset + World.STORAGE_WIDTH * 2),m_platformid,SimulationItemType.PARKINGSPOT_STORAGE, -1, -1),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 3.0f, y_offset, -World.containerSize().z - z_offset + World.STORAGE_WIDTH * 2),m_platformid,SimulationItemType.PARKINGSPOT_STORAGE, -1, -1),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 4.0f, y_offset, -World.containerSize().z - z_offset + World.STORAGE_WIDTH * 2),m_platformid,SimulationItemType.PARKINGSPOT_STORAGE, -1, -1),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 5.0f, y_offset, -World.containerSize().z - z_offset + World.STORAGE_WIDTH * 2),m_platformid,SimulationItemType.PARKINGSPOT_STORAGE, -1, -1),
        };
    }
}
