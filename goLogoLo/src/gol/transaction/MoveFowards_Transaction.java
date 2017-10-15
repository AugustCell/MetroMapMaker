package gol.transaction;

import djf.AppTemplate;
import gol.data.golData;
import jtps.jTPS_Transaction;

/**
 *
 * @author Augusto
 */
public class MoveFowards_Transaction implements jTPS_Transaction{
    AppTemplate appHelp;
    golData dataManager;
    
    
    public MoveFowards_Transaction(AppTemplate app){
        appHelp = app;
        dataManager = (golData)app.getDataComponent();
    }
    
    @Override
    public void doTransaction() {
        dataManager.moveSelectedShapeToFront();
    }

    @Override
    public void undoTransaction() {
       dataManager.moveSelectedShapeToBack();
    }
}
