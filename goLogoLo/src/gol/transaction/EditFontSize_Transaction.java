package gol.transaction;

import djf.AppTemplate;
import gol.data.DraggableText;
import javafx.scene.Node;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import jtps.jTPS_Transaction;

/**
 *
 * @author Augusto
 * This edits the size of the text when one chooses a size
 */
public class EditFontSize_Transaction implements jTPS_Transaction{
    public Node changedNode;
    AppTemplate appHelp;
    double fontChange;
    String originalFont;
    double originalSize;
    
    public EditFontSize_Transaction(AppTemplate app, Node prevNode, double fontVal, String previousFont, double previousSize){
        changedNode = prevNode;
        appHelp = app;
        fontChange = fontVal;
        originalFont = previousFont;
        originalSize = previousSize;
    }

    @Override
    public void doTransaction() {
        ((DraggableText) changedNode).setFont(Font.font(((DraggableText) changedNode).getFont().getFamily(), FontWeight.NORMAL, fontChange));
    }

    @Override
    public void undoTransaction() {
        ((DraggableText) changedNode).setFont(Font.font(originalFont, FontWeight.NORMAL, originalSize));

    }
}
