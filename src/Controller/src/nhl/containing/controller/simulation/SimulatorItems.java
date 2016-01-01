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
    private Platform[] m_trainPlatforms = new Platform[4];
    private Platform[] m_seaPlatforms = new Platform[8];
    private Platform[] m_inlandPlatforms = new Platform[1];
    private Platform[] m_lorryPlatforms = new Platform[20];
    private List<Parkingspot> m_parkingspots = new ArrayList<>();
    private List<Node> m_Nodes = new ArrayList<>();
    
    private SimulatorItems(SimulatorItemList list){
        for(int i = list.getItemsList().size() - 1; i > 0; i--){
            SimulationItem item = list.getItemsList().get(i);
            switch(item.getType()){
                case PLATFORM_STORAGE:
                    addStorage((int)item.getId());
                    break;
                case PLATFORM_TRAIN:
                    addTrainPlatform((int)item.getId());
                    break;
                case PLATFORM_SEASHIP:
                    addSeashipPlatform((int)item.getId());
                    break;
                case PLATFORM_INLANDSHIP:
                    addInlandPlatform((int)item.getId());
                    break;
                case PLATFORM_LORRY:
                    addLorryPlatform((int)item.getId());
                    break;
                case PARKINGSPOT_STORAGE:
                case PARKINGSPOT_TRAIN:
                case PARKINGSPOT_LORRY:
                case PARKINGSPOT_SEASHIP:
                case PARKINGSPOT_INLANDSHIP:
                    Parkingspot spot = new Parkingspot(item.getId(), new Vector3f(item.getX(), item.getY(), item.getZ()));
                    if(item.getParentID() != -1){
                        switch(item.getType()){
                            case PARKINGSPOT_STORAGE:
                                m_storages[item.getParentID()].addParkingspot(spot);
                                break;
                            case PARKINGSPOT_TRAIN:
                                m_trainPlatforms[item.getParentID()].addParkingspot(spot);
                                break;
                            case PARKINGSPOT_SEASHIP:
                                m_seaPlatforms[item.getParentID()].addParkingspot(spot);
                                break;
                            case PARKINGSPOT_INLANDSHIP:
                                m_inlandPlatforms[item.getParentID()].addParkingspot(spot);
                                break;
                            case PARKINGSPOT_LORRY:
                                m_lorryPlatforms[item.getParentID()].addParkingspot(spot);
                                break;
                        }
                    }
                    m_parkingspots.add(spot);
                    break;
                case NODES:
                    m_Nodes.add(new Node((int)item.getId(), item.getX(), item.getY(), item.getConnectionsList()));
                    break;
            }
        }
    }
    
    public Storage[] getStorages(){
        return m_storages;
    }
    
    public Platform[] getTrainPlatforms(){
        return m_trainPlatforms;
    }
    
    public Platform[] getSeaShipPlatforms(){
        return m_seaPlatforms;
    }
    
    public Platform[] getInlandPlatforms(){
        return m_inlandPlatforms;
    }
    
    public Platform[] getLorryPlatforms(){
        return m_lorryPlatforms;
    }
    
    public List<Parkingspot> getParkingspots(){
        return m_parkingspots;
    }
    
    public List<Node> getNodes(){
        return m_Nodes;
    }
    
    private void addStorage(int id){
        m_storages[id] = new Storage(id);
    }
    
    private void addTrainPlatform(int id){
        m_trainPlatforms[id] = new Platform(id);
    }
    
    private void addSeashipPlatform(int id){
        m_seaPlatforms[id] = new Platform(id);
    }
    
    private void addInlandPlatform(int id){
        m_inlandPlatforms[id] = new Platform(id);
    }
    
    private void addLorryPlatform(int id){
        m_lorryPlatforms[id] = new Platform(id);
    }
    
    private void addParkingsport(long id, Vector3f position){
        m_parkingspots.add(new Parkingspot(id, position));
    }
    
    public static SimulatorItems creatSimulationItems(SimulatorItemList list){
        return new SimulatorItems(list);
    } 
    
}
