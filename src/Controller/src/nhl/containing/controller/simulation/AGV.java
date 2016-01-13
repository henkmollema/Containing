package nhl.containing.controller.simulation;

/**
 * AGV Class
 * @author Niels
 */
public class AGV
{
    private final int m_id;
    private ShippingContainer m_container = null;
    private boolean m_isWaiting = true;
    private int m_node = 20;
    
    /**
     * Constructor
     * @param id id of the AGV
     */
    public AGV(int id){
        m_id = id;
    }
    
    /**
     * sets the current node id
     * @param nodeid the id of the node
     */
    public void setNodeID(int nodeid){
        m_node = nodeid;
    }
    
    /**
     * Gets the current node id
     * @return 
     */
    public int getNodeID(){
        return m_node;
    }
    
    /**
     * Sets a container on an AGV
     * @param container container
     * @throws Exception when there is already a container
     */
    public void setContainer(ShippingContainer container) throws Exception{
        if(hasContainer())
            throw new Exception("Has already a container");
        m_container = container;
    }
    
    /**
     * Unsets a container
     */
    public void unsetContainer(){
        m_container = null;
    }
    
    /**
     * Gets the container
     * @return container when there is a container, otherwise null
     */
    public ShippingContainer getContainer(){
        return m_container;
    }
    
    /**
     * Checks if AGV has a container
     * @return true when there is a container, otherwise false
     */
    public boolean hasContainer(){
        return m_container != null;
    }
    
    /**
     * Sets the AGV to busy
     */
    public void setBusy(){
        m_isWaiting = false;
    }
    
    /**
     * Stops an AGV
     */
    public void stop(){
        m_isWaiting = true;
    }
    
    /**
     * Checks if AGV is moving
     * @return true when moving, otherwise false
     */
    public boolean isBusy(){
        return !m_isWaiting;
    }
    
    /**
     * Returns the ID of the AGV
     * @return ID
     */
    public int getID(){
        return m_id;
    }
}
