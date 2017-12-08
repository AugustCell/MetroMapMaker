/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package M3.transaction;

import M3.data.DraggableLine;
import M3.data.DraggableText;
import M3.data.LineGroups;
import M3.data.StationTracker;
import M3.data.m3Data;
import M3.gui.m3Workspace;
import djf.AppTemplate;
import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import jtps.jTPS_Transaction;

/**
 *
 * @author Augusto
 */
public class AddLine_Transaction implements jTPS_Transaction {

    public LineGroups tempNode;
    public DraggableText startLabel;
    public DraggableText endLabel;
    public String name;
    AppTemplate appHelp;
    m3Workspace workspace;
    m3Data dataManager;
    StationTracker newStationTracker;


    public AddLine_Transaction(AppTemplate app, m3Workspace work, LineGroups node, DraggableText originText, DraggableText endText, String tempName, StationTracker tempTracker) {
        tempNode = node;
        appHelp = app;
        startLabel = originText;
        endLabel = endText;
        workspace = work;
        dataManager = (m3Data) app.getDataComponent();
        name = tempName;
        newStationTracker = tempTracker;
        
    }

    @Override
    public void doTransaction() {
        startLabel.setText(name);
        endLabel.setText(name);
        dataManager.addLineName(name);
        tempNode.setLineName(name);
        dataManager.addLineGroupName(tempNode);
        workspace.getLineBox().getItems().add(name);
        workspace.getLineBox().valueProperty().set(name);
        newStationTracker.setName(name);
        dataManager.getStationTracker().add(newStationTracker);
        
        ((LineGroups) tempNode).startXProperty().bind(((DraggableText) startLabel).xProperty());
        ((LineGroups) tempNode).startYProperty().bind(((DraggableText) startLabel).yProperty());
        ((LineGroups) tempNode).endXProperty().bind(((DraggableText) endLabel).xProperty());
        ((LineGroups) tempNode).endYProperty().bind(((DraggableText) endLabel).yProperty());
        ((DraggableText) startLabel).setX(200);
        ((DraggableText) startLabel).setY(200);
        ((DraggableText) endLabel).setX(300);
        ((DraggableText) endLabel).setY(200);
        ((DraggableText) startLabel).setLineText(true);
        ((DraggableText) endLabel).setLineText(true);

        ((LineGroups) tempNode).setStrokeWidth(5);
        ((LineGroups) tempNode).setLeftEnd(((DraggableText) startLabel).getText());
        ((LineGroups) tempNode).setLeftElementType(((DraggableText) startLabel).getClass().toString());
        ((LineGroups) tempNode).setRightend(((DraggableText) endLabel).getText());
        ((LineGroups) tempNode).setRightElementType(((DraggableText) endLabel).getClass().toString());
        ((DraggableText) startLabel).setStartText(true);
        ((DraggableText) endLabel).setEndText(true);

        System.out.println("The line name : " + ((LineGroups) tempNode).getLineName());
        System.out.println("The start name : " + ((LineGroups) tempNode).getLeftEnd());
        System.out.println("The end name : " + ((LineGroups) tempNode).getRightEnd());

        
        workspace.getCanvas().getChildren().add(startLabel);
        workspace.getCanvas().getChildren().add(tempNode);
        workspace.getCanvas().getChildren().add(endLabel);

    }

    @Override
    public void undoTransaction() {
        dataManager.removeLineName(name);
            for (int i = 0; i < dataManager.getLineStationGroups().size(); i++) {
                if (dataManager.getLineStationGroups().get(i).getLineName().equals(name)) {
                    dataManager.removeLineGroupName(dataManager.getLineStationGroups().get(i));
                }
            }

            ArrayList<StationTracker> tempTracker = dataManager.getStationTracker();
            for (int i = 0; i < tempTracker.size(); i++) {
                if (tempTracker.get(i).getName().equals(name)) {
                    tempTracker.remove(i);
                }
            }

            
            workspace.getCanvas().getChildren().remove(startLabel);
            workspace.getCanvas().getChildren().remove(tempNode);
            workspace.getCanvas().getChildren().remove(endLabel);
            
            for(int i = dataManager.getShapes().size() - 1; i>= 0; i--){
                if(dataManager.getShapes().get(i) instanceof LineGroups){
                    LineGroups tempGroup = (LineGroups) dataManager.getShapes().get(i);
                    if(tempGroup.getLineName().equals(name)){
                        workspace.getCanvas().getChildren().remove(tempGroup);
                    }
                }
                else if(dataManager.getShapes().get(i) instanceof DraggableText){
                    DraggableText tempText = (DraggableText) dataManager.getShapes().get(i);
                    if(tempText.getText().equals(name)){
                        workspace.getCanvas().getChildren().remove(tempText);
                    }
                }
            }
            workspace.getLineBox().getItems().remove(name);
            workspace.getOriginBox().getItems().remove(name);
            workspace.getDestinationBox().getItems().remove(name);

            workspace.getLineBox().getSelectionModel().selectFirst();


    }
}
