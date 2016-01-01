/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.controller.simulation;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Niels
 */
public class Platform
{
    protected final int m_id;
    protected List<Parkingspot> m_parkingspots = new ArrayList<>();
    
    public Platform(int id){
        m_id = id;
    }
    
    public void addParkingspot(Parkingspot spot){
        m_parkingspots.add(spot);
    }
    
    public int getID(){
        return m_id;
    }
    
    public List<Parkingspot> getParkingspots(){
        return m_parkingspots;
    }
}
