/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.world;

import com.jme3.scene.Spatial;
import nhl.containing.simulator.simulation.Transform;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sietse
 */
public class ContainerPool {
    private static List<Transform> m_activePool = new ArrayList<Transform>();
    private static List<Transform> m_inactivePool = new ArrayList<Transform>();
    
    /**
     * Get a new or recycled Transform
     * @return 
     */
    public static Transform get() {
        Transform t;
        if (m_inactivePool.size() < 1) {
            m_activePool.add(t = create());
        } else {
            m_activePool.add(t = m_inactivePool.remove(0));
        }
        t.setCullHint(Spatial.CullHint.Dynamic);
        return t;
    }
    /**
     * Store the transform
     * @param t
     * @return 
     */
    public static boolean dispose(Transform t) {
        boolean b = m_activePool.remove(t);
        if (b) {
            b = m_inactivePool.add(t);
            t.setCullHint(Spatial.CullHint.Always);
        }
        return b;
    }
    
    private static Transform create() {
        Transform t = new Transform();
        WorldCreator.createBox(t);
        return t;
    }
}
