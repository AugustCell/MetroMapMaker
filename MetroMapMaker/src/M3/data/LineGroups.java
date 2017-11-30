/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package M3.data;

import static M3.data.Draggable.LINE;
import java.util.ArrayList;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;

/**
 *
 * @author Augusto
 * The purpose of this class is to make a group to make it easier to track lines.
 * This will have each line is respective start station, end station, and station list.
 * This will also track the name of the line so it will be more convinient to save 
 * and load from JSON
 */
public class LineGroups extends Line{
    public String lineName;
    public ArrayList<String> metroStations;
    public String startStation;
    public String endStation;
    
    public LineGroups(){
        lineName = "";
        metroStations = new ArrayList<String>();
        startStation = "";
        endStation = "";
    }
    
    public void setLineName(String name){
        lineName = name;
    }
    public void setStartStation(String stationName){
        startStation = stationName;
    }
    public void setEndStation(String stationName){
        endStation = stationName;
    }
    public void addToMetroStationsList(String stationName){
        metroStations.add(stationName);
    }
    public String getLineName(){
        return lineName;
    }
    public String getStartStation(){
        return startStation;
    }
    public String getEndStation(){
        return endStation;
    }
    public ArrayList<String> getMetroStations(){
        return metroStations;
    }
    
    public String getShapeType(){
        return LINE;
    }
}
