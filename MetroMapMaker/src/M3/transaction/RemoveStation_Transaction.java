/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package M3.transaction;

import M3.data.DraggableStation;
import M3.data.DraggableText;
import M3.data.StationEnds;
import M3.data.m3Data;
import M3.gui.StationController;
import M3.gui.m3Workspace;
import djf.AppTemplate;
import java.util.ArrayList;
import jtps.jTPS_Transaction;

/**
 *
 * @author Augusto
 */
public class RemoveStation_Transaction implements jTPS_Transaction {
    public m3Workspace workspace;
    public AppTemplate appHelp;
    public StationController stationController;
    public String nodeToRemoveString;
    public m3Data dataManager;

    
    public RemoveStation_Transaction(AppTemplate app, m3Workspace work, String removeString, StationController help){
        appHelp = app;
        workspace = work;
        nodeToRemoveString = removeString;
        stationController = help;
        dataManager = (m3Data) app.getDataComponent();
    }

    @Override
    public void doTransaction() {
        dataManager.removeStationName(nodeToRemoveString);
            workspace.getOriginBox().getItems().remove(nodeToRemoveString);
            workspace.getDestinationBox().getItems().remove(nodeToRemoveString);
            workspace.getStationBox().getItems().remove(nodeToRemoveString);
            for(int i = 0; i < dataManager.getShapes().size(); i++){
                if(dataManager.getShapes().get(i) instanceof DraggableStation){
                    DraggableStation tempStation = (DraggableStation) dataManager.getShapes().get(i);
                    if(tempStation.getStationName().equals(nodeToRemoveString)){
                        ArrayList<StationEnds> tempEnds = tempStation.getStationEnds();
                        if(!tempEnds.isEmpty()){
                            stationController.removeSpecificStation(nodeToRemoveString);
                        }
                    }
                }
            }
            for (int i = dataManager.getShapes().size() - 1; i >= 0; i--) {
                if(dataManager.getShapes().get(i) instanceof DraggableStation){
                    DraggableStation tempStation = (DraggableStation) dataManager.getShapes().get(i);
                    if(tempStation.getStationName().equals(nodeToRemoveString)){
                        dataManager.getShapes().remove(tempStation);
                    }
                }
                else if(dataManager.getShapes().get(i) instanceof DraggableText){
                    DraggableText tempText = (DraggableText) dataManager.getShapes().get(i);
                    if(tempText.getText().equals(nodeToRemoveString)){
                        dataManager.getShapes().remove(tempText);
                    }
                }
             
            }
            workspace.getStationBox().getSelectionModel().selectFirst();
    }

    @Override
    public void undoTransaction() {
        DraggableText stationLabel = new DraggableText();
        DraggableStation tempNode = new DraggableStation();
        
        stationLabel.setText(nodeToRemoveString);
        dataManager.getStations().add(nodeToRemoveString);
        workspace.getStationBox().getItems().add(nodeToRemoveString);
        workspace.getStationBox().valueProperty().set(nodeToRemoveString);
        workspace.getOriginBox().getItems().add(nodeToRemoveString);
        workspace.getDestinationBox().getItems().add(nodeToRemoveString);
        
        tempNode.setStationName(nodeToRemoveString);
        stationLabel.xProperty().bind(tempNode.centerXProperty().add(tempNode.getRadius()));
        stationLabel.yProperty().bind(((DraggableStation) tempNode).centerYProperty().subtract(((DraggableStation) tempNode).getRadius()));
        tempNode.setTopRight(true);
        workspace.getCanvas().getChildren().add(stationLabel);
        workspace.getCanvas().getChildren().add(tempNode);
    }
}
