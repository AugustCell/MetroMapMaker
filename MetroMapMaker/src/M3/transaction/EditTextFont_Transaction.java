/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package M3.transaction;

import M3.data.DraggableText;
import djf.AppTemplate;
import javafx.scene.Node;
import javafx.scene.text.Font;
import jtps.jTPS_Transaction;

/**
 *
 * @author Augusto
 */
public class EditTextFont_Transaction implements jTPS_Transaction {
    public DraggableText changedNode;
    public AppTemplate appHelp;
    public Font font;
    public Font oldFont;
    
    public EditTextFont_Transaction(AppTemplate app, DraggableText modifiedNode, Font newFont){
       changedNode = modifiedNode;
       appHelp = app;
       font = newFont;
       oldFont = modifiedNode.getFont();
    }
    
    @Override
    public void doTransaction() {
        changedNode.setFont(font);
    }

    @Override
    public void undoTransaction() {
        changedNode.setFont(oldFont);
    }
    
}
