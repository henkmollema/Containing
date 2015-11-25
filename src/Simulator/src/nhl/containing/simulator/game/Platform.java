/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.game;

import nhl.containing.simulator.simulation.Bounds;
import nhl.containing.simulator.simulation.Transform;

/**
 *
 * @author sietse
 */
public abstract class Platform extends ContainerCarrier {
    private Bounds m_bounds;
    
    abstract void createPlatform();
    
    public Platform() {
        super();
    }
    public Platform(Transform parent) {
        super(parent);
    }
}
