/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.controller;

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
    
    @Override
    public boolean equals(Object obj)
    {
        Point3 thisPoint3 = (Point3)this;
        Point3 otherPoint3 = (Point3)obj;
        return thisPoint3.x == otherPoint3.x && thisPoint3.y == otherPoint3.y && thisPoint3.z == otherPoint3.z;
    }
    
    /**
     * Constructor
     * @param v 
     */
    public Point3(Vector3f v) {
        this.x = (int) Math.round((double) v.x);
        this.y = (int) Math.round((double) v.y);
        this.z = (int) Math.round((double) v.z);
    }
    /**
     * Constructor
     * @param v
     * @param round 
     */
    public Point3(Vector3f v, boolean round) {
        if (round) {
            this.x = (int) Math.round((double) v.x);
            this.y = (int) Math.round((double) v.y);
            this.z = (int) Math.round((double) v.z);
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
        return (float) Math.sqrt((double) sqrmagnitudef());
    }
    /**
     * 
     * @return 
     */
    public Point3 above() {
        return new Point3(this.x, this.y + 1, this.z);
    }
    /**
     * 
     * @return 
     */
    public Point3 below() {
        return new Point3(this.x, this.y - 1, this.z);
    }
    /**
     * 
     * @param v 
     */
    public Point3 add(Point3 v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
        return this;
    }
    /**
     * 
     * @param v 
     */
    public Point3 sbstract(Point3 v) {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;
        return this;
    }
    /**
     * 
     * @param v 
     */
    public Point3 scale(Point3 v) {
        this.x *= v.x;
        this.y *= v.y;
        this.z *= v.z;
        return this;
    }
    /**
     * 
     * @param i 
     */
    public Point3 multiply(int i) {
        this.x *= i;
        this.y *= i;
        this.z *= i;
        return this;
    }
    /**
     * 
     * @param i 
     */
    public Point3 devide(int i) {
        this.x /= i;
        this.y /= i;
        this.z /= i;
        return this;
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
    
    @Override
    public String toString() {
        return ("[" + x + ", " + y + ", " + z + "]");
    }
}
