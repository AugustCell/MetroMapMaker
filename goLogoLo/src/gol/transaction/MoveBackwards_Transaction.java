package gol.transaction;

import djf.AppTemplate;
import gol.data.golData;
import jtps.jTPS_Transaction;

/**
 *
 * @author Augusto
 */
public class MoveBackwards_Transaction implements jTPS_Transaction{
    AppTemplate appHelp;
    golData dataManager;
    
    
    public MoveBackwards_Transaction(AppTemplate app){
        appHelp = app;
        dataManager = (golData)app.getDataComponent();
    }
    
    @Override
    public void doTransaction() {
        dataManager.moveSelectedShapeToBack();
    }

    @Override
    public void undoTransaction() {
       dataManager.moveSelectedShapeToFront();
    }
}
