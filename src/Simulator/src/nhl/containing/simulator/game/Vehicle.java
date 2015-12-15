/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.game;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import nhl.containing.simulator.framework.Path;
import nhl.containing.simulator.framework.Utilities;
import nhl.containing.simulator.simulation.Main;
import nhl.containing.simulator.world.MaterialCreator;

/**
 *
 * @author sietse
 */
public class Vehicle extends MovingItem {
    
    private final static String BASE_MODEL_PATH = "models/";
    
    private Spatial m_frontSpatial;
    private Spatial m_holderSpatial;
    
    private Material m_frontMaterial;
    private Material m_holderMaterial;
    
    
    
    public Vehicle(String frontModel, String holderModel, float emptySpeed, float loadedSpeed) {
        super(null, loadedSpeed, emptySpeed);
        
        
        if (m_holderMaterial == null) {
            m_holderMaterial = MaterialCreator.unshadedRandom();
        }
        
        if (frontModel != null) {
            if (m_frontMaterial == null)
                m_frontMaterial = MaterialCreator.unshadedRandom();
            
            m_frontSpatial = Main.assets().loadModel(BASE_MODEL_PATH + frontModel);
            m_frontSpatial.setMaterial(m_frontMaterial);
        }
        
        
    }
    public void hide() {
        
    }
    public void reuse() {
        
    }
}
