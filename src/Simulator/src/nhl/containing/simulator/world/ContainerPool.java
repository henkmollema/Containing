/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.world;

import com.jme3.math.Quaternion;
import com.jme3.scene.Spatial;
import nhl.containing.simulator.framework.Transform;
import java.util.ArrayList;
import java.util.List;
import nhl.containing.simulator.framework.Utilities;
import nhl.containing.simulator.game.Container;
import nhl.containing.simulator.simulation.Main;

/**
 *
 * @author sietse
 */
public class ContainerPool {
    private static List<Transform> m_activePool = new ArrayList<>();
    private static List<Transform> m_inactivePool = new ArrayList<>();
    
    /**
     * Get a new or recycled Transform
     * @return 
     */
    public static void get(Container c) {
        if (c == null || c.transform != null)
            return;
        
        if (m_inactivePool.size() < 1) {
            m_activePool.add(c.transform = create());
        } else {
            m_activePool.add(c.transform = m_inactivePool.remove(0));
        }
        c.transform.setCullHint(Spatial.CullHint.Dynamic);
    }
    /**
     * Store the transform
     * @param t
     * @return 
     */
    public static boolean dispose(Container c) {
        if (c == null || c.transform == null)
            return false;
        
        boolean b = m_activePool.remove(c.transform);
        if (b) {
            b = m_inactivePool.add(c.transform);
            c.transform.setCullHint(Spatial.CullHint.Always);
            c.transform = null;
        }
        return b;
    }
    /**
     * Create new
     * @return 
     */
    private static Transform create() {
        Transform t = new Transform();
        WorldCreator.createContainer(t);
        return t;
    }
}
