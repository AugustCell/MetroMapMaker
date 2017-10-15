package gol.transaction;

import djf.AppTemplate;
import gol.gui.golWorkspace;
import javafx.scene.Node;
import jtps.jTPS_Transaction;

/**
 *
 * @author Augusto
 * This helpes modify the text when one double clicks on the text box
 */
public class EditText_Transaction implements jTPS_Transaction{
    public Node oldNode;
    public Node newNode;
    AppTemplate appHelp;
    
    
    public EditText_Transaction(AppTemplate app, Node prevNode, Node nextNode){
        oldNode = prevNode;
        newNode = nextNode;
        appHelp = app;
    }
    
    @Override
    public void doTransaction() {
        ((golWorkspace)appHelp.getWorkspaceComponent()).getCanvas().getChildren().remove(oldNode);
        ((golWorkspace)appHelp.getWorkspaceComponent()).getCanvas().getChildren().add(newNode);
    }

    @Override
    public void undoTransaction() {
        ((golWorkspace)appHelp.getWorkspaceComponent()).getCanvas().getChildren().add(oldNode);
        ((golWorkspace)appHelp.getWorkspaceComponent()).getCanvas().getChildren().remove(newNode);
    }
}
