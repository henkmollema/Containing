/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package World;

import Game.ContainerCarrier;
import Game.CraneHook;
import Game.RailCrane;
import Game.StoragePlatform;
import Simulation.Behaviour;
import Simulation.Callback;
import Simulation.Debug;
import Simulation.EaseType;
import Simulation.LoopMode;
import Simulation.Main;
import Simulation.Mathf;
import Simulation.Path;
import Simulation.Point3;
import Simulation.Time;
import Simulation.Transform;
import Simulation.Utilities;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import java.util.Arrays;
import java.util.UUID;
import networking.Proto.InstructionProto;
import networking.Proto.InstructionProto.Instruction;
import networking.protocol.InstructionType;

/**
 *
 * @author sietse
 */
public class World extends Behaviour {
    public static final Vector3f containerSize() {
        return new Vector3f(2.438f, 2.438f, 12.192f);
    }
    
    
    // Main
    private DirectionalLight m_sun;
    
    // World
    
    
    // External
    
    
    @Override
    public void awake() {
        m_sun = LightCreator.createSun(ColorRGBA.White, new Vector3f(-0.5f, -0.5f, -0.5f));
        LightCreator.createAmbient(new ColorRGBA(0.2f, 0.2f, 0.2f, 1.0f));
        Main.camera().createShadowsFiler(m_sun);
        
        createObjects();
        //Time.setFixedTimeScale(0.3f);
    }
    
    private void createObjects() {
        Vector3f offset = Utilities.zero();
        for (int i = 0; i < 12; ++i) {
            new StoragePlatform(offset);
            offset.x += containerSize().x * 6 + 15.0f;
        }
        
        
        
    }
    private void createStorage(Vector3f position) {
        Transform t = new Transform();
        t.position(position);
        ContainerCarrier carrier = new ContainerCarrier(t, new Point3(6, 6, 14));
    }
    /*
     * Transform testCube;
    Transform testCube2;
    Path m_testCube2Path = new Path(
            null, 
            null, 
            false, 
            true, 
            8.0f, 
            3.0f, 
            LoopMode.Loop, 
            EaseType.EaseInSine, 
            null, 
            new Vector3f(-10.0f, 0.0f, 10.0f),
            new Vector3f(-10.0f, 0.0f, -10.0f),
            new Vector3f(10.0f, 0.0f, -10.0f),
            new Vector3f(10.0f, 0.0f, 10.0f)
     );
    
    CraneHook m_testHook;
    RailCrane m_testCrane;
    
    boolean goingBack;
    float prev;
     * @Override
    public void update() {
        testCube.move(testCube.forward(), Time.deltaTime() * 3.0f);
        m_testCube2Path.update();
        testCube2.position(m_testCube2Path.getPosition());
        m_testCrane._update();
        if (m_testHook.finishedWaiting())
            m_testHook.moveDown(false, -7f);
        
        
        boolean ppp = goingBack;
        if (!Mathf.inRange(Mathf.delta(prev, testCube.eulerAngles().x), Mathf.deltaAngle(prev, testCube.eulerAngles().x), 10.0f))
            goingBack = !goingBack;
            
        prev = testCube.eulerAngles().x;
        
        if (goingBack)
            testCube.rotate(-80.8f * Time.deltaTime(), 0.0f, 0.0f);
        else
            testCube.rotate( 80.8f * Time.deltaTime(), 0.0f, 0.0f);
        
        if (ppp != goingBack)
            prev = testCube.eulerAngles().x;
    }
    @Override
    public void fixedUpdate() {
        testCube.setMaterial(MaterialCreator.unshadedRandom());
    }
    
    private void createObjects() {
        
        // Testing cube
        testCube = new Transform();
        testCube2 = new Transform();
        
        Box b = new Box(30, 1, 30);
        Geometry geom = new Geometry("Box", b);
        geom.setLocalTranslation(0.0f, -17.0f, 0.0f);
        geom.setMaterial(MaterialCreator.diffuse(new ColorRGBA(0.7f, 0.7f, 0.7f, 1.0f)));
        geom.setShadowMode(RenderQueue.ShadowMode.Receive);
        Main.root().attachChild(geom);
        
        Box bb = new Box(5, 5, 5);
        Geometry geomb = new Geometry("Box", bb);
        geomb.setLocalTranslation(0.0f, -15.0f, 0.0f);
        geomb.setMaterial(MaterialCreator.diffuse(new ColorRGBA(0.7f, 0.7f, 0.7f, 1.0f)));
        geomb.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        Main.root().attachChild(geomb);
        
        Box ba = new Box(1, 1, 1);
        Geometry geoma = new Geometry("Box", ba);
        geoma.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        geoma.setMaterial(MaterialCreator.diffuse());
        
        Box bc = new Box(1, 1, 1);
        Geometry geomc = new Geometry("Box", bc);
        geomc.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        geomc.setMaterial(MaterialCreator.diffuse());
        
        m_testHook = new CraneHook(testCube2, 2.0f, 3.0f, 1.0f, new Vector3f(0.0f, -1.0f, 0.0f));
        m_testCrane = new RailCrane(null, m_testHook);
        //m_testHook.attachChild(geomc);
        
        
        m_testHook.moveDown(false, -10.0f);
        
        
        
        testCube.attachChild(geoma);
        testCube2.attachChild(geoma.clone(true));
        m_testCube2Path.setPosition(testCube2.position());
        
        
        m_testCube2Path.setCallback(new Callback(this, "test"));
        
        
        Main.instance().camera().setTarget(testCube2);
    }
    public void test() {
        Debug.log("This is awesome!!!");
        Instruction i = Instruction.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setInstructionType(InstructionType.CONSOLE_COMMAND)
                .setData(InstructionProto.InstructionData.newBuilder().setTime(0l).setMessage("test command").build())
                .build();
        Main.instance().simClient().getComProtocol().sendInstruction(i);
        m_testHook.moveUp(false);
    }
     * 
     */
    
}
