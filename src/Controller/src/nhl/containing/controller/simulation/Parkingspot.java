/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.controller.simulation;

import nhl.containing.controller.Vector3f;

/**
 * Parkingspot class
 * @author Niels
 */
public final class Parkingspot
{
    private final long m_id;
    private Platform m_parent;
    private Vector3f m_position;
    private final int m_arrivalNodeID;
    private final int m_departNodeID;
    private AGV m_agv = null;
    
    /**
     * Constructor
     * @param id id of parkingspot
     * @param position position of parkingspot
     * @param arrival id of arrival node
     * @param depart id of depart node
     */
    public Parkingspot(long id,Vector3f position,int arrival,int depart){
        m_id = id;
        m_position = position;
        m_arrivalNodeID = arrival;
        m_departNodeID = depart;
    }
    
    /**
     * Gets the arrival node ID
     * @return 
     */
    public int getArrivalNodeID(){
        return m_arrivalNodeID;
    }
    
    /**
     * Gets the depart node ID
     * @return 
     */
    public int getDepartNodeID(){
        return m_departNodeID;
    }
    
    /**
     * Sets the parent platform of the parkingspot
     * @param parent parent
     */
    public void setParent(Platform parent){
        m_parent = parent;
    }
    
    /**
     * Gets the parent platform of the parkingspot
     * @return parent
     */
    public Platform getPlatform(){
        return m_parent;
    }
    
    /**
     * Get id
     * @return id
     */
    public long getId(){
        return m_id;
    }
    
    /**
     * Get position
     * @return position
     */
    public Vector3f getPosition(){
        return m_position;
    }
    
    /**
     * Sets an AGV on the parkingspot
     * @param agv AGV
     * @throws Exception when already occupied
     */
    public void setAGV(AGV agv) throws Exception{
        if(hasAGV())
            throw new Exception("Has already an AGV");
        m_agv = agv;
    }
    
    /**
     * Remove an AGV
     */
    public void removeAGV(){
        m_agv = null;
    }
    
    /**
     * Gives the AGV on the parkingspot
     * @return AGV
     */
    public AGV getAGV(){
        return m_agv;
    }
    
    /**
     * Checks if occupied
     * @return true wanneer occupied, otherwise false
     */
    public boolean hasAGV(){
        return m_agv != null;
    }
    
}
