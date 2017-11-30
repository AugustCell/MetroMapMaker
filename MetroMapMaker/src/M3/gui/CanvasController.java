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
import static M3.data.m3State.ADDING_STATION_TO_LINE;
import static M3.data.m3State.REMOVING_STATION_FROM_LINE;
import M3.transaction.DragShape_Transaction;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
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
            Node stationTextNode = dataManager.getStationName(x, y);
            String stationName = "";
            if (stationTextNode != null) {
                stationName = ((DraggableText) ((Group) stationTextNode).getChildren().get(0)).getText();
            }
            System.out.println("This is the station name : " + stationName);
            DraggableStation stationShape = (DraggableStation) shape;
            Scene scene = app.getGUI().getPrimaryScene();
            String compareLine = "";
            if (shape != null) {
                String lineString = workspace.getLineBox().getSelectionModel().getSelectedItem();

                for (int i = dataManager.getShapes().size() - 1; i >= 0; i--) {
                    Node temp = (Node) dataManager.getShapes().get(i);
                    DraggableText tempText = new DraggableText();
                    Group originalStationGroup = new Group();
                    for(int l = 0; l < dataManager.getShapes().size(); l++){
                        if(dataManager.getShapes().get(l) instanceof Group){
                            if(((DraggableText) ((Group) dataManager.getShapes().get(l)).getChildren().get(0)).getText().equals(stationShape.getStationName())){
                                workspace.getCanvas().getChildren().remove(dataManager.getShapes().get(l));
                            }
                        }
                    }
                    if (temp instanceof Group) {
                        if (((Group) temp).getChildren().get(0) instanceof DraggableText) {
                            tempText = (DraggableText) ((Group) temp).getChildren().get(0);
                            compareLine = tempText.getText();
                        }
                        if (workspace.getLineBox().getSelectionModel().getSelectedItem().equals(compareLine)) {
                            LineGroups newLine = new LineGroups();
                            LineGroups originalLine = (LineGroups) ((Group) temp).getChildren().get((((Group) temp).getChildren().size() - 2));
                            DraggableText originalEndText = (DraggableText) ((Group) temp).getChildren().get(((Group) temp).getChildren().size() - 1);
                            for(int l = 0; l < ((Group) temp).getChildren().size(); l++){
                                System.out.println(((Group) temp).getChildren().get(l).getClass().toString());
                                if(((Group) temp).getChildren().get(l) instanceof DraggableStation){
                                    System.out.println(((DraggableStation) ((Group) temp).getChildren().get(l)).getStationName());
                                }
                            }
                            System.out.println("end of class types");
                            originalLine.endXProperty().unbindBidirectional(originalEndText.xProperty());
                            originalLine.endYProperty().unbindBidirectional(originalEndText.yProperty());
                            stationShape.setCenterX(originalLine.getEndX());
                            stationShape.setCenterY(originalLine.getEndY());
                            tempText.xProperty().unbind();
                            tempText.yProperty().unbind();
                            DraggableText stationText = new DraggableText();
                            stationText.setText(stationShape.getStationName());
                            stationText.xProperty().bind(stationShape.centerXProperty().add(stationShape.getRadius()));
                            stationText.yProperty().bind(stationShape.centerYProperty().subtract(stationShape.getRadius()));
                            Group newStationGroup = new Group();
                            double offsetX = originalLine.getEndX() - originalLine.getStartX();
                            double offsetY = originalLine.getEndY() - originalLine.getStartY();
                            newLine.setStartX(originalLine.getEndX());
                            newLine.setStartY(originalLine.getEndY());
                            newLine.setEndX(newLine.getStartX() + offsetX);
                            newLine.setEndY(newLine.getStartY() + offsetY);
                            newLine.setStroke(originalLine.getStroke());
                            newLine.setStrokeWidth(5);
                            originalEndText.setX(newLine.getEndX());
                            originalEndText.setY(newLine.getEndY());
                            originalLine.endXProperty().bindBidirectional(stationShape.centerXProperty());
                            originalLine.endYProperty().bindBidirectional(stationShape.centerYProperty());
                            originalLine.addToMetroStationsList(stationText.getText());
                            newLine.startXProperty().bindBidirectional(stationShape.centerXProperty());
                            newLine.startYProperty().bindBidirectional(stationShape.centerYProperty());
                            newLine.endXProperty().bindBidirectional(originalEndText.xProperty());
                            newLine.endYProperty().bindBidirectional(originalEndText.yProperty());
                            newLine.setLineName(originalLine.getLineName());
                            workspace.getCanvas().getChildren().remove(temp);
                            workspace.getCanvas().getChildren().remove(stationShape);
                            newStationGroup.getChildren().add(stationText);
                            newStationGroup.getChildren().add(stationShape);
                            ((Group) temp).getChildren().add(((Group) temp).getChildren().size() - 1, stationShape);
                            ((Group) temp).getChildren().add(((Group) temp).getChildren().size() - 1, newLine);
                            for (int k = 0; k < dataManager.getLineStationGroups().size(); k++) {
                                if (dataManager.getLineStationGroups().get(k).getLineName().equals(originalEndText.getText())) {
                                    LineGroups modifyGroup = dataManager.getLineStationGroups().get(k);
                                    if (modifyGroup.getStartStation().isEmpty()) {
                                        modifyGroup.setStartStation(stationName);
                                        modifyGroup.setEndStation(stationName);

                                    } else {
                                        modifyGroup.setEndStation(stationName);
                                    }
                                    System.out.println("Start station : " + modifyGroup.getStartStation().toString());
                                    System.out.println("End station : " + modifyGroup.getEndStation().toString());
                                }
                            }
                            workspace.getCanvas().getChildren().add(temp);
                            workspace.getCanvas().getChildren().add(newStationGroup);
                        }
                    }
                }

            } else {
                scene.setCursor(Cursor.DEFAULT);
                dataManager.setState(SELECTING_SHAPE);
                app.getWorkspaceComponent().reloadWorkspace(dataManager);
            }
        } 
        
        else if (dataManager.isInState(REMOVING_STATION_FROM_LINE)) {
            Node shape = dataManager.selectTopShape(x, y);
            Node stationTextNode = dataManager.getStationName(x, y);
            String stationName = "";
            if (stationTextNode != null) {
                stationName = ((DraggableText) ((Group) stationTextNode).getChildren().get(0)).getText();
            }
            DraggableStation stationShape = (DraggableStation) shape;
            Scene scene = app.getGUI().getPrimaryScene();
            String compareLine = "";
            if (shape != null) {
                String lineString = workspace.getLineBox().getSelectionModel().getSelectedItem();

                for (int i = dataManager.getShapes().size() - 1; i >= 0; i--) {
                    Node temp = (Node) dataManager.getShapes().get(i);
                    DraggableText tempText = new DraggableText();
                    if (temp instanceof Group) {
                        if (((Group) temp).getChildren().get(0) instanceof DraggableText) {
                            tempText = (DraggableText) ((Group) temp).getChildren().get(0);
                            compareLine = tempText.getText();
                        }
                        if (workspace.getLineBox().getSelectionModel().getSelectedItem().equals(compareLine)) {//IF LINE IS EQUAL THEN TEMP IS METRO LINE
                            DraggableStation stationToRemove = new DraggableStation();
                            LineGroups previousLine = new LineGroups();
                            LineGroups nextLine = new LineGroups();
                            for (int l = 0; l < dataManager.getShapes().size(); l++) {
                                if (dataManager.getShapes().get(l) instanceof Group) {
                                    if (((DraggableText) ((Group) dataManager.getShapes().get(l)).getChildren().get(0)).getText().equals(stationName)) {
                                        workspace.getCanvas().getChildren().remove(dataManager.getShapes().get(l));
                                    }
                                }
                            }

                            for (int l = 0; l < ((Group) temp).getChildren().size(); l++) {
                                if (((Group) temp).getChildren().get(l) instanceof DraggableStation) { //CHECK THROUGH CHILDREN OF LINE GROUP
                                    System.out.println(((DraggableStation) ((Group) temp).getChildren().get(l)).getStationName());
                                    if (((DraggableStation) ((Group) temp).getChildren().get(l)).getStationName().equals(((DraggableStation) shape).getStationName())) {//CHECK IF EQUAL TO NAME
                                        for(int k = 0; k < ((Group) temp).getChildren().size(); k++){
                                            System.out.println("Originally before start the group is : " + ((Group) temp).getChildren().get(k).getClass());
                                        }
                                        stationToRemove = (DraggableStation) ((Group) temp).getChildren().get(l); //STATION IS HERE
                                        previousLine = (LineGroups) ((Group) temp).getChildren().get(l - 1);
                                        nextLine = (LineGroups) ((Group) temp).getChildren().get(l + 1);
                                        boolean textBeforeTextAfter = false;
                                        boolean textBeforeStationAfter = false;
                                        boolean stationBeforeTextAfter = false;
                                        boolean stationBeforeStationAfter = false;
                                        LineGroups newLine = new LineGroups();
                                        newLine.setStrokeWidth(5);
                                        newLine.setStroke(nextLine.getStroke());
                                        newLine.setLineName(previousLine.getLineName());
                                        workspace.getCanvas().getChildren().remove(tempText);
                                        DraggableText text = new DraggableText();
                                        text.setText(stationToRemove.getStationName());
                                        Group newStationGroup = new Group();
                                        previousLine.endXProperty().unbindBidirectional(stationToRemove.centerXProperty());
                                        previousLine.endYProperty().unbindBidirectional(stationToRemove.centerYProperty());
                                        nextLine.startXProperty().unbindBidirectional(stationToRemove.centerXProperty());
                                        nextLine.startYProperty().unbindBidirectional(stationToRemove.centerYProperty());
                                        
                                        if (((Group) temp).getChildren().get(l - 2) instanceof DraggableText) {
                                            Node startText = ((Group) temp).getChildren().get(l - 2);
                                            newLine.startXProperty().bindBidirectional(((DraggableText) startText).xProperty());
                                            newLine.startYProperty().bindBidirectional(((DraggableText) startText).yProperty());
                                            if (((Group) temp).getChildren().get(l + 2) instanceof DraggableText) { //IF BEGGINING AND END ARE TEXT
                                                Node endText = ((Group) temp).getChildren().get(l + 2);
                                                newLine.endXProperty().bindBidirectional(((DraggableText) endText).xProperty());
                                                newLine.endYProperty().bindBidirectional(((DraggableText) endText).yProperty());
                                                ((Group) temp).getChildren().remove(((Group) temp).getChildren().get(l - 1));
                                                ((Group) temp).getChildren().remove(((Group) temp).getChildren().get(l));
                                                textBeforeTextAfter = true; 
                                            } else { //IF BEGGINING IS TEXT AND END IS STATION
                                                Node stationBinder = ((Group) temp).getChildren().get(l + 2);
                                                newLine.endXProperty().bindBidirectional(((DraggableStation) stationBinder).centerXProperty());
                                                newLine.endYProperty().bindBidirectional(((DraggableStation) stationBinder).centerYProperty());
                                                ((Group) temp).getChildren().remove(((Group) temp).getChildren().get(l - 1));
                                                ((Group) temp).getChildren().remove(((Group) temp).getChildren().get(l));
                                                textBeforeStationAfter = true;
                                            }
                                        } else if (((Group) temp).getChildren().get(l - 2) instanceof DraggableStation) {
                                            Node stationBinder = ((Group) temp).getChildren().get(l - 2);
                                            newLine.startXProperty().bindBidirectional(((DraggableStation) stationBinder).centerXProperty());
                                            newLine.startYProperty().bindBidirectional(((DraggableStation) stationBinder).centerYProperty());
                                            if (((Group) temp).getChildren().get(l + 2) instanceof DraggableText) { //IF BEGGINING IS STATION, END IS TEXT
                                                Node endText = ((Group) temp).getChildren().get(l + 2);
                                                newLine.endXProperty().bindBidirectional(((DraggableText) endText).xProperty());
                                                newLine.endYProperty().bindBidirectional(((DraggableText) endText).yProperty());
                                                ((Group) temp).getChildren().remove(((Group) temp).getChildren().get(l - 1));
                                                ((Group) temp).getChildren().remove(((Group) temp).getChildren().get(l));
                                                stationBeforeTextAfter = true;
                                            } else {    //IF BEGGINING AND END IS STATION
                                                Node endStation = ((Group) temp).getChildren().get(l + 2);
                                                newLine.endXProperty().bindBidirectional(((DraggableStation) endStation).centerXProperty());
                                                newLine.endYProperty().bindBidirectional(((DraggableStation) endStation).centerYProperty());
                                                ((Group) temp).getChildren().remove(((Group) temp).getChildren().get(l - 1));
                                                ((Group) temp).getChildren().remove(((Group) temp).getChildren().get(l));
                                                stationBeforeStationAfter = true;
                                            }
                                        }
                                        ((Group) temp).getChildren().remove(((Group) temp).getChildren().get(l - 1));
                                        ((Group) temp).getChildren().add(l - 1, newLine);
                                        for (int p = 0; p < ((Group) temp).getChildren().size(); p++) {
                                            System.out.println("This is after adding new line " + ((Group) temp).getChildren().get(p).getClass());
                                        }
                                        workspace.getCanvas().getChildren().remove(temp);
                                        dataManager.getShapes().remove(temp);
                                        stationToRemove.setCenterX(stationToRemove.getCenterX() + 10);
                                        stationToRemove.setCenterY(stationToRemove.getCenterY() + 10);
                                        text.xProperty().bind(stationToRemove.centerXProperty().add(stationToRemove.getRadius()));
                                        text.yProperty().bind(stationToRemove.centerYProperty().subtract(stationToRemove.getRadius()));
                                        newStationGroup.getChildren().add(text);
                                        newStationGroup.getChildren().add(stationToRemove);
                                        workspace.getCanvas().getChildren().add(newStationGroup);
                                        workspace.getCanvas().getChildren().add(temp);
                                    }
                                }
                            }

                        }
                    }
                }

            } else {
                scene.setCursor(Cursor.DEFAULT);
                dataManager.setState(SELECTING_SHAPE);
                app.getWorkspaceComponent().reloadWorkspace(dataManager);
            }
        }
    }
    /*
    Click the station that you want to add
    Put the station is center properties at the end of the line
    make a new line
    at the end of that line you put the text the end of the new line
    the start of the new line is the center property of the circle
    you add the new line into the group MAYBE CHECK THIS SHIT
    
    */
    
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
        //FIX THIS PROBLEM ASAP   
        // transaction = new DragShape_Transaction(app, DraggedShape, originX, originY, x, y);
          //  dataManager.getjTPS().addTransaction(transaction);
        } else if (dataManager.isInState(m3State.DRAGGING_NOTHING)) {
            dataManager.setState(SELECTING_SHAPE);
        }
    }
    
  
}
