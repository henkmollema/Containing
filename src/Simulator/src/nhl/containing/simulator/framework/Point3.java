/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.framework;

import com.jme3.math.Vector3f;

/**
 *
 * @author sietse
 */
public class Point3 {
    public int x;
    public int y;
    public int z;
    
    /**
     * [0, 0, 0]
     * @return 
     */
    public static Point3 zero() {
        return new Point3(0, 0, 0);
    }
    /**
     * [1, 1, 1]
     * @return 
     */
    public static Point3 one() {
        return new Point3(1, 1, 1);
    }
    /**
     * [1, 1, 1]
     * @return 
     */
    public static Point3 onei() {
        return new Point3(-1, -1, -1);
    }
    /**
     * [0, 1, 0]
     * @return 
     */
    public static Point3 up() {
        return new Point3(0, 1, 0);
    }
    /**
     * [0, -1, 0]
     * @return 
     */
    public static Point3 down() {
        return new Point3(0, -1, 0);
    }
    /**
     * [-1, 0, 0]
     * @return 
     */
    public static Point3 left() {
        return new Point3(-1, 0, 0);
    }
    /**
     * [1, 0, 0]
     * @return 
     */
    public static Point3 right() {
        return new Point3(1, 0, 0);
    }
    /**
     * [0, 0, 1]
     * @return 
     */
    public static Point3 forward() {
        return new Point3(0, 0, 1);
    }
    /**
     * [0, 0, -1]
     * @return 
     */
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
     * Converts to vector3f
     * @return 
     */
    public Vector3f toVector3f() {
        return new Vector3f(x, y, z);
    }
    
    /**
     * magnitude of point squared (integer)
     * @return 
     */
    public int sqrmagnitude() {
        return (x * x + y * y + z * z);
    }
    /**
     * magnitude of point squared (float)
     * @return 
     */
    public float sqrmagnitudef() {
        return sqrmagnitude();
    }
    /**
     * magnitude of point
     * @return 
     */
    public float magnitude() {
        return Mathf.sqrt(sqrmagnitudef());
    }
    /**
     * current + up
     * @return 
     */
    public Point3 above() {
        return new Point3(this.x, this.y + 1, this.z);
    }
    /**
     * current - up (current + down)
     * @return 
     */
    public Point3 below() {
        return new Point3(this.x, this.y - 1, this.z);
    }
    /**
     * adds point to point
     * @param v 
     */
    public Point3 add(Point3 v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
        return this;
    }
    /**
     * substracts
     * @param v 
     */
    public Point3 sbstract(Point3 v) {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;
        return this;
    }
    /**
     * multiplies point by point
     * @param v 
     */
    public Point3 scale(Point3 v) {
        this.x *= v.x;
        this.y *= v.y;
        this.z *= v.z;
        return this;
    }
    /**
     * multiplie
     * @param i 
     */
    public Point3 multiply(int i) {
        this.x *= i;
        this.y *= i;
        this.z *= i;
        return this;
    }
    /**
     * devide
     * @param i 
     */
    public Point3 devide(int i) {
        this.x /= i;
        this.y /= i;
        this.z /= i;
        return this;
    }
    
    /**
     * sums two points
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
     * substracts
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
     * multiplies two points
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
     * multiplie
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
     * devide
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
    
    /**
     * Convert to readable string
     * @return 
     */
    @Override
    public String toString() {
        return ("[" + x + ", " + y + ", " + z + "]");
    }
}
