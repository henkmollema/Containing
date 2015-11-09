/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author sietse
 */
public final class Utilities {
    public static final Vector3f zero() { return new Vector3f(0.0f, 0.0f, 0.0f).clone(); }
    public static final Vector3f one() { return new Vector3f(1.0f, 1.0f, 1.0f).clone(); }
    public static final Vector3f up() { return new Vector3f(0.0f, 1.0f, 0.0f).clone(); }
    public static final Vector3f down() { return new Vector3f(0.0f, -1.0f, 0.0f).clone(); }
    public static final Vector3f left() { return new Vector3f(-1.0f, 0.0f, 0.0f).clone(); }
    public static final Vector3f right() { return new Vector3f(1.0f, 0.0f, 0.0f).clone(); }
    public static final Vector3f forward() { return new Vector3f(0.0f, 0.0f, 1.0f).clone(); }
    public static final Vector3f back() { return new Vector3f(0.0f, 0.0f, -1.0f).clone(); }
    
    public static Vector3f Horizontal(Vector3f v) {
        return new Vector3f(v.x, 0.0f, v.z);
    }
    
    public static void setWorldRotation(Spatial spatial, Quaternion rotation) {
        Spatial parent = spatial.getParent();
        Quaternion localRotation;
        
        if (parent != null) {
            localRotation = parent.getWorldRotation().clone();
            localRotation = localRotation.inverse();
            localRotation = localRotation.multLocal(rotation);
            localRotation = localRotation.normalizeLocal();
        } else {
            localRotation = rotation.clone();
        }
        
        spatial.setLocalRotation(localRotation);
    }
}
