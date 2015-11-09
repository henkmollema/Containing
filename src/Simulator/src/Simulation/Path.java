/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation;

import Utilities.Utilities;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sietse
 */
public class Path {
    
    private List<Vector3f> m_nodes = new ArrayList<Vector3f>();
    private Vector3f m_previousPosition = Vector3f.ZERO;
    private int m_targetNode = -1;
    
    private float m_speed = 1.0f;
    private float m_waitTime = 0.0f;
    private LoopMode m_loopMode = LoopMode.Loop;
    private EaseType m_easeType = EaseType.Linear;
    
    private float m_timer = 0.0f;
    private boolean m_goBack = false;
    
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
    public Path(List<Vector3f> nodes, float speed, int startTargetNode, LoopMode loopMode) {
        this.m_nodes = nodes;
        this.m_targetNode = startTargetNode;
        this.m_speed = speed;
        this.m_loopMode = loopMode;
    }
    public Path(List<Vector3f> nodes, float speed, float waitTime, int startTargetNode, LoopMode loopMode) {
        this.m_nodes = nodes;
        this.m_targetNode = startTargetNode;
        this.m_speed = speed;
        this.m_waitTime = waitTime;
        this.m_loopMode = loopMode;
    }
    public Path(List<Vector3f> nodes, float speed, int startTargetNode, LoopMode loopMode, EaseType easeType) {
        this.m_nodes = nodes;
        this.m_targetNode = startTargetNode;
        this.m_speed = speed;
        this.m_loopMode = loopMode;
        this.m_easeType = easeType;
    }
    public Path(List<Vector3f> nodes, float speed, float waitTime, int startTargetNode, LoopMode loopMode, EaseType easeType) {
        this.m_nodes = nodes;
        this.m_targetNode = startTargetNode;
        this.m_speed = speed;
        this.m_waitTime = waitTime;
        this.m_loopMode = loopMode;
        this.m_easeType = easeType;
    }
    
    
    public void update() {
        if (m_timer < 1.0f)
            m_timer += Time.deltaTime() * Mathf.min(Utilities.NaNSafeFloat(m_speed / Utilities.distance(m_previousPosition, m_nodes.get(m_targetNode))), 100000.0f);
        else if (m_timer < 1.0f + m_waitTime)
            m_timer += Time.deltaTime();
        else {
            next();
            m_timer -= 1.0f + m_waitTime;
        }
    }
    public void next() {
        savePosition();
        
        if (m_nodes.size() < 2)
            return;
        
        switch(m_loopMode) {
            case Loop: m_targetNode = (m_targetNode + 1) % m_nodes.size(); break;
            case Once: if (m_targetNode < m_nodes.size() - 1) m_targetNode++; break;
            case PingPong:
                if (m_goBack) {
                    if (--m_targetNode < 0) {
                        m_targetNode = 1;
                        m_goBack = false;
                    }
                }
                else if (++m_targetNode >= m_nodes.size()) {
                    m_targetNode = m_nodes.size() - 2;
                    m_goBack = true;
                }
                break;
        }
    }
    public void setTarget(int target) {
        m_targetNode = target;
    }
    private void savePosition() {
        setPosition(getPosition());
    }
    public Vector3f getPosition() {
        return Interpolate.ease(m_easeType, m_previousPosition, m_nodes.get(m_targetNode), m_timer);
    }

    public void setPosition(Vector3f position) {
        m_previousPosition = position.clone();
    }
}
 