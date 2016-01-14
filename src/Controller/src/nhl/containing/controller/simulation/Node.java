package nhl.containing.controller.simulation;

import java.util.List;
import nhl.containing.controller.Vector2f;

/**
 * Represents a node in the simulation.
 *
 * @author Niels
 */
public class Node
{
    public final int m_id;
    public final Vector2f m_position;
    public final int[] m_connections;

    /**
     * Constructor
     *
     * @param id id of node
     * @param position position of node
     * @param connections connections of node
     */
    public Node(int id, Vector2f position, List<Integer> connections)
    {
        m_id = id;
        m_position = position;
        m_connections = ListToArray(connections);
    }

    /**
     * Constructor
     *
     * @param id id of node
     * @param x x position of node
     * @param y y position of node
     * @param connections connections of node
     */
    public Node(int id, float x, float y, List<Integer> connections)
    {
        this(id, new Vector2f(x, y), connections);
    }

    /**
     * Makes an int array of a list of Integers
     *
     * @param list list
     * @return array
     */
    private int[] ListToArray(List<Integer> list)
    {
        int[] array = new int[list.size()];
        for (int i = 0; i < list.size(); i++)
        {
            array[i] = list.get(i);
        }
        return array;
    }
}
