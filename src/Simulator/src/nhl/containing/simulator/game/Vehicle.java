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
    public enum VehicleState
    {
        Disposed,
        Waiting,
        ToLoad,
        ToOut
    }
    private boolean m_initialized = false;
    private final static String BASE_MODEL_PATH = "models/";
    public Spatial m_frontSpatial;
    public Material m_frontMaterial;
    private Vector3f m_frontOffset;
    public Vector3f[] from;
    public Vector3f[] to;
    private VehicleState m_currentState;
    private boolean _busy;
    private VehicleStateApplied _callback;

    public Vehicle(Point3 size, float speed, String frontModel, float frontScale, Vector3f frontOffset)
    {
        super(size, speed);
        init(frontModel, frontScale, frontOffset);
    }
    
    public void setFrontOffset(Vector3f frontOffset)
    {
        m_frontSpatial.setLocalTranslation(frontOffset);
    }

    private void init(String frontModel, float frontScale, Vector3f frontOffset)
    {
        m_frontOffset = frontOffset == null ? Utilities.zero() : frontOffset;

        if (!Utilities.nullOrEmpty(frontModel))
        {
            if (m_frontMaterial == null)
            {
                m_frontMaterial = MaterialCreator.unshadedRandom();
            }

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

    public void onVehicle()
    {
        switch (m_currentState)
        {
            case ToLoad:
                if (path().atLast())
                {
                    onWaitingStart();
                    state(VehicleState.Waiting);
                }
                break;
            case ToOut:
                if (path().atLast())
                {
                    state(VehicleState.Disposed);
                    if (_busy && _callback != null)
                    {
                        _busy = false;
                        _callback.done(this);
                        _callback = null;
                    }
                }
                break;
        }
    }

    public void update()
    {
        if (!m_initialized)
        {
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
    
    public void onWaitingStart() { onDone(); }
    public void onWaitingUpdate(){ }
    protected void onDone() {
        if (_busy && _callback != null) {
            _busy = false;
            _callback.done(this);
            _callback = null;
        }
    }
    
    public void state(VehicleState state)
    {
        state(state, null);
    }

    public void state(VehicleState state, VehicleStateApplied callback)
    {
        if (m_currentState == state)
        {
            return;
        }

        switch (state)
        {
            case Disposed:
                this.setCullHint(CullHint.Always);
                _busy = false;
                break;
            case ToLoad:
                if (!_busy && callback != null)
                {
                    _busy = true;
                    _callback = callback;
                }
                this.setCullHint(CullHint.Dynamic);
                path().setPathf(from[0], from);
                break;
            case ToOut:
                if (!_busy && callback != null)
                {
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

    public VehicleState state()
    {
        return m_currentState;
    }

    /**
     * Callback when a vehicle arrives in a certain state.
     */
    public interface VehicleStateApplied
    {
        void done(Vehicle v);
    }
    
    public void init(List<InstructionProto.Container> containers){
        Container[] c = new Container[containers.size()];
        for(InstructionProto.Container container : containers){
            c[container.getX()] = new Container(new RFID(container));
            c[container.getX()].show();
        }
        init(c);
    }
    public void init(int size)
    {
        Container[] c = new Container[size];
        
        for (int i = 0; i < c.length; i++) {
            c[i] = new Container(new RFID());
            c[i].show();
        }
        
        init(c);
    }
    public void init(Container... containers)
    {
        if (m_containerSpots.length < 1) {
            System.out.println("Contianerspots don't have a size");
            return;
        }
        
        
        clear();
        
        int size = containers.length;
        Point3 max = new Point3(
            m_containerSpots.length, 
            m_containerSpots[0].length, 
            m_containerSpots[0][0].length
        );
        
        position(Utilities.zero());
        initSpots(new Point3(max));
        //max.add(Point3.one());
        
        for (int x = 1; x <= max.x; x++) {
            for (int y = 1; y <= max.y; y++){
                for (int z = 1; z <= max.z; z++) {
                    
                    if (--size < 0)
                        break;
                    
                    setContainer(new Point3(x, y, z), containers[size], false);
                }
            }
        }
        updateOuter();
        
        System.out.println("adafsaasdf: " + (getContainer(2, 2, 2) == null));
    }
}
