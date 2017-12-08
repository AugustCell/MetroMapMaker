/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package M3.transaction;

import M3.data.DraggableText;
import M3.data.m3Data;
import M3.gui.m3Workspace;
import djf.AppTemplate;
import javafx.scene.text.Font;
import jtps.jTPS_Transaction;

/**
 *
 * @author Augusto
 */
public class RemoveText_Transaction implements jTPS_Transaction{
    m3Workspace workspace;
    AppTemplate appHelp;
    m3Data dataManager;
    DraggableText removedText;
    Font oldFont;
    
    public RemoveText_Transaction(AppTemplate app, m3Workspace work, DraggableText text){
        appHelp = app;
        dataManager = (m3Data) app.getDataComponent();
        removedText = text;
        workspace = work;
        oldFont = text.getFont();
    }

    @Override
    public void doTransaction() {
        workspace.getCanvas().getChildren().remove(removedText);
    }

    @Override
    public void undoTransaction() {
        workspace.getCanvas().getChildren().add(removedText);
    }
}
