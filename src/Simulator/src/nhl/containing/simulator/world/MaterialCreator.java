/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.world;

import nhl.containing.simulator.simulation.Main;
import nhl.containing.simulator.simulation.Mathf;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author sietse
 */
public final class MaterialCreator {
    
    private static final float DEFAULT_SPECULAR = 2.0f;
    
    /**
     * Return unshaded blue material
     * @return 
     */
    public static Material unshadedBlue() {
        return unshaded(ColorRGBA.Blue);
    }
    public static Material unshadedBlack() {
        return unshaded(ColorRGBA.Black);
    }
    public static Material unshadedBrown() {
        return unshaded(ColorRGBA.Brown);
    }
    public static Material unshadedCyan() {
        return unshaded(ColorRGBA.Cyan);
    }
    public static Material unshadedDarkGray() {
        return unshaded(ColorRGBA.DarkGray);
    }
    public static Material unshadedGray() {
        return unshaded(ColorRGBA.Gray);
    }
    public static Material unshadedGreen() {
        return unshaded(ColorRGBA.Green);
    }
    public static Material unshadedLightGray() {
        return unshaded(ColorRGBA.LightGray);
    }
    public static Material unshadedMagenta() {
        return unshaded(ColorRGBA.Magenta);
    }
    public static Material unshadedOrange() {
        return unshaded(ColorRGBA.Orange);
    }
    public static Material unshadedPink() {
        return unshaded(ColorRGBA.Pink);
    }
    public static Material unshadedRed() {
        return unshaded(ColorRGBA.Red);
    }
    public static Material unshadedWhite() {
        return unshaded(ColorRGBA.White);
    }
    public static Material unshadedYellow() {
        return unshaded(ColorRGBA.Yellow);
    }
    public static Material unshadedRandom() {
        return unshaded(ColorRGBA.randomColor());
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
        return diffuse(color, color, DEFAULT_SPECULAR, ColorRGBA.Black);
    }
    public static Material diffuse(ColorRGBA color, float specular) {
        specular = Mathf.clamp(specular);
        return diffuse(color, color, DEFAULT_SPECULAR, new ColorRGBA(specular, specular, specular, 1.0f));
    }
    public static Material diffuse(ColorRGBA colorAmbient, ColorRGBA colorDiffuse, float specular, ColorRGBA colorSpecular) {
        
        Material m = new Material(Main.assets(), "Common/MatDefs/Light/Lighting.j3md"); 
        m.setBoolean("UseMaterialColors", true); 
        m.setColor("Ambient", colorAmbient.clone()); 
        m.setColor("Diffuse", colorDiffuse.clone());
        m.setFloat("Shininess", Mathf.max(specular, 0.001f));
        m.setColor("Specular", colorSpecular);
        return m;
    }
    
    public static Material rope() {
        return diffuse(ColorRGBA.Brown);
    }
}
