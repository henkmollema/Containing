package nhl.containing.simulator.game;

import com.jme3.math.ColorRGBA;
import nhl.containing.simulator.framework.Point3;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.SimulationItemType;
import nhl.containing.simulator.world.World;
import nhl.containing.simulator.world.WorldCreator;

/**
 * Platform extion,
 * This is the main Container storage
 * The thing in the middle
 * @author sietse
 */
public final class PlatformStorage extends Platform {

    //right side top
    private final int m_arrivalIDRightTop = 20;
    private final int m_departIDRightTop = 22;
    
    //right side down
    private final int m_arrivalIDRightDown = 33;
    private final int m_departIDRightDown = 31;
    
    //left side top
    private final int m_arrivalIDLeftTop = 16;
    private final int m_departIDLeftTop = 19;
    
    //left side down
    private final int m_arrivalIDLeftDown = 30;
    private final int m_departIDLeftDown = 28;
    
    private final float yOffset = 0.0f;
    private final float zOffset = 1110.0f;
    
    
    /**
     * Constructor
     * @param id id of the platformstorage
     * @param offset  
     */
    public PlatformStorage(Vector3f offset,int id) {
        super(offset,id, true);
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
        
        Geometry g = WorldCreator.createBox(this, new Vector3f(16.0f, 1.0f, 550.0f), new ColorRGBA(0.45f, 0.45f, 0.45f, 1.0f));
        g.setLocalTranslation(new Vector3f(16.0f, - 0.7f, 550.0f));
    }

    /**
     * Create parking spots
     * @return 
     */
    @Override
    protected ParkingSpot[] parkingSpots() {
        
        int arrivalTop, departTop, arrivalDown, departDown;
        
        if(m_platformid - World.STORAGE_BEGIN <= 35){
            arrivalTop = m_arrivalIDRightTop;
            departTop = m_departIDRightTop;
            arrivalDown = m_arrivalIDRightDown;
            departDown = m_departIDRightDown;
        }
        else{
            arrivalTop = m_arrivalIDLeftTop;
            departTop = m_departIDLeftTop;
            arrivalDown = m_arrivalIDLeftDown;
            departDown = m_departIDLeftDown;
        }
        
        return new ParkingSpot[] {
            
            // 
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 2 * 0.0f, yOffset, -World.containerSize().z),m_platformid,SimulationItemType.PARKINGSPOT_STORAGE, arrivalDown, departDown, false),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 2 * 1.0f, yOffset, -World.containerSize().z),m_platformid,SimulationItemType.PARKINGSPOT_STORAGE, arrivalDown, departDown, false),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 2 * 2.0f, yOffset, -World.containerSize().z),m_platformid,SimulationItemType.PARKINGSPOT_STORAGE, arrivalDown, departDown, false),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 2 * 3.0f, yOffset, -World.containerSize().z),m_platformid,SimulationItemType.PARKINGSPOT_STORAGE, arrivalDown, departDown, false),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 2 * 4.0f, yOffset, -World.containerSize().z),m_platformid,SimulationItemType.PARKINGSPOT_STORAGE, arrivalDown, departDown, false),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 2 * 5.0f, yOffset, -World.containerSize().z),m_platformid,SimulationItemType.PARKINGSPOT_STORAGE, arrivalDown, departDown, false),
            
            // 
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 2 * 0.0f, yOffset, zOffset),m_platformid,SimulationItemType.PARKINGSPOT_STORAGE, arrivalTop, departTop, false),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 2 * 1.0f, yOffset, zOffset),m_platformid,SimulationItemType.PARKINGSPOT_STORAGE, arrivalTop, departTop, false),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 2 * 2.0f, yOffset, zOffset),m_platformid,SimulationItemType.PARKINGSPOT_STORAGE, arrivalTop, departTop, false),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 2 * 3.0f, yOffset, zOffset),m_platformid,SimulationItemType.PARKINGSPOT_STORAGE, arrivalTop, departTop, false),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 2 * 4.0f, yOffset, zOffset),m_platformid,SimulationItemType.PARKINGSPOT_STORAGE, arrivalTop, departTop, false),
            new ParkingSpot(this, new Vector3f(World.containerSize().x * 2 * 5.0f, yOffset, zOffset),m_platformid,SimulationItemType.PARKINGSPOT_STORAGE, arrivalTop, departTop, false),
        };
    }
}