/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.world;

import nhl.containing.simulator.simulation.Main;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.water.SimpleWaterProcessor;
import nhl.containing.simulator.game.Crane;
import nhl.containing.simulator.framework.LoopMode;
import nhl.containing.simulator.framework.Path;
import nhl.containing.simulator.framework.Point3;
import nhl.containing.simulator.framework.Transform;
import nhl.containing.simulator.framework.Utilities;
import nhl.containing.simulator.game.Train;
import nhl.containing.simulator.game.Vehicle;

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
    
    
    public static Path createDefaultCranePath(Vector3f basePosition) {
        Path path = new Path();
        path.m_useTimeInsteadOfSpeed = false;
        path.m_speed = 3.0f;
        path.m_waitTime = 0.0f;
        path.m_loopMode = LoopMode.Once;
        path.m_previousPosition = new Vector3f(basePosition);
        return path;
    }
    public static Crane createStorageCrane(Transform parent) {
        Vector3f _basePosition = new Vector3f(0.0f, 0.0f, 0.0f);
        
        return new Crane(
                parent,                                         // Parent
                createDefaultCranePath(_basePosition),          // Node 1
                "storageCrane.obj",                               // Crane model name
                "storageCraneHook.obj",                           // Hook model name
                MaterialCreator.diffuse(ColorRGBA.Blue, 0.3f),  // Crane model Material
                MaterialCreator.diffuse(ColorRGBA.Red, 0.4f),   // Hook model Material
                3.0f,                                           // Attach Time
                25.0f,                                          // Rope height
                new Vector3f(14.0f, 33.0f, World.STORAGE_WIDTH),                // Base position
                new Vector3f(0.0f,  0.0f, 0.0f),                // Crane offset
                new Vector3f(0.0f,  0.0f, 0.0f),               // Hook offset
                new Vector3f(0.0f, 7.0f, 10.0f),                 // Rope offset
                new Vector3f(World.containerSize().x, World.containerSize().y,  World.containerSize().z),               // Container offset
                new Vector3f(15.0f, 0.0f, 20.0f),                // Crane spatial offset
                new Vector3f(0.0f, -27f - World.containerSize().y / 2.0f, 8.0f +  World.containerSize().z),                 // Hook spatial offset
                2.3f,                                           // Crane scale
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
                new Vector3f(0.0f, 0.0f, 0.0f),                 // Rope offset
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
                new Vector3f(0.0f, 0.0f, 0.0f),                 // Rope offset
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
                new Vector3f(0.0f, 0.0f, 0.0f),                 // Rope offset
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
                new Vector3f(0.0f, 0.0f, 0.0f),                 // Rope offset
                new Vector3f(0.0f, 0.0f, -30.0f),               // Container offset
                new Vector3f(5.0f, 0.0f, 30.0f),                // Crane spatial offset
                new Vector3f(3.0f, -11f, 0.0f),                 // Hook spatial offset
                3.0f,                                           // Crane scale
                2.0f                                            // Hook scale
         );
    }
    
    public static Vehicle createLorry(Vector3f from, Vector3f to) {
        Vehicle v = new Vehicle(
                new Point3(1, 1, 1),
                5.0f,// speed
                "Sietse/Truck/Mater.obj", // holder model
                5.0f, // front scale
                new Vector3f(0.0f, 0.0f, 0.0f) // front offset
        );
        
        v.from = new Vector3f[] { new Vector3f(from), new Vector3f(to) };
        v.to = new Vector3f[] { new Vector3f(to), new Vector3f(from) };
        v.m_frontSpatial.setMaterial(v.m_frontMaterial = MaterialCreator.unshaded("models/Sietse/Truck/mater1_lod0.png"));
        v.state(Vehicle.VehicleState.Disposed);
        v.containerOffset(new Vector3f(0.0f, -5.0f, 60.0f));
        
        return v;
    }
    
    /**
     * 
     * @param from The point that is out of the map
     * @param to The point that is the loading platform
     * @return 
     */
    public static Train createTrain(Vector3f from, Vector3f to) {
        
        Train v = new Train(
                new Point3(1, 1, 0),
                30.0f,//speed
                "Sietse/Train/Thomas_Train.obj", // front model
                7.0f, // front scale
                new Vector3f(World.containerSize().x, -10, 0.0f) // front offset
        );
        v.m_frontSpatial.setMaterial(v.m_frontMaterial = MaterialCreator.unshaded("models/Sietse/Train/Thomas_Train.png"));
        v.state(Vehicle.VehicleState.Disposed);
        
        v.from = new Vector3f[] { new Vector3f(from), new Vector3f(to) };
        v.to = new Vector3f[] { new Vector3f(to), new Vector3f(from) };
        v.path().setPosition(from);
        
        v.invZ = true;
        v.containerOffset(new Vector3f(0.0f, 0.0f, -36.0f));
        
        return v;
    }
    
    public static Vehicle createInland(Vector3f[] from, Vector3f[] to) {
        Vehicle v = new Vehicle(
                new Point3(),
                10.0f,// speed
                "henk/Voertuigen/seaShip.obj", // front model
                1.0f, // front scale
                new Vector3f(0.0f, 0.0f, 0.0f) // front offset
        );
        v.state(Vehicle.VehicleState.Disposed);
        
        v.from = from;
        v.to = to;
        v.path().setPosition(from[0]);
        
        return v;
    }
    public static Vehicle createSea(Vector3f[] from, Vector3f[] to) {
        Vehicle v = new Vehicle(
                new Point3(),
                1.0f,// speed
                "", // front model
                1.0f, // front scale
                new Vector3f(0.0f, 0.0f, 0.0f) // front offset
        );
        v.state(Vehicle.VehicleState.Disposed);
        
        v.from = from;
        v.to = to;
        v.path().setPosition(from[0]);
        
        return v;
    }
    
    public static Geometry createWaterPlane(Vector3f location, float size, float depth, float distortionScale, float waveSpeed, float waveSize)
    {
        //From: http://wiki.jmonkeyengine.org/doku.php/jme3:advanced:water
        // we create a water processor
        SimpleWaterProcessor waterProcessor = new SimpleWaterProcessor(Main.instance().getAssetManager());
        waterProcessor.setReflectionScene(Main.instance().getRootNode());

        // we set the water plane
        Vector3f waterLocation=new Vector3f(0,-9,0);
        waterProcessor.setPlane(new Plane(Vector3f.UNIT_Y, waterLocation.dot(Vector3f.UNIT_Y)));
        Main.instance().getViewPort().addProcessor(waterProcessor);

        // we set wave properties
        waterProcessor.setWaterDepth(depth);         // transparency of water
        waterProcessor.setDistortionScale(distortionScale); // strength of waves
        waterProcessor.setWaveSpeed(waveSpeed);       // speed of waves

        // we define the wave size by setting the size of the texture coordinates
        Quad quad = new Quad(size,size);
        quad.scaleTextureCoordinates(new Vector2f(waveSize,waveSize));

        // we create the water geometry from the quad
        Geometry waterPlane=new Geometry("water", quad);
        waterPlane.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
        waterPlane.setLocalTranslation(location);
        waterPlane.setShadowMode(ShadowMode.Receive);
        waterPlane.setMaterial(waterProcessor.getMaterial());

        return waterPlane;
    }
}
