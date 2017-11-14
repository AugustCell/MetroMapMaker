package M3.transaction;

import djf.AppTemplate;
import M3.data.Draggable;
import M3.data.m3Data;
import M3.gui.m3Workspace;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import jtps.jTPS_Transaction;

/**
 *
 * @author Augusto
 * This helpes modify the color of the selected shape.
 */
public class DragShape_Transaction implements jTPS_Transaction{
    AppTemplate appHelp;
    m3Data dataManager;
    Draggable node;
    int oldX;
    int oldY;
    int destX;
    int destY;
    
    
    public DragShape_Transaction(AppTemplate app, Draggable selectedNode, int previousX, int previousY, int newX, int newY){
        appHelp = app;
        dataManager = (m3Data)app.getDataComponent();
        node = selectedNode;
        oldX = previousX;
        oldY = previousY;
        destX = newX;
        destY = newY;
    }
    
    @Override
    public void doTransaction() {
        node.drag(destX, destY);
        node.setLocationAndSize(destX, destY, node.getWidth(), node.getHeight());
        //((m3Workspace)appHelp.getWorkspaceComponent()).getCanvas().getChildren().remove(oldNode);
       // ((m3Workspace)appHelp.getWorkspaceComponent()).getCanvas().getChildren().add(newNode);
    }

    @Override
    public void undoTransaction() {
        node.undoDrag(oldX, oldY);
        node.setLocationAndSize(oldX, oldY, node.getWidth(), node.getHeight());
       // ((m3Workspace)appHelp.getWorkspaceComponent()).getCanvas().getChildren().add(oldNode);
        //((m3Workspace)appHelp.getWorkspaceComponent()).getCanvas().getChildren().remove(newNode);
    }
}
