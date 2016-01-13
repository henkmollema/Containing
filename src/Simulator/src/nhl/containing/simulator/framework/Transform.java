package nhl.containing.simulator.framework;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.SimulationItemType;
import nhl.containing.simulator.simulation.Main;

/**
 * Node extention for easier translating/rotating
 * @author sietse
 */
public class Transform extends Node {
    
    protected final long m_id; // ID
    
    /**
     * Constructor
     * Sets node to root node
     */
    public Transform() {
        super("A name");
        Main.root().attachChild(this);
        this.setCullHint(CullHint.Dynamic);
        m_id = Main.register(this);
        position(Utilities.zero());
    }
    /**
     * Constructor
     * @param parent parent node
     */
    public Transform(Node parent) {
        super("A name");
        if (parent == null)
            Main.root().attachChild(this);
        else
            parent.attachChild(this);
        this.setCullHint(CullHint.Dynamic);
        m_id = Main.register(this);
        position(Utilities.zero());
    }
     /**
     * Adds the transform to the metalist
     * @param type type of the transform
     */
     public void register(SimulationItemType type){
        Main.getSimClient().addSimulationItem(id(), type, Utilities.Horizontal(position()),-1);
    }
     
     /**
     * Adds the transform to the metalist
     * @param type type of the transform
     * @param arrival id for arrivalnode parkingspot
     * @param depart id for departnode parkingspot
     */
     public void register(SimulationItemType type,int arrival,int depart){
        Main.getSimClient().addSimulationItem(id(), type, Utilities.Horizontal(position()),-1,arrival,depart);
    }
    
    /**
     * Adds the transform to the metalist
     * @param parentid parentid for storage parkingslots , else -1
     * @param type type of the transform
     */
    public void register(int parentid,SimulationItemType type){
        Main.getSimClient().addSimulationItem(id(), type, Utilities.Horizontal(position()),parentid);
    }
        /**
     * Adds the transform to the metalist
     * @param parentid parentid for storage parkingslots , else -1
     * @param type type of the transform
     * @param arrival id for arrivalnode parkingspot
     * @param depart id for departnode parkingspot
     */
    public void register(int parentid,SimulationItemType type,int arrival, int depart){
        Main.getSimClient().addSimulationItem(id(), type, Utilities.Horizontal(position()),parentid,arrival,depart);
    }
    
    
    /**
     * Adds the transform to the metalist
     * @param parentid parentid for storage parkingslots , else -1
     * @param id id of the current item
     * @param type type of the transform
     */
    public void register(int parentid,int id,SimulationItemType type){
        Main.getSimClient().addSimulationItem(id, type, Utilities.Horizontal(position()), parentid);
    }
    
    /**
     * Get ID
     * @return 
     */
    public long id() {
        return m_id + 0;
    }
    
    /**
     * Get position
     * @return 
     */
    public final Vector3f position() {
        return getWorldTranslation().clone();
    }
    /**
     * Set position
     * @param p 
     */
    public final void position(Vector3f p) {
        Vector3f pos = p == null ? Utilities.zero() : p.clone();
        move(pos.subtract(position()));
    }
    
    /**
     * Get localposition
     * @return 
     */
    public final Vector3f localPosition() {
        return getLocalTranslation().clone();
    }
    /**
     * Sets localposition
     * @param p 
     */
    public final void localPosition(Vector3f p) {
        Vector3f pos = p == null ? Utilities.zero() : new Vector3f(p);
        move(pos.subtract(localPosition()));
    }
    
    /**
     * Get rotation
     * @return 
     */
    public final Quaternion rotation() {
        return getWorldRotation().clone();
    }
    /**
     * Set rotation
     * @param q 
     */
    public final void rotation(Quaternion q) {
        Utilities.setWorldRotation(this, q == null ? Quaternion.IDENTITY : q.clone());
    }
    
    /**
     * Get local rotation
     * @return 
     */
    public final Quaternion localRotation() {
        return getLocalRotation().clone();
    }
    /**
     * Sets local rotation
     * @param q 
     */
    public final void localRotation(Quaternion q) {
        setLocalRotation(q == null ? Quaternion.IDENTITY : q.clone());
    }
    
    /**
     * Get euleranges
     * @return 
     */
    public final Vector3f eulerAngles() {
        Quaternion q = rotation();
        float[] a = q.toAngles(null);
        return new Vector3f(a[0] * Mathf.Rad2Deg, a[1] * Mathf.Rad2Deg, a[2] * Mathf.Rad2Deg);
    }
    /**
     * Set eulerangles
     * @param a 
     */
    public final void eulerAngles(Vector3f a) {
        Vector3f b = a == null ? Utilities.zero() : a.clone();
        Quaternion q = Quaternion.IDENTITY.clone();
        q.fromAngles(b.x, b.y, b.z);
        rotation(q);
    }
    
    /**
     * Get localeulerangels
     * @return 
     */
    public final Vector3f localEulerAngles() {
        Quaternion q = localRotation();
        float[] a = q.toAngles(null);
        return new Vector3f(a[0] * Mathf.Rad2Deg, a[1] * Mathf.Rad2Deg, a[2] * Mathf.Rad2Deg);
    }
    /**
     * Set localeulerangles
     * @param a 
     */
    public final void localEulerAngles(Vector3f a) {
        Vector3f b = a == null ? Utilities.zero() : a.clone();
        Quaternion q = Quaternion.IDENTITY.clone();
        q.fromAngles(b.x, b.y, b.z);
        localRotation(q);
    }
    
    /**
     * Local forward
     * @return 
     */
    public final Vector3f forward() {
        return transfromDirection(Utilities.forward());
    }
    /**
     * Local backward
     * @return 
     */
    public final Vector3f back() {
        return transfromDirection(Utilities.back());
    }
    /**
     * Local right
     * @return 
     */
    public final Vector3f right() {
        return transfromDirection(Utilities.right());
    }
    /**
     * Local left
     * @return 
     */
    public final Vector3f left() {
        return transfromDirection(Utilities.left());
    }
    /**
     * Local up
     * @return 
     */
    public final Vector3f up() {
        return transfromDirection(Utilities.up());
    }
    /**
     * Local down
     * @return 
     */
    public final Vector3f down() {
        return transfromDirection(Utilities.down());
    }
    /**
     * Get transform direction
     * @param v
     * @return 
     */
    public final Vector3f transfromDirection(Vector3f v) {
        Quaternion q = rotation();
        return q.mult(v);
    }
    
    /**
     * Get look direction (forward)
     * @return 
     */
    public final Vector3f lookDirection() {
        return forward();
    }
    /**
     * Set look diretion (set forward)
     * @param p 
     */
    public final void lookDirection(Vector3f p) {
        lookAt(position().add(p));
    }
    
    /**
     * Set look position
     * @param t 
     */
    public final void lookAt(Transform t) {
        lookAt(t.position());
    }
    /**
     * Set look position
     * @param p 
     */
    public final void lookAt(Vector3f p) {
        lookAt(p, Utilities.up());
    }
    
    /**
     * Move the transfrom
     * @param direction
     * @param speed 
     */
    public final void move(Vector3f direction, float speed) {
        if (direction == null)
            return;
        this.move(direction.clone().mult(speed));
    }
    /**
     * Move the transform multiplied by deltatime
     * @param direction 
     */
    public final void scaledMove(Vector3f direction) {
        if (direction == null)
            return;
        move(direction, Time.deltaTime());
    }
    /**
     * Move the transform multipled by deltatime
     * @param direction
     * @param speed 
     */
    public final void scaledMove(Vector3f direction, float speed) {
        if (direction == null)
            return;
        move(direction, speed * Time.deltaTime());
    }
    
    /**
     * Rotate in degrees
     * @param x
     * @param y
     * @param z
     * @return 
     */
    @Override
    public Spatial rotate(float x, float y, float z) {
        return super.rotate(x * Mathf.Deg2Rad, y * Mathf.Deg2Rad, z * Mathf.Deg2Rad);
    }
    
    /**
     * Attach child
     * @param spatial
     * @return 
     */
    @Override
    public int attachChild(Spatial spatial) {
        if (spatial == null)
            return - 1;
        if (spatial.getUserData(Main.TRANSFORM_ID_KEY) == null)
            spatial.setUserData(Main.TRANSFORM_ID_KEY, m_id);
        return super.attachChild(spatial);
    }
}
