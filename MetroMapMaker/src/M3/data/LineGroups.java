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
    public String leftEnd;
    public String rightEnd;
    public String leftElementType;
    public String rightElementType;
    boolean firstLine;
    boolean lastLine;
    boolean leftBounded;
    boolean rightBounded;
    
    
    public LineGroups(){
        lineName = "";
        metroStations = new ArrayList<String>();
        startStation = "";
        endStation = "";
        leftEnd = "";
        rightEnd = "";
        leftElementType = "";
        rightElementType = "";
        firstLine = false;
        lastLine = false;
        leftBounded = false;
        rightBounded = false;
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
    public String getLeftEnd(){
        return leftEnd;
    }
    public void setLeftEnd(String name){
        leftEnd = name;
    }
    public String getRightEnd(){
        return rightEnd;
    }
    public void setRightend(String name){
        rightEnd = name;
    }
    public void setLeftElementType(String type){
        leftElementType = type;
    }
    public String getLeftElementType(){
        return leftElementType;
    }
    public void setRightElementType(String type){
        rightElementType = type;
    }
    public String getRightElementType(){
        return rightElementType;
    }
    public String getShapeType(){
        return LINE;
    }
    public void setFirstLine(boolean result){
        firstLine = result;
    }
    public boolean getFirstLine(){
        return firstLine;
    }
    public void setLastLine(boolean result){
        lastLine = result;
    }
    public boolean getLastLine(){
        return lastLine;
    }
   
    
    public void setLocationAndSize(double initX, double initY, double endX, double endY) {
        startXProperty().set(initX);
        startYProperty().set(initY);
        endXProperty().set(endX);
        endYProperty().set(endY);
    }
}
