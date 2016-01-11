/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.game;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.List;
import nhl.containing.networking.protobuf.InstructionProto;
import nhl.containing.simulator.framework.Callback;
import nhl.containing.simulator.framework.LoopMode;
import nhl.containing.simulator.framework.Path;
import nhl.containing.simulator.framework.Point3;
import nhl.containing.simulator.framework.Utilities;
import nhl.containing.simulator.simulation.Main;
import nhl.containing.simulator.world.MaterialCreator;

/**
 *
 * @author sietse
 */
public class Vehicle extends MovingItem
{
    /**
     * State of vehicle
     */
    public enum VehicleState
    {
        Disposed,
        Waiting,
        ToLoad,
        ToOut
    }
    private boolean m_initialized = false;                      // Is initialized
    private final static String BASE_MODEL_PATH = "models/";    // Base model path
    public Spatial m_frontSpatial;                              // Vehicle spatial
    public Material m_frontMaterial;                            // Vehicle material
    private Vector3f m_frontOffset;                             // Vehicle spatial offset
    public Vector3f[] from;                                     // Arrival path
    public Vector3f[] to;                                       // Depart path
    private VehicleState m_currentState;                        // Current vehicle state
    private boolean _busy;                                      // Is busy
    private VehicleStateApplied _callback;                      // Callback on done
    public boolean needsContainer;

    /**
     * Constructor
     * @param size
     * @param speed
     * @param frontModel
     * @param frontScale
     * @param frontOffset 
     */
    public Vehicle(Point3 size, float speed, String frontModel, float frontScale, Vector3f frontOffset) {
        super(size, speed);
        init(frontModel, frontScale, frontOffset);
    }
    
    /**
     * Set spatial offset
     * @param frontOffset 
     */
    public void setFrontOffset(Vector3f frontOffset) {
        m_frontSpatial.setLocalTranslation(frontOffset);
    }

    /**
     * Offset
     * @param frontModel
     * @param frontScale
     * @param frontOffset 
     */
    private void init(String frontModel, float frontScale, Vector3f frontOffset) {
        m_frontOffset = frontOffset == null ? Utilities.zero() : frontOffset;

        if (!Utilities.nullOrEmpty(frontModel)) {
            
            // Set material
            if (m_frontMaterial == null)
                m_frontMaterial = MaterialCreator.unshadedRandom();

            // Init spatial
            m_frontSpatial = Main.assets().loadModel(BASE_MODEL_PATH + frontModel);
            m_frontSpatial.setMaterial(m_frontMaterial);
            m_frontSpatial.scale(frontScale);
            this.attachChild(m_frontSpatial);
            m_frontSpatial.setLocalTranslation(m_frontOffset);
        }

        // Init path
        path(new Path());
        path().m_callback = new Callback(this, "onVehicle");
        path().m_loopMode = LoopMode.Once;
        path().setSpeed(m_loadedSpeed);
        path().m_waitTime = 0.0f;
        path().m_useTimeInsteadOfSpeed = false;
    }

    /**
     * On new state
     */
    public void onVehicle()
    {
        switch (m_currentState)
        {
            case ToLoad:
                if (path().atLast()) {
                    onWaitingStart();
                    state(VehicleState.Waiting);
                }
                break;
            case ToOut:
                if (path().atLast()) {
                    state(VehicleState.Disposed);
                    if (_busy && _callback != null) {
                        _busy = false;
                        _callback.done(this);
                        _callback = null;
                    }
                }
                break;
        }
    }

    /**
     * Update vehicle
     */
    public void update()
    {
        // Wait one frame
        if (!m_initialized) {
            m_initialized = true;
            return;
        }

        switch (m_currentState)
        {
            case Waiting:
                onWaitingUpdate();
                break;
            case ToLoad:
                path().update();
                position(path().getPosition());
                break;
            case ToOut:
                path().update();
                position(path().getPosition());
                break;
        }

    }
    
    /**
     * On waiting start default (used as virtual void)
     */
    public void onWaitingStart() { onDone(); }
    /**
     * On waiting update (used as virtual void)
     */
    public void onWaitingUpdate() { }
    
    /**
     * Called when can go away
     */
    protected void onDone() {
        if (_busy && _callback != null) {
            _busy = false;
            _callback.done(this);
            _callback = null;
        }
    }
    
    /**
     * Set state
     * @param state 
     */
    public void state(VehicleState state) {
        state(state, null);
    }

    /**
     * Set state
     * @param state
     * @param callback 
     */
    public void state(VehicleState state, VehicleStateApplied callback)
    {
        if (m_currentState == state)
            return;
        
        switch (state)
        {
            case Disposed:
                this.setCullHint(CullHint.Always);
                _busy = false;
                break;
            case ToLoad:
                if (!_busy && callback != null) {
                    _busy = true;
                    _callback = callback;
                }
                this.setCullHint(CullHint.Dynamic);
                path().setPathf(from[0], from);
                break;
            case ToOut:
                if (!_busy && callback != null) {
                    _busy = true;
                    _callback = callback;
                }
                path().setPathf(to[0], to);
                break;
            case Waiting:
                break;
        }
        m_currentState = state;
    }

    /**
     * Get state
     * @return 
     */
    public VehicleState state() {
        return m_currentState;
    }

    /**
     * Callback when a vehicle arrives in a certain state.
     */
    public interface VehicleStateApplied {
        void done(Vehicle v);
    }
    
    /**
     * init
     * @param containers 
     */
    public void init(List<InstructionProto.Container> containers) {
        Container[] c = new Container[containers.size()];
        for(int i = 0; i < c.length;i++){
            c[i] = new Container(new RFID(containers.get(i)));
            c[i].show();
        }
        init(c);
    }
    /**
     * Init
     * @param size 
     */
    public void init(int size) {
        Container[] c = new Container[size];
        
        for (int i = 0; i < c.length; i++) {
            c[i] = new Container(new RFID());
            c[i].show();
        }
        
        init(c);
    }
    /**
     * Init
     * @param containers 
     */
    public void init(Container... containers)
    {
        if (m_containerSpots.length < 1) {
            System.out.println("Containerspots don't have a size");
            return;
        }
        
        // Clear old containers
        clear();
        
        // Get max
        int size = 0;
        int maxSize = containers.length;
        Point3 max = new Point3( 
            m_containerSpots.length,
            m_containerSpots[0].length,
            m_containerSpots[0][0].length
        );
        
        // reset
        position(Utilities.zero());
        initSpots(new Point3(max));
        
        for (int x = 0; x < max.x; x++) {
            for (int y = 0; y < max.y; y++){
                for (int z = 0; z < max.z; z++) {
                    
                    if (size == maxSize)
                        break;
                    
                    // Set container
                    setContainer(new Point3(x, y, z), containers[size], false);
                    size++;
                }
            }
        }
        
        // Set culling
        updateOuter();
    }
}
