/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package M3.transaction;

import M3.data.DraggableLine;
import M3.data.DraggableText;
import M3.data.LineGroups;
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
    AppTemplate appHelp;

    public AddLine_Transaction(AppTemplate app, Node node, Node originText, Node endText) {
        tempNode = node;
        appHelp = app;
        startLabel = originText;
        endLabel = endText;
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
         

        ((DraggableLine) tempNode).startXProperty().bind(((DraggableText) startLabel).xProperty().add(((DraggableText) startLabel).getWidth()));
        ((DraggableLine) tempNode).startYProperty().bind(((DraggableText) startLabel).yProperty());
        ((DraggableLine) tempNode).endXProperty().bind(((DraggableText) endLabel).xProperty());
        ((DraggableLine) tempNode).endYProperty().bind(((DraggableText) endLabel).yProperty());
        */
        
        ((LineGroups) tempNode).startXProperty().bind(((DraggableText) startLabel).xProperty());
        ((LineGroups) tempNode).startYProperty().bind(((DraggableText) startLabel).yProperty());
        ((LineGroups) tempNode).endXProperty().bind(((DraggableText) endLabel).xProperty());
        ((LineGroups) tempNode).endYProperty().bind(((DraggableText) endLabel).yProperty());
        ((DraggableText) startLabel).setX(200);
        ((DraggableText) startLabel).setY(200);
        ((DraggableText) endLabel).setX(300);
        ((DraggableText) endLabel).setY(200);

        ((LineGroups) tempNode).setStrokeWidth(5);
        ((LineGroups) tempNode).setLeftEnd(((DraggableText) startLabel).getText());
        ((LineGroups) tempNode).setRightend(((DraggableText) endLabel).getText());
        ((LineGroups) tempNode).setLeftElementType("text");
        ((LineGroups) tempNode).setRightElementType("text");
        ((DraggableText) startLabel).setStartText(true);
        ((DraggableText) endLabel).setEndText(true);

        System.out.println("The line name : " + ((LineGroups) tempNode).getLineName());
        System.out.println("The start name : " + ((LineGroups) tempNode).getLeftEnd());
        System.out.println("The end name : " + ((LineGroups) tempNode).getRightEnd());


        /*
        ((DraggableText) startLabel).xProperty().bindBidirectional(((DraggableLine) tempNode).startXProperty());
        ((DraggableText) startLabel).yProperty().bindBidirectional(((DraggableLine) tempNode).startYProperty());



        ((DraggableText) endLabel).xProperty().bindBidirectional(((DraggableLine) tempNode).endXProperty());
        ((DraggableText) endLabel).xProperty().add(((DraggableText) startLabel).getWidth());
        ((DraggableText) endLabel).yProperty().bindBidirectional(((DraggableLine) tempNode).endYProperty());
        */

        
        ((m3Workspace) appHelp.getWorkspaceComponent()).getCanvas().getChildren().add(startLabel);
        ((m3Workspace) appHelp.getWorkspaceComponent()).getCanvas().getChildren().add(tempNode);
        ((m3Workspace) appHelp.getWorkspaceComponent()).getCanvas().getChildren().add(endLabel);

    }

    @Override
    public void undoTransaction() {
        ((m3Workspace) appHelp.getWorkspaceComponent()).getCanvas().getChildren().remove(startLabel);
        ((m3Workspace) appHelp.getWorkspaceComponent()).getCanvas().getChildren().remove(tempNode);
        ((m3Workspace) appHelp.getWorkspaceComponent()).getCanvas().getChildren().remove(endLabel);


    }
}
