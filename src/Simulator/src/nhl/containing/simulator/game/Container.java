/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.game;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import nhl.containing.simulator.simulation.Transform;
import nhl.containing.simulator.world.MaterialCreator;

/**
 *
 * @author sietse
 */
public class Container extends Transform {
    public static final Vector3f OFFSET = new Vector3f(0.0f, 0.0f, 0.0f);
    
    private RFID m_rfid;
    private final Material m_material;
    
    public Container(RFID id) {
        super();
        this.m_rfid = id;
        this.m_material = MaterialCreator.diffuse(ColorRGBA.randomColor(), 0.5f);
    }
    public Container(RFID id, Material material) {
        super();
        this.m_rfid = id;
        this.m_material = material;
    }
    public Container(Transform parent, RFID id) {
        super();
        this.m_rfid = id;
        this.m_material = MaterialCreator.diffuse(ColorRGBA.randomColor(), 0.5f);
    }
    public Container(Transform parent, Material material, RFID id) {
        super();
        this.m_rfid = id;
        this.m_material = material;
    }
    
    public RFID getRFID() {
        return this.m_rfid;
    }
}
