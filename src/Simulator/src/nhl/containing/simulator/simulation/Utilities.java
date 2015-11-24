/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.simulation;

import nhl.containing.simulator.simulation.Mathf;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.Arrays;
import java.util.List;

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

    public static Vector3f substract(Vector3f a, Vector3f b) {
        return new Vector3f(
            a.x - b.x,
            a.y - b.y,
            a.z - b.z);
    }
    public static float distance(Vector3f a, Vector3f b) {
        return Mathf.abs(substract(a, b).length());
    }

    public static float NaNSafeFloat(float f) {
        return ((Float)f).isNaN() ? 0.0f : f;
    }
    
    public static boolean nullOrEmpty(String s) {
        return (s == null || s.length() < 1);
    }
    
    
    public static <T> List<T> addAll(List<T> base, T[] added) {
        base.addAll(Arrays.asList(added));
        return base;
    }
    
    public static Vector3f rotateY(Vector3f cur, float rotation) {
        Vector3f __new = zero();
        __new.x = cur.z * Mathf.sin(Mathf.Deg2Rad * rotation) - cur.x * Mathf.cos(Mathf.Deg2Rad * rotation);
        __new.y = cur.y;
        __new.z = cur.z * Mathf.cos(Mathf.Deg2Rad * rotation) + cur.x * Mathf.sin(Mathf.Deg2Rad * rotation);
        
        return __new;
    }

}
