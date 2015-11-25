/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.simulation;

import com.jme3.math.Vector3f;

/**
 *
 * @author sietse
 */
public class Path {
    
    private Vector3f[] m_nodes = new Vector3f[0];
    private Vector3f m_previousPosition = Vector3f.ZERO;
    private int m_targetNode = -1;
    
    private boolean m_manual = false;
    private boolean m_useTimeInsteadOfSpeed = false;
    
    private float m_speed = 1.0f;
    private float m_waitTime = 0.0f;
    private LoopMode m_loopMode = LoopMode.Loop;
    private EaseType m_easeType = EaseType.Linear;
    private Callback m_callback = null;
    
    private float m_timer = 0.0f;
    private boolean m_goBack = false;
    
    
    /**
     * Use a null for default value
     * @param currentPosition
     * @param startNode
     * @param manual
     * @param useSpeed
     * @param speed
     * @param waitTime
     * @param loopMode
     * @param easeType
     * @param callback
     * @param nodes 
     */
    public Path(Vector3f currentPosition, Integer startNode, boolean manual, boolean useSpeed, float speed, Float waitTime, LoopMode loopMode, EaseType easeType, Callback callback, Vector3f... nodes) {
        init(currentPosition, startNode, manual, useSpeed, speed, waitTime, loopMode, easeType, callback, nodes);
    }
    
    /**
     * Constructor extention to reduce code
     * @param currentPosition
     * @param startNode
     * @param manual
     * @param useSpeed
     * @param speed
     * @param waitTime
     * @param loopMode
     * @param easeType
     * @param callback
     * @param nodes 
     */
    private void init(Vector3f currentPosition, Integer startNode, boolean manual, boolean useSpeed, float speed, Float waitTime, LoopMode loopMode, EaseType easeType, Callback callback, Vector3f... nodes) {
        
        this.m_manual = manual;
        this.m_useTimeInsteadOfSpeed = !useSpeed;
        this.m_speed = speed;
        this.m_waitTime = waitTime == null ? 0.0f : waitTime;
        this.m_loopMode = loopMode == null ? LoopMode.PingPong : loopMode;
        this.m_easeType = easeType == null ? EaseType.Linear : easeType;
        this.m_callback = callback;
        
        setPathf(new Vector3f(currentPosition == null ? (nodes.length < 1 ? Vector3f.ZERO : nodes[0]) : currentPosition), nodes);
        this.m_targetNode = startNode == null ? 0 : startNode;
    }
    
    public void setPath(Vector3f... nodes) {
        setPathf(getPosition(), nodes);
    }
    public void setPathf(Vector3f from, Vector3f... nodes) {
        setPosition(from);
        
        this.m_timer = 0.0f;
        this.m_goBack = false;
        this.m_targetNode = 0;
        
        // Clone nodes
        Vector3f[] __nodes = new Vector3f[nodes.length];
        for(int i = 0; i < nodes.length; i++)
            __nodes[i] = new Vector3f(nodes[i]);
        m_nodes = __nodes;
    }
    
    /**
     * Update this every frame
     */
    public void update() {
        if (m_timer < 1.0f) {
            m_timer += m_useTimeInsteadOfSpeed ? Time.deltaTime() / m_speed : Time.deltaTime() * Mathf.min(Utilities.NaNSafeFloat(m_speed / Utilities.distance(m_previousPosition, m_nodes[m_targetNode])), 1.0f);
            if (m_timer >= 1.0f && m_callback != null)
                m_callback.invoke();
        }
        else if (m_timer < 1.0f + m_waitTime) {
            m_timer += Time.deltaTime();
        }
        else {
            if (!m_manual) {
                next();
                m_timer -= (1.0f + m_waitTime);
            }
        }
    }
    /**
     * Set target to next point
     */
    public void next() {
        savePosition();
        
        if (m_manual)
            m_timer = 0.0f;
            
        if (m_nodes.length < 2)
            return;
        
        switch(m_loopMode) {
            case Loop: m_targetNode = (m_targetNode + 1) % m_nodes.length; break;
            case Once: if (m_targetNode < m_nodes.length - 1) m_targetNode++; break;
            case PingPong:
                if (m_goBack) {
                    if (--m_targetNode < 0) {
                        m_targetNode = 1;
                        m_goBack = false;
                    }
                }
                else if (++m_targetNode >= m_nodes.length) {
                    m_targetNode = m_nodes.length - 2;
                    m_goBack = true;
                }
                break;
        }
    }
    /**
     * Set target index
     * @param target 
     */
    public void setTarget(int target) {
        m_targetNode = target;
    }
    /**
     * safe position from interpolation
     */
    private void savePosition() {
        setPosition(getPosition());
    }
    /**
     * Get the current positio
     * @return 
     */
    public Vector3f getPosition() {
        return Interpolate.ease(m_easeType, m_previousPosition, m_nodes[m_targetNode], m_timer);
    }

    /**
     * Set position
     * @param position 
     */
    public void setPosition(Vector3f position) {
        m_previousPosition = position.clone();
    }
    /**
     * Set the callback at wait start
     * @param callback 
     */
    public void setCallback(Callback callback) {
        m_callback = callback;
    }
    public void setSpeed(float speed) {
        this.m_speed = speed;
    }
    public boolean atFirst() {
        return atFirst(0.001f);
    }
    public boolean atFirst(float range) {
        return (new Vector3f(m_nodes[0]).distanceSquared(getPosition()) < range * range);
    }
    public boolean atLast() {
        return atLast(0.001f);
    }
    public boolean atLast(float range) {
        return (new Vector3f(m_nodes[m_nodes.length - 1]).distanceSquared(getPosition()) < range * range);
    }
    public int getTargetIndex() {
        return m_targetNode;
    }
    public boolean finishedWaiting() {
        return m_timer >= 1.0f + m_waitTime;
    }
}
 
/*
 * public Path(float speed, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            false,  // manual
            true, // use Speed instead of time
            speed,  // speed
            null, // wait time
            null, // loop mode
            null, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(float speed, float waitTime, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            false,  // manual
            true, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            null, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(float speed, float waitTime, Callback callback, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            false,  // manual
            true, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            null, // ease type
            callback, // callback
            nodes // nodes
        );
    }
    public Path(float speed, boolean useSpeed, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            false,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            null, // wait time
            null, // loop mode
            null, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(float speed, boolean useSpeed, float waitTime, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            false,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            null, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(float speed, boolean useSpeed, Callback callback, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            false,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            null, // wait time
            null, // loop mode
            null, // ease type
            callback, // callback
            nodes // nodes
        );
    }
    public Path(float speed, boolean useSpeed, float waitTime, Callback callback, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            false,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            null, // ease type
            callback, // callback
            nodes // nodes
        );
    }
    public Path(float speed, boolean useSpeed, LoopMode loopmode, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            false,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            null, // wait time
            loopmode, // loop mode
            null, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(float speed, LoopMode loopmode, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            false,  // manual
            false, // use Speed instead of time
            speed,  // speed
            null, // wait time
            loopmode, // loop mode
            null, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(float speed, boolean useSpeed, EaseType easetype, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            false,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            null, // wait time
            null, // loop mode
            easetype, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(float speed, EaseType easetype, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            false,  // manual
            false, // use Speed instead of time
            speed,  // speed
            null, // wait time
            null, // loop mode
            easetype, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(float speed, boolean useSpeed, EaseType easetype, float waitTime, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            false,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            easetype, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(float speed, EaseType easetype, float waitTime, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            false,  // manual
            false, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            easetype, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(float speed, boolean useSpeed, EaseType easetype, float waitTime, Callback callback, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            false,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            easetype, // ease type
            callback, // callback
            nodes // nodes
        );
    }
    public Path(float speed, EaseType easetype, float waitTime, Callback callback, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            false,  // manual
            false, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            easetype, // ease type
            callback, // callback
            nodes // nodes
        );
    }
    public Path(float speed, boolean useSpeed, EaseType easetype, LoopMode loopmode, float waitTime, Callback callback, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            false,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            loopmode, // loop mode
            easetype, // ease type
            callback, // callback
            nodes // nodes
        );
    }
    public Path(float speed, EaseType easetype, LoopMode loopmode, float waitTime, Callback callback, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            false,  // manual
            false, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            loopmode, // loop mode
            easetype, // ease type
            callback, // callback
            nodes // nodes
        );
    }
    
    public Path(boolean manual, float speed, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            manual,  // manual
            true, // use Speed instead of time
            speed,  // speed
            null, // wait time
            null, // loop mode
            null, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(boolean manual, float speed, float waitTime, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            manual,  // manual
            true, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            null, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(boolean manual, float speed, float waitTime, Callback callback, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            manual,  // manual
            true, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            null, // ease type
            callback, // callback
            nodes // nodes
        );
    }
    public Path(boolean manual, float speed, boolean useSpeed, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            manual,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            null, // wait time
            null, // loop mode
            null, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(boolean manual, float speed, boolean useSpeed, float waitTime, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            manual,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            null, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(boolean manual, float speed, boolean useSpeed, Callback callback, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            manual,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            null, // wait time
            null, // loop mode
            null, // ease type
            callback, // callback
            nodes // nodes
        );
    }
    public Path(boolean manual, float speed, boolean useSpeed, float waitTime, Callback callback, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            manual,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            null, // ease type
            callback, // callback
            nodes // nodes
        );
    }
    public Path(boolean manual, float speed, boolean useSpeed, LoopMode loopmode, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            manual,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            null, // wait time
            loopmode, // loop mode
            null, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(boolean manual, float speed, LoopMode loopmode, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            manual,  // manual
            false, // use Speed instead of time
            speed,  // speed
            null, // wait time
            loopmode, // loop mode
            null, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(boolean manual, float speed, boolean useSpeed, EaseType easetype, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            manual,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            null, // wait time
            null, // loop mode
            easetype, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(boolean manual, float speed, EaseType easetype, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            manual,  // manual
            false, // use Speed instead of time
            speed,  // speed
            null, // wait time
            null, // loop mode
            easetype, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(boolean manual, float speed, boolean useSpeed, EaseType easetype, float waitTime, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            manual,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            easetype, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(boolean manual, float speed, EaseType easetype, float waitTime, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            manual,  // manual
            false, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            easetype, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(boolean manual, float speed, boolean useSpeed, EaseType easetype, float waitTime, Callback callback, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            manual,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            easetype, // ease type
            callback, // callback
            nodes // nodes
        );
    }
    public Path(boolean manual, float speed, EaseType easetype, float waitTime, Callback callback, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            manual,  // manual
            false, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            easetype, // ease type
            callback, // callback
            nodes // nodes
        );
    }
    public Path(boolean manual, float speed, boolean useSpeed, EaseType easetype, LoopMode loopmode, float waitTime, Callback callback, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            manual,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            loopmode, // loop mode
            easetype, // ease type
            callback, // callback
            nodes // nodes
        );
    }
    public Path(boolean manual, float speed, EaseType easetype, LoopMode loopmode, float waitTime, Callback callback, Vector3f... nodes) {
        init(
            null, // startPosition
            null, // startTarget
            manual,  // manual
            false, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            loopmode, // loop mode
            easetype, // ease type
            callback, // callback
            nodes // nodes
        );
    }
    
    public Path(Vector3f sp, float speed, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            false,  // manual
            true, // use Speed instead of time
            speed,  // speed
            null, // wait time
            null, // loop mode
            null, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, float speed, float waitTime, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            false,  // manual
            true, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            null, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, float speed, float waitTime, Callback callback, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            false,  // manual
            true, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            null, // ease type
            callback, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, float speed, boolean useSpeed, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            false,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            null, // wait time
            null, // loop mode
            null, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, float speed, boolean useSpeed, float waitTime, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            false,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            null, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, float speed, boolean useSpeed, Callback callback, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            false,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            null, // wait time
            null, // loop mode
            null, // ease type
            callback, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, float speed, boolean useSpeed, float waitTime, Callback callback, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            false,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            null, // ease type
            callback, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, float speed, boolean useSpeed, LoopMode loopmode, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            false,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            null, // wait time
            loopmode, // loop mode
            null, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, float speed, LoopMode loopmode, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            false,  // manual
            false, // use Speed instead of time
            speed,  // speed
            null, // wait time
            loopmode, // loop mode
            null, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, float speed, boolean useSpeed, EaseType easetype, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            false,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            null, // wait time
            null, // loop mode
            easetype, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, float speed, EaseType easetype, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            false,  // manual
            false, // use Speed instead of time
            speed,  // speed
            null, // wait time
            null, // loop mode
            easetype, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, float speed, boolean useSpeed, EaseType easetype, float waitTime, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            false,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            easetype, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, float speed, EaseType easetype, float waitTime, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            false,  // manual
            false, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            easetype, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, float speed, boolean useSpeed, EaseType easetype, float waitTime, Callback callback, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            false,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            easetype, // ease type
            callback, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, float speed, EaseType easetype, float waitTime, Callback callback, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            false,  // manual
            false, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            easetype, // ease type
            callback, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, float speed, boolean useSpeed, EaseType easetype, LoopMode loopmode, float waitTime, Callback callback, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            false,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            loopmode, // loop mode
            easetype, // ease type
            callback, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, float speed, EaseType easetype, LoopMode loopmode, float waitTime, Callback callback, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            false,  // manual
            false, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            loopmode, // loop mode
            easetype, // ease type
            callback, // callback
            nodes // nodes
        );
    }
    
    public Path(Vector3f sp, boolean manual, float speed, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            manual,  // manual
            true, // use Speed instead of time
            speed,  // speed
            null, // wait time
            null, // loop mode
            null, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, boolean manual, float speed, float waitTime, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            manual,  // manual
            true, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            null, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, boolean manual, float speed, float waitTime, Callback callback, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            manual,  // manual
            true, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            null, // ease type
            callback, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, boolean manual, float speed, boolean useSpeed, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            manual,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            null, // wait time
            null, // loop mode
            null, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, boolean manual, float speed, boolean useSpeed, float waitTime, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            manual,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            null, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, boolean manual, float speed, boolean useSpeed, Callback callback, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            manual,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            null, // wait time
            null, // loop mode
            null, // ease type
            callback, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, boolean manual, float speed, boolean useSpeed, float waitTime, Callback callback, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            manual,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            null, // ease type
            callback, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, boolean manual, float speed, boolean useSpeed, LoopMode loopmode, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            manual,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            null, // wait time
            loopmode, // loop mode
            null, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, boolean manual, float speed, LoopMode loopmode, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            manual,  // manual
            false, // use Speed instead of time
            speed,  // speed
            null, // wait time
            loopmode, // loop mode
            null, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, boolean manual, float speed, boolean useSpeed, EaseType easetype, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            manual,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            null, // wait time
            null, // loop mode
            easetype, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, boolean manual, float speed, EaseType easetype, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            manual,  // manual
            false, // use Speed instead of time
            speed,  // speed
            null, // wait time
            null, // loop mode
            easetype, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, boolean manual, float speed, boolean useSpeed, EaseType easetype, float waitTime, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            manual,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            easetype, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, boolean manual, float speed, EaseType easetype, float waitTime, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            manual,  // manual
            false, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            easetype, // ease type
            null, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, boolean manual, float speed, boolean useSpeed, EaseType easetype, float waitTime, Callback callback, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            manual,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            easetype, // ease type
            callback, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, boolean manual, float speed, EaseType easetype, float waitTime, Callback callback, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            manual,  // manual
            false, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            null, // loop mode
            easetype, // ease type
            callback, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, boolean manual, float speed, boolean useSpeed, EaseType easetype, LoopMode loopmode, float waitTime, Callback callback, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            manual,  // manual
            useSpeed, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            loopmode, // loop mode
            easetype, // ease type
            callback, // callback
            nodes // nodes
        );
    }
    public Path(Vector3f sp, boolean manual, float speed, EaseType easetype, LoopMode loopmode, float waitTime, Callback callback, Vector3f... nodes) {
        init(
            sp, // startPosition
            null, // startTarget
            manual,  // manual
            false, // use Speed instead of time
            speed,  // speed
            waitTime, // wait time
            loopmode, // loop mode
            easetype, // ease type
            callback, // callback
            nodes // nodes
        );
    }
    
 */