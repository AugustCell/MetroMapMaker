/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package M3.transaction;

import M3.data.m3Data;
import M3.gui.m3Workspace;
import djf.AppTemplate;
import javafx.scene.Node;
import jtps.jTPS_Transaction;

/**
 *
 * @author Augusto
 */
public class RemoveShape_Transaction implements jTPS_Transaction{
    AppTemplate appHelp;
    m3Data dataManager;
    Node removedNode;
    
    
    public RemoveShape_Transaction(AppTemplate app, Node node){
        appHelp = app;
        dataManager = (m3Data)app.getDataComponent();
        removedNode = node;
    }
    
    @Override
    public void doTransaction() {
       ((m3Workspace)appHelp.getWorkspaceComponent()).getCanvas().getChildren().remove(removedNode);
    }

    @Override
    public void undoTransaction() {
       ((m3Workspace)appHelp.getWorkspaceComponent()).getCanvas().getChildren().add(removedNode);
    }
    
}
