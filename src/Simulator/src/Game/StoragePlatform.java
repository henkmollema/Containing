/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import Simulation.Point3;
import com.jme3.math.Vector3f;

/**
 *
 * @author sietse
 */
public final class StoragePlatform extends LoadingPlatform {

    public StoragePlatform(Vector3f position) {
        super();
        createPlatform();
        this.position(position);
    }
    @Override
    void createPlatform() {
        m_crane = new RailCrane(this, new CraneHook(this, 5.0f, 5.0f, 5.0f, new Vector3f(0.0f, 5.0f, 0.0f)), new Vector3f(6.0f, 0.0f, 20.0f));
        
        initSpots(new Point3(6, 6, 20));
        updateOuter();
    }
    
    
    
}
