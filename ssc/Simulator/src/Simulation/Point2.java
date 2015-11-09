/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

/**
 *
 * @author sietse
 */
public class Point2 {
    public int x;
    public int y;
    
    public Point2() {
        x = 0;
        y = 0;
    }
    public Point2(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Point2(Point2 copy) {
        x = copy.x;
        y = copy.y;
    }
    public Point2(Vector2f v) {
        this.x = Mathf.round2Int(v.x);
        this.x = Mathf.round2Int(v.y);
    }
    public Point2(Vector2f v, boolean round) {
        if (round) {
            this.x = Mathf.round2Int(v.x);
            this.x = Mathf.round2Int(v.y);
            return;
        }
        this.x = (int)v.x;
        this.x = (int)v.y;
    }
    
    public Vector2f toVector2f() {
        return new Vector2f(x, y);
    }
    public Vector3f toVector3f() {
        return new Vector3f(x, y, 0);
    }
    
    public float sqrmagnitude() {
        return (x * x + y * y);
    }
    public float magnitude() {
        return Mathf.sqrt(sqrmagnitude());
    }
}
