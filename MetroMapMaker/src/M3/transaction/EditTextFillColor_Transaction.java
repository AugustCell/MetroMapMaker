/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package M3.transaction;

import M3.data.DraggableText;
import djf.AppTemplate;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import jtps.jTPS_Transaction;

/**
 *
 * @author Augusto
 */
public class EditTextFillColor_Transaction implements jTPS_Transaction {
    public DraggableText modifiedNode;
    public Paint oldColor;
    public Paint newColor;
    AppTemplate appHelp;
    
    public EditTextFillColor_Transaction(AppTemplate app, DraggableText newNode, Paint color){
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
