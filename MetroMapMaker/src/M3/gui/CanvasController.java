package M3.gui;

import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.shape.Shape;
import M3.data.m3Data;
import M3.data.Draggable;
import M3.data.m3State;
import static M3.data.m3State.DRAGGING_NOTHING;
import static M3.data.m3State.DRAGGING_SHAPE;
import static M3.data.m3State.SELECTING_SHAPE;
import djf.AppTemplate;
import M3.data.DraggableImage;
import M3.data.DraggableLine;
import M3.data.DraggableStation;
import M3.data.DraggableText;
import M3.data.LineGroups;
import M3.data.StationEnds;
import M3.data.StationTracker;
import static M3.data.m3State.ADDING_STATION_TO_LINE;
import static M3.data.m3State.REMOVING_STATION_FROM_LINE;
import M3.transaction.AddStationLine_Transaction;
import M3.transaction.DragShape_Transaction;
import M3.transaction.RemoveStationLine_Transaction;
import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Line;
import jtps.jTPS;
import jtps.jTPS_Transaction;
import jtps.test.AddToNum_Transaction;

/**
 * This class responds to interactions with the rendering surface.
 *
 * @author Richard McKenna
 * @author ?
 * @version 1.0
 */
public class CanvasController {

    AppTemplate app;
    jTPS_Transaction transaction;
    int destLocationX;
    int destLocationY;
    int originX;
    int originY;

    
    public CanvasController(AppTemplate initApp) {
        app = initApp;
    }

    public int getDestinationX() {
        return destLocationX;
    }

    public int getDestinationY() {
        return destLocationY;
    }

    public void processKeyPressed(KeyCode key){
        m3Data dataManager = (m3Data) app.getDataComponent();
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        if(key == KeyCode.W){
            workspace.getCanvasPane().setVvalue(workspace.getCanvasPane().getVvalue() - .05);
        }
        else if(key == KeyCode.A){
            workspace.getCanvasPane().setHvalue(workspace.getCanvasPane().getHvalue() - .05);
        }
        else if(key == KeyCode.S){
            workspace.getCanvasPane().setVvalue(workspace.getCanvasPane().getVvalue() + .05);
        }
        else if(key == KeyCode.D){
            workspace.getCanvasPane().setHvalue(workspace.getCanvasPane().getHvalue() + .05);
        }
    }
    
    /**
     * Respond to mouse presses on the rendering surface, which we call canvas,
     * but is actually a Pane.
     */
    public void processCanvasMousePress(int x, int y) {
        m3Data dataManager = (m3Data) app.getDataComponent();
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        if (dataManager.isInState(SELECTING_SHAPE)) {
            // SELECT THE TOP SHAPE
            Node shape = dataManager.selectTopShape(x, y);
            Scene scene = app.getGUI().getPrimaryScene();

            // AND START DRAGGING IT
            if (shape != null) {

                scene.setCursor(Cursor.MOVE);
                dataManager.setState(m3State.DRAGGING_SHAPE);
                originX = x;
                originY = y;
                app.getGUI().updateToolbarControls(false);
            } else {
                scene.setCursor(Cursor.DEFAULT);
                dataManager.setState(DRAGGING_NOTHING);
                app.getWorkspaceComponent().reloadWorkspace(dataManager);
            }
            workspace.reloadWorkspace(dataManager);
        } 
        
        else if (dataManager.isInState(ADDING_STATION_TO_LINE)) {
            Node shape = dataManager.selectTopShape(x, y);
            ArrayList<StationTracker> tempTracker = dataManager.getStationTracker();
            DraggableStation stationShape = (DraggableStation) shape;
            StationEnds newStationEnd = new StationEnds();
            Scene scene = app.getGUI().getPrimaryScene();
            if (shape != null) {
                ArrayList<StationEnds> stationEnds = stationShape.getStationEnds();
                String lineString = workspace.getLineBox().getSelectionModel().getSelectedItem();
                transaction = new AddStationLine_Transaction(app, workspace, stationShape, tempTracker,
                        scene, stationEnds, lineString, shape, newStationEnd);
                dataManager.getjTPS().addTransaction(transaction);
            } else {
                scene.setCursor(Cursor.DEFAULT);
                dataManager.setState(SELECTING_SHAPE);
                app.getWorkspaceComponent().reloadWorkspace(dataManager);
            }
        } 
        
        else if (dataManager.isInState(REMOVING_STATION_FROM_LINE)) {
            Node shape = dataManager.selectTopShape(x, y);
            ArrayList<StationTracker> tempTracker = dataManager.getStationTracker();
            String stationName = "";
            if (shape instanceof DraggableStation) {
                    stationName = ((DraggableStation) shape).getStationName();
            }
            DraggableStation stationShape = (DraggableStation) shape;
            
            Scene scene = app.getGUI().getPrimaryScene();
            String compareLine = "";
            if (shape != null) {
                ArrayList<StationEnds> stationEnds = stationShape.getStationEnds();
                String lineString = workspace.getLineBox().getSelectionModel().getSelectedItem();
                for(int i = 0; i < stationEnds.size(); i++){
                    
                }
                transaction = new RemoveStationLine_Transaction(app, workspace, stationShape, tempTracker,
                        scene, stationEnds, lineString, shape);
                dataManager.getjTPS().addTransaction(transaction);
                for (int i = 0; i < dataManager.getShapes().size(); i++) {
                    Node temp = (Node) dataManager.getShapes().get(i);
                    if (temp instanceof LineGroups) {
                        LineGroups tempGroup = (LineGroups) temp;
                        if(tempGroup.getLineName().equals(lineString)){ //THIS MEANS WE HAVE THE CORRECT LINE IN TEMPGROUP
                            String leftStationEnd = "";
                            String rightStationEnd = "";
                            for(int l = 0; l < stationEnds.size(); l++){
                                if(stationEnds.get(l).getLineName().equals(lineString)){
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
                                if (dataManager.getShapes().get(l) instanceof DraggableText){
                                    DraggableText helpText = (DraggableText) dataManager.getShapes().get(l);
                                    if(helpText.getText().equals(leftStationEnd) && helpText.getStartResult()){
                                        leftEndElement = helpText;
                                        leftText = true;
                                    }
                                    else if(helpText.getText().equals(rightStationEnd) && helpText.getEndResult()){
                                        rightEndElement = helpText;
                                        rightText = true;
                                    }
                                }
                                else if(dataManager.getShapes().get(l) instanceof DraggableStation){
                                    DraggableStation helpStation = (DraggableStation) dataManager.getShapes().get(l);
                                    if(helpStation.getStationName().equals(leftStationEnd)){
                                        leftEndElement = helpStation;
                                        leftStation = true;
                                    }
                                    else if(helpStation.getStationName().equals(rightStationEnd)){
                                        rightEndElement = helpStation;
                                        rightStation = true;
                                    }
                                }
                            }
                            
                            System.out.println(leftText);
                            System.out.println(rightText);
                            System.out.println(leftStation);
                            System.out.println(rightStation);
                            if(leftText){
                                if(rightText) { //LEFT NODE IS TEXT RIGHT NODE IS TEXT
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
                                    
                                    for(int l = 0; l < tempTracker.size(); l++){
                                        if(tempTracker.get(l).getName().equals(newLine.getLineName())){
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
                                    if(oldLeftLine.getFirstLine() && oldRightLine.getLastLine()){
                                        newLine.setFirstLine(false);
                                        newLine.setLastLine(false);
                                    }
                                    else if(oldLeftLine.getFirstLine()){
                                        newLine.setFirstLine(true);
                                    }
                                    else if(oldRightLine.getLastLine()){
                                        newLine.setFirstLine(true);
                                    }
                                   
                                }
                                else if(rightStation){ //LEFT NODE IS TEXT RIGHT NODE IS STATION
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
                                    
                                    for(int l = 0; l < tempTracker.size(); l++){
                                        if(tempTracker.get(l).getName().equals(newLine.getLineName())){
                                            tempTracker.get(l).removeStationName(stationShape.getStationName());
                                        }
                                    }
                                    newLine.setLeftEnd(((DraggableText) leftEndElement).getText());
                                    newLine.setRightend(((DraggableStation) rightEndElement).getStationName());
                                    
                                    ArrayList<StationEnds> tempEnds = ((DraggableStation) rightEndElement).getStationEnds();
                                    for(int l = 0; l < tempEnds.size(); l++){
                                        if(tempEnds.get(l).getLineName().equals(lineString)){
                                            tempEnds.get(l).setLeftEnd(((DraggableText) leftEndElement).getText());
                                        }
                                    }
                                  //  ((DraggableStation) rightEndElement).setLeftEnd(((DraggableText) leftEndElement).getText());
                                  
                                    newLine.setStrokeWidth(5);
                                    newLine.setStroke(oldLeftLine.getStroke());
                                    workspace.getCanvas().getChildren().remove(oldLeftLine);
                                    workspace.getCanvas().getChildren().remove(oldRightLine);
                                    workspace.getCanvas().getChildren().add(newLine);
                                    if(oldLeftLine.getFirstLine() && oldRightLine.getLastLine()){
                                        newLine.setFirstLine(false);
                                        newLine.setLastLine(false);
                                    }
                                    else if(oldLeftLine.getFirstLine()){
                                        newLine.setFirstLine(true);
                                    }
                                    else if(oldRightLine.getLastLine()){
                                        newLine.setFirstLine(true);
                                    }
                                }
                            }
                            else if(leftStation){ //LEFT NODE IS STATION RIGHT NODE IS TEXT
                                if(rightText){
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
                                    
                                    for(int l = 0; l < tempTracker.size(); l++){
                                        if(tempTracker.get(l).getName().equals(newLine.getLineName())){
                                            tempTracker.get(l).removeStationName(stationShape.getStationName());
                                        }
                                    }
                                    newLine.setLeftEnd(((DraggableStation) leftEndElement).getStationName());
                                    newLine.setRightend(((DraggableText) rightEndElement).getText());
                                    
                                    ArrayList<StationEnds> tempEnds = ((DraggableStation) leftEndElement).getStationEnds();
                                    for(int l = 0; l < tempEnds.size(); l++){
                                        if(tempEnds.get(l).getLineName().equals(lineString)){
                                            tempEnds.get(l).setRightEnd(((DraggableText) rightEndElement).getText());
                                        }
                                    }
                                    //((DraggableStation) leftEndElement).setRightend(((DraggableText) rightEndElement).getText());
                                    
                                    newLine.setStrokeWidth(5);
                                    newLine.setStroke(oldLeftLine.getStroke());
                                    workspace.getCanvas().getChildren().remove(oldLeftLine);
                                    workspace.getCanvas().getChildren().remove(oldRightLine);
                                    workspace.getCanvas().getChildren().add(newLine);
                                  
                                    if(oldLeftLine.getFirstLine() && oldRightLine.getLastLine()){
                                        newLine.setFirstLine(false);
                                        newLine.setLastLine(false);
                                    }
                                    else if(oldLeftLine.getFirstLine()){
                                        newLine.setFirstLine(true);
                                    }
                                    else if(oldRightLine.getLastLine()){
                                        newLine.setFirstLine(true);
                                    }
                                }
                                else if(rightStation){ //LEFT NODE IS STATION RIGHT NODE IS STATION
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
                                    
                                    for(int l = 0; l < tempTracker.size(); l++){
                                        if(tempTracker.get(l).getName().equals(newLine.getLineName())){
                                            tempTracker.get(l).removeStationName(stationShape.getStationName());
                                        }
                                    }
                                    newLine.setLeftEnd(((DraggableStation) leftEndElement).getStationName());
                                    newLine.setRightend(((DraggableStation) rightEndElement).getStationName());
                                    
                                    ArrayList<StationEnds> tempLeftEnds = ((DraggableStation) leftEndElement).getStationEnds();
                                    for(int l = 0; l < tempLeftEnds.size(); l++){
                                        if(tempLeftEnds.get(l).getLineName().equals(lineString)){
                                            tempLeftEnds.get(l).setRightEnd(((DraggableStation) rightEndElement).getStationName());
                                        }
                                    }
                                    ArrayList<StationEnds> tempRightEnds = ((DraggableStation) rightEndElement).getStationEnds();
                                    for(int l = 0; l < tempRightEnds.size(); l++){
                                        if(tempRightEnds.get(l).getLineName().equals(lineString)){
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
                                  
                                    if(oldLeftLine.getFirstLine() && oldRightLine.getLastLine()){
                                        newLine.setFirstLine(false);
                                        newLine.setLastLine(false);
                                    }
                                    else if(oldLeftLine.getFirstLine()){
                                        newLine.setFirstLine(true);
                                    }
                                    else if(oldRightLine.getLastLine()){
                                        newLine.setFirstLine(true);
                                    }
                                }
                            }
                            
                            for(int l = 0; l < stationEnds.size(); l++){
                                if(stationEnds.get(l).getLineName().equals(lineString)){
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
           else {
                scene.setCursor(Cursor.DEFAULT);
                dataManager.setState(SELECTING_SHAPE);
                app.getWorkspaceComponent().reloadWorkspace(dataManager);
            }
        }
    }
   
    
        /**
         * Respond to mouse dragging on the rendering surface, which we call
         * canvas, but is actually a Pane.
         */
    public void processCanvasMouseDragged(int x, int y) {
        m3Data dataManager = (m3Data) app.getDataComponent();
        if (dataManager.isInState(DRAGGING_SHAPE)) {
            Draggable selectedDraggableShape = (Draggable) dataManager.getSelectedShape();
            selectedDraggableShape.drag(x, y);
           // transaction = new DragShape_Transaction(app, selectedDraggableShape, originX, originY, x, y);
           // dataManager.getjTPS().addTransaction(transaction);
            app.getGUI().updateToolbarControls(false);
        }
    }

    /**
     * Respond to mouse button release on the rendering surface, which we call
     * canvas, but is actually a Pane.
     */
    public void processCanvasMouseRelease(int x, int y) {
        m3Data dataManager = (m3Data) app.getDataComponent();
        if (dataManager.isInState(m3State.DRAGGING_SHAPE)) {
            dataManager.setState(SELECTING_SHAPE);
            Draggable DraggedShape = (Draggable) dataManager.getSelectedShape();
            Scene scene = app.getGUI().getPrimaryScene();
            scene.setCursor(Cursor.DEFAULT);
            app.getGUI().updateToolbarControls(false);
           // transaction = new DragShape_Transaction(app, DraggedShape, originX, originY, x, y);
           // dataManager.getjTPS().addTransaction(transaction);
        } else if (dataManager.isInState(m3State.DRAGGING_NOTHING)) {
            dataManager.setState(SELECTING_SHAPE);
        }
    }
    
  
}
