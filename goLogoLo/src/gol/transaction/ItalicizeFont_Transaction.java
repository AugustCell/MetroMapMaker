package gol.transaction;

import djf.AppTemplate;
import gol.data.DraggableText;
import javafx.scene.Node;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import jtps.jTPS_Transaction;

/**
 *
 * @author Augusto
 * This edits the size of the text when one chooses a size
 */
public class ItalicizeFont_Transaction implements jTPS_Transaction{
    public Node changedNode;
    AppTemplate appHelp;
    String originalFont;
    double originalSize;
    FontPosture originalWeight;
    
    public ItalicizeFont_Transaction(AppTemplate app, Node prevNode, String previousFont, double previousSize, FontPosture type){
        changedNode = prevNode;
        appHelp = app;
        originalFont = previousFont;
        originalSize = previousSize;
        originalWeight = FontPosture.REGULAR;
    }

    @Override
    public void doTransaction() {
        ((DraggableText) changedNode).setFont(Font.font(((DraggableText) changedNode).getFont().getFamily(), FontPosture.ITALIC, ((DraggableText) changedNode).getFont().getSize()));
    }

    @Override
    public void undoTransaction() {
        ((DraggableText) changedNode).setFont(Font.font(originalFont, originalWeight, originalSize));

    }
}