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
import M3.data.DraggableText;
import static M3.data.m3State.ADDING_STATION_TO_LINE;
import M3.transaction.DragShape_Transaction;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
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
            m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
            workspace.reloadWorkspace(dataManager);
        }
        else if(dataManager.isInState(ADDING_STATION_TO_LINE)){
            
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
