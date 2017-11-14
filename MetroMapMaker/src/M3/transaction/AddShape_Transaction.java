/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package M3.transaction;

import djf.AppTemplate;
import M3.gui.m3Workspace;
import javafx.scene.Node;
import jtps.jTPS_Transaction;

/**
 *
 * @author Augusto
 */
public class AddShape_Transaction implements jTPS_Transaction{
    public Node tempNode;
    AppTemplate appHelp;
    
    
    public AddShape_Transaction(AppTemplate app, Node node){
        tempNode = node;
        appHelp = app;
    }
    
    @Override
    public void doTransaction() {
        ((m3Workspace)appHelp.getWorkspaceComponent()).getCanvas().getChildren().add(tempNode);
    }

    @Override
    public void undoTransaction() {
        ((m3Workspace)appHelp.getWorkspaceComponent()).getCanvas().getChildren().remove(tempNode);
    }
    
    
    
}
