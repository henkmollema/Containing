/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.game;

import nhl.containing.simulator.simulation.Bounds;

/**
 *
 * @author sietse
 */
public abstract class Platform extends ContainerCarrier {
    private Bounds m_bounds;
    
    abstract void createPlatform();
}
