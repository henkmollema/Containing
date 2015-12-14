package nhl.containing.simulator.game;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import nhl.containing.simulator.framework.Transform;
import nhl.containing.simulator.world.ContainerPool;
import nhl.containing.simulator.world.MaterialCreator;

/**
 *
 * @author sietse
 */
public class Container {
    
    public Transform transform = null;
    
    private final RFID m_rfid;                // Container properties
    private final Material m_material;  // Container material (saved here for pool system)
    
    /**
     * Constructor
     * @param id 
     */
    public Container(RFID id) {
        this.m_rfid = id;
        this.m_material = MaterialCreator.diffuse(ColorRGBA.randomColor(), 0.5f);
        show();
    }
    /**
     * Constructor
     * @param id
     * @param material 
     */
    public Container(RFID id, Material material) {
        this.m_rfid = id;
        this.m_material = material;
        show();
    }
    /**
     * Constructor
     * @param parent
     * @param id 
     */
    public Container(Transform parent, RFID id) {
        this.m_rfid = id;
        this.m_material = MaterialCreator.diffuse(ColorRGBA.randomColor(), 0.5f);
        show();
    }
    /**
     * Constructor
     * @param parent
     * @param material
     * @param id 
     */
    public Container(Transform parent, Material material, RFID id) {
        this.m_rfid = id;
        this.m_material = material;
        show();
    }
    
    public final void show() {
        if (transform != null)
            return;
        
        ContainerPool.get(this);
        transform.setMaterial(this.m_material);
        
        System.out.println(this);
    }
    public final void hide() {
        if (transform == null)
            return;
        
        ContainerPool.dispose(this);
    }
    
    /**
     * Get ID
     * @return 
     */
    public RFID getRFID() {
        return this.m_rfid;
    }
}
