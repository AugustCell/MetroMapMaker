package gol.transaction;

import djf.AppTemplate;
import gol.data.Draggable;
import gol.data.golData;
import gol.gui.golWorkspace;
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
    golData dataManager;
    Draggable node;
    int oldX;
    int oldY;
    int destX;
    int destY;
    
    
    public DragShape_Transaction(AppTemplate app, Draggable selectedNode, int previousX, int previousY, int newX, int newY){
        appHelp = app;
        dataManager = (golData)app.getDataComponent();
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
        //((golWorkspace)appHelp.getWorkspaceComponent()).getCanvas().getChildren().remove(oldNode);
       // ((golWorkspace)appHelp.getWorkspaceComponent()).getCanvas().getChildren().add(newNode);
    }

    @Override
    public void undoTransaction() {
        node.undoDrag(oldX, oldY);
        node.setLocationAndSize(oldX, oldY, node.getWidth(), node.getHeight());
       // ((golWorkspace)appHelp.getWorkspaceComponent()).getCanvas().getChildren().add(oldNode);
        //((golWorkspace)appHelp.getWorkspaceComponent()).getCanvas().getChildren().remove(newNode);
    }
}
