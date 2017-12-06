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
import M3.gui.m3Workspace;
import djf.AppTemplate;
import java.util.ArrayList;
import javafx.scene.control.Alert;
import javafx.scene.paint.Paint;
import jtps.jTPS_Transaction;

/**
 *
 * @author Augusto
 */
public class EditLine_Transaction implements jTPS_Transaction {

    
    public Paint oldColor;
    public Paint newColor;
    public String oldText;
    public String newText;
    AppTemplate appHelp;
    m3Data dataManager;
    m3Workspace workspace;
    String editedText;
    ArrayList<StationTracker> tempTracker;

    public EditLine_Transaction(AppTemplate app, m3Workspace work, Paint modifiedColor, String modifiedText, String nodeToModify, ArrayList<StationTracker> tracker) {
        appHelp = app;
        workspace = work;
        newColor = modifiedColor;
        newText = modifiedText;
        editedText = nodeToModify;
        tempTracker = tracker;
        dataManager = (m3Data) app.getDataComponent();

    }

    @Override
    public void doTransaction() {
        if (dataManager.getLines().size() != 0) {
            if (dataManager.getLines().contains(newText)) {
                Alert warningAlert = new Alert(Alert.AlertType.WARNING);
                warningAlert.setTitle("Similar name");
                warningAlert.setContentText("This line already exists");
                warningAlert.showAndWait();
            } else if (newText != null && !newText.isEmpty()) {
                for (int i = dataManager.getShapes().size() - 1; i >= 0; i--) {
                    if (dataManager.getShapes().get(i) instanceof LineGroups) {
                        LineGroups tempGroup = (LineGroups) dataManager.getShapes().get(i);
                        if (tempGroup.getLineName().equals(editedText)) {
                            oldColor = tempGroup.getStroke();
                            tempGroup.setStroke(newColor);
                        }
                    } else if (dataManager.getShapes().get(i) instanceof DraggableText) {
                        DraggableText tempText = (DraggableText) dataManager.getShapes().get(i);
                        if (tempText.getText().equals(editedText)) {
                            oldText = tempText.getText();
                            tempText.setText(newText);
                        }
                    }
                }
                for (int i = dataManager.getShapes().size() - 1; i >= 0; i--) {
                    if (dataManager.getShapes().get(i) instanceof DraggableStation) {
                        DraggableStation tempStation = (DraggableStation) dataManager.getShapes().get(i);
                        ArrayList<StationEnds> stationEnds = tempStation.getStationEnds();
                        for (int l = 0; l < stationEnds.size(); l++) {
                            if (stationEnds.get(l).getLeftEnd().equals(editedText)) {
                                stationEnds.get(l).setLeftEnd(newText);
                            }
                            if (stationEnds.get(l).getRightEnd().equals(editedText)) {
                                stationEnds.get(l).setRightEnd(newText);
                            }
                        }

                    } else if (dataManager.getShapes().get(i) instanceof LineGroups) {
                        LineGroups tempGroup = (LineGroups) dataManager.getShapes().get(i);
                        if (tempGroup.getLineName().equals(editedText)) {
                            tempGroup.setLineName(newText);
                        }
                        if (tempGroup.getLeftEnd().equals(editedText)) {
                            tempGroup.setLeftEnd(newText);
                        }
                        if (tempGroup.getRightEnd().equals(editedText)) {
                            tempGroup.setRightend(newText);
                        }
                    }
                }
                for (int i = 0; i < tempTracker.size(); i++) {
                    if (tempTracker.get(i).getName().equals(editedText)) {
                        tempTracker.get(i).setName(newText);
                    }
                }
                dataManager.removeLineName(editedText);
                dataManager.addLineName(newText);
                workspace.getLineBox().getItems().add(newText);
                workspace.getLineBox().getItems().remove(editedText);
                workspace.getLineBox().valueProperty().set(newText);
                System.out.println(workspace.getLineBox().getValue());
            }
        } else {
            if (newText != null && !newText.isEmpty()) {
                for (int i = dataManager.getShapes().size() - 1; i >= 0; i--) {
                    if (dataManager.getShapes().get(i) instanceof LineGroups) {
                        LineGroups tempGroup = (LineGroups) dataManager.getShapes().get(i);
                        if (tempGroup.getLineName().equals(editedText)) {
                            tempGroup.setStroke(newColor);
                        }
                    } else if (dataManager.getShapes().get(i) instanceof DraggableText) {
                        DraggableText tempText = (DraggableText) dataManager.getShapes().get(i);
                        if (tempText.getText().equals(editedText)) {
                            tempText.setText(newText);
                        }
                    }
                }
                for (int i = dataManager.getShapes().size() - 1; i >= 0; i--) {
                    if (dataManager.getShapes().get(i) instanceof DraggableStation) {
                        DraggableStation tempStation = (DraggableStation) dataManager.getShapes().get(i);
                        ArrayList<StationEnds> stationEnds = tempStation.getStationEnds();
                        for (int l = 0; l < stationEnds.size(); l++) {
                            if (stationEnds.get(l).getLeftEnd().equals(editedText)) {
                                stationEnds.get(l).setLeftEnd(newText);
                            }
                            if (stationEnds.get(l).getRightEnd().equals(editedText)) {
                                stationEnds.get(l).setRightEnd(newText);
                            }
                        }

                    } else if (dataManager.getShapes().get(i) instanceof LineGroups) {
                        LineGroups tempGroup = (LineGroups) dataManager.getShapes().get(i);
                        if (tempGroup.getLineName().equals(editedText)) {
                            tempGroup.setLineName(newText);
                        }
                        if (tempGroup.getLeftEnd().equals(editedText)) {
                            tempGroup.setLeftEnd(newText);
                        }
                        if (tempGroup.getRightEnd().equals(editedText)) {
                            tempGroup.setRightend(newText);
                        }
                    }
                }
                for (int i = 0; i < tempTracker.size(); i++) {
                    if (tempTracker.get(i).getName().equals(editedText)) {
                        tempTracker.get(i).setName(newText);
                    }
                }
                dataManager.removeLineName(editedText);
                dataManager.addLineName(newText);
                workspace.getLineBox().getItems().add(newText);
                workspace.getLineBox().getItems().remove(editedText);
                workspace.getLineBox().valueProperty().set(newText);
            } else {
                Alert warningAlert = new Alert(Alert.AlertType.WARNING);
                warningAlert.setTitle("Error");
                warningAlert.setContentText("There is nothing typed into the field");
                warningAlert.showAndWait();
            }
        }
    }

    @Override
    public void undoTransaction() {
        if (dataManager.getLines().size() != 0) {
            if (dataManager.getLines().contains(oldText)) {
                Alert warningAlert = new Alert(Alert.AlertType.WARNING);
                warningAlert.setTitle("Similar name");
                warningAlert.setContentText("This line already exists");
                warningAlert.showAndWait();
            } else if (oldText != null && !oldText.isEmpty()) {
                for (int i = dataManager.getShapes().size() - 1; i >= 0; i--) {
                    if (dataManager.getShapes().get(i) instanceof LineGroups) {
                        LineGroups tempGroup = (LineGroups) dataManager.getShapes().get(i);
                        if (tempGroup.getLineName().equals(newText)) {
                            tempGroup.setStroke(oldColor);
                        }
                    } else if (dataManager.getShapes().get(i) instanceof DraggableText) {
                        DraggableText tempText = (DraggableText) dataManager.getShapes().get(i);
                        if (tempText.getText().equals(newText)) {
                            tempText.setText(oldText);
                        }
                    }
                }
                for (int i = dataManager.getShapes().size() - 1; i >= 0; i--) {
                    if (dataManager.getShapes().get(i) instanceof DraggableStation) {
                        DraggableStation tempStation = (DraggableStation) dataManager.getShapes().get(i);
                        ArrayList<StationEnds> stationEnds = tempStation.getStationEnds();
                        for (int l = 0; l < stationEnds.size(); l++) {
                            if (stationEnds.get(l).getLeftEnd().equals(newText)) {
                                stationEnds.get(l).setLeftEnd(oldText);
                            }
                            if (stationEnds.get(l).getRightEnd().equals(newText)) {
                                stationEnds.get(l).setRightEnd(oldText);
                            }
                        }

                    } else if (dataManager.getShapes().get(i) instanceof LineGroups) {
                        LineGroups tempGroup = (LineGroups) dataManager.getShapes().get(i);
                        if (tempGroup.getLineName().equals(newText)) {
                            tempGroup.setLineName(oldText);
                        }
                        if (tempGroup.getLeftEnd().equals(newText)) {
                            tempGroup.setLeftEnd(oldText);
                        }
                        if (tempGroup.getRightEnd().equals(newText)) {
                            tempGroup.setRightend(oldText);
                        }
                    }
                }
                for (int i = 0; i < tempTracker.size(); i++) {
                    if (tempTracker.get(i).getName().equals(newText)) {
                        tempTracker.get(i).setName(oldText);
                    }
                }
                dataManager.removeLineName(newText);
                dataManager.addLineName(oldText);
                workspace.getLineBox().getItems().add(oldText);
                workspace.getLineBox().getItems().remove(newText);
                workspace.getLineBox().valueProperty().set(oldText);
                System.out.println(workspace.getLineBox().getValue());
            }
        } else {
            if (oldText != null && !oldText.isEmpty()) {
                for (int i = dataManager.getShapes().size() - 1; i >= 0; i--) {
                    if (dataManager.getShapes().get(i) instanceof LineGroups) {
                        LineGroups tempGroup = (LineGroups) dataManager.getShapes().get(i);
                        if (tempGroup.getLineName().equals(newText)) {
                            tempGroup.setStroke(oldColor);
                        }
                    } else if (dataManager.getShapes().get(i) instanceof DraggableText) {
                        DraggableText tempText = (DraggableText) dataManager.getShapes().get(i);
                        if (tempText.getText().equals(newText)) {
                            tempText.setText(oldText);
                        }
                    }
                }
                for (int i = dataManager.getShapes().size() - 1; i >= 0; i--) {
                    if (dataManager.getShapes().get(i) instanceof DraggableStation) {
                        DraggableStation tempStation = (DraggableStation) dataManager.getShapes().get(i);
                        ArrayList<StationEnds> stationEnds = tempStation.getStationEnds();
                        for (int l = 0; l < stationEnds.size(); l++) {
                            if (stationEnds.get(l).getLeftEnd().equals(newText)) {
                                stationEnds.get(l).setLeftEnd(oldText);
                            }
                            if (stationEnds.get(l).getRightEnd().equals(newText)) {
                                stationEnds.get(l).setRightEnd(oldText);
                            }
                        }

                    } else if (dataManager.getShapes().get(i) instanceof LineGroups) {
                        LineGroups tempGroup = (LineGroups) dataManager.getShapes().get(i);
                        if (tempGroup.getLineName().equals(newText)) {
                            tempGroup.setLineName(oldText);
                        }
                        if (tempGroup.getLeftEnd().equals(newText)) {
                            tempGroup.setLeftEnd(oldText);
                        }
                        if (tempGroup.getRightEnd().equals(newText)) {
                            tempGroup.setRightend(oldText);
                        }
                    }
                }
                for (int i = 0; i < tempTracker.size(); i++) {
                    if (tempTracker.get(i).getName().equals(newText)) {
                        tempTracker.get(i).setName(oldText);
                    }
                }
                dataManager.removeLineName(newText);
                dataManager.addLineName(oldText);
                workspace.getLineBox().getItems().add(oldText);
                workspace.getLineBox().getItems().remove(newText);
                workspace.getLineBox().valueProperty().set(oldText);
            } else {
                Alert warningAlert = new Alert(Alert.AlertType.WARNING);
                warningAlert.setTitle("Error");
                warningAlert.setContentText("There is nothing typed into the field");
                warningAlert.showAndWait();
            }
        }
    }
}
