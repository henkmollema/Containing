/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.controller.simulation;

import java.util.ArrayList;
import java.util.List;
import nhl.containing.controller.Vector3f;
import nhl.containing.networking.protobuf.SimulationItemProto.*;

/**
 *
 * @author Niels
 */
public class SimulatorItems
{
    private Storage[] m_storages = new Storage[72];
    private List<Parkingspot> m_parkingspots = new ArrayList<>();
    
    private SimulatorItems(SimulatorItemList list){
        for(int i = list.getItemsList().size() - 1; i > 0; i--){
            SimulationItem item = list.getItemsList().get(i);
            switch(item.getType()){
                case PLATFORM_STORAGE:
                    addStorage((int)item.getId());
                    break;
                case PARKINGSPOT_STORAGE:
                    Parkingspot spot = new Parkingspot(item.getId(), new Vector3f(item.getX(), item.getY(), item.getZ()));
                    if(item.getParentID() != -1)
                        m_storages[item.getParentID()].addParkingspot(spot);
                    m_parkingspots.add(spot);
                    break;
                case NODES:
                    //add nodes
                    break;
            }
        }
//        for(SimulationItem item : list.getItemsList()){
//            switch(item.getType()){
//                case PLATFORM:
//                    addStorage((int)item.getId());
//                    break;
//                case PARKINGSPOT:
//                    Parkingspot spot = new Parkingspot(item.getId(), new Vector3f(item.getX(), item.getY(), item.getZ()));
//                    if(item.getParentID() != -1)
//                        m_storages[item.getParentID()].addParkingspot(spot);
//                    m_parkingspots.add(spot);
//                    break;
//                case NODES:
//                    //add nodes
//                    break;
//            }
//        }
    }
    
    private void addStorage(int id){
        m_storages[id] = new Storage(id);
    }
    
    private void addParkingsport(long id, Vector3f position){
        m_parkingspots.add(new Parkingspot(id, position));
    }
    
    public static SimulatorItems creatSimulationItems(SimulatorItemList list){
        return new SimulatorItems(list);
    } 
    
}
