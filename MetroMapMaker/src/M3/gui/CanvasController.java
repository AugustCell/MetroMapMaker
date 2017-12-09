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
import M3.data.GridLine;
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
    m3Data dataManager;


    
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
                StationEnds helpEnds = new StationEnds();
                String leftHelp = "";
                String rightHelp = "";
                helpEnds.setLineName(lineString);
                for (int i = 0; i < stationEnds.size(); i++) {
                    if (stationEnds.get(i).getLineName().equals(lineString)) {
                        leftHelp = stationEnds.get(i).getLeftEnd();
                        rightHelp = stationEnds.get(i).getRightEnd();
                    }
                }
                helpEnds.setLeftEnd(leftHelp);
                helpEnds.setRightEnd(leftHelp);
                transaction = new RemoveStationLine_Transaction(app, workspace, stationShape, tempTracker,
                        scene, stationEnds, lineString, shape, helpEnds);
                dataManager.getjTPS().addTransaction(transaction);
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
