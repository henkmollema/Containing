/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.world;

import com.jme3.asset.TextureKey;
import nhl.containing.simulator.simulation.Main;
import nhl.containing.simulator.simulation.Mathf;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;
import nhl.containing.simulator.simulation.Debug;

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
    /**
     * unshaded black material
     * @return 
     */
    public static Material unshadedBlack() {
        return unshaded(ColorRGBA.Black);
    }
    /**
     * unshaded brown material
     * @return 
     */
    public static Material unshadedBrown() {
        return unshaded(ColorRGBA.Brown);
    }
    /**
     * unshaded cyan material
     * @return 
     */
    public static Material unshadedCyan() {
        return unshaded(ColorRGBA.Cyan);
    }
    /**
     * unshaded dark gray material
     * @return 
     */
    public static Material unshadedDarkGray() {
        return unshaded(ColorRGBA.DarkGray);
    }
    /**
     * unshaded gray material
     * @return 
     */
    public static Material unshadedGray() {
        return unshaded(ColorRGBA.Gray);
    }
    /**
     * unshaded green material
     * @return 
     */
    public static Material unshadedGreen() {
        return unshaded(ColorRGBA.Green);
    }
    /**
     * unshaded light gray material
     * @return 
     */
    public static Material unshadedLightGray() {
        return unshaded(ColorRGBA.LightGray);
    }
    /**
     * unshaded magenta material
     * @return 
     */
    public static Material unshadedMagenta() {
        return unshaded(ColorRGBA.Magenta);
    }
    /**
     * unshaded orange material
     * @return 
     */
    public static Material unshadedOrange() {
        return unshaded(ColorRGBA.Orange);
    }
    /**
     * unshaded pink material
     * @return 
     */
    public static Material unshadedPink() {
        return unshaded(ColorRGBA.Pink);
    }
    /**
     * unshaded red material
     * @return 
     */
    public static Material unshadedRed() {
        return unshaded(ColorRGBA.Red);
    }
    /**
     * unshaded white material
     * @return 
     */
    public static Material unshadedWhite() {
        return unshaded(ColorRGBA.White);
    }
    /**
     * unshaded yellow material
     * @return 
     */
    public static Material unshadedYellow() {
        return unshaded(ColorRGBA.Yellow);
    }
    /**
     * unshaded random color material
     * @return 
     */
    public static Material unshadedRandom() {
        return unshaded(ColorRGBA.randomColor());
    }
    /**
     * unshaded X color material
     * @param color the X int the story above
     * @return Your awesome unshaded material
     */
    public static Material unshaded(ColorRGBA color) {
        Material m = new Material(Main.assets(), "Common/MatDefs/Misc/Unshaded.j3md");
        m.setColor("Color", color.clone());
        
        return m;
    }
    /**
     * unshaded texturized material
     * @param texture texture location
     * @return 
     */
    public static Material unshaded(String texture) {
        return unshaded(texture, ColorRGBA.White);
    }
    /**
     * unshaded texturized material
     * @param texture texture location
     * @param color
     * @return 
     */
    public static Material unshaded(String texture, ColorRGBA color) {
        Material m = new Material(Main.assets(), "Common/MatDefs/Misc/Unshaded.j3md");
        
        m.setColor("Color", color);
        if (texture != null) {
            Texture _tex = Main.assets().loadTexture(new TextureKey(texture));
            m.setTexture("ColorMap", _tex); 
        }
        
        return m;
    }
    
    /**
     * diffuse white color
     * @return 
     */
    public static Material diffuse() {
        return diffuse(ColorRGBA.White);
    }
    /**
     * diffuse X color
     * @param color X in the story above
     * @return 
     */
    public static Material diffuse(ColorRGBA color) {
        return diffuse(color, color, DEFAULT_SPECULAR, ColorRGBA.Black);
    }
    /**
     * diffuse specular with X color
     * @param color X in the story above
     * @param specular specularity
     * @return 
     */
    public static Material diffuse(ColorRGBA color, float specular) {
        specular = Mathf.clamp(specular);
        return diffuse(color, color, DEFAULT_SPECULAR, new ColorRGBA(specular, specular, specular, 1.0f));
    }
    /**
     * 
     * @param colorAmbient
     * @param colorDiffuse
     * @param specular
     * @param colorSpecular
     * @return 
     */
    public static Material diffuse(ColorRGBA colorAmbient, ColorRGBA colorDiffuse, float specular, ColorRGBA colorSpecular) {
        
        if (!World.USE_DIFFUSE) {
            return unshaded(colorDiffuse);
        }
        
        Material m = new Material(Main.assets(), "Common/MatDefs/Light/Lighting.j3md"); 
        m.setBoolean("UseMaterialColors", true); 
        m.setColor("Ambient", colorAmbient.clone());
        m.setColor("Diffuse", colorDiffuse.clone());
        m.setFloat("Shininess", Mathf.max(specular, 0.001f));
        m.setColor("Specular", colorSpecular);
        
        return m;
    }
    
    /**
     * Material for ropes
     * @return 
     */
    public static Material rope() {
        return diffuse(ColorRGBA.Brown);
    }
}
