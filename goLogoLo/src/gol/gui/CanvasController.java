package gol.gui;

import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.shape.Shape;
import gol.data.golData;
import gol.data.Draggable;
import gol.data.golState;
import static gol.data.golState.DRAGGING_NOTHING;
import static gol.data.golState.DRAGGING_SHAPE;
import static gol.data.golState.SELECTING_SHAPE;
import static gol.data.golState.SIZING_SHAPE;
import djf.AppTemplate;
import gol.data.DraggableRectangle;
import gol.data.DraggableText;
import gol.data.UndoRedoState;
import static gol.data.golState.DRAGGING_IMAGE;
import static gol.data.golState.DRAGGING_TEXT;
import static gol.data.golState.SELECTING_IMAGE;
import static gol.data.golState.SELECT_TEXT;
import gol.transaction.DragShape_Transaction;
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

    UndoRedoState stateHelper;
    AppTemplate app;
    jTPS_Transaction transaction;
    int destLocationX;
    int destLocationY;
    int originX;
    int originY;
    

    public CanvasController(AppTemplate initApp) {
        app = initApp;
    }

    public int getDestinationX(){
        return destLocationX;
    }
    
    public int getDestinationY(){
        return destLocationY;
    }
    
    /**
     * Respond to mouse presses on the rendering surface, which we call canvas,
     * but is actually a Pane.
     */
    public void processCanvasMousePress(int x, int y) {
        golData dataManager = (golData) app.getDataComponent();
        if (dataManager.isInState(SELECTING_SHAPE)) {
            // SELECT THE TOP SHAPE
            Node shape = dataManager.selectTopShape(x, y);
            Scene scene = app.getGUI().getPrimaryScene();

            // AND START DRAGGING IT
            if (shape != null) {
                scene.setCursor(Cursor.MOVE);
                dataManager.setState(golState.DRAGGING_SHAPE);
                originX = x;
                originY = y;
                app.getGUI().updateToolbarControls(false);
            } else {
                scene.setCursor(Cursor.DEFAULT);
                dataManager.setState(DRAGGING_NOTHING);
                app.getWorkspaceComponent().reloadWorkspace(dataManager);
            }
        } else if (dataManager.isInState(golState.STARTING_RECTANGLE)) {
            dataManager.startNewRectangle(x, y);
        } else if (dataManager.isInState(golState.STARTING_ELLIPSE)) {
            dataManager.startNewEllipse(x, y);
        }
        golWorkspace workspace = (golWorkspace) app.getWorkspaceComponent();
        workspace.reloadWorkspace(dataManager);
    }

    /**
     * Respond to mouse dragging on the rendering surface, which we call canvas,
     * but is actually a Pane.
     */
    public void processCanvasMouseDragged(int x, int y) {
        golData dataManager = (golData) app.getDataComponent();
        if (dataManager.isInState(SIZING_SHAPE)) {
            Draggable newDraggableShape = (Draggable) dataManager.getNewShape();
            newDraggableShape.size(x, y);
        } else if (dataManager.isInState(DRAGGING_SHAPE)) {
            Draggable selectedDraggableShape = (Draggable) dataManager.getSelectedShape();
            selectedDraggableShape.drag(x, y);
           // transaction = new DragShape_Transaction(app, selectedDraggableShape, originX, originY, x, y);
           // dataManager.getjTPS().addTransaction(transaction);
            selectedDraggableShape.drag(x, y);
            app.getGUI().updateToolbarControls(false);
        }
    }

    /**
     * Respond to mouse button release on the rendering surface, which we call canvas,
     * but is actually a Pane.
     */
    public void processCanvasMouseRelease(int x, int y) {
        golData dataManager = (golData) app.getDataComponent();
        if (dataManager.isInState(SIZING_SHAPE)) {
            dataManager.selectSizedShape();
           app.getGUI().updateToolbarControls(false);
        } else if (dataManager.isInState(golState.DRAGGING_SHAPE)) {
            dataManager.setState(SELECTING_SHAPE);
            Draggable DraggedShape = (Draggable) dataManager.getSelectedShape();
            Scene scene = app.getGUI().getPrimaryScene();
            scene.setCursor(Cursor.DEFAULT);
            app.getGUI().updateToolbarControls(false);
            transaction = new DragShape_Transaction(app, DraggedShape, originX, originY, x, y);
            dataManager.getjTPS().addTransaction(transaction);
        } else if (dataManager.isInState(golState.DRAGGING_NOTHING)) {
            dataManager.setState(SELECTING_SHAPE);
        }
      
    }
    
  
}
