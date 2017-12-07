/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package M3.gui;

import M3.data.DraggableStation;
import M3.data.DraggableText;
import M3.data.LineGroups;
import M3.data.StationEnds;
import M3.data.StationTracker;
import M3.data.m3Data;
import djf.AppTemplate;
import java.util.ArrayList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import jtps.jTPS_Transaction;

/**
 *
 * @author Augusto
 */
public class StationController {
    AppTemplate app;
    m3Data dataManager;
    Color imageFill;
    jTPS_Transaction transaction;
   
    public StationController(AppTemplate initApp) {
        app = initApp;
        dataManager = (m3Data) app.getDataComponent();
    }
    
    
    public void removeSpecificStation(String name) {
        DraggableStation stationShape = new DraggableStation();
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        for (int i = 0; i < dataManager.getShapes().size(); i++) {
            if (dataManager.getShapes().get(i) instanceof DraggableStation) {
                DraggableStation tempStation = (DraggableStation) dataManager.getShapes().get(i);
                if (tempStation.getStationName().equals(name)) {
                    stationShape = tempStation;
                }
            }
        }
        ArrayList<StationTracker> tempTracker = dataManager.getStationTracker();
        String stationName = "";
        Scene scene = app.getGUI().getPrimaryScene();
        String compareLine = "";
        
        ArrayList<StationEnds> stationEnds = stationShape.getStationEnds();
        while (!stationEnds.isEmpty()) {
            String lineString = stationEnds.get(0).getLineName();
            for (int i = 0; i < dataManager.getShapes().size(); i++) {
                Node temp = (Node) dataManager.getShapes().get(i);
                if (temp instanceof LineGroups) {
                    LineGroups tempGroup = (LineGroups) temp;
                    if (tempGroup.getLineName().equals(lineString)) { //THIS MEANS WE HAVE THE CORRECT LINE IN TEMPGROUP
                        String leftStationEnd = "";
                        String rightStationEnd = "";
                        for (int l = 0; l < stationEnds.size(); l++) {
                            if (stationEnds.get(l).getLineName().equals(lineString)) {
                                leftStationEnd = stationEnds.get(l).getLeftEnd(); //LEFT END OF THE STATION
                                rightStationEnd = stationEnds.get(l).getRightEnd(); //RIGHT END OF THE STATION
                            }
                        }
                        //    String leftStationEnd = stationShape.getLeftEnd(); //THIS IS THE LEFT END OF THE STATION
                        //   String rightStationEnd = stationShape.getRightEnd(); //THIS IS THE RIGHT END OF THE STATION
                        String leftEnd = tempGroup.getLeftEnd();
                        String rightEnd = tempGroup.getRightEnd();
                        LineGroups oldLeftLine = new LineGroups();
                        LineGroups oldRightLine = new LineGroups();
                        LineGroups newLine = new LineGroups();
                        //CHECK IF LINE START AND END ARE THE STATION AND TEXT, THAT WILL BE YOUR ANSWER

                        Node leftEndElement = null;
                        Node rightEndElement = null;
                        boolean leftText = false;
                        boolean leftStation = false;
                        boolean rightText = false;
                        boolean rightStation = false;
                        for (int l = dataManager.getShapes().size() - 1; l >= 0; l--) {
                            if (dataManager.getShapes().get(l) instanceof LineGroups) {
                                LineGroups tempGroups = (LineGroups) dataManager.getShapes().get(l);
                                if (tempGroups.getRightEnd().equals(stationShape.getStationName()) && tempGroups.getLineName().equals(lineString)) {
                                    oldLeftLine = (LineGroups) tempGroups;
                                }
                                if (tempGroups.getLeftEnd().equals(stationShape.getStationName()) && tempGroups.getLineName().equals(lineString)) {
                                    oldRightLine = (LineGroups) tempGroups;
                                }
                            }
                        }
                        for (int l = dataManager.getShapes().size() - 1; l >= 0; l--) {
                            if (dataManager.getShapes().get(l) instanceof DraggableText) {
                                DraggableText helpText = (DraggableText) dataManager.getShapes().get(l);
                                if (helpText.getText().equals(leftStationEnd) && helpText.getStartResult()) {
                                    leftEndElement = helpText;
                                    leftText = true;
                                } else if (helpText.getText().equals(rightStationEnd) && helpText.getEndResult()) {
                                    rightEndElement = helpText;
                                    rightText = true;
                                }
                            } else if (dataManager.getShapes().get(l) instanceof DraggableStation) {
                                DraggableStation helpStation = (DraggableStation) dataManager.getShapes().get(l);
                                if (helpStation.getStationName().equals(leftStationEnd)) {
                                    leftEndElement = helpStation;
                                    leftStation = true;
                                } else if (helpStation.getStationName().equals(rightStationEnd)) {
                                    rightEndElement = helpStation;
                                    rightStation = true;
                                }
                            }
                        }

                        System.out.println(leftText);
                        System.out.println(rightText);
                        System.out.println(leftStation);
                        System.out.println(rightStation);
                        if (leftText) {
                            if (rightText) { //LEFT NODE IS TEXT RIGHT NODE IS TEXT
                                oldLeftLine.startXProperty().unbind();
                                oldLeftLine.startYProperty().unbind();
                                oldLeftLine.endXProperty().unbind();
                                oldLeftLine.endYProperty().unbind();
                                oldRightLine.startXProperty().unbind();
                                oldRightLine.startYProperty().unbind();
                                oldRightLine.endXProperty().unbind();
                                oldRightLine.endYProperty().unbind();
                                newLine.setLineName(oldLeftLine.getLineName());

                                newLine.startXProperty().bind(((DraggableText) leftEndElement).xProperty());
                                newLine.startYProperty().bind(((DraggableText) leftEndElement).yProperty());
                                newLine.endXProperty().bind(((DraggableText) rightEndElement).xProperty());
                                newLine.endYProperty().bind(((DraggableText) rightEndElement).yProperty());

                                for (int l = 0; l < tempTracker.size(); l++) {
                                    if (tempTracker.get(l).getName().equals(newLine.getLineName())) {
                                        tempTracker.get(l).removeStationName(stationShape.getStationName());
                                    }
                                }
                                newLine.setLeftEnd(((DraggableText) leftEndElement).getText());
                                newLine.setRightend(((DraggableText) rightEndElement).getText());
                                newLine.setStrokeWidth(5);
                                newLine.setStroke(oldLeftLine.getStroke());
                                workspace.getCanvas().getChildren().remove(oldLeftLine);
                                workspace.getCanvas().getChildren().remove(oldRightLine);
                                workspace.getCanvas().getChildren().add(newLine);
                                if (oldLeftLine.getFirstLine() && oldRightLine.getLastLine()) {
                                    newLine.setFirstLine(false);
                                    newLine.setLastLine(false);
                                } else if (oldLeftLine.getFirstLine()) {
                                    newLine.setFirstLine(true);
                                } else if (oldRightLine.getLastLine()) {
                                    newLine.setFirstLine(true);
                                }

                            } else if (rightStation) { //LEFT NODE IS TEXT RIGHT NODE IS STATION
                                oldLeftLine.startXProperty().unbind();
                                oldLeftLine.startYProperty().unbind();
                                oldLeftLine.endXProperty().unbind();
                                oldLeftLine.endYProperty().unbind();
                                oldRightLine.startXProperty().unbind();
                                oldRightLine.startYProperty().unbind();
                                oldRightLine.endXProperty().unbind();
                                oldRightLine.endYProperty().unbind();
                                newLine.setLineName(oldLeftLine.getLineName());

                                newLine.startXProperty().bind(((DraggableText) leftEndElement).xProperty());
                                newLine.startYProperty().bind(((DraggableText) leftEndElement).yProperty());
                                newLine.endXProperty().bind(((DraggableStation) rightEndElement).centerXProperty());
                                newLine.endYProperty().bind(((DraggableStation) rightEndElement).centerYProperty());

                                for (int l = 0; l < tempTracker.size(); l++) {
                                    if (tempTracker.get(l).getName().equals(newLine.getLineName())) {
                                        tempTracker.get(l).removeStationName(stationShape.getStationName());
                                    }
                                }
                                newLine.setLeftEnd(((DraggableText) leftEndElement).getText());
                                newLine.setRightend(((DraggableStation) rightEndElement).getStationName());

                                ArrayList<StationEnds> tempEnds = ((DraggableStation) rightEndElement).getStationEnds();
                                for (int l = 0; l < tempEnds.size(); l++) {
                                    if (tempEnds.get(l).getLineName().equals(lineString)) {
                                        tempEnds.get(l).setLeftEnd(((DraggableText) leftEndElement).getText());
                                    }
                                }
                                //  ((DraggableStation) rightEndElement).setLeftEnd(((DraggableText) leftEndElement).getText());

                                newLine.setStrokeWidth(5);
                                newLine.setStroke(oldLeftLine.getStroke());
                                workspace.getCanvas().getChildren().remove(oldLeftLine);
                                workspace.getCanvas().getChildren().remove(oldRightLine);
                                workspace.getCanvas().getChildren().add(newLine);
                                if (oldLeftLine.getFirstLine() && oldRightLine.getLastLine()) {
                                    newLine.setFirstLine(false);
                                    newLine.setLastLine(false);
                                } else if (oldLeftLine.getFirstLine()) {
                                    newLine.setFirstLine(true);
                                } else if (oldRightLine.getLastLine()) {
                                    newLine.setFirstLine(true);
                                }
                            }
                        } else if (leftStation) { //LEFT NODE IS STATION RIGHT NODE IS TEXT
                            if (rightText) {
                                oldLeftLine.startXProperty().unbind();
                                oldLeftLine.startYProperty().unbind();
                                oldLeftLine.endXProperty().unbind();
                                oldLeftLine.endYProperty().unbind();
                                oldRightLine.startXProperty().unbind();
                                oldRightLine.startYProperty().unbind();
                                oldRightLine.endXProperty().unbind();
                                oldRightLine.endYProperty().unbind();
                                newLine.setLineName(oldLeftLine.getLineName());

                                newLine.startXProperty().bind(((DraggableStation) leftEndElement).centerXProperty());
                                newLine.startYProperty().bind(((DraggableStation) leftEndElement).centerYProperty());
                                newLine.endXProperty().bind(((DraggableText) rightEndElement).xProperty());
                                newLine.endYProperty().bind(((DraggableText) rightEndElement).yProperty());

                                for (int l = 0; l < tempTracker.size(); l++) {
                                    if (tempTracker.get(l).getName().equals(newLine.getLineName())) {
                                        tempTracker.get(l).removeStationName(stationShape.getStationName());
                                    }
                                }
                                newLine.setLeftEnd(((DraggableStation) leftEndElement).getStationName());
                                newLine.setRightend(((DraggableText) rightEndElement).getText());

                                ArrayList<StationEnds> tempEnds = ((DraggableStation) leftEndElement).getStationEnds();
                                for (int l = 0; l < tempEnds.size(); l++) {
                                    if (tempEnds.get(l).getLineName().equals(lineString)) {
                                        tempEnds.get(l).setRightEnd(((DraggableText) rightEndElement).getText());
                                    }
                                }
                                //((DraggableStation) leftEndElement).setRightend(((DraggableText) rightEndElement).getText());

                                newLine.setStrokeWidth(5);
                                newLine.setStroke(oldLeftLine.getStroke());
                                workspace.getCanvas().getChildren().remove(oldLeftLine);
                                workspace.getCanvas().getChildren().remove(oldRightLine);
                                workspace.getCanvas().getChildren().add(newLine);

                                if (oldLeftLine.getFirstLine() && oldRightLine.getLastLine()) {
                                    newLine.setFirstLine(false);
                                    newLine.setLastLine(false);
                                } else if (oldLeftLine.getFirstLine()) {
                                    newLine.setFirstLine(true);
                                } else if (oldRightLine.getLastLine()) {
                                    newLine.setFirstLine(true);
                                }
                            } else if (rightStation) { //LEFT NODE IS STATION RIGHT NODE IS STATION
                                oldLeftLine.startXProperty().unbind();
                                oldLeftLine.startYProperty().unbind();
                                oldLeftLine.endXProperty().unbind();
                                oldLeftLine.endYProperty().unbind();
                                oldRightLine.startXProperty().unbind();
                                oldRightLine.startYProperty().unbind();
                                oldRightLine.endXProperty().unbind();
                                oldRightLine.endYProperty().unbind();
                                newLine.setLineName(oldLeftLine.getLineName());

                                newLine.startXProperty().bind(((DraggableStation) leftEndElement).centerXProperty());
                                newLine.startYProperty().bind(((DraggableStation) leftEndElement).centerYProperty());
                                newLine.endXProperty().bind(((DraggableStation) rightEndElement).centerXProperty());
                                newLine.endYProperty().bind(((DraggableStation) rightEndElement).centerYProperty());

                                for (int l = 0; l < tempTracker.size(); l++) {
                                    if (tempTracker.get(l).getName().equals(newLine.getLineName())) {
                                        tempTracker.get(l).removeStationName(stationShape.getStationName());
                                    }
                                }
                                newLine.setLeftEnd(((DraggableStation) leftEndElement).getStationName());
                                newLine.setRightend(((DraggableStation) rightEndElement).getStationName());

                                ArrayList<StationEnds> tempLeftEnds = ((DraggableStation) leftEndElement).getStationEnds();
                                for (int l = 0; l < tempLeftEnds.size(); l++) {
                                    if (tempLeftEnds.get(l).getLineName().equals(lineString)) {
                                        tempLeftEnds.get(l).setRightEnd(((DraggableStation) rightEndElement).getStationName());
                                    }
                                }
                                ArrayList<StationEnds> tempRightEnds = ((DraggableStation) rightEndElement).getStationEnds();
                                for (int l = 0; l < tempRightEnds.size(); l++) {
                                    if (tempRightEnds.get(l).getLineName().equals(lineString)) {
                                        tempRightEnds.get(l).setLeftEnd(((DraggableStation) leftEndElement).getStationName());
                                    }
                                }
                                //   ((DraggableStation) leftEndElement).setRightend(((DraggableStation) rightEndElement).getStationName());
                                // ((DraggableStation) rightEndElement).setLeftEnd(((DraggableStation) leftEndElement).getStationName());

                                newLine.setStrokeWidth(5);
                                newLine.setStroke(oldLeftLine.getStroke());
                                workspace.getCanvas().getChildren().remove(oldLeftLine);
                                workspace.getCanvas().getChildren().remove(oldRightLine);
                                workspace.getCanvas().getChildren().add(newLine);

                                if (oldLeftLine.getFirstLine() && oldRightLine.getLastLine()) {
                                    newLine.setFirstLine(false);
                                    newLine.setLastLine(false);
                                } else if (oldLeftLine.getFirstLine()) {
                                    newLine.setFirstLine(true);
                                } else if (oldRightLine.getLastLine()) {
                                    newLine.setFirstLine(true);
                                }
                            }
                        }

                        for (int l = 0; l < stationEnds.size(); l++) {
                            if (stationEnds.get(l).getLineName().equals(lineString)) {
                                stationEnds.remove(l);
                            }
                        }
                        System.out.println("Start result : " + leftEndElement.getClass().toString());
                        System.out.println("End result : " + rightEndElement.getClass().toString());
                        break;
                    }
                }
            }
        }
    }
    
    public void removeSpeificStationFromLine(String name, String stationLine) {
        DraggableStation stationShape = new DraggableStation();
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        for (int i = 0; i < dataManager.getShapes().size(); i++) {
            if (dataManager.getShapes().get(i) instanceof DraggableStation) {
                DraggableStation tempStation = (DraggableStation) dataManager.getShapes().get(i);
                if (tempStation.getStationName().equals(name)) {
                    stationShape = tempStation;
                }
            }
        }
        ArrayList<StationTracker> tempTracker = dataManager.getStationTracker();
        Scene scene = app.getGUI().getPrimaryScene();
        String compareLine = "";

        ArrayList<StationEnds> stationEnds = stationShape.getStationEnds();
        String lineString = stationLine;
        for (int i = 0; i < dataManager.getShapes().size(); i++) {
            Node temp = (Node) dataManager.getShapes().get(i);
            if (temp instanceof LineGroups) {
                LineGroups tempGroup = (LineGroups) temp;
                if (tempGroup.getLineName().equals(lineString)) { //THIS MEANS WE HAVE THE CORRECT LINE IN TEMPGROUP
                    String leftStationEnd = "";
                    String rightStationEnd = "";
                    for (int l = 0; l < stationEnds.size(); l++) {
                        if (stationEnds.get(l).getLineName().equals(lineString)) {
                            leftStationEnd = stationEnds.get(l).getLeftEnd(); //LEFT END OF THE STATION
                            rightStationEnd = stationEnds.get(l).getRightEnd(); //RIGHT END OF THE STATION
                        }
                    }
                    String leftEnd = tempGroup.getLeftEnd();
                    String rightEnd = tempGroup.getRightEnd();
                    LineGroups oldLeftLine = new LineGroups();
                    LineGroups oldRightLine = new LineGroups();
                    LineGroups newLine = new LineGroups();
                    //CHECK IF LINE START AND END ARE THE STATION AND TEXT, THAT WILL BE YOUR ANSWER

                    Node leftEndElement = null;
                    Node rightEndElement = null;
                    boolean leftText = false;
                    boolean leftStation = false;
                    boolean rightText = false;
                    boolean rightStation = false;
                    for (int l = dataManager.getShapes().size() - 1; l >= 0; l--) {
                        if (dataManager.getShapes().get(l) instanceof LineGroups) {
                            LineGroups tempGroups = (LineGroups) dataManager.getShapes().get(l);
                            if (tempGroups.getRightEnd().equals(stationShape.getStationName()) && tempGroups.getLineName().equals(lineString)) {
                                oldLeftLine = (LineGroups) tempGroups;
                            }
                            if (tempGroups.getLeftEnd().equals(stationShape.getStationName()) && tempGroups.getLineName().equals(lineString)) {
                                oldRightLine = (LineGroups) tempGroups;
                            }
                        }
                    }
                    for (int l = dataManager.getShapes().size() - 1; l >= 0; l--) {
                        if (dataManager.getShapes().get(l) instanceof DraggableText) {
                            DraggableText helpText = (DraggableText) dataManager.getShapes().get(l);
                            if (helpText.getText().equals(leftStationEnd) && helpText.getStartResult()) {
                                leftEndElement = helpText;
                                leftText = true;
                            } else if (helpText.getText().equals(rightStationEnd) && helpText.getEndResult()) {
                                rightEndElement = helpText;
                                rightText = true;
                            }
                        } else if (dataManager.getShapes().get(l) instanceof DraggableStation) {
                            DraggableStation helpStation = (DraggableStation) dataManager.getShapes().get(l);
                            if (helpStation.getStationName().equals(leftStationEnd)) {
                                leftEndElement = helpStation;
                                leftStation = true;
                            } else if (helpStation.getStationName().equals(rightStationEnd)) {
                                rightEndElement = helpStation;
                                rightStation = true;
                            }
                        }
                    }

                    System.out.println(leftText);
                    System.out.println(rightText);
                    System.out.println(leftStation);
                    System.out.println(rightStation);
                    if (leftText) {
                        if (rightText) { //LEFT NODE IS TEXT RIGHT NODE IS TEXT
                            oldLeftLine.startXProperty().unbind();
                            oldLeftLine.startYProperty().unbind();
                            oldLeftLine.endXProperty().unbind();
                            oldLeftLine.endYProperty().unbind();
                            oldRightLine.startXProperty().unbind();
                            oldRightLine.startYProperty().unbind();
                            oldRightLine.endXProperty().unbind();
                            oldRightLine.endYProperty().unbind();
                            newLine.setLineName(oldLeftLine.getLineName());

                            newLine.startXProperty().bind(((DraggableText) leftEndElement).xProperty());
                            newLine.startYProperty().bind(((DraggableText) leftEndElement).yProperty());
                            newLine.endXProperty().bind(((DraggableText) rightEndElement).xProperty());
                            newLine.endYProperty().bind(((DraggableText) rightEndElement).yProperty());

                            for (int l = 0; l < tempTracker.size(); l++) {
                                if (tempTracker.get(l).getName().equals(newLine.getLineName())) {
                                    tempTracker.get(l).removeStationName(stationShape.getStationName());
                                }
                            }
                            newLine.setLeftEnd(((DraggableText) leftEndElement).getText());
                            newLine.setRightend(((DraggableText) rightEndElement).getText());
                            newLine.setStrokeWidth(5);
                            newLine.setStroke(oldLeftLine.getStroke());
                            workspace.getCanvas().getChildren().remove(oldLeftLine);
                            workspace.getCanvas().getChildren().remove(oldRightLine);
                            workspace.getCanvas().getChildren().add(newLine);
                            if (oldLeftLine.getFirstLine() && oldRightLine.getLastLine()) {
                                newLine.setFirstLine(false);
                                newLine.setLastLine(false);
                            } else if (oldLeftLine.getFirstLine()) {
                                newLine.setFirstLine(true);
                            } else if (oldRightLine.getLastLine()) {
                                newLine.setFirstLine(true);
                            }

                        } else if (rightStation) { //LEFT NODE IS TEXT RIGHT NODE IS STATION
                            oldLeftLine.startXProperty().unbind();
                            oldLeftLine.startYProperty().unbind();
                            oldLeftLine.endXProperty().unbind();
                            oldLeftLine.endYProperty().unbind();
                            oldRightLine.startXProperty().unbind();
                            oldRightLine.startYProperty().unbind();
                            oldRightLine.endXProperty().unbind();
                            oldRightLine.endYProperty().unbind();
                            newLine.setLineName(oldLeftLine.getLineName());

                            newLine.startXProperty().bind(((DraggableText) leftEndElement).xProperty());
                            newLine.startYProperty().bind(((DraggableText) leftEndElement).yProperty());
                            newLine.endXProperty().bind(((DraggableStation) rightEndElement).centerXProperty());
                            newLine.endYProperty().bind(((DraggableStation) rightEndElement).centerYProperty());

                            for (int l = 0; l < tempTracker.size(); l++) {
                                if (tempTracker.get(l).getName().equals(newLine.getLineName())) {
                                    tempTracker.get(l).removeStationName(stationShape.getStationName());
                                }
                            }
                            newLine.setLeftEnd(((DraggableText) leftEndElement).getText());
                            newLine.setRightend(((DraggableStation) rightEndElement).getStationName());

                            ArrayList<StationEnds> tempEnds = ((DraggableStation) rightEndElement).getStationEnds();
                            for (int l = 0; l < tempEnds.size(); l++) {
                                if (tempEnds.get(l).getLineName().equals(lineString)) {
                                    tempEnds.get(l).setLeftEnd(((DraggableText) leftEndElement).getText());
                                }
                            }
                            //  ((DraggableStation) rightEndElement).setLeftEnd(((DraggableText) leftEndElement).getText());

                            newLine.setStrokeWidth(5);
                            newLine.setStroke(oldLeftLine.getStroke());
                            workspace.getCanvas().getChildren().remove(oldLeftLine);
                            workspace.getCanvas().getChildren().remove(oldRightLine);
                            workspace.getCanvas().getChildren().add(newLine);
                            if (oldLeftLine.getFirstLine() && oldRightLine.getLastLine()) {
                                newLine.setFirstLine(false);
                                newLine.setLastLine(false);
                            } else if (oldLeftLine.getFirstLine()) {
                                newLine.setFirstLine(true);
                            } else if (oldRightLine.getLastLine()) {
                                newLine.setFirstLine(true);
                            }
                        }
                    } else if (leftStation) { //LEFT NODE IS STATION RIGHT NODE IS TEXT
                        if (rightText) {
                            oldLeftLine.startXProperty().unbind();
                            oldLeftLine.startYProperty().unbind();
                            oldLeftLine.endXProperty().unbind();
                            oldLeftLine.endYProperty().unbind();
                            oldRightLine.startXProperty().unbind();
                            oldRightLine.startYProperty().unbind();
                            oldRightLine.endXProperty().unbind();
                            oldRightLine.endYProperty().unbind();
                            newLine.setLineName(oldLeftLine.getLineName());

                            newLine.startXProperty().bind(((DraggableStation) leftEndElement).centerXProperty());
                            newLine.startYProperty().bind(((DraggableStation) leftEndElement).centerYProperty());
                            newLine.endXProperty().bind(((DraggableText) rightEndElement).xProperty());
                            newLine.endYProperty().bind(((DraggableText) rightEndElement).yProperty());

                            for (int l = 0; l < tempTracker.size(); l++) {
                                if (tempTracker.get(l).getName().equals(newLine.getLineName())) {
                                    tempTracker.get(l).removeStationName(stationShape.getStationName());
                                }
                            }
                            newLine.setLeftEnd(((DraggableStation) leftEndElement).getStationName());
                            newLine.setRightend(((DraggableText) rightEndElement).getText());

                            ArrayList<StationEnds> tempEnds = ((DraggableStation) leftEndElement).getStationEnds();
                            for (int l = 0; l < tempEnds.size(); l++) {
                                if (tempEnds.get(l).getLineName().equals(lineString)) {
                                    tempEnds.get(l).setRightEnd(((DraggableText) rightEndElement).getText());
                                }
                            }
                            //((DraggableStation) leftEndElement).setRightend(((DraggableText) rightEndElement).getText());

                            newLine.setStrokeWidth(5);
                            newLine.setStroke(oldLeftLine.getStroke());
                            workspace.getCanvas().getChildren().remove(oldLeftLine);
                            workspace.getCanvas().getChildren().remove(oldRightLine);
                            workspace.getCanvas().getChildren().add(newLine);

                            if (oldLeftLine.getFirstLine() && oldRightLine.getLastLine()) {
                                newLine.setFirstLine(false);
                                newLine.setLastLine(false);
                            } else if (oldLeftLine.getFirstLine()) {
                                newLine.setFirstLine(true);
                            } else if (oldRightLine.getLastLine()) {
                                newLine.setFirstLine(true);
                            }
                        } else if (rightStation) { //LEFT NODE IS STATION RIGHT NODE IS STATION
                            oldLeftLine.startXProperty().unbind();
                            oldLeftLine.startYProperty().unbind();
                            oldLeftLine.endXProperty().unbind();
                            oldLeftLine.endYProperty().unbind();
                            oldRightLine.startXProperty().unbind();
                            oldRightLine.startYProperty().unbind();
                            oldRightLine.endXProperty().unbind();
                            oldRightLine.endYProperty().unbind();
                            newLine.setLineName(oldLeftLine.getLineName());

                            newLine.startXProperty().bind(((DraggableStation) leftEndElement).centerXProperty());
                            newLine.startYProperty().bind(((DraggableStation) leftEndElement).centerYProperty());
                            newLine.endXProperty().bind(((DraggableStation) rightEndElement).centerXProperty());
                            newLine.endYProperty().bind(((DraggableStation) rightEndElement).centerYProperty());

                            for (int l = 0; l < tempTracker.size(); l++) {
                                if (tempTracker.get(l).getName().equals(newLine.getLineName())) {
                                    tempTracker.get(l).removeStationName(stationShape.getStationName());
                                }
                            }
                            newLine.setLeftEnd(((DraggableStation) leftEndElement).getStationName());
                            newLine.setRightend(((DraggableStation) rightEndElement).getStationName());

                            ArrayList<StationEnds> tempLeftEnds = ((DraggableStation) leftEndElement).getStationEnds();
                            for (int l = 0; l < tempLeftEnds.size(); l++) {
                                if (tempLeftEnds.get(l).getLineName().equals(lineString)) {
                                    tempLeftEnds.get(l).setRightEnd(((DraggableStation) rightEndElement).getStationName());
                                }
                            }
                            ArrayList<StationEnds> tempRightEnds = ((DraggableStation) rightEndElement).getStationEnds();
                            for (int l = 0; l < tempRightEnds.size(); l++) {
                                if (tempRightEnds.get(l).getLineName().equals(lineString)) {
                                    tempRightEnds.get(l).setLeftEnd(((DraggableStation) leftEndElement).getStationName());
                                }
                            }
                            //   ((DraggableStation) leftEndElement).setRightend(((DraggableStation) rightEndElement).getStationName());
                            // ((DraggableStation) rightEndElement).setLeftEnd(((DraggableStation) leftEndElement).getStationName());

                            newLine.setStrokeWidth(5);
                            newLine.setStroke(oldLeftLine.getStroke());
                            workspace.getCanvas().getChildren().remove(oldLeftLine);
                            workspace.getCanvas().getChildren().remove(oldRightLine);
                            workspace.getCanvas().getChildren().add(newLine);

                            if (oldLeftLine.getFirstLine() && oldRightLine.getLastLine()) {
                                newLine.setFirstLine(false);
                                newLine.setLastLine(false);
                            } else if (oldLeftLine.getFirstLine()) {
                                newLine.setFirstLine(true);
                            } else if (oldRightLine.getLastLine()) {
                                newLine.setFirstLine(true);
                            }
                        }
                    }

                    for (int l = 0; l < stationEnds.size(); l++) {
                        if (stationEnds.get(l).getLineName().equals(lineString)) {
                            stationEnds.remove(l);
                        }
                    }
                    System.out.println("Start result : " + leftEndElement.getClass().toString());
                    System.out.println("End result : " + rightEndElement.getClass().toString());
                    break;
                }
            }
        }
    }
    
    public void addStationSpecificLine(String name, String lineString, StationEnds ends, int originalEndsPos){
        DraggableStation stationShape = new DraggableStation();
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        for (int i = 0; i < dataManager.getShapes().size(); i++) {
            if (dataManager.getShapes().get(i) instanceof DraggableStation) {
                DraggableStation tempStation = (DraggableStation) dataManager.getShapes().get(i);
                if (tempStation.getStationName().equals(name)) {
                    stationShape = tempStation;
                }
            }
        }
        ArrayList<StationTracker> tempTracker = dataManager.getStationTracker();
        StationEnds tempEnds = ends;
        Scene scene = app.getGUI().getPrimaryScene();
        ArrayList<StationEnds> stationEnds = stationShape.getStationEnds();
        String leftHelp = ends.getLeftEnd();
        String rightHelp = ends.getRightEnd();
        
        for (int i = 0; i < dataManager.getShapes().size(); i++) {
            Node temp = (Node) dataManager.getShapes().get(i);
            if (temp instanceof LineGroups) {
                LineGroups tempGroup = (LineGroups) temp;
                if (tempGroup.getLineName().equals(lineString)) { //THIS MEANS WE HAVE THE CORRECT LINE IN TEMPGROUP
                    for (int l = 0; l < dataManager.getShapes().size(); l++) {
                        if (dataManager.getShapes().get(l) instanceof LineGroups) {
                            LineGroups tempLine = (LineGroups) dataManager.getShapes().get(l);
                            if (tempLine.getLeftEnd().equals(leftHelp) && tempLine.getRightEnd().equals(rightHelp)) {
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
                            newLeftLine.setStrokeWidth(5);
                            newRightLine.setStrokeWidth(5);
                            newLeftLine.setStroke(tempGroup.getStroke());
                            newRightLine.setStroke(tempGroup.getStroke());

                            stationEnds.add(originalEndsPos, ends);
                            
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
                            newLeftLine.setStrokeWidth(5);
                            newRightLine.setStrokeWidth(5);
                            newLeftLine.setStroke(tempGroup.getStroke());
                            newRightLine.setStroke(tempGroup.getStroke());

                            stationEnds.add(originalEndsPos, ends);
                            
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
                            newLeftLine.setStrokeWidth(5);
                            newRightLine.setStrokeWidth(5);
                            newLeftLine.setStroke(tempGroup.getStroke());
                            newRightLine.setStroke(tempGroup.getStroke());

                            stationEnds.add(originalEndsPos, ends);
                            
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
                            newLeftLine.setStrokeWidth(5);
                            newRightLine.setStrokeWidth(5);
                            newLeftLine.setStroke(tempGroup.getStroke());
                            newRightLine.setStroke(tempGroup.getStroke());

                            stationEnds.add(originalEndsPos, ends);

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
}
