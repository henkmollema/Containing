/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.simulation;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author sietse
 */
public class Transform extends Node {
    
    protected final long m_id;
    //private Vector3f m_boundExtends = null;
    
    /**
     * Constructor
     * Sets node to root node
     */
    public Transform() {
        Main.root().attachChild(this);
        this.setCullHint(CullHint.Dynamic);
        m_id = Main.register(this);
    }
    /**
     * Constructor
     * @param parent parent node
     */
    public Transform(Node parent) {
        if (parent == null)
            Main.root().attachChild(this);
        else
            parent.attachChild(this);
        
        this.setCullHint(CullHint.Dynamic);
        m_id = Main.register(this);
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
    public Vector3f position() {
        return getWorldTranslation().clone();
    }
    /**
     * Set position
     * @param p 
     */
    public void position(Vector3f p) {
        Vector3f pos = p == null ? Utilities.zero() : p.clone();
        move(pos.subtract(position()));
    }
    
    /**
     * Get localposition
     * @return 
     */
    public Vector3f localPosition() {
        return getLocalTranslation().clone();
    }
    /**
     * Sets localposition
     * @param p 
     */
    public void localPosition(Vector3f p) {
        Vector3f pos = p == null ? Utilities.zero() : p.clone();
        move(pos.subtract(localPosition()));
    }
    
    /**
     * Get rotation
     * @return 
     */
    public Quaternion rotation() {
        return getWorldRotation().clone();
    }
    /**
     * Set rotation
     * @param q 
     */
    public void rotation(Quaternion q) {
        Utilities.setWorldRotation(this, q == null ? Quaternion.IDENTITY : q.clone());
    }
    
    /**
     * Get local rotation
     * @return 
     */
    public Quaternion localRotation() {
        return getLocalRotation().clone();
    }
    /**
     * Sets local rotation
     * @param q 
     */
    public void localRotation(Quaternion q) {
        setLocalRotation(q == null ? Quaternion.IDENTITY : q.clone());
    }
    
    /**
     * Get euleranges
     * @return 
     */
    public Vector3f eulerAngles() {
        Quaternion q = rotation();
        float[] a = q.toAngles(null);
        return new Vector3f(a[0] * Mathf.Rad2Deg, a[1] * Mathf.Rad2Deg, a[2] * Mathf.Rad2Deg);
    }
    /**
     * Set eulerangles
     * @param a 
     */
    public void eulerAngles(Vector3f a) {
        Vector3f b = a == null ? Utilities.zero() : a.clone();
        Quaternion q = Quaternion.IDENTITY;
        q.fromAngles(b.x, b.y, b.z);
        rotation(q);
    }
    
    /**
     * Get localeulerangels
     * @return 
     */
    public Vector3f localEulerAngles() {
        Quaternion q = localRotation();
        float[] a = q.toAngles(null);
        return new Vector3f(a[0] * Mathf.Rad2Deg, a[1] * Mathf.Rad2Deg, a[2] * Mathf.Rad2Deg);
    }
    /**
     * Set localeulerangles
     * @param a 
     */
    public void localEulerAngles(Vector3f a) {
        Vector3f b = a == null ? Utilities.zero() : a.clone();
        Quaternion q = Quaternion.IDENTITY;
        q.fromAngles(b.x, b.y, b.z);
        localRotation(q);
    }
    
    /**
     * Local forward
     * @return 
     */
    public Vector3f forward() {
        return transfromDirection(Utilities.forward());
    }
    /**
     * Local backward
     * @return 
     */
    public Vector3f back() {
        return transfromDirection(Utilities.back());
    }
    /**
     * Local right
     * @return 
     */
    public Vector3f right() {
        return transfromDirection(Utilities.right());
    }
    /**
     * Local left
     * @return 
     */
    public Vector3f left() {
        return transfromDirection(Utilities.left());
    }
    /**
     * Local up
     * @return 
     */
    public Vector3f up() {
        return transfromDirection(Utilities.up());
    }
    /**
     * Local down
     * @return 
     */
    public Vector3f down() {
        return transfromDirection(Utilities.down());
    }
    /**
     * Get transform direction
     * @param v
     * @return 
     */
    public Vector3f transfromDirection(Vector3f v) {
        Quaternion q = rotation();
        return q.mult(v);
    }
    
    /**
     * Get look direction (forward)
     * @return 
     */
    public Vector3f lookDirection() {
        return forward();
    }
    /**
     * Set look diretion (set forward)
     * @param p 
     */
    public void lookDirection(Vector3f p) {
        lookAt(position().add(p));
    }
    
    /**
     * Set look position
     * @param t 
     */
    public void lookAt(Transform t) {
        lookAt(t.position());
    }
    /**
     * Set look position
     * @param p 
     */
    public void lookAt(Vector3f p) {
        lookAt(p, Utilities.up());
    }
    
    /**
     * Move the transfrom
     * @param direction
     * @param speed 
     */
    public void move(Vector3f direction, float speed) {
        if (direction == null)
            return;
        this.move(direction.clone().mult(speed));
    }
    /**
     * Move the transform multiplied by deltatime
     * @param direction 
     */
    public void scaledMove(Vector3f direction) {
        if (direction == null)
            return;
        move(direction, Time.deltaTime());
    }
    /**
     * Move the transform multipled by deltatime
     * @param direction
     * @param speed 
     */
    public void scaledMove(Vector3f direction, float speed) {
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
        spatial.setUserData(Main.TRANSFORM_ID_KEY, m_id);
        return super.attachChild(spatial);
    }
}
