/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.world;

import nhl.containing.simulator.simulation.Main;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import nhl.containing.simulator.game.Crane;
import nhl.containing.simulator.simulation.LoopMode;
import nhl.containing.simulator.simulation.Path;
import nhl.containing.simulator.simulation.Transform;

/**
 *
 * @author sietse
 */
public final class WorldCreator {
    /**
     * Create box with the size of a container
     * @param parent
     * @return 
     */
    public static Geometry createBox(Node parent) {
        Vector3f v = World.containerSize();
        return createBox(parent, v);
    }
    /**
     * Creates a box
     * @param parent
     * @param size
     * @return 
     */
    public static Geometry createBox(Node parent, Vector3f size) {
        return createBox(parent, size, ColorRGBA.randomColor());
    }
    /**
     * 
     * @param parent
     * @param size
     * @param color
     * @return 
     */
    public static Geometry createBox(Node parent, Vector3f size, ColorRGBA color) {
        return createBox(parent, size, ColorRGBA.randomColor(), false, false);
    }
    /**
     * 
     * @param parent
     * @param size
     * @param color
     * @param useDiffuse
     * @param useShadows
     * @return 
     */
    public static Geometry createBox(Node parent, Vector3f size, ColorRGBA color, boolean useDiffuse, boolean useShadows) {
        if (parent == null) 
            parent = Main.root();
        
        Box boxMesh = new Box(size.x, size.y, size.z); 
        Geometry boxGeo = new Geometry("Colored Box", boxMesh);
        
        if (useDiffuse)
            boxGeo.setMaterial(MaterialCreator.diffuse(color, 0.5f)); 
        else
            boxGeo.setMaterial(MaterialCreator.unshaded(color));
        
        boxGeo.setShadowMode(useShadows ? RenderQueue.ShadowMode.CastAndReceive : RenderQueue.ShadowMode.Off);
        
        parent.attachChild(boxGeo);
        return boxGeo;
    }
    
    
    private static Path createDefaultCranePath(Vector3f basePosition) {
        return new Path(                                       // Path
                    null,                                       // Current position 
                    null,                                       // Start node
                    false,                                      // manual
                    true,                                       // use speed instead of time
                    3.0f,                                       // speed/time
                    1.0f,                                       // wait time
                    LoopMode.Once,                              // Loopmode
                    null,                                       // ease type
                    null,                                       // callback
                    basePosition,                               // Node 0
                    basePosition                                // Node 1
                );
    }
    public static Crane createStorageCrane(Transform parent) {
        Vector3f _basePosition = new Vector3f(0.0f, 0.0f, 0.0f);
        
        return new Crane(
                parent,                                         // Parent
                createDefaultCranePath(_basePosition),          // Node 1
                "TrainCrane.obj",                               // Crane model name
                "TrainCraneHook.obj",                           // Hook model name
                MaterialCreator.diffuse(ColorRGBA.Blue, 0.3f),  // Crane model Material
                MaterialCreator.diffuse(ColorRGBA.Red, 0.4f),   // Hook model Material
                3.0f,                                           // Attach Time
                25.0f,                                          // Rope height
                new Vector3f(0.0f, 15.0f, 0.0f),                // Base position
                new Vector3f(0.0f,  0.0f, 0.0f),                // Crane offset
                new Vector3f(0.0f,  0.0f, 30.0f),               // Hook offset
                new Vector3f(0.0f, 0.0f, -30.0f),               // Container offset
                new Vector3f(5.0f, 0.0f, 30.0f),                // Crane spatial offset
                new Vector3f(3.0f, -11f, 0.0f),                 // Hook spatial offset
                3.0f,                                           // Crane scale
                2.0f                                            // Hook scale
         );
    }
    public static Crane createLorryCrane(Transform parent) {
        Vector3f _basePosition = new Vector3f(0.0f, 0.0f, 0.0f);
        
        return new Crane(
                parent,                                         // Parent
                createDefaultCranePath(_basePosition),          // Node 1
                "TrainCrane.obj",                               // Crane model name
                "TrainCraneHook.obj",                           // Hook model name
                MaterialCreator.diffuse(ColorRGBA.Blue, 0.3f),  // Crane model Material
                MaterialCreator.diffuse(ColorRGBA.Red, 0.4f),   // Hook model Material
                3.0f,                                           // Attach Time
                25.0f,                                          // Rope height
                new Vector3f(0.0f, 15.0f, 0.0f),                // Base position
                new Vector3f(0.0f,  0.0f, 0.0f),                // Crane offset
                new Vector3f(0.0f,  0.0f, 30.0f),               // Hook offset
                new Vector3f(0.0f, 0.0f, -30.0f),               // Container offset
                new Vector3f(5.0f, 0.0f, 30.0f),                // Crane spatial offset
                new Vector3f(3.0f, -11f, 0.0f),                 // Hook spatial offset
                3.0f,                                           // Crane scale
                2.0f                                            // Hook scale
         );
    }
    public static Crane createInlandCrane(Transform parent) {
        Vector3f _basePosition = new Vector3f(0.0f, 0.0f, 0.0f);
        
        return new Crane(
                parent,                                         // Parent
                createDefaultCranePath(_basePosition),          // Node 1
                "TrainCrane.obj",                               // Crane model name
                "TrainCraneHook.obj",                           // Hook model name
                MaterialCreator.diffuse(ColorRGBA.Blue, 0.3f),  // Crane model Material
                MaterialCreator.diffuse(ColorRGBA.Red, 0.4f),   // Hook model Material
                3.0f,                                           // Attach Time
                25.0f,                                          // Rope height
                new Vector3f(0.0f, 15.0f, 0.0f),                // Base position
                new Vector3f(0.0f,  0.0f, 0.0f),                // Crane offset
                new Vector3f(0.0f,  0.0f, 30.0f),               // Hook offset
                new Vector3f(0.0f, 0.0f, -30.0f),               // Container offset
                new Vector3f(5.0f, 0.0f, 30.0f),                // Crane spatial offset
                new Vector3f(3.0f, -11f, 0.0f),                 // Hook spatial offset
                3.0f,                                           // Crane scale
                2.0f                                            // Hook scale
         );
    }
    public static Crane createSeaCrane(Transform parent) {
        Vector3f _basePosition = new Vector3f(0.0f, 0.0f, 0.0f);
        
        return new Crane(
                parent,                                         // Parent
                createDefaultCranePath(_basePosition),          // Node 1
                "TrainCrane.obj",                               // Crane model name
                "TrainCraneHook.obj",                           // Hook model name
                MaterialCreator.diffuse(ColorRGBA.Blue, 0.3f),  // Crane model Material
                MaterialCreator.diffuse(ColorRGBA.Red, 0.4f),   // Hook model Material
                3.0f,                                           // Attach Time
                25.0f,                                          // Rope height
                new Vector3f(0.0f, 15.0f, 0.0f),                // Base position
                new Vector3f(0.0f,  0.0f, 0.0f),                // Crane offset
                new Vector3f(0.0f,  0.0f, 30.0f),               // Hook offset
                new Vector3f(0.0f, 0.0f, -30.0f),               // Container offset
                new Vector3f(5.0f, 0.0f, 30.0f),                // Crane spatial offset
                new Vector3f(3.0f, -11f, 0.0f),                 // Hook spatial offset
                3.0f,                                           // Crane scale
                2.0f                                            // Hook scale
         );
    }
    public static Crane createTrainCrane(Transform parent) {
        Vector3f _basePosition = new Vector3f(0.0f, 0.0f, 0.0f);
        
        return new Crane(
                parent,                                         // Parent
                createDefaultCranePath(_basePosition),          // Node 1
                "TrainCrane.obj",                               // Crane model name
                "TrainCraneHook.obj",                           // Hook model name
                MaterialCreator.diffuse(ColorRGBA.Blue, 0.3f),  // Crane model Material
                MaterialCreator.diffuse(ColorRGBA.Red, 0.4f),   // Hook model Material
                3.0f,                                           // Attach Time
                25.0f,                                          // Rope height
                new Vector3f(0.0f, 15.0f, 0.0f),                // Base position
                new Vector3f(0.0f,  0.0f, 0.0f),                // Crane offset
                new Vector3f(0.0f,  0.0f, 30.0f),               // Hook offset
                new Vector3f(0.0f, 0.0f, -30.0f),               // Container offset
                new Vector3f(5.0f, 0.0f, 30.0f),                // Crane spatial offset
                new Vector3f(3.0f, -11f, 0.0f),                 // Hook spatial offset
                3.0f,                                           // Crane scale
                2.0f                                            // Hook scale
         );
    }
}
