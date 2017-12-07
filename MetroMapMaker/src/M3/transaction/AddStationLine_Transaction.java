/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package M3.transaction;

import M3.data.DraggableStation;
import M3.data.DraggableText;
import M3.data.LineGroups;
import M3.data.StationEnds;
import M3.data.StationTracker;
import M3.data.m3Data;
import static M3.data.m3State.SELECTING_SHAPE;
import M3.gui.StationController;
import M3.gui.m3Workspace;
import djf.AppTemplate;
import java.util.ArrayList;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import jtps.jTPS_Transaction;

/**
 *
 * @author Augusto
 */
public class AddStationLine_Transaction implements jTPS_Transaction {

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

    public AddStationLine_Transaction(AppTemplate app, m3Workspace work, DraggableStation selectedNode, ArrayList<StationTracker> tracker,
            Scene tempScene, ArrayList<StationEnds> ends, String lineName, Node tempShape, StationEnds stationEnd) {
        appHelp = app;
        workspace = work;
        dataManager = (m3Data) app.getDataComponent();
        stationShape = selectedNode;
        tempTracker = tracker;
        scene = tempScene;
        stationEnds = ends;
        lineString = lineName;
        shape = tempShape;
        newStationEnd = stationEnd;
        stationString = selectedNode.getStationName();
        stationController = new StationController(app);
    }

    @Override
    public void doTransaction() {
        for (int i = 0; i < dataManager.getShapes().size(); i++) {
            Node temp = (Node) dataManager.getShapes().get(i);
            if (temp instanceof LineGroups) {
                LineGroups tempGroup = (LineGroups) temp;
                if (tempGroup.getLineName().equals(lineString)) { //THIS MEANS WE HAVE THE CORRECT LINE IN TEMPGROUP
                    for (int l = 0; l < dataManager.getShapes().size(); l++) {
                        if (dataManager.getShapes().get(l) instanceof LineGroups) {
                            LineGroups tempLine = (LineGroups) dataManager.getShapes().get(l);
                            if (tempLine.getLastLine() && tempLine.getLineName().equals(lineString)) {
                                tempGroup = tempLine;
                            }
                        }
                    }

                    String leftEnd = tempGroup.getLeftEnd();
                    String rightEnd = tempGroup.getRightEnd();
                    LineGroups newLeftLine = new LineGroups();
                    LineGroups newRightLine = new LineGroups();
                    Node leftEndElement = null;
                    Node rightEndElement = null;
                    boolean leftText = false;
                    boolean leftStation = false;
                    boolean rightText = false;
                    boolean rightStation = false;
                    for (int l = dataManager.getShapes().size() - 1; l >= 0; l--) {
                        if (dataManager.getShapes().get(l) instanceof DraggableText) {
                            DraggableText helpText = (DraggableText) dataManager.getShapes().get(l);
                            if (helpText.getText().equals(leftEnd) && helpText.getStartResult()) {
                                leftEndElement = helpText;
                                leftText = true;
                            } else if (helpText.getText().equals(rightEnd) && helpText.getEndResult()) {
                                rightEndElement = helpText;
                                rightText = true;
                            }
                        } else if (dataManager.getShapes().get(l) instanceof DraggableStation) {
                            DraggableStation helpStation = (DraggableStation) dataManager.getShapes().get(l);
                            if (helpStation.getStationName().equals(leftEnd)) {
                                leftEndElement = helpStation;
                                leftStation = true;
                            } else if (helpStation.getStationName().equals(rightEnd)) {
                                rightEndElement = helpStation;
                                rightStation = true;
                            }
                        }
                    }
                    newStationEnd.setLineName(tempGroup.getLineName());
                    System.out.println(leftText);
                    System.out.println(rightText);
                    System.out.println(leftStation);
                    System.out.println(rightStation);
                    if (leftText) {
                        if (rightText) { //LEFT NODE IS TEXT RIGHT NODE IS TEXT
                            tempGroup.startXProperty().unbind();
                            tempGroup.startYProperty().unbind();
                            tempGroup.endXProperty().unbind();
                            tempGroup.endYProperty().unbind();
                            newLeftLine.setLineName(tempGroup.getLineName());
                            newRightLine.setLineName(tempGroup.getLineName());

                            newLeftLine.startXProperty().bind(((DraggableText) leftEndElement).xProperty());
                            newLeftLine.startYProperty().bind(((DraggableText) leftEndElement).yProperty());
                            newLeftLine.endXProperty().bind(stationShape.centerXProperty());
                            newLeftLine.endYProperty().bind(stationShape.centerYProperty());

                            newRightLine.startXProperty().bind(stationShape.centerXProperty());
                            newRightLine.startYProperty().bind(stationShape.centerYProperty());
                            newRightLine.endXProperty().bind(((DraggableText) rightEndElement).xProperty());
                            newRightLine.endYProperty().bind(((DraggableText) rightEndElement).yProperty());

                            newLeftLine.getMetroStations().add(stationShape.getStationName());
                            for (int l = 0; l < tempTracker.size(); l++) {
                                if (tempTracker.get(l).getName().equals(newLeftLine.getLineName())) {
                                    tempTracker.get(l).addStationName(stationShape.getStationName());
                                }
                            }
                            newLeftLine.setLeftEnd(((DraggableText) leftEndElement).getText());
                            newLeftLine.setRightend(stationShape.getStationName());
                            newRightLine.setLeftEnd(stationShape.getStationName());
                            newRightLine.setRightend(((DraggableText) rightEndElement).getText());
                            
                            newLeftLine.setLeftElementType(((DraggableText) leftEndElement).getClass().toString());
                            newLeftLine.setRightElementType(stationShape.getClass().toString());
                            newRightLine.setLeftElementType(stationShape.getClass().toString());
                            newRightLine.setRightElementType(((DraggableText) rightEndElement).getClass().toString());
                            
                            newLeftLine.setStrokeWidth(5);
                            newRightLine.setStrokeWidth(5);
                            newLeftLine.setStroke(tempGroup.getStroke());
                            newRightLine.setStroke(tempGroup.getStroke());

                            newStationEnd.setLeftEnd(((DraggableText) leftEndElement).getText());
                            newStationEnd.setRightEnd(((DraggableText) rightEndElement).getText());
                            if (!stationEnds.isEmpty()) {
                                for (int l = 0; l < stationEnds.size(); l++) {
                                    if (stationEnds.get(l).getLineName().equals(tempGroup.getLineName())) {
                                        stationEnds.get(l).setLeftEnd(((DraggableText) leftEndElement).getText());
                                        stationEnds.get(l).setRightEnd(((DraggableText) rightEndElement).getText());
                                    } else {
                                        stationEnds.add(newStationEnd);
                                    }
                                }
                            } else {
                                stationEnds.add(newStationEnd);
                            }

                            workspace.getCanvas().getChildren().remove(tempGroup);
                            workspace.getCanvas().getChildren().add(newLeftLine);
                            workspace.getCanvas().getChildren().add(newRightLine);

                            newLeftLine.setFirstLine(true);
                            newRightLine.setLastLine(true);

                        } else if (rightStation) { //LEFT NODE IS TEXT RIGHT NODE IS STATION
                            tempGroup.startXProperty().unbind();
                            tempGroup.startYProperty().unbind();
                            tempGroup.endXProperty().unbind();
                            tempGroup.endYProperty().unbind();
                            newLeftLine.setLineName(tempGroup.getLineName());
                            newRightLine.setLineName(tempGroup.getLineName());

                            newLeftLine.startXProperty().bind(((DraggableText) leftEndElement).xProperty());
                            newLeftLine.startYProperty().bind(((DraggableText) leftEndElement).yProperty());
                            newLeftLine.endXProperty().bind(stationShape.centerXProperty());
                            newLeftLine.endYProperty().bind(stationShape.centerYProperty());

                            newRightLine.startXProperty().bind(stationShape.centerXProperty());
                            newRightLine.startYProperty().bind(stationShape.centerYProperty());
                            newRightLine.endXProperty().bind(((DraggableStation) rightEndElement).centerXProperty());
                            newRightLine.endYProperty().bind(((DraggableStation) rightEndElement).centerYProperty());

                            newLeftLine.getMetroStations().add(stationShape.getStationName());
                            for (int l = 0; l < tempTracker.size(); l++) {
                                if (tempTracker.get(l).getName().equals(newLeftLine.getLineName())) {
                                    tempTracker.get(l).addStationName(stationShape.getStationName());
                                }
                            }
                            newLeftLine.setLeftEnd(((DraggableText) leftEndElement).getText());
                            newLeftLine.setRightend(stationShape.getStationName());
                            newRightLine.setLeftEnd(stationShape.getStationName());
                            newRightLine.setRightend(((DraggableStation) rightEndElement).getStationName());
                            
                            newLeftLine.setLeftElementType(((DraggableText) leftEndElement).getClass().toString());
                            newLeftLine.setRightElementType(stationShape.getClass().toString());
                            newRightLine.setLeftElementType(stationShape.getClass().toString());
                            newRightLine.setRightElementType(((DraggableStation) rightEndElement).getClass().toString());
                            
                            newLeftLine.setStrokeWidth(5);
                            newRightLine.setStrokeWidth(5);
                            newLeftLine.setStroke(tempGroup.getStroke());
                            newRightLine.setStroke(tempGroup.getStroke());

                            newStationEnd.setLeftEnd(((DraggableText) leftEndElement).getText());
                            newStationEnd.setRightEnd(((DraggableStation) rightEndElement).getStationName());
                            if (!stationEnds.isEmpty()) {
                                for (int l = 0; l < stationEnds.size(); l++) {
                                    if (stationEnds.get(l).getLineName().equals(tempGroup.getLineName())) {
                                        stationEnds.get(l).setLeftEnd(((DraggableText) leftEndElement).getText());
                                        stationEnds.get(l).setRightEnd(((DraggableStation) rightEndElement).getStationName());
                                    } else {
                                        stationEnds.add(newStationEnd);
                                    }
                                }
                            } else {
                                stationEnds.add(newStationEnd);
                            }
                            ArrayList<StationEnds> tempEnds = ((DraggableStation) rightEndElement).getStationEnds();
                            if (!tempEnds.isEmpty()) {
                                for (int l = 0; l < tempEnds.size(); l++) {
                                    if (tempEnds.get(l).getLineName().equals(tempGroup.getLineName())) {
                                        tempEnds.get(l).setLeftEnd(stationShape.getStationName());
                                    }
                                }
                            }

                            workspace.getCanvas().getChildren().remove(tempGroup);
                            workspace.getCanvas().getChildren().add(newLeftLine);
                            workspace.getCanvas().getChildren().add(newRightLine);

                            newRightLine.setLastLine(true);
                        }
                    } else if (leftStation) { //LEFT NODE IS STATION RIGHT NODE IS TEXT
                        if (rightText) {
                            tempGroup.startXProperty().unbind();
                            tempGroup.startYProperty().unbind();
                            tempGroup.endXProperty().unbind();
                            tempGroup.endYProperty().unbind();
                            newLeftLine.setLineName(tempGroup.getLineName());
                            newRightLine.setLineName(tempGroup.getLineName());

                            newLeftLine.startXProperty().bind(((DraggableStation) leftEndElement).centerXProperty());
                            newLeftLine.startYProperty().bind(((DraggableStation) leftEndElement).centerYProperty());
                            newLeftLine.endXProperty().bind(stationShape.centerXProperty());
                            newLeftLine.endYProperty().bind(stationShape.centerYProperty());

                            newRightLine.startXProperty().bind(stationShape.centerXProperty());
                            newRightLine.startYProperty().bind(stationShape.centerYProperty());
                            newRightLine.endXProperty().bind(((DraggableText) rightEndElement).xProperty());
                            newRightLine.endYProperty().bind(((DraggableText) rightEndElement).yProperty());

                            newLeftLine.getMetroStations().add(stationShape.getStationName());
                            for (int l = 0; l < tempTracker.size(); l++) {
                                if (tempTracker.get(l).getName().equals(newLeftLine.getLineName())) {
                                    tempTracker.get(l).addStationName(stationShape.getStationName());
                                }
                            }
                            newLeftLine.setLeftEnd(((DraggableStation) leftEndElement).getStationName());
                            newLeftLine.setRightend(stationShape.getStationName());
                            newRightLine.setLeftEnd(stationShape.getStationName());
                            newRightLine.setRightend(((DraggableText) rightEndElement).getText());
                            
                            newLeftLine.setLeftElementType(((DraggableStation) leftEndElement).getClass().toString());
                            newLeftLine.setRightElementType(stationShape.getClass().toString());
                            newRightLine.setLeftElementType(stationShape.getClass().toString());
                            newRightLine.setRightElementType(((DraggableText) rightEndElement).getClass().toString());
                            
                            newLeftLine.setStrokeWidth(5);
                            newRightLine.setStrokeWidth(5);
                            newLeftLine.setStroke(tempGroup.getStroke());
                            newRightLine.setStroke(tempGroup.getStroke());

                            newStationEnd.setLeftEnd(((DraggableStation) leftEndElement).getStationName());
                            newStationEnd.setRightEnd(((DraggableText) rightEndElement).getText());
                            if (!stationEnds.isEmpty()) {
                                for (int l = 0; l < stationEnds.size(); l++) {
                                    if (stationEnds.get(l).getLineName().equals(tempGroup.getLineName())) {
                                        stationEnds.get(l).setLeftEnd(((DraggableStation) leftEndElement).getStationName());
                                        stationEnds.get(l).setRightEnd(((DraggableText) rightEndElement).getText());
                                    } else {
                                        stationEnds.add(newStationEnd);
                                    }
                                }
                            } else {
                                stationEnds.add(newStationEnd);
                            }
                            ArrayList<StationEnds> tempEnds = ((DraggableStation) leftEndElement).getStationEnds();
                            if (!tempEnds.isEmpty()) {
                                for (int l = 0; l < tempEnds.size(); l++) {
                                    if (tempEnds.get(l).getLineName().equals(tempGroup.getLineName())) {
                                        tempEnds.get(l).setRightEnd(stationShape.getStationName());
                                    }
                                }
                            }

                            workspace.getCanvas().getChildren().remove(tempGroup);
                            workspace.getCanvas().getChildren().add(newLeftLine);
                            workspace.getCanvas().getChildren().add(newRightLine);

                            newRightLine.setLastLine(true);

                        } else if (rightStation) { //LEFT NODE IS STATION RIGHT NODE IS STATION
                            tempGroup.startXProperty().unbind();
                            tempGroup.startYProperty().unbind();
                            tempGroup.endXProperty().unbind();
                            tempGroup.endYProperty().unbind();
                            newLeftLine.setLineName(tempGroup.getLineName());
                            newRightLine.setLineName(tempGroup.getLineName());

                            newLeftLine.startXProperty().bind(((DraggableStation) leftEndElement).centerXProperty());
                            newLeftLine.startYProperty().bind(((DraggableStation) leftEndElement).centerYProperty());
                            newLeftLine.endXProperty().bind(stationShape.centerXProperty());
                            newLeftLine.endYProperty().bind(stationShape.centerYProperty());

                            newRightLine.startXProperty().bind(stationShape.centerXProperty());
                            newRightLine.startYProperty().bind(stationShape.centerYProperty());
                            newRightLine.endXProperty().bind(((DraggableStation) rightEndElement).centerXProperty());
                            newRightLine.endYProperty().bind(((DraggableStation) rightEndElement).centerYProperty());

                            newLeftLine.getMetroStations().add(stationShape.getStationName());
                            for (int l = 0; l < tempTracker.size(); l++) {
                                if (tempTracker.get(l).getName().equals(newLeftLine.getLineName())) {
                                    tempTracker.get(l).addStationName(stationShape.getStationName());
                                }
                            }
                            newLeftLine.setLeftEnd(((DraggableStation) leftEndElement).getStationName());
                            newLeftLine.setRightend(stationShape.getStationName());
                            newRightLine.setLeftEnd(stationShape.getStationName());
                            newRightLine.setRightend(((DraggableStation) rightEndElement).getStationName());
                            
                            newLeftLine.setLeftElementType(((DraggableStation) leftEndElement).getClass().toString());
                            newLeftLine.setRightElementType(stationShape.getClass().toString());
                            newRightLine.setLeftElementType(stationShape.getClass().toString());
                            newRightLine.setRightElementType(((DraggableStation) rightEndElement).getClass().toString());
                            
                            newLeftLine.setStrokeWidth(5);
                            newRightLine.setStrokeWidth(5);
                            newLeftLine.setStroke(tempGroup.getStroke());
                            newRightLine.setStroke(tempGroup.getStroke());

                            newStationEnd.setLeftEnd(((DraggableStation) leftEndElement).getStationName());
                            newStationEnd.setRightEnd(((DraggableStation) rightEndElement).getStationName());
                            if (!stationEnds.isEmpty()) {
                                for (int l = 0; l < stationEnds.size(); l++) {
                                    if (stationEnds.get(l).getLineName().equals(tempGroup.getLineName())) {
                                        stationEnds.get(l).setLeftEnd(((DraggableStation) leftEndElement).getStationName());
                                        stationEnds.get(l).setRightEnd(((DraggableStation) rightEndElement).getStationName());
                                    } else {
                                        stationEnds.add(newStationEnd);
                                    }
                                }
                            } else {
                                stationEnds.add(newStationEnd);
                            }
                            // stationShape.setLeftEnd(((DraggableStation) leftEndElement).getStationName());
                            // stationShape.setRightend(((DraggableStation) rightEndElement).getStationName());

                            ArrayList<StationEnds> tempLeftEnds = ((DraggableStation) leftEndElement).getStationEnds();
                            if (!tempLeftEnds.isEmpty()) {
                                for (int l = 0; l < tempLeftEnds.size(); l++) {
                                    if (tempLeftEnds.get(l).getLineName().equals(tempGroup.getLineName())) {
                                        tempLeftEnds.get(l).setRightEnd(stationShape.getStationName());
                                    }
                                }
                            }
                            ArrayList<StationEnds> tempRightEnds = ((DraggableStation) rightEndElement).getStationEnds();
                            if (!tempRightEnds.isEmpty()) {
                                for (int l = 0; l < tempRightEnds.size(); l++) {
                                    if (tempRightEnds.get(l).getLineName().equals(tempGroup.getLineName())) {
                                        tempRightEnds.get(l).setLeftEnd(stationShape.getStationName());
                                    }
                                }
                            }
                            //  ((DraggableStation) leftEndElement).setRightend(stationShape.getStationName());
                            //  ((DraggableStation) rightEndElement).setLeftEnd(stationShape.getStationName());

                            workspace.getCanvas().getChildren().remove(tempGroup);
                            workspace.getCanvas().getChildren().add(newLeftLine);
                            workspace.getCanvas().getChildren().add(newRightLine);

                            newRightLine.setLastLine(true);
                        }
                    }
                    System.out.println("Start result : " + leftEndElement.getClass().toString());
                    System.out.println("End result : " + rightEndElement.getClass().toString());
                    break;
                }
            }
        }

    }


    @Override
    public void undoTransaction() {
        stationController.removeSpeificStationFromLine(stationString, lineString);
    }
}
