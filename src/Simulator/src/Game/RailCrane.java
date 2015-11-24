/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import Simulation.Path;
import Simulation.Transform;
import World.MaterialCreator;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

/**
 *
 * @author sietse
 */
public class RailCrane extends Crane {

    public RailCrane(Transform parent, CraneHook hook) {
        super(parent, hook);
    }
    public RailCrane(Transform parent, CraneHook hook, Vector3f v) {
        super(parent, hook, v);
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
        return new Path(null, null, false, true, 3.0f, 1.0f, null, null, null, new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(20.0f, 0.0f, 0.0f));
    }
    
}
