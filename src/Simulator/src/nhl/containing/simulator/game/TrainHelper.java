package nhl.containing.simulator.game;

import com.jme3.math.*;
import com.jme3.scene.*;
import nhl.containing.simulator.framework.*;
import nhl.containing.simulator.simulation.*;
import nhl.containing.simulator.world.*;


public class TrainHelper
{
    public static void attachWagonToTrain(Vehicle v, int amount)
    {
        final int size = 30;
        
        // todo: set the front offset to allow more wagons.
        //v.setFrontOffset(new Vector3f(World.containerSize().x, -10, (amount * 3.3333f) + 2 * size * World.containerSize().z));
        
        for (int i = 0; i < amount; i++) {
            // todo: use high models
            Spatial s = Main.assets().loadModel("models/elo/low/train/wagon.j3o");
            s.setMaterial(MaterialCreator.unshadedRandom());
            s.scale(4.0f);
            
            v.attachChild(s);
            
            Vector3f off = World.containerSize();
            off.y -= 7.5f;
            off.z *= (0.8 + i) * 5;
            
            s.setLocalTranslation(off);
            s.setLocalRotation(Utilities.euler2Quaternion(new Vector3f(0.0f, 0.0f, 90.0f)));
        }
    }
}
