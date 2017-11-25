/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package M3.transaction;

import M3.data.DraggableLine;
import M3.data.DraggableText;
import M3.gui.m3Workspace;
import djf.AppTemplate;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import jtps.jTPS_Transaction;

/**
 *
 * @author Augusto
 */
public class AddLine_Transaction implements jTPS_Transaction {

    public Node tempNode;
    public Node startLabel;
    public Node endLabel;
    Group lineGroup;
    AppTemplate appHelp;

    public AddLine_Transaction(AppTemplate app, Node node, Node originText, Node endText) {
        tempNode = node;
        appHelp = app;
        startLabel = originText;
        endLabel = endText;
        lineGroup = new Group();
    }

    @Override
    public void doTransaction() {
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

        ((DraggableLine) tempNode).startXProperty().bind(((DraggableText) startLabel).xProperty().add(((DraggableText) startLabel).getWidth()));
        ((DraggableLine) tempNode).startYProperty().bind(((DraggableText) startLabel).yProperty());
        ((DraggableLine) tempNode).endXProperty().bind(((DraggableText) endLabel).xProperty());
        ((DraggableLine) tempNode).endYProperty().bind(((DraggableText) endLabel).yProperty());

        
        /*
        ((DraggableText) startLabel).xProperty().bindBidirectional(((DraggableLine) tempNode).startXProperty());
        ((DraggableText) startLabel).yProperty().bindBidirectional(((DraggableLine) tempNode).startYProperty());



        ((DraggableText) endLabel).xProperty().bindBidirectional(((DraggableLine) tempNode).endXProperty());
        ((DraggableText) endLabel).xProperty().add(((DraggableText) startLabel).getWidth());
        ((DraggableText) endLabel).yProperty().bindBidirectional(((DraggableLine) tempNode).endYProperty());
        */


        lineGroup.getChildren().add(startLabel);
        lineGroup.getChildren().add(tempNode);
        lineGroup.getChildren().add(endLabel);
        ((m3Workspace) appHelp.getWorkspaceComponent()).getCanvas().getChildren().add(lineGroup);

    }

    @Override
    public void undoTransaction() {
        ((m3Workspace) appHelp.getWorkspaceComponent()).getCanvas().getChildren().remove(lineGroup);

    }
}
