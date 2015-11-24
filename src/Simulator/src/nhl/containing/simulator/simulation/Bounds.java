/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.simulation;

import com.jme3.math.Vector3f;

/**
 *
 * @author sietse
 */
public class Bounds {
    private Vector3f m_center;      // The center of the bounding box
    private Vector3f m_extents;     // The extents of the box. This is always half of the size
    
    /**
     * Constructor
     * Creates new Bounds with a given center and total size. Bound extents will be half the given size.
     * @param center Center
     * @param size Size
     */
    public Bounds(Vector3f center, Vector3f size) {
        this.m_center = new Vector3f(center);
        this.m_extents = new Vector3f(size).mult(0.5f);
    }
    
    /**
     * Set center
     * @param center 
     */
    public void center(Vector3f center) {
        this.m_center = new Vector3f(center);
    }
    /**
     * Get center
     * @return 
     */
    public Vector3f center() {
        return this.m_center.clone();
    }
    
    /**
     * Set size
     * The total size of the box. This is always twice as large as the extents.
     * @param size 
     */
    public void size(Vector3f size) {
        this.m_extents = new Vector3f(size).mult(0.5f);
    }
    /**
     * get Size
     * The total size of the box. This is always twice as large as the extents.
     * @return 
     */
    public Vector3f size() {
        return this.m_extents.clone().mult(2.0f);
    }
    
    /**
     * Set extents
     * The extents of the box. This is always half of the size
     * @param extents 
     */
    public void extents(Vector3f extents) {
        this.m_extents = new Vector3f(extents);
    }
    /**
     * Get extends
     * The extents of the box. This is always half of the size.
     * @return 
     */
    public Vector3f extents() {
        return this.m_extents.clone();
    }
    
    /**
     * Set min
     * The minimal point of the box. This is always equal to center-extents
     * @param v 
     */
    public void min(Vector3f v) {
        setMinMax(v, this.max());
    }
    /**
     * Get min
     * The minimal point of the box. This is always equal to center-extents
     * @return 
     */
    public Vector3f min() {
        return this.m_center.clone().subtract(this.m_extents);
    }
    
    /**
     * Set max
     * The maximal point of the box. This is always equal to center+extents
     * @param v 
     */
    public void max(Vector3f v) {
        setMinMax(this.min(), v);
    }
    /**
     * Get max
     * The maximal point of the box. This is always equal to center+extents
     * @return 
     */
    public Vector3f max() {
        return this.m_center.clone().add(this.m_extents);
    }
    
    /**
     * Sets the bounds to the min and max value of the box
     * @param min
     * @param max 
     */
    public void setMinMax(Vector3f min, Vector3f max) {
        this.extents(max.clone().subtract(min).mult(0.5f));
        this.center(min.clone().add(this.extents()));
    }
    
    /**
     * Is point contained in the bounding box?
     * @param point
     * @return Is in box
     */
    public boolean contains(Vector3f point) {
        return 
                Mathf.inRange(point.x, Mathf.sclamp(point.x, min().x, max().x)) && 
                Mathf.inRange(point.y, Mathf.sclamp(point.y, min().y, max().y)) && 
                Mathf.inRange(point.z, Mathf.sclamp(point.z, min().z, max().z));
    }
    
    @Override
    public String toString() {
        return "Center: " + m_center.toString() + ", Extents: " + m_extents.toString();
    }
}
