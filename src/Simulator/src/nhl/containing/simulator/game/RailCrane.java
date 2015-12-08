package nhl.containing.simulator.game;

import nhl.containing.simulator.simulation.Path;
import nhl.containing.simulator.simulation.Transform;
import nhl.containing.simulator.world.MaterialCreator;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import nhl.containing.simulator.simulation.Debug;
import nhl.containing.simulator.simulation.LoopMode;
import nhl.containing.simulator.world.ContainerPool;
import nhl.containing.simulator.world.World;

/**
 * An extention to Crane
 * Maybe just create in World.java
 * a Crane method that fills in all the 
 * members of a Crane so there will be
 * no child classes anymore of Crane
 * @author sietse
 */
public class RailCrane extends Crane {

    public RailCrane(Transform parent) {
        super(
                parent, 
                new Vector3f(0.0f,  0.0f, 0.0f),               // crane offset
                new Vector3f(0.0f,  0.0f, 30.0f),           // hook offset
                new Vector3f(0.0f, 0.0f, -30.0f),          // contianer offset
                new Vector3f(5.0f, 0.0f, 30.0f),          // frame spatial offset
                new Vector3f(3.0f, -11f, 0.0f));              // hook spatial offset
        //ContainerPool.dispose(getContainer());
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
        return new Path(null, null, false, true, 3.0f, 1.0f, LoopMode.Once, null, null, basePosition(), basePosition());
    }

    @Override
    protected float attachTime() {
        return 3.0f;
    }

    @Override
    protected float ropeHeight() {
        return 25.0f;
    }

    @Override
    protected Vector3f basePosition() {
        return new Vector3f(0.0f, 15.0f, 0.0f);
    }
    
}
