/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation;

import com.jme3.math.Vector3f;

/**
 *
 * @author sietse
 */
public class Bounds {
    private Vector3f m_center;
    private Vector3f m_extents;
    
    public Bounds(Vector3f center, Vector3f size) {
        this.m_center = new Vector3f(center);
        this.m_extents = new Vector3f(size).mult(0.5f);
    }
    
    public void center(Vector3f center) {
        this.m_center = new Vector3f(center);
    }
    public Vector3f center() {
        return this.m_center.clone();
    }
    
    public void size(Vector3f size) {
        this.m_extents = new Vector3f(size).mult(0.5f);
    }
    public Vector3f size() {
        return this.m_extents.clone().mult(2.0f);
    }
    
    public void extents(Vector3f extents) {
        this.m_extents = new Vector3f(extents);
    }
    public Vector3f extents() {
        return this.m_extents.clone();
    }
    
    public void min(Vector3f v) {
        setMinMax(v, this.max());
    }
    public Vector3f min() {
        return this.m_center.clone().subtract(this.m_extents);
    }
    
    public void max(Vector3f v) {
        setMinMax(this.min(), v);
    }
    public Vector3f max() {
        return this.m_center.clone().add(this.m_extents);
    }
    
    public void setMinMax(Vector3f min, Vector3f max) {
        this.extents(max.clone().subtract(min).mult(0.5f));
        this.center(min.clone().add(this.extents()));
    }
    
    public boolean contains(Vector3f point) {
        return 
                Mathf.inRange(point.x, Mathf.sclamp(point.x, min().x, max().x)) && 
                Mathf.inRange(point.y, Mathf.sclamp(point.y, min().y, max().y)) && 
                Mathf.inRange(point.z, Mathf.sclamp(point.z, min().z, max().z));
    }
}
