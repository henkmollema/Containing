/*
 * For every moving item in the simulator
 */
package nhl.containing.simulator.game;

import nhl.containing.simulator.simulation.Path;
import nhl.containing.simulator.simulation.Point3;
import nhl.containing.simulator.simulation.Transform;

/**
 *
 * @author sietse
 */
public class MovingItem extends ContainerCarrier {
    protected float m_empySpeed;        // The speed when it doesn't carrie anything
    protected float m_loadedSpeed;      // The speed when it is carring a container
    
    private Path m_path;            // Path
    
    /**
     * Constructor
     */
    public MovingItem() {
        super();
    }
    /**
     * Constructor
     * @param parent 
     */
    public MovingItem(Transform parent) {
        super(parent, Point3.one());
    }
    /**
     * Constructor
     * @param parent
     * @param loadedSpeed
     * @param emptySpeed 
     */
    public MovingItem(Transform parent, float loadedSpeed, float emptySpeed) {
        super(parent,  Point3.one());
        this.m_empySpeed = emptySpeed;
        this.m_loadedSpeed = loadedSpeed;
    }
    /**
     * Constructor
     * @param parent
     * @param loadedSpeed
     * @param emptySpeed
     * @param path 
     */
    public MovingItem(Transform parent, float loadedSpeed, float emptySpeed, Path path) {
        super(parent, Point3.one());
        this.m_empySpeed = emptySpeed;
        this.m_loadedSpeed = loadedSpeed;
    }
    
    /**
     * Set path
     * @param path 
     */
    protected void path(Path path) {
        m_path = path;
    }
    /**
     * Get path
     * @return 
     */
    public Path path() {
        return m_path;
    }
}
