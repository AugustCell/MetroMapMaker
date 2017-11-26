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
import static M3.data.m3State.ADDING_STATION_TO_LINE;
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
        } else if (dataManager.isInState(ADDING_STATION_TO_LINE)) {
            Node shape = dataManager.selectTopShape(x, y);
            DraggableStation stationShape = (DraggableStation) shape;
            Scene scene = app.getGUI().getPrimaryScene();
            String compareLine = "";
            if (shape != null) {
                String lineString = workspace.getLineBox().getSelectionModel().getSelectedItem();

                for (int i = dataManager.getShapes().size() - 1; i >= 0; i--) {
                    Node temp = (Node) dataManager.getShapes().get(i);
                    if (temp instanceof Group) {
                        if (((Group) temp).getChildren().get(0) instanceof DraggableText) {
                            DraggableText tempText = (DraggableText) ((Group) temp).getChildren().get(0);
                            compareLine = tempText.getText();
                        }
                        if (workspace.getLineBox().getSelectionModel().getSelectedItem().equals(compareLine)) {
                            Line newLine = new Line();
                            Line originalLine = (Line) ((Group) temp).getChildren().get((((Group) temp).getChildren().size() - 2));
                            DraggableText originalEndText = (DraggableText) ((Group) temp).getChildren().get(((Group) temp).getChildren().size() - 1);
                            originalLine.endXProperty().unbindBidirectional(originalEndText.xProperty());
                            originalLine.endYProperty().unbindBidirectional(originalEndText.yProperty());
                            stationShape.setCenterX(originalLine.getEndX());
                            stationShape.setCenterY(originalLine.getEndY());
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
                            newLine.startXProperty().bindBidirectional(stationShape.centerXProperty());
                            newLine.startYProperty().bindBidirectional(stationShape.centerYProperty());
                            newLine.endXProperty().bindBidirectional(originalEndText.xProperty());
                            newLine.endYProperty().bindBidirectional(originalEndText.yProperty());
                            workspace.getCanvas().getChildren().remove(temp);
                            ((Group) temp).getChildren().add(((Group) temp).getChildren().size() - 1, newLine);
                            workspace.getCanvas().getChildren().add(temp);

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
