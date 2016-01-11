package nhl.containing.simulator.game;


import com.jme3.collision.CollisionResults;
import nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.*;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.List;
import nhl.containing.networking.protocol.InstructionType;
import nhl.containing.simulator.framework.EaseType;
import nhl.containing.simulator.framework.Interpolate;
import nhl.containing.simulator.framework.LoopMode;
import nhl.containing.simulator.framework.Mathf;
import nhl.containing.simulator.framework.Path;
import nhl.containing.simulator.framework.Point3;
import nhl.containing.simulator.framework.Time;
import nhl.containing.simulator.framework.Utilities;
import nhl.containing.simulator.networking.SimulatorClient;
import nhl.containing.simulator.simulation.Main;
import nhl.containing.simulator.world.MaterialCreator;

/**
 *
 * @author sietse
 */
public final class AGV extends MovingItem {
    public class AgvData {
        public final int id;
        public float speed;
        
        public AgvData() {
            m_register.add(this);
            this.id = m_register.size() - 1;
        }
    }
    private static int idCounter = 0;
    private static List<AgvData> m_register = new ArrayList<>();
    private static AgvData getData(int id) {
        return m_register.get(id);
    }
    
    
    
    // Constants
    private static final Vector3f startPosition = new Vector3f(-10f, 0f, 625);
    private static final Vector3f transformOffset = new Vector3f(0.0f, 1.7f, -5f);
    private static final Vector3f spatialOffset = new Vector3f(0.0f, 0.0f, 0.0f);
    private static final float spatialScale = 2.2f;
    private static final Vector3f containerOffset = new Vector3f(-1.2f, 0.4f, -6.0f);
    private static final String model = "models/henk/AGV.obj";
    private static final Material material = MaterialCreator.diffuse(new ColorRGBA(0.7f, 0.9f, 0.56f, 1.0f));
    private static final float loadedSpeed = 20.0f;
    private static final float unloadedSpeed = 40.0f;
    
    // Fields
    private static float m_distance = 25.0f;
    private static float m_stopDistance = 20.0f;
    private static float m_rotationSpeed = 0.01f;
    
    // Members
    private boolean m_waiting = false;
    private boolean m_instructionSend = true;
    private int m_parkingspot;
    private Vector3f m_targetDirection = null;
    private Vector3f m_previousDirection = Utilities.zero();
    private Vector3f m_previousPosition = null;
    
    // Raycast
    private float m_raycastTimer;
    private final float m_raycastRate = 1.0f;
    
    
    // Components
    private Spatial m_spatial;
    private AgvData m_info;
    
    public AGV() {
        super(Main.getAgvNode(), loadedSpeed, unloadedSpeed);
        init();
        this.register(SimulationItemType.AGV);
        initSpots(Point3.one());
        Main.register(this);
        
        m_info = new AgvData();
        setUserData(Main.AGV_INFO_KEY, m_info.id);
        
        //path().setPathf(Utilities.zero(), AgvPath.getPath(new int[]{0, 1, 3}, new Vector3f(0.0f, 0.0f, 0.0f)));
        //path().setPathf(Utilities.zero(), new Vector3f(50.0f, 0.0f, 0.0f), new Vector3f(50.0f, 0.0f, 50.0f), new Vector3f(0.0f, 0.0f, 50.0f), new Vector3f(100.0f, 0.0f, 0.0f), new Vector3f(50.0f, 0.0f, 50.0f));
    }
    
    public void setParkingspotID(int id){
        m_instructionSend = false;
        m_parkingspot = id;
    }
    
    //private boolean ff = true;//testing
    
    public void update() {
        /*Testing
        if (ff) {
            Vector3f[] p = AgvPath.getPath(new int[]{0, 1, 10, 18, 19, 20, 21, 13, 12, 11, 10, 9, 0}, Main.instance().getWorld().getLorryPlatforms().get(0).a.getParkingSpot());
            path().setPathf(p[0], p);
            ff = false;
        }*/
        
        m_raycastTimer += Time.deltaTime();
        if (m_raycastTimer >= m_raycastRate) {
            raycast();
            m_raycastTimer = 0.0f;
        }
        updateData();
        
        
        path().update();
        if(path().atLast() && !m_instructionSend){
            m_instructionSend = true;
            SimulatorClient.sendTaskDone((int)m_id, 0, InstructionType.AGV_READY);
            ParkingSpot p = (ParkingSpot)Main.getTransform(m_parkingspot);
            p.agv(this);
        }
        Vector3f v = path().getPosition();
        position(v.clone().add(transformOffset));
        
        Vector3f t = path().getTargetPosition();
        if (m_previousPosition == null) {
            m_previousPosition = position();
            return;
        }
        if (t != null) {
            m_targetDirection = v.subtract(m_previousPosition).divide(Time.deltaTime());
            m_previousPosition = new Vector3f(v);
        }
        if (m_targetDirection != null) {
            float length = m_targetDirection.subtract(m_previousDirection).length() / Time.timeScale();
            if (length < 0.01f)
                return;
            
            Vector3f d = Interpolate.ease(
                EaseType.Linear, 
                m_previousDirection, 
                m_targetDirection, 
                m_rotationSpeed * path().m_speed / length);
            
            m_previousDirection = new Vector3f(d).normalize();
            
            lookDirection(m_previousDirection);
        }
    }
    
    private void init() {
        
        // 
        position(Utilities.zero());
        m_spatial = Main.assets().loadModel(model);
        m_spatial.setMaterial(material);
        m_spatial.scale(spatialScale);
        attachChild(m_spatial);
        m_spatial.setLocalTranslation(new Vector3f(spatialOffset));
        m_spatial.setUserData(Main.AGV_INFO_KEY, m_info);
        
        //
        containerOffset(new Vector3f(containerOffset));
        
        // Init path
        Path path = new Path();
        path.m_useTimeInsteadOfSpeed = false;
        path.m_speed = unloadedSpeed;
        path.m_waitTime = 0.0f;
        path.m_loopMode = LoopMode.Once;
        path.m_previousPosition = new Vector3f(startPosition);
        path(path);
    }
    private void raycast() {
        if (m_targetDirection == null)
            return;
        
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(position(), m_targetDirection.clone());
        Main.getAgvNode().collideWith(ray, results);
        
        for (int i = 0; i < results.size(); i++) {
            float dist = results.getCollision(i).getDistance();
            if (dist <= m_distance) {
                Integer index = results.getCollision(i).getGeometry().getUserData(Main.AGV_INFO_KEY);
                if (index == null)
                    continue;
                
                path().setSpeed(getData(index).speed * Mathf.inverseLerp(m_stopDistance, m_distance, dist));
                return;
            }
        }
        
        path().setSpeed(getContainer() == null ? unloadedSpeed: loadedSpeed);
    }
    private void updateData() {
        m_info.speed = path().m_speed + 0.0f;
    }
}
