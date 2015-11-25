/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.game;

import nhl.containing.simulator.simulation.Path;
import nhl.containing.simulator.simulation.Transform;
import nhl.containing.simulator.world.MaterialCreator;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import nhl.containing.simulator.simulation.LoopMode;

/**
 *
 * @author sietse
 */
public class RailCrane extends Crane {

    public RailCrane(Transform parent) {
        super(parent);
    }
    public RailCrane(Transform parent, Vector3f v) {
        super(parent, v);
    }
    @Override
    protected String craneModelName() {
        return "TrainCrane.obj";
    }

    @Override
    protected String hookModelName() {
        return "TrainCraneHook.obj";
    }

    @Override
    protected Material craneModelMaterial() {
        return MaterialCreator.diffuse(ColorRGBA.Blue, 0.3f);
    }

    @Override
    protected Material hookModelMaterial() {
        return MaterialCreator.diffuse(ColorRGBA.Red, 0.4f);
    }

    @Override
    protected Path getCranePath() {
        return new Path(null, null, false, true, 3.0f, 1.0f, LoopMode.Once, null, null, m_base, m_base);
    }

    @Override
    protected float attachTime() {
        return 2.0f;
    }
    
}
