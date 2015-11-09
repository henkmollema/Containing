/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation;

import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sietse
 */
public class Path {
    
    private List<Vector3f> m_nodes = new ArrayList<Vector3f>();
    private int m_currentNode = -1;
    
    private float m_speed = 1.0f;
    private float m_waitTime = 0.0f;
    private LoopMode m_loopMode = LoopMode.Loop;
    
    public Path(List<Vector3f> nodes) {
        this.m_nodes = nodes;
    }
    public Path(List<Vector3f> nodes, float speed) {
        this.m_nodes = nodes;
        this.m_speed = speed;
    }
    public Path(List<Vector3f> nodes, float speed, LoopMode loopMode) {
        this.m_nodes = nodes;
        this.m_speed = speed;
        this.m_loopMode = loopMode;
    }
    public Path(List<Vector3f> nodes, float speed, int startNode, LoopMode loopMode) {
        this.m_nodes = nodes;
        this.m_currentNode = startNode;
        this.m_speed = speed;
        this.m_loopMode = loopMode;
    }
    public Path(List<Vector3f> nodes, float speed, float waitTime, int startNode, LoopMode loopMode) {
        this.m_nodes = nodes;
        this.m_currentNode = startNode;
        this.m_speed = speed;
        this.m_loopMode = loopMode;
    }
}
 