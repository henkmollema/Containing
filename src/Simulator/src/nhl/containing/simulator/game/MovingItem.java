/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
    protected float m_empySpeed;
    protected float m_loadedSpeed;
    
    private Path m_path;
    
    public MovingItem() {
        super();
    }
    public MovingItem(Transform parent) {
        super(parent, Point3.one());
    }
    public MovingItem(Transform parent, float loadedSpeed, float emptySpeed) {
        super(parent,  Point3.one());
        this.m_empySpeed = emptySpeed;
        this.m_loadedSpeed = loadedSpeed;
    }
    public MovingItem(Transform parent, float loadedSpeed, float emptySpeed, Path path) {
        super(parent, Point3.one());
        this.m_empySpeed = emptySpeed;
        this.m_loadedSpeed = loadedSpeed;
    }
    protected void path(Path path) {
        m_path = path;
    }
    public Path path() {
        return m_path;
    }
    
    
    
    
    
}
