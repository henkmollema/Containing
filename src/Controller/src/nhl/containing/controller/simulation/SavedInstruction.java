/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.controller.simulation;

/**
 * Used for saving AGV move instructions when agvs are all busy
 * @author Niels
 */
public class SavedInstruction
{
    private Platform m_to;
    private Parkingspot m_spot;
    
    /**
     * Constuctor
     * @param to to platform
     * @param spot to parkingspot
     */
    public SavedInstruction(Platform to, Parkingspot spot){
        m_to = to;
        m_spot = spot;
    }
    
    /**
     * Gets the platform
     * @return platform
     */
    public Platform getPlatform(){
        return m_to;
    }
    
    /**
     * Gets the parkingspot
     * @return parkingspot
     */
    public Parkingspot getParkingspot(){
        return m_spot;
    } 
}
