/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package M3.transaction;

import M3.data.DraggableLine;
import M3.data.DraggableStation;
import M3.data.DraggableText;
import M3.gui.m3Workspace;
import djf.AppTemplate;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.text.Text;
import jtps.jTPS_Transaction;

/**
 *
 * @author Augusto
 */
public class AddStation_Transaction implements jTPS_Transaction{
    
    public Node tempNode;
    public Node stationLabel;
    Group stationGroup;
    AppTemplate appHelp;
    
    public AddStation_Transaction(AppTemplate app, Node node, Node stationText) {
        tempNode = node;
        appHelp = app;
        stationLabel = stationText;
        stationGroup = new Group();
    }
    
    public void doTransaction() {
        ((DraggableText)stationLabel).xProperty().bind(((DraggableStation) tempNode).centerXProperty().add(((DraggableStation) tempNode).getRadius()));
        ((DraggableText)stationLabel).yProperty().bind(((DraggableStation) tempNode).centerYProperty().subtract(((DraggableStation) tempNode).getRadius()));
        stationGroup.getChildren().add(stationLabel);
        stationGroup.getChildren().add(tempNode);
        ((m3Workspace) appHelp.getWorkspaceComponent()).getCanvas().getChildren().add(stationGroup);

        
        /* ((DraggableText) startLabel).xProperty().bind(((DraggableLine) tempNode).startXProperty().subtract(((DraggableText) startLabel).getWidth()));
        ((DraggableText) startLabel).xProperty().subtract(5.0);
        ((DraggableText) startLabel).yProperty().bind(((DraggableLine) tempNode).startYProperty());
        ((DraggableText) endLabel).xProperty().bind(((DraggableLine) tempNode).endXProperty());
        ((DraggableText) endLabel).yProperty().bind(((DraggableLine) tempNode).endYProperty());
        ((DraggableLine) tempNode).startXProperty().bind(((DraggableText) startLabel).xProperty());
        ((DraggableLine) tempNode).startYProperty().bind(((DraggableText) startLabel).yProperty());
        ((DraggableLine) tempNode).endXProperty().bind(((DraggableText) endLabel).xProperty());
        ((DraggableLine) tempNode).endYProperty().bind(((DraggableText) endLabel).yProperty());
         */

        
        /*
        ((DraggableText) startLabel).xProperty().bindBidirectional(((DraggableLine) tempNode).startXProperty());
        ((DraggableText) startLabel).yProperty().bindBidirectional(((DraggableLine) tempNode).startYProperty());



        ((DraggableText) endLabel).xProperty().bindBidirectional(((DraggableLine) tempNode).endXProperty());
        ((DraggableText) endLabel).xProperty().add(((DraggableText) startLabel).getWidth());
        ((DraggableText) endLabel).yProperty().bindBidirectional(((DraggableLine) tempNode).endYProperty());
        */

/*
        lineGroup.getChildren().add(startLabel);
        lineGroup.getChildren().add(tempNode);
        lineGroup.getChildren().add(endLabel);
        ((m3Workspace) appHelp.getWorkspaceComponent()).getCanvas().getChildren().add(lineGroup);
*/
    }

    @Override
    public void undoTransaction() {
        ((m3Workspace) appHelp.getWorkspaceComponent()).getCanvas().getChildren().remove(stationGroup);

    }
    
}
