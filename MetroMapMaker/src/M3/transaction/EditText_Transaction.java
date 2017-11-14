package M3.transaction;

import djf.AppTemplate;
import M3.gui.m3Workspace;
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
        ((m3Workspace)appHelp.getWorkspaceComponent()).getCanvas().getChildren().remove(oldNode);
        ((m3Workspace)appHelp.getWorkspaceComponent()).getCanvas().getChildren().add(newNode);
    }

    @Override
    public void undoTransaction() {
        ((m3Workspace)appHelp.getWorkspaceComponent()).getCanvas().getChildren().add(oldNode);
        ((m3Workspace)appHelp.getWorkspaceComponent()).getCanvas().getChildren().remove(newNode);
    }
}
