/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.controller.simulation;

import java.util.ArrayList;
import java.util.List;

/**
 * Platform class
 * @author Niels
 */
public class Platform
{
    protected final int m_id;
    protected List<Parkingspot> m_parkingspots = new ArrayList<>();
    protected List<ShippingContainer> m_containers = new ArrayList<>();
    protected boolean m_busy = false;
    
    /**
     * Constructor
     * @param id id of platform
     */
    public Platform(int id){
        m_id = id;
    }
    
    /**
     * Set crane to busy
     */
    public void setBusy(){
        m_busy = true;
    }
    
    /**
     * Checks if platform is busy
     * @return 
     */
    public boolean isBusy(){
        return m_busy;
    }
    
    /**
     * Set crane to unbusy
     */
    public void unsetBusy(){
        m_busy = false;
    }
    
    /**
     * Adds a parkingspot to the platform
     * @param spot parkingspot
     */
    public void addParkingspot(Parkingspot spot){
        m_parkingspots.add(spot);
        spot.setParent(this);
    }
    
    /**
     * Gets the id
     * @return id
     */
    public int getID(){
        return m_id;
    }
    
    /**
     * Get the parkingspot where agv is on
     * @param id id of agv
     * @return parkingspot or null when not found
     */
    public Parkingspot getParkingspotForAGV(int id){
        for(Parkingspot p : m_parkingspots){
            if(p.hasAGV() && p.getAGV().getID() == id)
                return p;
        }
        return null;
    }
    
    /**
     * Gets the parkingspots
     * @return parkingspots
     */
    public List<Parkingspot> getParkingspots(){
        return m_parkingspots;
    }
    
    public List<ShippingContainer> getShippingContainers(){
        return m_containers;
    }
    
    /**
     * Check if there is a working platform in a array of platforms
     * @param platforms platforms
     * @return true when a working platform is found, otherwise false
     */
    public static boolean checkIfBusy(Platform[] platforms){
        for(Platform p : platforms){
            if(p.isBusy())
                return true;
        }
        return false;
    }
}
