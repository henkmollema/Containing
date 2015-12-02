/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.simulation;
import com.jme3.math.Vector3f;

/**
 * 
 * @author Jens
 */
public class Line3DNode {
    public Vector3f position    = Vector3f.ZERO;
    public float width          = 1f;
    
    public Line3DNode (Vector3f position, float width) {
        this.position = position.clone();
        this.width = width;
    }
    
}
