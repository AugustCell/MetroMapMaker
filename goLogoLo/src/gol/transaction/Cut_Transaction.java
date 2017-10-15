/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gol.transaction;

import djf.AppTemplate;
import gol.gui.golWorkspace;
import javafx.scene.Node;
import jtps.jTPS_Transaction;

/**
 *
 * @author Augusto
 */
public class Cut_Transaction implements jTPS_Transaction{
    public Node tempNode;
    AppTemplate appHelp;
    
    
    public Cut_Transaction(AppTemplate app, Node node){
        tempNode = node;
        appHelp = app;
    }
    
    @Override
    public void doTransaction() {
        ((golWorkspace)appHelp.getWorkspaceComponent()).getCanvas().getChildren().add(tempNode);
    }

    @Override
    public void undoTransaction() {
        ((golWorkspace)appHelp.getWorkspaceComponent()).getCanvas().getChildren().remove(tempNode);
    }
    
    
    
}
