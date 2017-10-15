package gol.transaction;

import djf.AppTemplate;
import gol.data.golData;
import gol.gui.golWorkspace;
import javafx.scene.Node;
import jtps.jTPS_Transaction;

/**
 *
 * @author Augusto
 */
public class Remove_Transaction implements jTPS_Transaction{
    AppTemplate appHelp;
    golData dataManager;
    Node removedNode;
    
    
    public Remove_Transaction(AppTemplate app, Node node){
        appHelp = app;
        dataManager = (golData)app.getDataComponent();
        removedNode = node;
    }
    
    @Override
    public void doTransaction() {
       ((golWorkspace)appHelp.getWorkspaceComponent()).getCanvas().getChildren().remove(removedNode);
    }

    @Override
    public void undoTransaction() {
       ((golWorkspace)appHelp.getWorkspaceComponent()).getCanvas().getChildren().add(removedNode);
    }
}
