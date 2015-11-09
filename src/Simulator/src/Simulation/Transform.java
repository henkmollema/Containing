/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation;

import Utilities.Utilities;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author sietse
 */
public class Transform extends Node {
    
    public Transform() {
        Main.root().attachChild(this);
    }
    public Transform(Node parent) {
        parent.attachChild(this);
    }
    
    public Vector3f position() {
        return getWorldTranslation().clone();
    }
    public void position(Vector3f p) {
        Vector3f pos = p.clone();
        move(pos.subtract(position()));
    }
    
    public Vector3f localPosition() {
        return getLocalTranslation().clone();
    }
    public void localPosition(Vector3f p) {
        Vector3f pos = p.clone();
        move(pos.subtract(localPosition()));
    }
    
    public Quaternion rotation() {
        return getWorldRotation().clone();
    }
    public void rotation(Quaternion q) {
        Utilities.setWorldRotation(this, q.clone());
    }
    
    public Quaternion localRotation() {
        return getLocalRotation().clone();
    }
    public void localRotation(Quaternion q) {
        setLocalRotation(q.clone());
    }
    
    public Vector3f eulerAngles() {
        Quaternion q = rotation();
        float[] a = q.toAngles(null);
        return new Vector3f(a[0] * Mathf.Rad2Deg, a[1] * Mathf.Rad2Deg, a[2] * Mathf.Rad2Deg);
    }
    public void eulerAngles(Vector3f a) {
        Quaternion q = Quaternion.IDENTITY;
        q.fromAngles(a.x, a.y, a.z);
        rotation(q);
    }
    
    public Vector3f localEulerAngles() {
        Quaternion q = localRotation();
        float[] a = q.toAngles(null);
        return new Vector3f(a[0] * Mathf.Rad2Deg, a[1] * Mathf.Rad2Deg, a[2] * Mathf.Rad2Deg);
    }
    public void localEulerAngles(Vector3f a) {
        Quaternion q = Quaternion.IDENTITY;
        q.fromAngles(a.x, a.y, a.z);
        localRotation(q);
    }
    
    public Vector3f forward() {
        return transfromDirection(Utilities.forward());
    }
    public Vector3f back() {
        return transfromDirection(Utilities.back());
    }
    public Vector3f right() {
        return transfromDirection(Utilities.right());
    }
    public Vector3f left() {
        return transfromDirection(Utilities.left());
    }
    public Vector3f up() {
        return transfromDirection(Utilities.up());
    }
    public Vector3f down() {
        return transfromDirection(Utilities.down());
    }
    public Vector3f transfromDirection(Vector3f v) {
        Quaternion q = rotation();
        return q.mult(v);
    }
    
    public Vector3f lookDirection() {
        return forward();
    }
    public void lookDirection(Vector3f p) {
        lookAt(position().add(p));
    }
    
    public void lookAt(Transform t) {
        lookAt(t.position());
    }
    public void lookAt(Vector3f p) {
        lookAt(p, Utilities.up());
    }
    
    public void move(Vector3f direction, float speed) {
        this.move(direction.clone().mult(speed));
    }
    public void scaledMove(Vector3f direction) {
        move(direction, Time.deltaTime());
    }
    public void scaledMove(Vector3f direction, float speed) {
        move(direction, speed * Time.deltaTime());
    }
    
    @Override
    public Spatial rotate(float x, float y, float z) {
        return super.rotate(x * Mathf.Deg2Rad, y * Mathf.Deg2Rad, z * Mathf.Deg2Rad);
    }
}
