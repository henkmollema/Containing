package nhl.containing.controller;

import java.awt.Dimension;
import java.awt.Point;
import java.util.List;
import nhl.containing.controller.simulation.Node;

/**
 * Class with native methods for path finding.
 * 
 * @author Dudecake
 */
public class PathFinder
{
    private int number = 88;
    
    public int getNumber()
    {
        return number;
    }
    /**
     * initialises the roadmap with the given Dimension.
     * be sure to call PathFinder.cleanup() before shutting down
     * @param dimension the size of the roadmap
     * @throws IllegalArgumentException when the dimension is null
     * @throws IllegalStateException when this method is called more than once
     */
    public static native void initPath(Dimension dimension) throws IllegalArgumentException, IllegalStateException;
    /**
     * initialises the roadmap with the given Array of Nodes
     * be sure to call PathFinder.cleanup() before shutting down
     * @param nodes the Array of Nodes to be converted to the roadmap
     * @throws IllegalArgumentException when the Array is empty
     * @throws IllegalStateException when this method is called more than once
     */
    public static native void initPath(Node[] nodes) throws IllegalArgumentException, IllegalStateException;
    /**
     * initialises the roadmap with the given List of Nodes.
     * is essentially the same as PathFinder.initPath(nodes.toArray(new Node[nodes.size()]));
     * be sure to call PathFinder.cleanup() before shutting down
     * @param nodes the List of Nodes to be converted to the roadmap
     * @throws IllegalArgumentException when the List is empty
     * @throws IllegalStateException when this method is called more than once
     */
    public static void initPath(List<Node> nodes) throws IllegalArgumentException, IllegalStateException
    {
        initPath(nodes.toArray(new Node[nodes.size()]));
    }
    /**
     * finds the path from the node with the given id of the origin to the id of the destination
     * @param from the id of the origin
     * @param to the id of the destination
     * @return
     * @throws IllegalArgumentException when the origin or destenation id are not in the roadmap
     */
    public static int[] getPath(int from, int to) throws IllegalArgumentException
    {
        return getPath(from, to, 5.0f);
    }
    
    private static native int[] getPath(int from, int to, float speed) throws IllegalArgumentException;
    
    public static native void setOccupied(int originId, boolean occupied);
        
    public static native void setOccupied(Point loc, boolean occupied) throws IllegalArgumentException;
    
    public static native void setOccupied(int orgx, int orgy, boolean occupied);
    
    public static native boolean getOccupied(Point loc) throws IllegalArgumentException;;
    /*
    public static native void helloFromC();
    
    public static native double avgFromC(int[] x);
    
    public static native int intFromC(int[] x);
    
    public static native Integer integerFromC(int x);
    
    public static native Point pointInC(int x, int y);
    
    public native void changeNumberInC();
    */
    /**
     * removes the raodmap
     */
    public static native void cleanup();
}
