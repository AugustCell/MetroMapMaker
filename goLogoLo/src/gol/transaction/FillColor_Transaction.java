package gol.transaction;

import djf.AppTemplate;
import gol.data.golData;
import gol.gui.golWorkspace;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import jtps.jTPS_Transaction;

/**
 *
 * @author Augusto
 * This helpes modify the color of the selected shape.
 */
public class FillColor_Transaction implements jTPS_Transaction{
    AppTemplate appHelp;
    Color oldColor;
    Color selectedColor;
    golData dataManager;
    
    
    public FillColor_Transaction(AppTemplate app, Color prevColor, Color newColor){
        oldColor = prevColor;
        selectedColor = newColor;
        appHelp = app;
        dataManager = (golData)app.getDataComponent();
    }
    
    @Override
    public void doTransaction() {
        dataManager.setCurrentFillColor(selectedColor);
        //((golWorkspace)appHelp.getWorkspaceComponent()).getCanvas().getChildren().remove(oldNode);
       // ((golWorkspace)appHelp.getWorkspaceComponent()).getCanvas().getChildren().add(newNode);
    }

    @Override
    public void undoTransaction() {
        dataManager.setCurrentFillColor(oldColor);
       // ((golWorkspace)appHelp.getWorkspaceComponent()).getCanvas().getChildren().add(oldNode);
        //((golWorkspace)appHelp.getWorkspaceComponent()).getCanvas().getChildren().remove(newNode);
    }
}
