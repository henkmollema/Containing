package nhl.containing.simulator.simulation;
import com.jme3.math.Vector3f;
import nhl.containing.simulator.framework.Utilities;

/**
 * Node data for line renderer
 * @author Jens
 */
public class Line3DNode {
    public Vector3f position    = Utilities.zero();
    public float width          = 1f;
    
    /**
     * Constructor
     * @param position
     * @param width 
     */
    public Line3DNode (Vector3f position, float width) {
        this.position = position.clone();
        this.width = width;
    }
    
}
