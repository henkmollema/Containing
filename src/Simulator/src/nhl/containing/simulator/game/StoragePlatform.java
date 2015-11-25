/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.game;

import nhl.containing.simulator.simulation.Point3;
import com.jme3.math.Vector3f;
import nhl.containing.simulator.simulation.Debug;
import nhl.containing.simulator.simulation.Transform;

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
    public StoragePlatform(Transform parent, Vector3f position) {
        super(parent);
        createPlatform();
        this.position(position);
    }
    @Override
    void createPlatform() {
        m_crane = new RailCrane(this, new Vector3f(6.0f, 0.0f, 20.0f));
        Debug.log(m_crane.path().getTargetIndex() + "");
        initSpots(new Point3(6, 6, 20));
        updateOuter();
    }
}
