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
        super(
                parent, 
                new Vector3f(),         // base
                new Vector3f(),               // crane offset
                new Vector3f(),           // hook offset
                new Vector3f(0.0f, -4.5f, 0.0f),          // contianer offset
                new Vector3f(),          // crane spatial offset
                new Vector3f(3.0f, -12.5f, 0.0f));              // hook spatial offset
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
