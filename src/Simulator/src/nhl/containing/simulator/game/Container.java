package nhl.containing.simulator.game;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import nhl.containing.simulator.framework.Transform;
import nhl.containing.simulator.simulation.Main;
import nhl.containing.simulator.world.ContainerPool;
import nhl.containing.simulator.world.MaterialCreator;

/**
 *
 * @author sietse
 */
public class Container {
    
    public Transform transform = null;  // "Physical" conainer
    private long m_id;                  // Container id
    
    private final RFID m_rfid;          // Container properties
    private final Material m_material;  // Container material (saved here for pool system)
    
    /**
     * Constructor
     * @param id 
     */
    public Container(RFID id) {
        this.m_rfid = id;
        this.m_material = MaterialCreator.getRandomContainerMaterial();
        m_id = Main.register(this);
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
        m_id = Main.register(this);
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
        m_id = Main.register(this);
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
        m_id = Main.register(this);
        show();
    }
    
    /**
     * Get ID
     * @return 
     */
    public long id() {
        return m_id;
    }
    
    /**
     * Get RFID
     * @return 
     */
    public RFID getRFID() {
        return this.m_rfid;
    }
    
    /**
     * Show container at position
     * @param pos 
     */
    public final void show(Vector3f pos) {
        show();
        transform.position(pos);
    }
    
    /**
     * Show container
     */
    public final void show() {
        if (transform == null) {
            ContainerPool.get(this);
            transform.setMaterial(this.m_material);
        }
    }
    /**
     * Hide container
     */
    public final void hide() {
        if (transform == null)
            return;
        ContainerPool.dispose(this);
    }
}
