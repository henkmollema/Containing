package nhl.containing.simulator.game;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import nhl.containing.simulator.framework.Transform;
import nhl.containing.simulator.framework.Utilities;
import nhl.containing.simulator.simulation.Main;
import nhl.containing.simulator.world.ContainerPool;
import nhl.containing.simulator.world.MaterialCreator;

/**
 *
 * @author sietse
 */
public class Container {
    
    public Transform transform = null;
    private long m_id;
    
    private final RFID m_rfid;                // Container properties
    private final Material m_material;  // Container material (saved here for pool system)
    
    /**
     * Constructor
     * @param id 
     */
    public Container(RFID id) {
        this.m_rfid = id;
        //this.m_material = MaterialCreator.diffuse(ColorRGBA.randomColor(), 0.5f);
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
    public long id()
    {
        return m_id;
    }
    public final void show(Vector3f pos) {
        show();
        transform.position(pos);
    }
    public final void show() {
        if (transform == null) {
            ContainerPool.get(this);
            transform.setMaterial(this.m_material);
        }
        
        //transform.position(Utilities.zero());
        //transform.getChild(0).setLocalTranslation(Utilities.zero());
        //transform.getChild(0).setLocalRotation(Quaternion.IDENTITY);
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
