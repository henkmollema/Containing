package nhl.containing.simulator.framework;

import com.jme3.math.Vector3f;

/**
 * The class for the Path
 * @author sietse
 */
public class Path {
    
    // Main
    public Vector3f[] m_nodes = new Vector3f[0];            // Path node positions
    public Vector3f m_previousPosition = Utilities.zero();  // Previous position, used for switching between nodes
    public int m_targetNode = 0;                            // Target node
    
    // Settings
    public boolean m_manual = false;                        // Manual update
    public boolean m_useTimeInsteadOfSpeed = false;         // true -> Use time based | false -> Use speed based
    
    // Behaviours
    public float m_speed = 1.0f;                            // Speed
    public float m_waitTime = 0.0f;                         // Wait time at node
    public LoopMode m_loopMode = LoopMode.Loop;             // Loop mode
    public EaseType m_easeType = EaseType.Linear;           // Ease type (interpolation type)
    public Callback m_callback = null;                      // Callback at node
    
    // Other
    private float m_timer = 0.0f;                           // Move timer
    private boolean m_goBack = false;                       // Go inverse direction
    
    
    /**
     * Set path
     * @param nodes 
     */
    public void setPath(Vector3f... nodes) {
        setPathf(getPosition(), nodes);
    }
    /**
     * Set path raw
     * @param from
     * @param nodes 
     */
    public void setPathf(Vector3f from, Vector3f... nodes) {
        setPosition(from);
        
        // Reset
        this.m_timer = 0.0f;
        this.m_goBack = false;
        this.m_targetNode = 0;
        
        // Clone nodes
        Vector3f[] __nodes = new Vector3f[nodes.length];
        for(int i = 0; i < nodes.length; i++)
            __nodes[i] = new Vector3f(nodes[i]);
        
        // Set
        m_nodes = __nodes;
    }
    
    /**
     * Update this every frame
     */
    public void update() {
        if (m_nodes.length < 1)
            return;
        
        if (m_timer < 1.0f) { // Stage 1: move
            
            // Raise timer (by time or speed)
            m_timer += m_useTimeInsteadOfSpeed ? Time.deltaTime() / m_speed : Time.deltaTime() * Mathf.min(Utilities.NaNSafeFloat(m_speed / Utilities.distance(m_previousPosition, m_nodes[m_targetNode])), 1.0f);
            
            // Timer finished
            if (m_timer >= 1.0f && m_callback != null)
                m_callback.invoke();
        }
        else if (m_timer < 1.0f + m_waitTime) { // Stage 2: wait
            m_timer += Time.deltaTime();
        }
        else {
            if (!m_manual) { // Stage 3: to next
                next();
                m_timer -= (1.0f + m_waitTime);
            }
        }
    }
    /**
     * Set target to next point
     */
    public void next() {
        
        // override previous position with current position
        savePosition();
        
        if (m_manual)
            m_timer = 0.0f;
        
        if (m_nodes.length < 2)
            return;
        
        switch(m_loopMode) {
            case Loop: // Loop index
                m_targetNode = (m_targetNode + 1) % m_nodes.length; 
                break;
            case Once: // Stop when at last node
                if (m_targetNode < m_nodes.length - 1) 
                    m_targetNode++; 
                break;
            case PingPong: // Pingpong index
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
        if (m_targetNode < 0 || m_targetNode >= m_nodes.length)
            return new Vector3f(m_previousPosition);
        
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
    /**
     * Set speed
     * @param speed 
     */
    public void setSpeed(float speed) {
        this.m_speed = speed;
    }
    /**
     * Is at first node
     * @return 
     */
    public boolean atFirst() {
        return atFirst(0.001f);
    }
    /**
     * Is at first node
     * @param range
     * @return 
     */
    public boolean atFirst(float range) {
        if (m_nodes.length < 1)
            return true;
        
        return (new Vector3f(m_nodes[0]).distanceSquared(getPosition()) < range * range);
    }
    /**
     * Is at last node
     * @return 
     */
    public boolean atLast() {
        return atLast(0.001f);
    }
    /**
     * Is at last node
     * @param range
     * @return 
     */
    public boolean atLast(float range) {
        if (m_nodes.length < 1)
            return true;
        if(targetIsLast())
            return m_timer >= 1.0f;
        return false;
        
        //return (new Vector3f(m_nodes[m_nodes.length - 1]).distanceSquared(getPosition()) < range * range);
    }
    
    /**
     * Get the target position
     * @return 
     */
    public Vector3f getTargetPosition() {
        if (m_nodes == null || m_targetNode < 0 || m_targetNode >= m_nodes.length)
            return null;
        return m_nodes[m_targetNode].clone();
    }
    /**
     * Get position in index
     * @param index
     * @return 
     */
    public Vector3f getPosition(int index) {
        return m_nodes[index].clone();
    }
    /**
     * Get current target node
     * @return 
     */
    public int getTargetIndex() {
        return m_targetNode;
    }
    /**
     * Check if target is last target in the array
     * @return 
     */
    public boolean targetIsLast() {
        return (m_targetNode == (m_nodes.length - 1));
    }
    /**
     * Finished waitng
     * @return 
     */
    public boolean finishedWaiting() {
        return m_timer >= 1.0f + m_waitTime;
    }
}