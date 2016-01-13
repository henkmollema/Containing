package nhl.containing.simulator.framework;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

/**
 * Vector2f only then in int's
 * So an X anb Y axis in int's
 * 
 * @author sietse
 */
public class Point2 {
    public int x;
    public int y;
    
    /**
     * [0, 0]
     * @return 
     */
    public static Point2 zero() {
        return new Point2(0, 0);
    }
    /**
     * [1, 1]
     * @return 
     */
    public static Point2 one() {
        return new Point2(1, 1);
    }
    /**
     * [0, 1]
     * @return 
     */
    public static Point2 up() {
        return new Point2(0, 1);
    }
    /**
     * [0, -1]
     * @return 
     */
    public static Point2 down() {
        return new Point2(0, -1);
    }
    /**
     * [-1, 0]
     * @return 
     */
    public static Point2 left() {
        return new Point2(-1, 0);
    }
    /**
     * [1, 0]
     * @return 
     */
    public static Point2 right() {
        return new Point2(1, 0);
    }
    
    /**
     * Constructor
     * Point2.zero()
     */
    public Point2() {
        x = 0;
        y = 0;
    }
    /**
     * Constructor
     * @param x X value
     * @param y Y value
     */
    public Point2(int x, int y) {
        this.x = x;
        this.y = y;
    }
    /**
     * Constructor
     * @param copy create copy of
     */
    public Point2(Point2 copy) {
        x = copy.x;
        y = copy.y;
    }
    /**
     * Constructor
     * @param v CreateCopy of rounded Vector
     */
    public Point2(Vector2f v) {
        this.x = Mathf.round2Int(v.x);
        this.y = Mathf.round2Int(v.y);
    }
    /**
     * Constructor
     * @param v Create copy of vector
     * @param round Round the floats
     */
    public Point2(Vector2f v, boolean round) {
        if (round) {
            this.x = Mathf.round2Int(v.x);
            this.y = Mathf.round2Int(v.y);
            return;
        }
        this.x = (int)v.x;
        this.y = (int)v.y;
    }
    
    /**
     * Convert Point to vector
     * @return 
     */
    public Vector2f toVector2f() {
        return new Vector2f(x, y);
    }
    /**
     * Convert point to vector
     * @return 
     */
    public Vector3f toVector3f() {
        return new Vector3f(x, y, 0);
    }
    
    /**
     * The square magnitude of point as vector
     * @return 
     */
    public int sqrmagnitude() {
        return (x * x + y * y);
    }
    /**
     * The square magnitude of qoint as vector
     * @return 
     */
    public float sqrmagnitudef() {
        return sqrmagnitude();
    }
    /**
     * The magnitude of point as vector
     * @return 
     */
    public float magnitude() {
        return Mathf.sqrt(sqrmagnitudef());
    }
    
    /**
     * Add vector to the current one
     * @param v 
     */
    public void add(Point2 v) {
        this.x += v.x;
        this.y += v.y;
    }
    /**
     * Substract vector from the current one
     * @param v 
     */
    public void sbstract(Point2 v) {
        this.x -= v.x;
        this.y -= v.y;
    }
    /**
     * Scale
     * @param v 
     */
    public void scale(Point2 v) {
        this.x *= v.x;
        this.y *= v.y;
    }
    /**
     * multiply
     * @param i 
     */
    public void multiply(int i) {
        this.x *= i;
        this.y *= i;
    }
    /**
     * devide
     * @param i 
     */
    public void devide(int i) {
        this.x /= i;
        this.y /= i;
    }
    
    /**
     * Sum
     * @param a
     * @param b
     * @return 
     */
    public static Point2 sum(Point2 a, Point2 b) {
        return new Point2(
            a.x + b.x,
            a.y + b.y);
    }
    /**
     * Substract
     * @param a
     * @param b
     * @return 
     */
    public static Point2 substract(Point2 a, Point2 b) {
        return new Point2(
            a.x - b.x,
            a.y - b.y);
    }
    /**
     * Scale
     * @param a
     * @param b
     * @return 
     */
    public static Point2 scale(Point2 a, Point2 b) {
        return new Point2(
            a.x * b.x,
            a.y * b.y);
    }
    /**
     * Multiply
     * @param a
     * @param b
     * @return 
     */
    public static Point2 multiply (Point2 a, int b) {
        return new Point2(
            a.x * b,
            a.y * b);
    }
    /**
     * Devide
     * @param a
     * @param b
     * @return 
     */
    public static Point2 devide (Point2 a, int b) {
        return new Point2(
            a.x / b,
            a.y / b);
    }
    
    /**
     * Converts to readable string
     * @return 
     */
    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }
}
