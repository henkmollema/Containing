/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.world;

import nhl.containing.simulator.simulation.Main;
import nhl.containing.simulator.framework.Transform;
import com.jme3.light.*;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.control.LightControl;
import com.jme3.shadow.DirectionalLightShadowRenderer;

/**
 *
 * @author sietse
 */
public final class LightCreator {
    
    
    /**
     * Create sun
     * @param color
     * @param direction
     * @return 
     */
    public static DirectionalLight createSun(ColorRGBA color, Vector3f direction) {
        
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((direction.clone()).normalizeLocal());
        sun.setColor(color.clone());
        Main.root().addLight(sun); 
        return sun;
    }
    /**
     * Create ambient light
     * @param color
     * @return 
     */
    public static AmbientLight createAmbient(ColorRGBA color) {
        
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(color.clone());
        Main.root().addLight(ambient); 
        return ambient;
    }
    
    
}
