/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.world;

import nhl.containing.simulator.simulation.Main;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 *
 * @author sietse
 */
public final class WorldCreator {
    public static final Geometry createBox(Node parent) {
        /* A colored lit cube. Needs light source! */ 
        float m = 0.48f;
        Box boxMesh = new Box(World.containerSize().x * m, World.containerSize().y * m, World.containerSize().z * m); 
        Geometry boxGeo = new Geometry("Colored Box", boxMesh);
        //boxGeo.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        boxGeo.setMaterial(MaterialCreator.diffuse(ColorRGBA.randomColor(), 0.5f)); 
        parent.attachChild(boxGeo);
        return boxGeo;
    }
    
    
}
