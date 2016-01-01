/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.controller.simulation;

import java.util.List;
import nhl.containing.controller.Vector2f;

/**
 *
 * @author Niels
 */
public class Node
{
    private final int m_id;
    private final Vector2f m_position;
    private final int [] m_connections;
    
    public Node(int id,Vector2f position,List<Integer> connections){
        m_id = id;
        m_position = position;
        m_connections = ListToArray(connections);
    }
    
    public Node(int id, float x, float y, List<Integer> connections){
        this(id,new Vector2f(x, y),connections);
    }
    
    private int [] ListToArray(List<Integer> list){
        int[] array = new int[list.size()];
        for(int i = 0; i < list.size();i++){
            array[i] = list.get(i);
        }
        return array;
    }
}
