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

/**
 *
 * @author sietse
 */
public final class WorldCreator {
    public static Geometry createBox(Node parent) {
        Vector3f v = World.containerSize();
        float m = 0.48f;
        v = v.mult(m);
        
        return createBox(parent, v);
    }
    public static Geometry createBox(Node parent, Vector3f size) {
        if (parent == null) 
            parent = Main.root();
        
        Box boxMesh = new Box(size.x, size.y, size.z); 
        Geometry boxGeo = new Geometry("Colored Box", boxMesh);
        //boxGeo.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        //boxGeo.setMaterial(MaterialCreator.diffuse(ColorRGBA.randomColor(), 0.5f)); 
        
        
        boxGeo.setShadowMode(RenderQueue.ShadowMode.Off);
        boxGeo.setMaterial(MaterialCreator.unshadedRandom());
        
        parent.attachChild(boxGeo);
        return boxGeo;
    }
    
    
}
