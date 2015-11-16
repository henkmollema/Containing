/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation;

import com.jme3.math.Vector3f;

/**
 *
 * @author sietse
 */
public class Point3 {
    public int x;
    public int y;
    public int z;
    
    public static Point3 zero() {
        return new Point3(0, 0, 0);
    }
    public static Point3 one() {
        return new Point3(1, 1, 1);
    }
    public static Point3 up() {
        return new Point3(0, 1, 0);
    }
    public static Point3 down() {
        return new Point3(0, -1, 0);
    }
    public static Point3 left() {
        return new Point3(-1, 0, 0);
    }
    public static Point3 right() {
        return new Point3(1, 0, 0);
    }
    public static Point3 forward() {
        return new Point3(0, 0, 1);
    }
    public static Point3 backward() {
        return new Point3(0, 0, -1);
    }
    
    
    /**
     * Constructor
     */
    public Point3() {
        x = y = z = 0;
    }
    /**
     * Constructor
     * @param x
     * @param y
     * @param z 
     */
    public Point3(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    /**
     * Constructor
     * @param copy 
     */
    public Point3(Point3 copy) {
        x = copy.x;
        y = copy.y;
        z = copy.z;
    }
    /**
     * Constructor
     * @param v 
     */
    public Point3(Vector3f v) {
        this.x = Mathf.round2Int(v.x);
        this.y = Mathf.round2Int(v.y);
        this.z = Mathf.round2Int(v.z);
    }
    /**
     * Constructor
     * @param v
     * @param round 
     */
    public Point3(Vector3f v, boolean round) {
        if (round) {
            this.x = Mathf.round2Int(v.x);
            this.y = Mathf.round2Int(v.y);
            this.z = Mathf.round2Int(v.z);
            return;
        }
        this.x = (int)v.x;
        this.y = (int)v.y;
        this.z = (int)v.z;
    }
    
    /**
     * 
     * @return 
     */
    public Vector3f toVector3f() {
        return new Vector3f(x, y, z);
    }
    
    /**
     * 
     * @return 
     */
    public int sqrmagnitude() {
        return (x * x + y * y + z * z);
    }
    /**
     * 
     * @return 
     */
    public float sqrmagnitudef() {
        return sqrmagnitude();
    }
    /**
     * 
     * @return 
     */
    public float magnitude() {
        return Mathf.sqrt(sqrmagnitudef());
    }
    
    /**
     * 
     * @param v 
     */
    public void add(Point3 v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
    }
    /**
     * 
     * @param v 
     */
    public void sbstract(Point3 v) {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;
    }
    /**
     * 
     * @param v 
     */
    public void scale(Point3 v) {
        this.x *= v.x;
        this.y *= v.y;
        this.z *= v.z;
    }
    /**
     * 
     * @param i 
     */
    public void multiply(int i) {
        this.x *= i;
        this.y *= i;
        this.z *= i;
    }
    /**
     * 
     * @param i 
     */
    public void devide(int i) {
        this.x /= i;
        this.y /= i;
        this.z /= i;
    }
    
    /**
     * 
     * @param a
     * @param b
     * @return 
     */
    public static Point3 sum(Point3 a, Point3 b) {
        return new Point3(
            a.x + b.x,
            a.y + b.y,
            a.z + b.z);
    }
    /**
     * 
     * @param a
     * @param b
     * @return 
     */
    public static Point3 substract(Point3 a, Point3 b) {
        return new Point3(
            a.x - b.x,
            a.y - b.y,
            a.z - b.z);
    }
    /**
     * 
     * @param a
     * @param b
     * @return 
     */
    public static Point3 scale(Point3 a, Point3 b) {
        return new Point3(
            a.x * b.x,
            a.y * b.y,
            a.z * b.z);
    }
    /**
     * 
     * @param a
     * @param b
     * @return 
     */
    public static Point3 multiply (Point3 a, int b) {
        return new Point3(
            a.x * b,
            a.y * b,
            a.z * b);
    }
    /**
     * 
     * @param a
     * @param b
     * @return 
     */
    public static Point3 devide (Point3 a, int b) {
        return new Point3(
            a.x / b,
            a.y / b,
            a.z / b);
    }
    
}
