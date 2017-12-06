/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package M3.transaction;

import M3.data.DraggableStation;
import M3.data.DraggableText;
import M3.data.LineGroups;
import M3.data.StationTracker;
import M3.data.m3Data;
import M3.gui.StationController;
import M3.gui.m3Workspace;
import djf.AppTemplate;
import java.util.ArrayList;
import javafx.scene.paint.Paint;
import jtps.jTPS_Transaction;

/**
 *
 * @author Augusto
 */
public class RemoveLine_Transaction implements jTPS_Transaction{
    public m3Workspace workspace;
    public AppTemplate appHelp;
    public String nodeToRemoveString;
    public m3Data dataManager;
    public Paint previousFill;
    
    public RemoveLine_Transaction(AppTemplate app, m3Workspace work, String removeString){
        appHelp = app;
        workspace = work;
        nodeToRemoveString = removeString;
        dataManager = (m3Data) app.getDataComponent();
    }

    @Override
    public void doTransaction() {
        dataManager.removeLineName(nodeToRemoveString);
            for (int i = 0; i < dataManager.getLineStationGroups().size(); i++) {
                if (dataManager.getLineStationGroups().get(i).getLineName().equals(nodeToRemoveString)) {
                    dataManager.removeLineGroupName(dataManager.getLineStationGroups().get(i));
                }
            }

            ArrayList<StationTracker> tempTracker = dataManager.getStationTracker();
            for (int i = 0; i < tempTracker.size(); i++) {
                if (tempTracker.get(i).getName().equals(nodeToRemoveString)) {
                    tempTracker.remove(i);
                }
            }

            for (int i = dataManager.getShapes().size() - 1; i >= 0; i--) {
                if (dataManager.getShapes().get(i) instanceof LineGroups) {
                    LineGroups tempGroup = (LineGroups) dataManager.getShapes().get(i);
                    if (tempGroup.getLineName().equals(nodeToRemoveString)) {
                        previousFill = tempGroup.getStroke();
                        dataManager.getShapes().remove(tempGroup);
                    }
                } else if (dataManager.getShapes().get(i) instanceof DraggableText) {
                    DraggableText tempText = (DraggableText) dataManager.getShapes().get(i);
                    if (tempText.getText().equals(nodeToRemoveString)) {
                        dataManager.getShapes().remove(tempText);
                    }
                }
            }

            workspace.getLineBox().getItems().remove(nodeToRemoveString);
            workspace.getOriginBox().getItems().remove(nodeToRemoveString);
            workspace.getDestinationBox().getItems().remove(nodeToRemoveString);

            workspace.getLineBox().getSelectionModel().selectFirst();
    }

    @Override
    public void undoTransaction() {
        DraggableText startLabel = new DraggableText();
        DraggableText endLabel = new DraggableText();
        LineGroups tempNode = new LineGroups();
        StationTracker newStationTracker = new StationTracker();
        startLabel.setText(nodeToRemoveString);
        endLabel.setText(nodeToRemoveString);
        dataManager.addLineName(nodeToRemoveString);
        tempNode.setLineName(nodeToRemoveString);
        dataManager.addLineGroupName(tempNode);
        workspace.getLineBox().getItems().add(nodeToRemoveString);
        workspace.getLineBox().valueProperty().set(nodeToRemoveString);
        newStationTracker.setName(nodeToRemoveString);
        dataManager.getStationTracker().add(newStationTracker);
        
        tempNode.startXProperty().bind(startLabel.xProperty());
        tempNode.startYProperty().bind(startLabel.yProperty());
        tempNode.endXProperty().bind(endLabel.xProperty());
        tempNode.endYProperty().bind(endLabel.yProperty());
        startLabel.setX(200);
        startLabel.setY(200);
        endLabel.setX(300);
        endLabel.setY(200);

        tempNode.setStrokeWidth(5);
        tempNode.setStroke(previousFill);
        tempNode.setLeftEnd(startLabel.getText());
        tempNode.setRightend(endLabel.getText());
        tempNode.setLeftElementType("text");
        tempNode.setRightElementType("text");
        startLabel.setStartText(true);
        endLabel.setEndText(true);

        System.out.println("The line name : " + tempNode.getLineName());
        System.out.println("The start name : " + tempNode.getLeftEnd());
        System.out.println("The end name : " + tempNode.getRightEnd());

        
        workspace.getCanvas().getChildren().add(startLabel);
        workspace.getCanvas().getChildren().add(tempNode);
        workspace.getCanvas().getChildren().add(endLabel);
        
        
    }
}
