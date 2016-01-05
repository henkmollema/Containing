/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.game;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.List;
import nhl.containing.networking.protobuf.InstructionProto;
import nhl.containing.simulator.framework.Point3;
import nhl.containing.simulator.framework.Utilities;
import nhl.containing.simulator.simulation.Main;
import nhl.containing.simulator.world.MaterialCreator;
import nhl.containing.simulator.world.World;

/**
 *
 * @author sietse
 */
public class Train extends Vehicle {
    
    private List<Spatial> m_wagonInactivePool = new ArrayList<>();
    private List<Spatial> m_wagonActivePool = new ArrayList<>();
    
    public List<Container> m_containers2take;
    public List<Container> m_containers2bring;
    private boolean isDone = false;
    
    public Train(Point3 size, float speed, String frontModel, float frontScale, Vector3f frontOffset) {
        super(size, speed, frontModel, frontScale, frontOffset);
    }
    
    private static Vector3f wagonOffset() {
        return new Vector3f(1.0f, -7.5f, -66.0f).add(World.containerSize());
    }
    private final float wagonScale = 4.0f;
    private final float wagonSize = -60.0f;
    private final float containersPerWagon = 2.44f;
    
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
        clear();
        
        int size = containers.length;
        
        position(Utilities.zero());
        initSpots(new Point3(1, 1, size));
        for (int i = 0; i < size; i++) {
            setContainer(new Point3(0, 0, i), containers[i]);
        }
        
        size = (int)(size / containersPerWagon) + ((size / containersPerWagon) % 1 > 0.0f ? 1 : 0); 
        
        
        while (m_wagonActivePool.size() > size) {
            // Remove current
            Spatial _temp;
            m_wagonInactivePool.add(_temp = m_wagonActivePool.remove(size));
            _temp.setCullHint(CullHint.Always);
        } while(m_wagonActivePool.size() < size) {
            if (m_wagonInactivePool.size() < 1) {
                // Create new
                Spatial s = Main.assets().loadModel("models/elo/low/train/wagon.j3o");
                s.setMaterial(MaterialCreator.unshadedRandom());
                s.scale(wagonScale);
                attachChild(setPosition(s, m_wagonActivePool.size()));
                m_wagonActivePool.add(s);
            } else {
                // Get from old
                m_wagonActivePool.add(setPosition(m_wagonInactivePool.remove(0), m_wagonActivePool.size()));
            }
        }
    }
    private Spatial setPosition(Spatial s, int index) {
        Vector3f off = wagonOffset();
        off.z += wagonSize * index;
        
        s.setLocalTranslation(off);
        s.setLocalRotation(Utilities.euler2Quaternion(new Vector3f(0.0f, 0.0f, 90.0f)));
        return s;
    }
    
    @Override
    public void update() {
        super.update();
    }
    
    @Override
    public void onWaitingStart() {
        //
        Main.instance().getWorld().trainArrived();
        isDone = false;
        callDone();
    }
    @Override
    public void onWaitingUpdate() {
        //
    }
    
    public void callDone() {
        onDone();
        isDone = true;
    }
}
