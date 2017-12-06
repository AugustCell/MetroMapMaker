/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package M3.transaction;

import M3.data.DraggableStation;
import M3.data.DraggableText;
import djf.AppTemplate;
import javafx.scene.paint.Paint;
import jtps.jTPS_Transaction;

/**
 *
 * @author Augusto
 */
public class EditStationFillColor_Transaction implements jTPS_Transaction{
    public DraggableStation modifiedNode;
    public Paint oldColor;
    public Paint newColor;
    AppTemplate appHelp;
    
    public EditStationFillColor_Transaction(AppTemplate app, DraggableStation newNode, Paint color){
        appHelp = app;
        modifiedNode = newNode;
        newColor = color;
        oldColor = newNode.getFill();
    }

    @Override
    public void doTransaction() {
        modifiedNode.setFill(newColor);
    }

    @Override
    public void undoTransaction() {
        modifiedNode.setFill(oldColor);
    }
}
