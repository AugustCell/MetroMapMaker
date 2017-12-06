/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package M3.transaction;

import M3.data.DraggableStation;
import M3.data.DraggableText;
import M3.data.StationEnds;
import M3.data.StationTracker;
import M3.data.m3Data;
import M3.gui.StationController;
import M3.gui.m3Workspace;
import djf.AppTemplate;
import java.util.ArrayList;
import javafx.scene.Node;
import javafx.scene.Scene;
import jtps.jTPS_Transaction;

/**
 *
 * @author Augusto
 */
public class RemoveStationLine_Transaction implements jTPS_Transaction{
    public Node shape;
    public DraggableStation stationShape;
    public DraggableText startLabel;
    public DraggableText endLabel;
    public String name;
    public ArrayList<StationTracker> tempTracker;
    public Scene scene;
    public ArrayList<StationEnds> stationEnds;
    public String lineString;
    public String stationString;
    public StationEnds newStationEnd;
    AppTemplate appHelp;
    m3Workspace workspace;
    m3Data dataManager;
    StationTracker newStationTracker;
    StationController stationController;
    
    public RemoveStationLine_Transaction (AppTemplate app, m3Workspace work, DraggableStation selectedNode, ArrayList<StationTracker> tracker,
            Scene tempScene, ArrayList<StationEnds> ends, String lineName, Node tempShape){
        
    }

    @Override
    public void doTransaction() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void undoTransaction() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
