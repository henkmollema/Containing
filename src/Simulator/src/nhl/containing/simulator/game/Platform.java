package nhl.containing.simulator.game;

import nhl.containing.simulator.simulation.Bounds;
import nhl.containing.simulator.simulation.Transform;

/**
 * Base platform
 * @author sietse
 */
public abstract class Platform extends ContainerCarrier {
    private Bounds m_bounds;
    // private Node m_roadNode;
    
    /**
     * Create the platform
     */
    abstract void createPlatform();
    
    /**
     * Costructor
     */
    public Platform() {
        super();
    }
    /**
     * Constructor
     * @param parent 
     */
    public Platform(Transform parent) {
        super(parent);
    }
}
