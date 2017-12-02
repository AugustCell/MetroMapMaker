/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package M3.data;

import java.util.ArrayList;

/**
 *
 * @author Augusto
 */
public class StationTracker {
    public ArrayList<String> stationNames = new ArrayList<String>();
    public String name;
    
    public StationTracker(){
        name = "";
    }
    
    public ArrayList<String> getStationNames(){
        return stationNames;
    }
    public void addStationName(String name){
        stationNames.add(name);
    }
    public void removeStationName(String name){
        stationNames.remove(name);
    }
    public String getName(){
        return name;
    }
    public void setName(String lineName){
        name = lineName;
    }
    
}
