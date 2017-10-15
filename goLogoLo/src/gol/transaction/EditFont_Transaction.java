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
 * This helps to modify the font type
 */
public class EditFont_Transaction implements jTPS_Transaction{
    public Node changedNode;
    AppTemplate appHelp;
    String fontChange;
    String originalFont;
    double originalSize;

    public EditFont_Transaction(AppTemplate app, Node prevNode, String fontString, String previousFont, double previousSize) {
        changedNode = prevNode;
        appHelp = app;
        fontChange = fontString;
        originalFont = previousFont;
        originalSize = previousSize;
    }

    @Override
    public void doTransaction() {
        ((DraggableText) changedNode).setFont(Font.font(fontChange, FontWeight.NORMAL, ((DraggableText) changedNode).getFont().getSize()));

    }

    @Override
    public void undoTransaction() {
        ((DraggableText) changedNode).setFont(Font.font(originalFont, FontWeight.NORMAL, originalSize));

    }
}
