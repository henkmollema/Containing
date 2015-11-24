/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import Simulation.Bounds;

/**
 *
 * @author sietse
 */
public abstract class Platform extends ContainerCarrier {
    private Bounds m_bounds;
    
    abstract void createPlatform();
}
