/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

import Simulation.Main;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author sietse
 */
public final class MaterialCreator {
    
    public static Material unshadedBlue() {
        return unshaded(ColorRGBA.Blue);
    }
    public static Material unshaded(ColorRGBA color) {
        Material m = new Material(Main.assets(), "Common/MatDefs/Misc/Unshaded.j3md");
        m.setColor("Color", color.clone());
        
        return m;
    }
    
    public static Material diffuse() {
        return diffuse(ColorRGBA.White);
    }
    public static Material diffuse(ColorRGBA color) {
        return diffuse(color, color);
    }
    public static Material diffuse(ColorRGBA colorAmbient, ColorRGBA colorDiffuse) {
        
        Material m = new Material(Main.assets(), "Common/MatDefs/Light/Lighting.j3md"); 
        m.setBoolean("UseMaterialColors", true); 
        m.setColor("Ambient", colorAmbient.clone()); 
        m.setColor("Diffuse", colorDiffuse.clone());
        m.setFloat("Shininess", 5f);
        m.setColor("Specular",ColorRGBA.White);
        return m;
    }
    
}
