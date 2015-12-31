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
 *
 * @author Niels
 */
public class Storage
{
    private final int m_id;
    private List<Parkingspot> m_parkingspots = new ArrayList<>();
    private StorageItem[][][] m_storageplaces = new StorageItem[6][46][6];
    
    public Storage(int id){
        m_id = id;
        for(int x = 0; x < m_storageplaces.length; x++){
            for(int y = 0; y < m_storageplaces[0].length; y++){
                for(int z = 0; z < m_storageplaces[0][0].length; z++){
                    m_storageplaces[x][y][z] = new StorageItem(x, y, z);
                }
            }
        }
    }
    
    public void addParkingspot(Parkingspot spot){
        m_parkingspots.add(spot);
    }
    
    public void setContainer(ShippingContainer container, Point3 position) throws Exception{
        if(!checkPosition(position))
            throw new Exception("Not valid Position");
        StorageItem.place(this, container, position);
    }
    
    public int getID(){
        return m_id;
    }
    
    public List<Parkingspot> getParkingspots(){
        return m_parkingspots;
    }
    
    public StorageItem[][][] getStoragePlaces(){
        return m_storageplaces;
    }
    
    private boolean checkPosition(Point3 position){
        
        return position.x < 7 && position.y < 7 && position.z < 46 && StorageItem.find(this, position).isEmpty(); //zet 
    }
    
}