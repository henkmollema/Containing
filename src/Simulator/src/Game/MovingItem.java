/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import Simulation.Path;
import Simulation.Transform;

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
        super(parent);
    }
    public MovingItem(Transform parent, float loadedSpeed, float emptySpeed) {
        super(parent);
        this.m_empySpeed = emptySpeed;
        this.m_loadedSpeed = loadedSpeed;
    }
    public MovingItem(Transform parent, float loadedSpeed, float emptySpeed, Path path) {
        super(parent);
        this.m_empySpeed = emptySpeed;
        this.m_loadedSpeed = loadedSpeed;
    }
    protected void path(Path path) {
        m_path = path;
    }
    protected Path path() {
        return m_path;
    }
}
