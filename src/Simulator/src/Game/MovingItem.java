/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import Simulation.Transform;
import java.util.List;

/**
 *
 * @author sietse
 */
public class MovingItem extends ContainerCarrier {
    private float m_empySpeed;
    private float m_loadedSpeed;
    private List<Integer> m_nodesID;
    
    public MovingItem() {
        super();
    }
    public MovingItem(Transform parent) {
        super(parent);
    }
}
