/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package M3.transaction;

import M3.data.DraggableLine;
import M3.data.DraggableStation;
import M3.data.DraggableText;
import M3.data.m3Data;
import M3.gui.m3Workspace;
import djf.AppTemplate;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.text.Text;
import jtps.jTPS_Transaction;

/**
 *
 * @author Augusto
 */
public class AddStation_Transaction implements jTPS_Transaction{
    
    public Node tempNode;
    public Node stationLabel;
    public String stationName;
    AppTemplate appHelp;
    m3Data dataManager;
    m3Workspace workspace;


    public AddStation_Transaction(AppTemplate app, m3Workspace work, Node node, Node stationText, String name) {
        tempNode = node;
        appHelp = app;
        stationLabel = stationText;
        stationName = name;
        dataManager = (m3Data) app.getDataComponent();
        workspace = work;
    }
    
    public void doTransaction() {
        ((DraggableText) stationLabel).setText(stationName);
        dataManager.getStations().add(stationName);
        workspace.getStationBox().getItems().add(stationName);
        workspace.getStationBox().valueProperty().set(stationName);
        workspace.getOriginBox().getItems().add(stationName);
        workspace.getDestinationBox().getItems().add(stationName);
        ((DraggableStation) tempNode).setStationName(stationName);
        
        ((DraggableText)stationLabel).xProperty().bind(((DraggableStation) tempNode).centerXProperty().add(((DraggableStation) tempNode).getRadius()));
        ((DraggableText)stationLabel).yProperty().bind(((DraggableStation) tempNode).centerYProperty().subtract(((DraggableStation) tempNode).getRadius()));
        ((DraggableStation) tempNode).setTopRight(true);
        ((m3Workspace) appHelp.getWorkspaceComponent()).getCanvas().getChildren().add(stationLabel);
        ((m3Workspace) appHelp.getWorkspaceComponent()).getCanvas().getChildren().add(tempNode);
    }

    @Override
    public void undoTransaction() {
        ((m3Workspace) appHelp.getWorkspaceComponent()).getCanvas().getChildren().remove(stationLabel);
        ((m3Workspace) appHelp.getWorkspaceComponent()).getCanvas().getChildren().remove(tempNode);

    }

}
