/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.controller.simulation;

import java.util.ArrayList;
import java.util.List;
import nhl.containing.controller.Point3;

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
    
    private Shipment m_Shipment = null;
    
    public void removeContainerAtPosition(Point3 pos)
    {
        for(int i = 0; i < containers.size(); i++)
        {
            if(containers.get(i).position.x == pos.x 
                && containers.get(i).position.y == pos.y
                && containers.get(i).position.z == pos.z)
            {
                containers.remove(i);
                return;
            }
        }
    }
    
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
     * Gets the parkingspot index for a parkingspot
     * @param spot parkingspot
     * @return index
     */
    public int getParkingspotIndex(Parkingspot spot){
        for(int i = 0; i < parkingspots.size(); i++){
            if(parkingspots.get(i).getId() == spot.getId())
                return i;
        }
        return -1;
    }
    
    /**
     * Gets the first free parkingspot
     * @return parkingspot
     */
    public Parkingspot getFreeParkingspot(){
        for(Parkingspot p : parkingspots){
            if(!p.hasAGV())
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
    
    /**
     * Finds first free parkingspot
     * @param p platform
     * @return parkingspot
     */
    public static Parkingspot findFreeParkingspot(Platform p){
        for(Parkingspot ps : p.getParkingspots()){
            if(!ps.hasAGV())
                return ps;
        }
        return null;
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
