/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.controller.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nhl.containing.controller.PathFinder;
import nhl.containing.controller.Vector3f;
import nhl.containing.networking.protobuf.SimulationItemProto.*;

/**
 * Saves all the simulator items
 * @author Niels
 */
public class SimulatorItems
{
    public static final int SEA_SHIP_CRANE_COUNT = 8;
    public static final int TRAIN_CRANE_COUNT = 4;
    public static final int LORRY_CRANE_COUNT = 20;
    public static final int INLAND_SHIP_CRANE_COUNT = 1;
    public static final int STORAGE_CRANE_COUNT = 72;
    
    public static final int STORAGE_BEGIN = INLAND_SHIP_CRANE_COUNT + LORRY_CRANE_COUNT + SEA_SHIP_CRANE_COUNT;
    public static final int SEASHIP_BEGIN = INLAND_SHIP_CRANE_COUNT + LORRY_CRANE_COUNT;
    public static final int LORRY_BEGIN = INLAND_SHIP_CRANE_COUNT;
    public static final int TRAIN_BEGIN = INLAND_SHIP_CRANE_COUNT + LORRY_CRANE_COUNT + SEA_SHIP_CRANE_COUNT + STORAGE_CRANE_COUNT;
    
    private Storage[] m_storages = new Storage[STORAGE_CRANE_COUNT];
    private Platform[] m_trainPlatforms = new Platform[TRAIN_CRANE_COUNT];
    private Shipment m_trainShipment = null;
    
    private Platform[] m_seaPlatforms = new Platform[SEA_SHIP_CRANE_COUNT];
    private Shipment m_seashipShipment = null;
    
    private Platform[] m_inlandPlatforms = new Platform[INLAND_SHIP_CRANE_COUNT];
    private Shipment m_inlandShipment = null;
    
    private LorryPlatform[] m_lorryPlatforms = new LorryPlatform[LORRY_CRANE_COUNT];
    
    private Map<Long,Parkingspot> m_parkingspotsMap = new HashMap<>();
    private List<Parkingspot> m_parkingspots = new ArrayList<>();
    
    private List<Node> m_Nodes = new ArrayList<>();
    private List<AGV> m_AGVs = new ArrayList<>();
    
    /**
     * Creates a Simulatoritems object
     * @param list list with metadata from simulator
     */
    public SimulatorItems(SimulatorItemList list){
        for(int i = list.getItemsList().size() - 1; i > 0; i--){
            SimulationItem item = list.getItemsList().get(i);
            switch(item.getType()){
                case AGV:
                    m_AGVs.add(new AGV((int)item.getId()));
                    break;
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
                    if(item.getConnectionsCount() < 2){
                        continue; //TODO error?
                    }
                    Parkingspot spot = new Parkingspot(item.getId(), new Vector3f(item.getX(), item.getY(), item.getZ()),item.getConnections(0),item.getConnections(1));
                    m_parkingspotsMap.put(item.getId(), spot);
                    if(item.getParentID() != -1){
                        switch(item.getType()){
                            case PARKINGSPOT_STORAGE:
                                m_storages[item.getParentID() - STORAGE_BEGIN].addParkingspot(spot);
                                break;
                            case PARKINGSPOT_TRAIN:
                                m_trainPlatforms[item.getParentID() - TRAIN_BEGIN].addParkingspot(spot);
                                break;
                            case PARKINGSPOT_SEASHIP:
                                m_seaPlatforms[item.getParentID() - SEASHIP_BEGIN].addParkingspot(spot);
                                break;
                            case PARKINGSPOT_INLANDSHIP:
                                m_inlandPlatforms[item.getParentID()].addParkingspot(spot);
                                break;
                            case PARKINGSPOT_LORRY:
                                m_lorryPlatforms[item.getParentID() - LORRY_BEGIN].addParkingspot(spot);
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
        for(Storage s : m_storages){
            Collections.reverse(s.parkingspots);
        }
        PathFinder.initPath(m_Nodes);
    }
    
    /**
     * Gets platforms for a specific carrier
     * @param carrier carrier
     * @return platforms
     */
    public Platform[] getPlatformsByCarrier(Carrier carrier){
        if(carrier instanceof Train)
            return m_trainPlatforms;
        else if(carrier instanceof Truck)
            return m_lorryPlatforms;
        else if(carrier instanceof SeaShip)
            return m_seaPlatforms;
        else
            return m_inlandPlatforms;
    }
    
    /**
     * Gets an parkingspot by an ID
     * @param id id of parkingspot
     * @return parkingspot
     */
    public Parkingspot getParkingspotByID(long id){
        return m_parkingspotsMap.get(id);
    }
    
    /**
     * Get storages array
     * @return array of storages
     */
    public Storage[] getStorages(){
        return m_storages;
    }
    
    /**
     * Get trainPlatforms
     * @return Platforms
     */
    public Platform[] getTrainPlatforms(){
        return m_trainPlatforms;
    }
    
    /**
     * Get seashipPlatforms
     * @return Platforms
     */
    public Platform[] getSeaShipPlatforms(){
        return m_seaPlatforms;
    }
    
    /**
     * Get inlandPlatforms
     * @return Platforms
     */
    public Platform[] getInlandPlatforms(){
        return m_inlandPlatforms;
    }
    
    /**
     * get LorryPlatforms
     * @return Platforms
     */
    public LorryPlatform[] getLorryPlatforms(){
        return m_lorryPlatforms;
    }
    
    /**
     * get Parkingspots
     * @return Parkingspots
     */
    public List<Parkingspot> getParkingspots(){
        return m_parkingspots;
    }
    
    /**
     * Get Nodes
     * @return Nodes
     */
    public List<Node> getNodes(){
        return m_Nodes;
    }
    
    /**
     * Get node by a id
     * @param id id
     * @return node
     */
    public Node getNode(int id){
        for(Node n : m_Nodes){
            if(n.m_id == id)
                return n;
        }
        return null;
    }
    
    /**
     * Gets a platform by an AGV ID
     * @param id id of the agv
     * @return Platform
     */
    public Platform getPlatformByAGVID(int id){
        for(Parkingspot p : m_parkingspots){
            if(p.hasAGV() && p.getAGV().getID() == id)
                return  p.getPlatform();
        }
        return null;
    }
    
    /**
     * Gets current train shipment
     * @return shipment
     */
    public Shipment getTrainShipment(){
        return m_trainShipment;
    }
    
    /**
     * Sets a shipment as current train shipment
     * @param shipment shipment
     * @throws Exception when already occupied
     */
    public void setTrainShipment(Shipment shipment) throws Exception{
        if(hasTrainShipment())
            throw new Exception("Occupied");
        m_trainShipment = shipment;
    }
    
    /**
     * Unsets a current train shipment
     */
    public void unsetTrainShipment(){
        m_trainShipment = null;
    }
    
    /**
     * Checks if there is a current train shipment
     * @return true when there is a current shipment, otherwise false
     */
    public boolean hasTrainShipment(){
        return m_trainShipment != null;
    }
    
    /**
     * Gets current seashipment
     * @return shipment
     */
    public Shipment getSeaShipment(){
        return m_seashipShipment;
    }
    
    /**
     * Sets a shipment as current seaship shipment
     * @param shipment shipment
     * @throws Exception when already occupied
     */
    public void setSeaShipment(Shipment shipment) throws Exception{
        if(hasSeaShipment())
            throw new Exception("Occupied");
        m_seashipShipment = shipment;
    }
    
    /**
     * Unsets current seashipment
     */
    public void unsetSeaShipment(){
        m_seashipShipment = null;
    }
    
    /**
     * Check if there is a current seaships shipment
     * @return true when there is a shipment, otherwise false
     */
    public boolean hasSeaShipment(){
        return m_seashipShipment != null;
    }
    
    /**
     * Gets current inlandshipment
     * @return shipment
     */
    public Shipment getInlandShipment(){
        return m_inlandShipment;
    }
    
    /**
     * Sets current inland shipment
     * @param shipment shipment
     * @throws Exception when already occupied
     */
    public void setInlandShipment(Shipment shipment)throws Exception{
        if(hasInlandShipment())
            throw new Exception("Occupied");
        m_inlandShipment = shipment;
    }
    
    /**
     * Unsets current inland shipment
     */
    public void unsetInlandShipment(){
        m_inlandShipment = null;
    }
    
    /**
     * Checks if there is a current inland shipment
     * @return true when there is a current inland shipment, otherwise false
     */
    public boolean hasInlandShipment(){
        return m_inlandShipment != null;
    }
    
    /**
     * Gets a free AGV
     * @return agv
     */
    public AGV getFreeAGV(){
        for(AGV agv : m_AGVs){
            if(!agv.isBusy())
                return agv;
        }
        return null;
    }
    
    public AGV getAGV(int agvID)
    {
        for(AGV agv : m_AGVs){
            if(agv.getID() == agvID)
                return agv;
        }
        return null;
    }
    
    /**
     * Adds a storagePlatform
     * @param id id of storage
     */
    private void addStorage(int id){
        m_storages[id - STORAGE_BEGIN] = new Storage(id);
    }
    
    /**
     * Adds a trainPlatform
     * @param id id of trainPlatform
     */
    private void addTrainPlatform(int id){
        m_trainPlatforms[id - TRAIN_BEGIN] = new Platform(id);
    }
    
    /**
     * Adds seashipPlatform
     * @param id if of seashipPlatform
     */
    private void addSeashipPlatform(int id){
        m_seaPlatforms[id - SEASHIP_BEGIN] = new Platform(id);
    }
    
    /**
     * Adds inlandPlatform
     * @param id id of inlandPlatform
     */
    private void addInlandPlatform(int id){
        m_inlandPlatforms[id] = new Platform(id);
    }
    
    /**
     * Adds lorryPlatform
     * @param id if of lorryPlatform
     */
    private void addLorryPlatform(int id){
        m_lorryPlatforms[id - LORRY_BEGIN] = new LorryPlatform(id);
    }    
}
