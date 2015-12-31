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
public class StorageItem
{
    private ShippingContainer m_container = null;
    private final Point3 m_position;
    
    public StorageItem(Point3 position){
        m_position = position;
    }
    
    public StorageItem(int x, int y, int z){
        m_position = new Point3(x, y, z);
    }
    
    public void setContainer(ShippingContainer container) throws Exception{
        if(!isEmpty()){
            throw new Exception("Already in use");
        }
        m_container = container;
    }
    
    public boolean isEmpty(){
        return m_container == null;
    }
    
    public static StorageItem find(Storage storage,Point3 p){
       return storage.getStoragePlaces()[p.x][p.y][p.z];
    }
    
    public static void place(Storage storage, ShippingContainer container, Point3 p) throws Exception{
        storage.getStoragePlaces()[p.x][p.y][p.z].setContainer(container);
    }
}
