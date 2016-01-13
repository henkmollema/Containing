package nhl.containing.simulator.world;

import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import nhl.containing.simulator.simulation.Main;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

/**
 * Create ambient and sun light
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
