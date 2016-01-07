/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.controller.simulation;

/**
 * Lorry platform class
 * @author Niels
 */
public class LorryPlatform extends Platform
{
    private Shipment m_Shipment = null;
    
    /**
     * Constructor
     * @param id id of platform
     */
    public LorryPlatform(int id){
        super(id);
    }
    
     /**
     * Sets a shipment to the lorry platform
     * @param shipment shipment
     */
    public void setShipment(Shipment shipment){
        m_Shipment = shipment;
    }
    
    /**
     * Unsets a shipment
     */
    public void unsetShipment(){
        m_Shipment = null;
    }
    
    /**
     * Checks if platform has a shipment
     * @return true when has shipment, otherwise false
     */
    public boolean hasShipment(){
        return m_Shipment != null;
    }
    
    /**
     * Gets the shipment of the lorry
     * @return shipment
     */
    public Shipment getShipment(){
        return m_Shipment;
    } 
    

}
