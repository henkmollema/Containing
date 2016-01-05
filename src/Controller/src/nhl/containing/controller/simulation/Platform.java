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
    protected List<Parkingspot> parkingspots = new ArrayList<>();
    public List<ShippingContainer> containers = new ArrayList<>();
    protected boolean busy = false;
    
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
        busy = true;
    }
    
    /**
     * Checks if platform is busy
     * @return 
     */
    public boolean isBusy(){
        return busy;
    }
    
    /**
     * Set crane to unbusy
     */
    public void unsetBusy(){
        busy = false;
    }
    
    /**
     * Adds a parkingspot to the platform
     * @param spot parkingspot
     */
    public void addParkingspot(Parkingspot spot){
        parkingspots.add(spot);
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
        for(Parkingspot p : parkingspots){
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
        return parkingspots;
    }
    
    public List<ShippingContainer> getShippingContainers(){
        return containers;
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
    
    /**
     * Checks if there is a loading to be done in a array of platforms
     * @param platforms platforms
     * @return true when done, otherwise false
     */
    public static boolean checkIfShipmentDone(Platform[] platforms){
        for(Platform p : platforms){
            if(!p.containers.isEmpty())
                return false;
        }
        return true;
    }
}
