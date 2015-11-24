/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.game;

import nhl.containing.simulator.simulation.Point3;
import nhl.containing.simulator.simulation.Transform;

/**
 *
 * @author sietse
 */
public class ContainerCarrier extends Item {
    private Point3 m_stacks;
    
    public ContainerCarrier() {
        super();
    }
    public ContainerCarrier(Transform parent) {
        super(parent);
    }
}
