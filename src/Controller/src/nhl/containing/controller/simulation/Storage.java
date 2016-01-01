/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.controller.simulation;

import nhl.containing.controller.Point3;

/**
 *
 * @author Niels
 */
public class Storage extends Platform
{
    private StorageItem[][][] m_storageplaces = new StorageItem[6][46][6];
    
    public Storage(int id){
        super(id);
        for(int x = 0; x < m_storageplaces.length; x++){
            for(int y = 0; y < m_storageplaces[0].length; y++){
                for(int z = 0; z < m_storageplaces[0][0].length; z++){
                    m_storageplaces[x][y][z] = new StorageItem(x, y, z);
                }
            }
        }
    }
    
    public void setContainer(ShippingContainer container, Point3 position) throws Exception{
        if(!checkPosition(position))
            throw new Exception("Not valid Position");
        StorageItem.place(this, container, position);
    }
    
    public void setContainer(ShippingContainer container, int x, int y, int z) throws Exception{
        this.setContainer(container, new Point3(x, y, z));
    }
    
    public StorageItem[][][] getStoragePlaces(){
        return m_storageplaces;
    }
    
    private boolean checkPosition(Point3 position){
        
        return position.x < 7 && position.y < 7 && position.z < 46 && StorageItem.find(this, position).isEmpty(); //zet 
    }
    
}