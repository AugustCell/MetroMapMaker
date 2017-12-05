package M3.gui;

import M3.transaction.EditText_Transaction;
import M3.transaction.AddShape_Transaction;
import java.io.File;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javax.imageio.ImageIO;
import M3.data.m3Data;
import M3.data.m3State;
import djf.AppTemplate;
import M3.data.DraggableImage;
import M3.data.DraggableLine;
import M3.data.DraggableStation;
import M3.data.DraggableText;
import M3.data.GridLine;
import M3.data.LineGroups;
import M3.data.StationEnds;
import M3.data.StationTracker;
import static M3.data.m3State.SELECTING_SHAPE;
import M3.transaction.AddLine_Transaction;
import M3.transaction.AddStation_Transaction;
import M3.transaction.EditTextFillColor_Transaction;
import M3.transaction.EditTextFont_Transaction;
import M3.transaction.RemoveShape_Transaction;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import jtps.jTPS_Transaction;


/**
 * This class responds to interactions with other UI logo editing controls.
 * 
 * @author Richard McKenna
 * @author ?
 * @version 1.0
 */
public class MapEditController {
    AppTemplate app;
    m3Data dataManager;
    Color imageFill;
    jTPS_Transaction transaction;
    DraggableText t = new DraggableText();
    DraggableText tempText = new DraggableText();
    String oldText = new String();
    
    public Color getImageFill() {
        return imageFill;
    }

    public MapEditController(AppTemplate initApp) {
        app = initApp;
        dataManager = (m3Data) app.getDataComponent();
    }

    public DraggableText getText() {
        return t;
    }

    public void handleAddStationRequest() {
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        TextInputDialog dialog = new TextInputDialog();
        DraggableStation newStation = new DraggableStation();
        DraggableText t = new DraggableText();
        dialog.setTitle("Station Namer");
        dialog.setContentText("Please enter the name of this station: ");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (dataManager.getStations().size() != 0) {
                if (dataManager.getStations().contains(result.get())) {
                    Alert warningAlert = new Alert(AlertType.WARNING);
                    warningAlert.setTitle("Similar name");
                    warningAlert.setContentText("This station already exists");
                    warningAlert.showAndWait();
                } else {
                    /*t.setText(result.get());
                    dataManager.getStations().add(result.get());
                    workspace.getStationBox().getItems().add(result.get());
                    workspace.getStationBox().valueProperty().set(t.getText());
                    workspace.getOriginBox().getItems().add(t.getText());
                    workspace.getDestinationBox().getItems().add(t.getText());
                    newStation.setStationName(t.getText());
                    */transaction = new AddStation_Transaction(app, workspace, newStation, t, result.get());
                    dataManager.getjTPS().addTransaction(transaction);
                }
            } else {
                t.setText(result.get());
                dataManager.getStations().add(result.get());
                workspace.getStationBox().getItems().add(result.get());
                workspace.getStationBox().valueProperty().set(t.getText());
                workspace.getOriginBox().getItems().add(t.getText());
                workspace.getDestinationBox().getItems().add(t.getText());
                newStation.setStationName(t.getText());
              //  transaction = new AddStation_Transaction(app, newStation, t);
                dataManager.getjTPS().addTransaction(transaction);
            }
        } else {
            Alert warningAlert = new Alert(AlertType.WARNING);
            warningAlert.setTitle("Error");
            warningAlert.setContentText("There is nothing typed into the field");
            warningAlert.showAndWait();
        }
    }
    
    public void handleRemoveStationRequest(){
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        StationController stationController = new StationController(app);
        String nodeToRemoveString = workspace.getStationBox().getSelectionModel().getSelectedItem();
        Alert alertBox = new Alert(AlertType.CONFIRMATION);
        alertBox.setTitle("Confirmation");
        alertBox.setContentText("Are you sure you want to remove this metro station?");
        Optional<ButtonType> result = alertBox.showAndWait();
        if (result.get() == ButtonType.OK) {
            dataManager.removeStationName(nodeToRemoveString);
            workspace.getOriginBox().getItems().remove(nodeToRemoveString);
            workspace.getDestinationBox().getItems().remove(nodeToRemoveString);
            workspace.getStationBox().getItems().remove(nodeToRemoveString);
            for(int i = 0; i < dataManager.getShapes().size(); i++){
                if(dataManager.getShapes().get(i) instanceof DraggableStation){
                    DraggableStation tempStation = (DraggableStation) dataManager.getShapes().get(i);
                    if(tempStation.getStationName().equals(nodeToRemoveString)){
                        ArrayList<StationEnds> tempEnds = tempStation.getStationEnds();
                        if(!tempEnds.isEmpty()){
                            stationController.removeSpecificStation(nodeToRemoveString);
                        }
                    }
                }
            }
            for (int i = dataManager.getShapes().size() - 1; i >= 0; i--) {
                if(dataManager.getShapes().get(i) instanceof DraggableStation){
                    DraggableStation tempStation = (DraggableStation) dataManager.getShapes().get(i);
                    if(tempStation.getStationName().equals(nodeToRemoveString)){
                        dataManager.getShapes().remove(tempStation);
                    }
                }
                else if(dataManager.getShapes().get(i) instanceof DraggableText){
                    DraggableText tempText = (DraggableText) dataManager.getShapes().get(i);
                    if(tempText.getText().equals(nodeToRemoveString)){
                        dataManager.getShapes().remove(tempText);
                    }
                }
             
            }
          
            if (!workspace.getStationBox().getItems().isEmpty()) {
                workspace.getStationBox().getSelectionModel().selectFirst();
            }
            else{
                workspace.getStationBox().getSelectionModel().selectFirst();
            }
        }
     }
    
    public void handleEditStationRequest(){
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        String selectedStation = workspace.getStationBox().getSelectionModel().getSelectedItem();
        ColorPicker stationColor = new ColorPicker();
        VBox showBox = new VBox();
        Alert alertBox = new Alert(AlertType.CONFIRMATION);
        alertBox.setTitle("Metro stops list");
        DraggableStation tempStation = new DraggableStation();
        for(int i = 0; i < dataManager.getShapes().size(); i++){
            if(dataManager.getShapes().get(i) instanceof DraggableStation){
                DraggableStation helperStation = (DraggableStation) dataManager.getShapes().get(i);
                if(helperStation.getStationName().equals(selectedStation)){
                    tempStation = helperStation;
                }
            }
        }
        stationColor.setValue((Color)tempStation.getFill());
        alertBox.setHeaderText("Choose a color for " + selectedStation);
        showBox.setPadding(new Insets(0, 0, 0, 15));
        showBox.getChildren().add(stationColor);
        alertBox.getDialogPane().setContent(showBox);
        
        Optional<ButtonType> result = alertBox.showAndWait();
        if(result.get() == ButtonType.OK){
            tempStation.setFill(stationColor.getValue());
        }
    }
    
    public void handleAddLineRequest() {
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        Group tempRoot = new Group();
        Stage tempStage = new Stage();
        StationTracker newStationTracker = new StationTracker();
        LineGroups newLineGroup = new LineGroups();
        DraggableText originText = new DraggableText();
        DraggableText endText = new DraggableText();
        ColorPicker lineColor = new ColorPicker();
        Scene tempScene = new Scene(tempRoot, 250, 100);
        VBox sceneBox = new VBox();
        HBox inputBox = new HBox();
        HBox buttonBox = new HBox();
        Label windowTitle = new Label("Name the metro line");
        Label metroLineName = new Label("Metro Line Name: ");
        TextField textField = new TextField();
        Button okButton = new Button("OK");
        Button cancelButton = new Button("Cancel");

        lineColor.setOnAction(e -> {
            newLineGroup.setStroke(lineColor.getValue());
        });

        okButton.setOnAction(e -> {
            if (dataManager.getLines().size() != 0) {
                if (dataManager.getLines().contains(textField.getText())) {
                    Alert warningAlert = new Alert(AlertType.WARNING);
                    warningAlert.setTitle("Similar name");
                    warningAlert.setContentText("This line already exists");
                    
                } else if (textField.getText() != null && !textField.getText().isEmpty()) {
                    originText.setText(textField.getText());
                    endText.setText(textField.getText());
                    dataManager.addLineName(textField.getText());
                    newLineGroup.setLineName(textField.getText());
                    dataManager.addLineGroupName(newLineGroup);
                    workspace.getLineBox().getItems().add(textField.getText());
                    workspace.getLineBox().valueProperty().set(textField.getText());
                    newStationTracker.setName(textField.getText());
                    dataManager.getStationTracker().add(newStationTracker);
                    transaction = new AddLine_Transaction(app, newLineGroup, originText, endText);
                    dataManager.getjTPS().addTransaction(transaction);
                    tempStage.close();
                }
            } 
            else {
                if (textField.getText() != null && !textField.getText().isEmpty()) {
                    originText.setText(textField.getText());
                    endText.setText(textField.getText());
                    dataManager.addLineName(textField.getText());
                    newLineGroup.setLineName(textField.getText());
                    dataManager.addLineGroupName(newLineGroup);
                    workspace.getLineBox().getItems().add(textField.getText());
                    workspace.getLineBox().valueProperty().set(textField.getText());
                    newStationTracker.setName(textField.getText());
                    dataManager.getStationTracker().add(newStationTracker);
                    transaction = new AddLine_Transaction(app, newLineGroup, originText, endText);
                    dataManager.getjTPS().addTransaction(transaction);
                    tempStage.close();
                } else {
                    Alert warningAlert = new Alert(AlertType.WARNING);
                    warningAlert.setTitle("Error");
                    warningAlert.setContentText("There is nothing typed into the field");
                    warningAlert.showAndWait();
                }
            }
        });
        cancelButton.setOnAction(e -> {
            tempStage.close();
        });

        inputBox.getChildren().add(metroLineName);
        inputBox.getChildren().add(textField);
        buttonBox.getChildren().add(okButton);
        buttonBox.getChildren().add(cancelButton);
        sceneBox.getChildren().add(windowTitle);
        sceneBox.getChildren().add(inputBox);
        sceneBox.getChildren().add(lineColor);
        sceneBox.getChildren().add(buttonBox);
        sceneBox.setPadding(new Insets(0, 0, 30, 0));
        tempRoot.getChildren().add(sceneBox);
        tempStage.setScene(tempScene);
 
        tempStage.showAndWait();

    }

    public void handleRemoveLineRequest() {
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        String nodeToRemoveString = workspace.getLineBox().getSelectionModel().getSelectedItem();
        Alert alertBox = new Alert(AlertType.CONFIRMATION);
        alertBox.setTitle("Confirmation");
        alertBox.setContentText("Are you sure you want to remove this metro line?");
        Optional<ButtonType> result = alertBox.showAndWait();
        System.out.println(nodeToRemoveString);
        if (result.get() == ButtonType.OK) {
            dataManager.removeLineName(nodeToRemoveString);
            for (int i = 0; i < dataManager.getLineStationGroups().size(); i++) {
                if (dataManager.getLineStationGroups().get(i).getLineName().equals(nodeToRemoveString)) {
                    dataManager.removeLineGroupName(dataManager.getLineStationGroups().get(i));
                }
            }

            ArrayList<StationTracker> tempTracker = dataManager.getStationTracker();
            for (int i = 0; i < tempTracker.size(); i++) {
                if (tempTracker.get(i).getName().equals(nodeToRemoveString)) {
                    tempTracker.remove(i);
                }
            }

            for (int i = dataManager.getShapes().size() - 1; i >= 0; i--) {
                if (dataManager.getShapes().get(i) instanceof LineGroups) {
                    LineGroups tempGroup = (LineGroups) dataManager.getShapes().get(i);
                    if (tempGroup.getLineName().equals(nodeToRemoveString)) {
                        dataManager.getShapes().remove(tempGroup);
                    }
                } else if (dataManager.getShapes().get(i) instanceof DraggableText) {
                    DraggableText tempText = (DraggableText) dataManager.getShapes().get(i);
                    if (tempText.getText().equals(nodeToRemoveString)) {
                        dataManager.getShapes().remove(tempText);
                    }
                }
            }

            workspace.getLineBox().getItems().remove(nodeToRemoveString);
            workspace.getOriginBox().getItems().remove(nodeToRemoveString);
            workspace.getDestinationBox().getItems().remove(nodeToRemoveString);

            if (!workspace.getLineBox().getItems().isEmpty()) {
                workspace.getLineBox().getSelectionModel().selectFirst();
            } else {
                workspace.getLineBox().getSelectionModel().selectFirst();
            }
        }
    }

    public void handleEditLineRequest() {
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        String editedText = workspace.getLineBox().getSelectionModel().getSelectedItem();
        ArrayList<StationTracker> tempTracker = dataManager.getStationTracker();
        Stage tempStage = new Stage();
        ColorPicker lineColor = new ColorPicker();
        VBox sceneBox = new VBox();
        HBox inputBox = new HBox();
        HBox buttonBox = new HBox();
        Scene tempScene = new Scene(sceneBox, 250, 150);
        Label windowTitle = new Label("Name the metro line");
        Label metroLineName = new Label("Metro Line Name: ");
        TextField textField = new TextField();
        Button okButton = new Button("OK");
        Button cancelButton = new Button("Cancel");

        for (int i = dataManager.getShapes().size() - 1; i >= 0; i--) {
            if (dataManager.getShapes().get(i) instanceof LineGroups) {
                LineGroups tempGroup = (LineGroups) dataManager.getShapes().get(i);
                if (tempGroup.getLineName().equals(editedText)) {
                    lineColor.setValue((Color) tempGroup.getStroke());
                }
            }
        }

        okButton.setOnAction(e -> {
            if (dataManager.getLines().size() != 0) {
                if (dataManager.getLines().contains(textField.getText())) {
                    Alert warningAlert = new Alert(AlertType.WARNING);
                    warningAlert.setTitle("Similar name");
                    warningAlert.setContentText("This line already exists");
                    warningAlert.showAndWait();
                } else if (textField.getText() != null && !textField.getText().isEmpty()) {
                    for (int i = dataManager.getShapes().size() - 1; i >= 0; i--) {
                        if (dataManager.getShapes().get(i) instanceof LineGroups) {
                            LineGroups tempGroup = (LineGroups) dataManager.getShapes().get(i);
                            if (tempGroup.getLineName().equals(editedText)) {
                                tempGroup.setStroke(lineColor.getValue());
                            }
                        } else if (dataManager.getShapes().get(i) instanceof DraggableText) {
                            DraggableText tempText = (DraggableText) dataManager.getShapes().get(i);
                            if (tempText.getText().equals(editedText)) {
                                tempText.setText(textField.getText());
                            }
                        }
                    }
                    for (int i = dataManager.getShapes().size() - 1; i >= 0; i--) {
                        if (dataManager.getShapes().get(i) instanceof DraggableStation) {
                            DraggableStation tempStation = (DraggableStation) dataManager.getShapes().get(i);
                            ArrayList<StationEnds> stationEnds = tempStation.getStationEnds();
                            for(int l = 0; l < stationEnds.size(); l++){
                                if(stationEnds.get(l).getLeftEnd().equals(editedText)){
                                    stationEnds.get(l).setLeftEnd(textField.getText());
                                }
                                if(stationEnds.get(l).getRightEnd().equals(editedText)){
                                    stationEnds.get(l).setRightEnd(textField.getText());
                                }
                            }
                          /*  if (tempStation.getLeftEnd().equals(editedText)) {
                                tempStation.setLeftEnd(textField.getText());
                            }
                            if (tempStation.getRightEnd().equals(editedText)) {
                                tempStation.setRightend(textField.getText());
                            }*/
                        } else if (dataManager.getShapes().get(i) instanceof LineGroups) {
                            LineGroups tempGroup = (LineGroups) dataManager.getShapes().get(i);
                            if (tempGroup.getLineName().equals(editedText)) {
                                tempGroup.setLineName(textField.getText());
                            }
                            if (tempGroup.getLeftEnd().equals(editedText)) {
                                tempGroup.setLeftEnd(textField.getText());
                            }
                            if (tempGroup.getRightEnd().equals(editedText)) {
                                tempGroup.setRightend(textField.getText());
                            }
                        }
                    }
                    for(int i = 0; i < tempTracker.size(); i++){
                        if(tempTracker.get(i).getName().equals(editedText)){
                            tempTracker.get(i).setName(textField.getText());
                        }
                    }
                    dataManager.removeLineName(editedText);
                    dataManager.addLineName(textField.getText());
                    workspace.getLineBox().getItems().add(textField.getText());
                    workspace.getLineBox().getItems().remove(editedText);
                    workspace.getLineBox().valueProperty().set(textField.getText());
                    System.out.println(workspace.getLineBox().getValue());
                    tempStage.close();
                }
            } else {
                if (textField.getText() != null && !textField.getText().isEmpty()) {
                    for (int i = dataManager.getShapes().size() - 1; i >= 0; i--) {
                        if (dataManager.getShapes().get(i) instanceof LineGroups) {
                            LineGroups tempGroup = (LineGroups) dataManager.getShapes().get(i);
                            if (tempGroup.getLineName().equals(editedText)) {
                                tempGroup.setStroke(lineColor.getValue());
                            }
                        } else if (dataManager.getShapes().get(i) instanceof DraggableText) {
                            DraggableText tempText = (DraggableText) dataManager.getShapes().get(i);
                            if (tempText.getText().equals(editedText)) {
                                tempText.setText(textField.getText());
                            }
                        }
                    }
                    for (int i = dataManager.getShapes().size() - 1; i >= 0; i--) {
                        if (dataManager.getShapes().get(i) instanceof DraggableStation) {
                            DraggableStation tempStation = (DraggableStation) dataManager.getShapes().get(i);
                            ArrayList<StationEnds> stationEnds = tempStation.getStationEnds();
                            for(int l = 0; l < stationEnds.size(); l++){
                                if(stationEnds.get(l).getLeftEnd().equals(editedText)){
                                    stationEnds.get(l).setLeftEnd(textField.getText());
                                }
                                if(stationEnds.get(l).getRightEnd().equals(editedText)){
                                    stationEnds.get(l).setRightEnd(textField.getText());
                                }
                            }
                         
                        } else if (dataManager.getShapes().get(i) instanceof LineGroups) {
                            LineGroups tempGroup = (LineGroups) dataManager.getShapes().get(i);
                            if (tempGroup.getLineName().equals(editedText)) {
                                tempGroup.setLineName(textField.getText());
                            }
                            if (tempGroup.getLeftEnd().equals(editedText)) {
                                tempGroup.setLeftEnd(textField.getText());
                            }
                            if (tempGroup.getRightEnd().equals(editedText)) {
                                tempGroup.setRightend(textField.getText());
                            }
                        }
                    }
                    for(int i = 0; i < tempTracker.size(); i++){
                        if(tempTracker.get(i).getName().equals(editedText)){
                            tempTracker.get(i).setName(textField.getText());
                        }
                    }
                    dataManager.removeLineName(editedText);
                    dataManager.addLineName(textField.getText());
                    workspace.getLineBox().getItems().add(textField.getText());
                    workspace.getLineBox().getItems().remove(editedText);
                    workspace.getLineBox().valueProperty().set(textField.getText());
                    tempStage.close();
                } else {
                    Alert warningAlert = new Alert(AlertType.WARNING);
                    warningAlert.setTitle("Error");
                    warningAlert.setContentText("There is nothing typed into the field");
                    warningAlert.showAndWait();
                }
            }
        });
        cancelButton.setOnAction(e -> {
            tempStage.close();
        });

        inputBox.getChildren().add(metroLineName);
        inputBox.getChildren().add(textField);
        buttonBox.getChildren().add(okButton);
        buttonBox.getChildren().add(cancelButton);
        sceneBox.getChildren().add(windowTitle);
        sceneBox.getChildren().add(inputBox);
        sceneBox.getChildren().add(lineColor);
        sceneBox.getChildren().add(buttonBox);
        sceneBox.setPadding(new Insets(0, 0, 30, 0));
        tempStage.setScene(tempScene);

        tempStage.showAndWait();

    }

    public void handleListStationRequest() {
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        String selectedLine = workspace.getLineBox().getSelectionModel().getSelectedItem();
        ArrayList<StationTracker> tempTracker = dataManager.getStationTracker();
        ArrayList<String> stationElements = new ArrayList<String>();
        VBox showBox = new VBox();
        Alert alertBox = new Alert(AlertType.NONE);
        alertBox.setTitle("Metro stops list");
        alertBox.getDialogPane().getButtonTypes().add(ButtonType.OK);
        for (int i = 0; i < tempTracker.size(); i++) {
            if (tempTracker.get(i).getName().equals(selectedLine)) {
                ArrayList<String> stations = tempTracker.get(i).getStationNames();
                for (int l = 0; l < stations.size(); l++) {
                    stationElements.add(stations.get(l));
                }
            }
        }
        Text t = new Text(selectedLine + " metro stops: ");
        t.setStyle("-fx-font : 24 arial;");
        showBox.getChildren().add(t);
        for (int i = 0; i < stationElements.size(); i++) {
            Text text = new Text();
            text.setText(stationElements.get(i));
            showBox.getChildren().add(text);
        }
        alertBox.getDialogPane().setContent(showBox);
        alertBox.showAndWait();
       
    }
    
    public void handleMoveStationLabelRequest() {
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        String stationText = workspace.getStationBox().getSelectionModel().getSelectedItem();
        DraggableStation tempStation = new DraggableStation();
        DraggableText tempText = new DraggableText();
        for(int i = 0; i < dataManager.getShapes().size(); i++){
            if(dataManager.getShapes().get(i) instanceof DraggableText){
                DraggableText helperText = (DraggableText) dataManager.getShapes().get(i);
                if(helperText.getText().equals(stationText)){
                    tempText = helperText;
                }
            }
            else if(dataManager.getShapes().get(i) instanceof DraggableStation){
                DraggableStation helperStation = (DraggableStation) dataManager.getShapes().get(i);
                if(helperStation.getStationName().equals(stationText)){
                    tempStation = helperStation;
                }
            }
        }
        
        if(tempStation.getTopRight()){
            tempText.xProperty().unbind();
            tempText.yProperty().unbind();
            
            tempText.setX(tempStation.getCenterX());
            tempText.setY(tempStation.getCenterY());
            
            tempText.xProperty().bind(tempStation.centerXProperty().add(tempStation.getRadius()));
            tempText.yProperty().bind(tempStation.centerYProperty().add(tempStation.getRadius()));
            
            tempStation.setTopRight(false);
            tempStation.setBottomRight(true);
            System.out.println("This was top right originally");
        }
        else if(tempStation.getBottomRight()){
            tempText.xProperty().unbind();
            tempText.yProperty().unbind();
            
            tempText.setX(tempStation.getCenterX());
            tempText.setY(tempStation.getCenterY());
            
            tempText.xProperty().bind(tempStation.centerXProperty().subtract(tempStation.getRadius()).subtract(tempText.getWidth()));
            tempText.yProperty().bind(tempStation.centerYProperty().add(tempStation.getRadius()));
            
            tempStation.setBottomRight(false);
            tempStation.setBottomLeft(true);
            System.out.println("Thsi was bottom right originally");
        }
        else if(tempStation.getBottomLeft()){
            tempText.xProperty().unbind();
            tempText.yProperty().unbind();
            
            tempText.setX(tempStation.getCenterX());
            tempText.setY(tempStation.getCenterY());
            
            tempText.xProperty().bind(tempStation.centerXProperty().subtract(tempStation.getRadius()).subtract(tempText.getWidth()));
            tempText.yProperty().bind(tempStation.centerYProperty().subtract(tempStation.getRadius()));
            
            tempStation.setBottomLeft(false);
            tempStation.setTopLeft(true);
            System.out.println("This was bottom left originally");
        }
        else if(tempStation.getTopLeft()){
            tempText.xProperty().unbind();
            tempText.yProperty().unbind();
            
            tempText.setX(tempStation.getCenterX());
            tempText.setY(tempStation.getCenterY());

            tempText.xProperty().bind(tempStation.centerXProperty().add(tempStation.getRadius()));
            tempText.yProperty().bind(tempStation.centerYProperty().subtract(tempStation.getRadius()));

            tempStation.setTopLeft(false);
            tempStation.setTopRight(true);
            System.out.println("This was top left originally");
        }
    }

    public void handleRotateLabelRequest() {
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        String labelText = workspace.getStationBox().getSelectionModel().getSelectedItem();
        DraggableText tempText = new DraggableText();
        DraggableStation tempStation = new DraggableStation();
        for(int i = 0; i < dataManager.getShapes().size(); i++){
            if(dataManager.getShapes().get(i) instanceof DraggableText){
                DraggableText helperText = (DraggableText) dataManager.getShapes().get(i);
                if(helperText.getText().equals(labelText)){
                    tempText = helperText;
                }
            }
            else if(dataManager.getShapes().get(i) instanceof DraggableStation){
                DraggableStation helperStation = (DraggableStation) dataManager.getShapes().get(i);
                if(helperStation.getStationName().equals(labelText)){
                    tempStation = helperStation;
                }
            }
        }
        
        if (tempText.getRotate() == 0) {
            if(tempStation.getBottomLeft() || tempStation.getTopLeft()){
                tempText.setRotate(-90);
                tempText.xProperty().add(tempStation.getRadius());
            }
            else {
                tempText.setRotate(90);
                tempText.xProperty().subtract(tempStation.getRadius());
            }
        }
        else {
            tempText.setRotate(0);
        }
    }

    public void processLineThicknessSlider() {
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        int outLineThickness =(int) workspace.getLineThickness().getValue();
        String editedLineString = workspace.getLineBox().getSelectionModel().getSelectedItem();
        LineGroups editedLine = new LineGroups();
        for(int i = 0; i < dataManager.getShapes().size(); i++){
            if(dataManager.getShapes().get(i) instanceof LineGroups){
                LineGroups helperLine = (LineGroups) dataManager.getShapes().get(i);
                if(helperLine.getLineName().equals(editedLineString)){
                    editedLine = helperLine;
                }
            }
        }
        editedLine.setStrokeWidth(outLineThickness);
    }
    
    public void processStationThicknessSlider(){
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        int outLineThickness = (int) workspace.getStationRadius().getValue();
        String editedStationString = workspace.getStationBox().getSelectionModel().getSelectedItem();
        DraggableStation editedStation = new DraggableStation();
        for(int i = 0; i < dataManager.getShapes().size(); i++){
            if(dataManager.getShapes().get(i) instanceof DraggableStation){
                DraggableStation helperStation = (DraggableStation) dataManager.getShapes().get(i);
                if(helperStation.getStationName().equals(editedStationString)){
                    editedStation = helperStation;
                }
            }
        }
        editedStation.setRadius(outLineThickness);
    }
    
    public void handleAddStationLineRequest() {
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        workspace.getCanvas().getScene().setCursor(Cursor.CROSSHAIR);
        dataManager.setState(m3State.ADDING_STATION_TO_LINE);
    }

    public void handleRemoveStationLineRequest() {
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        workspace.getCanvas().getScene().setCursor(Cursor.CROSSHAIR);
        dataManager.setState(m3State.REMOVING_STATION_FROM_LINE);

    }
    
    
    public void handleAddImageRequest(){
        Scene scene = app.getGUI().getPrimaryScene();
        m3Workspace workspace = (m3Workspace)app.getWorkspaceComponent();
        FileChooser fc = new FileChooser();
        fc.setTitle("Open Resource File");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        ImageView image = new ImageView();
        
        File selectedFile = fc.showOpenDialog(null);
        try {
            BufferedImage bufferImg = ImageIO.read(selectedFile);
            Image img = SwingFXUtils.toFXImage(bufferImg, null);
            String imagePath = selectedFile.getPath();
            double widthImg = img.getWidth();
            double heightImg = img.getHeight();
            image.setImage(img);
            scene.setCursor(Cursor.DEFAULT);

            dataManager.setState(m3State.SELECTING_SHAPE);
            DraggableImage temp = new DraggableImage();
            temp.setFill(new ImagePattern(img));
            temp.setPathString(imagePath);
            temp.setHeight(heightImg);
            temp.setWidth(widthImg);
            ((Shape) temp).setStroke(Color.BLACK);
            ((Shape) temp).setStrokeWidth(2);
            m3Workspace workspaceManager = (m3Workspace) app.getWorkspaceComponent();
            transaction = new AddShape_Transaction(app, temp);
            dataManager.getjTPS().addTransaction(transaction);
            
        } catch (IOException ex) {
            Logger.getLogger(MapEditController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void handleAddTextRequets(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Text Box Dialog");
        dialog.setContentText("Please enter your text:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
           t.setText(result.get());
           oldText = result.get();
           System.out.println(oldText);
        }
        dataManager.setState(m3State.SELECTING_SHAPE);
        transaction = new AddShape_Transaction(app, t);
        dataManager.getjTPS().addTransaction(transaction);
         
    }

    /**
     * This method handles a user request to remove the selected shape.
     */
    public void processRemoveSelectedShape() {
    //    transaction = new RemoveShape_Transaction(app, dataManager.getSelectedShape());
    //    dataManager.getjTPS().addTransaction(transaction);
	
    }
    
    /**
     * This method processes a user request to select the 
     * background color.
     */
    public void processSelectBackgroundColor() {
	m3Workspace workspace = (m3Workspace)app.getWorkspaceComponent();
	Color selectedColor = workspace.getBackgroundColorPicker().getValue();
	if (selectedColor != null) {
	    dataManager.setBackgroundColor(selectedColor);
	    app.getGUI().updateToolbarControls(false);
	}
    }
    
    public void processImageAsBackground() throws IOException{
        Scene scene = app.getGUI().getPrimaryScene();
        m3Workspace workspace = (m3Workspace)app.getWorkspaceComponent();
        FileChooser fc = new FileChooser();
        fc.setTitle("Open Resource File");
        ArrayList<String> stringHelper = new ArrayList<String>();
        fc.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));

        File selectedFile = fc.showOpenDialog(null);
        System.out.println(selectedFile.getPath());
        
        
        stringHelper.add(selectedFile.getPath());

        for(int i = 0; i < stringHelper.size(); i++){
            if(stringHelper.get(i).equals("\\")){
                System.out.println("Detected at " + i);
                stringHelper.add(i , "\\");
            }
            
        }
        
        System.out.println(stringHelper);
        
        BufferedImage bufferImg = ImageIO.read(selectedFile);
        Image img = SwingFXUtils.toFXImage(bufferImg, null);
        String imagePath = selectedFile.getPath();
        scene.setCursor(Cursor.DEFAULT);

        dataManager.setState(m3State.SELECTING_SHAPE);
        DraggableImage temp = new DraggableImage();
        //ImagePattern tempPattern = new ImagePattern(img);

        //  m3Workspace workspaceManager = (m3Workspace) app.getWorkspaceComponent();
        //  transaction = new AddShape_Transaction(app, temp);
        //   dataManager.getjTPS().addTransaction(transaction);
    }

    public void processExpandMap() {
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        Rectangle r = dataManager.getClip();
        double xVal = workspace.getCanvas().getWidth() * .10;
        double yVal = workspace.getCanvas().getWidth() * .10;
        r.setLayoutX(-xVal);
        r.setLayoutY(-yVal);
        workspace.getCanvas().setClip(r);
        r.setWidth(r.getWidth() + (workspace.getCanvas().getWidth() * .10));
        r.setHeight(r.getHeight() + (workspace.getCanvas().getHeight() * .10));

    }

    public void processShrinkMap() {
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        Rectangle r = dataManager.getClip();
        double xVal = workspace.getCanvas().getWidth() * .10;
        double yVal = workspace.getCanvas().getWidth() * .10;
        r.setLayoutX(xVal);
        r.setLayoutY(yVal);
        workspace.getCanvas().setClip(r);
        r.setWidth(r.getWidth() - (workspace.getCanvas().getWidth() * .10));
        r.setHeight(r.getHeight() - (workspace.getCanvas().getHeight() * .10));

    }

    public void processZoomIn() {
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        Scale scale = new Scale();
        scale.setX(workspace.getCanvasGroup().getScaleX() * 1.1);
        scale.setY(workspace.getCanvasGroup().getScaleY() * 1.1);
        workspace.getCanvasGroup().getTransforms().add(scale);

    }

    public void processZoomOut() {
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        Scale scale = new Scale();
        scale.setX(workspace.getCanvasGroup().getScaleX() / 1.1);
        scale.setY(workspace.getCanvasGroup().getScaleY() / 1.1);
        workspace.getCanvasGroup().getTransforms().add(scale);

    }

    public void processDefaultBackgroundColor(Color color) {
        dataManager.setBackgroundColor(color);
        app.getGUI().updateToolbarControls(false);
    }
    
    /*
    This method will display all the grid lines when the user decides to 
    check the checkbox labled show grid
    */
    public void processShowGridLines(){
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        double width = 2000;
        double height = 2000;
        double i = 0;
        double j = 0;

        if (!dataManager.getChecked()) {
            while (i < height) {
                if (i % 20 == 0) {
                    GridLine newLine = new GridLine();
                    newLine.setStartX(0);
                    newLine.setStartY(i);
                    newLine.setEndX(width);
                    newLine.setEndY(i);
                    newLine.setStrokeWidth(1);
                    workspace.getCanvas().getChildren().add(newLine);
                    dataManager.setSelectedShape(newLine);
                    dataManager.moveSelectedShapeToBack();
                }
                i++;
            }
            while (j < width) {
                if (j % 20 == 0) {
                    GridLine newLine = new GridLine();
                    newLine.setStartX(j);
                    newLine.setStartY(0);
                    newLine.setEndX(j);
                    newLine.setStrokeWidth(1);
                    newLine.setEndY(height);
                    workspace.getCanvas().getChildren().add(newLine);
                    dataManager.setSelectedShape(newLine);
                    dataManager.moveSelectedShapeToBack();
                }
                j++;
            }
            dataManager.setChecked(true);
        }
        else{
            for(int l = dataManager.getShapes().size() - 1; l >= 0; l--){
                if(dataManager.getShapes().get(l) instanceof GridLine){
                    dataManager.getShapes().remove(l);
                }
            }
            dataManager.setChecked(false);
        }
    }
    
    /*
    This method will snap the selected shape to a specific 
    x and y intersection, which will be the closest x and y
    coordinate
    */
    public void processSnapped(){
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        Node shape = dataManager.getSelectedShape();
        double width = 2000;
        double height = 2000;
        double i = 0;
        double j = 0;
        if(shape instanceof DraggableText){
            DraggableText tempText = (DraggableText) shape;
            double xCord = tempText.getX(); //xcord of item.
            double yCord = tempText.getY(); //ycord of item
            double offset = 0; //offset, which is calculated by xcord - i
            double snapPointX = 0; //this will find the smallest difference between the lines and xcord
            double snapPointY = 0; //this willl find the smallest difference between the lines and ycord
            double newXcord = 0; //this will be the final x cord
            double newYcord = 0; //this will be the final y cord
            while(i < width){
                if(i % 20 == 0){
                    offset = xCord - i;
                    if(snapPointX == 0){
                        snapPointX = offset;
                    }
                    else{
                        if(offset < snapPointX && offset > 0){
                            snapPointX = offset;
                            newXcord = i;
                        }
                    }
                }
                i++;
            }
            tempText.setX(newXcord);
            while(j < height){
                if(j % 20 == 0){
                    offset = yCord - j;
                    if(snapPointY == 0){
                        snapPointY = offset;
                    }
                    else{
                        if(offset < snapPointY && offset > 0){
                            snapPointY = offset;
                            newYcord = j;
                        }
                    }
                }
                j++;
            }
            tempText.setY(newYcord);
        }
        else if(shape instanceof DraggableStation){
            DraggableStation tempStation = (DraggableStation) shape;
            double xCord = tempStation.getCenterX();
            double yCord = tempStation.getCenterY();
            double offset = 0;
            double snapPointX = 0;
            double snapPointY = 0;
            double newXcord = 0;
            double newYcord = 0;
            while (i < width) {
                if (i % 20 == 0) {
                    offset = xCord - i;
                    if (snapPointX == 0) {
                        snapPointX = offset;
                    } else {
                        if (offset < snapPointX && offset > 0) {
                            snapPointX = offset;
                            newXcord = i;
                        }
                    }
                }
                i++;
            }
            tempStation.setCenterX(newXcord);
            while (j < height) {
                if (j % 20 == 0) {
                    offset = yCord - j;
                    if (snapPointY == 0) {
                        snapPointY = offset;
                    } else {
                        if (offset < snapPointY && offset > 0) {
                            snapPointY = offset;
                            newYcord = j;
                        }
                    }
                }
                j++;
            }
            tempStation.setCenterY(newYcord);
        }
    }

    public void changeTextFont() {
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        Node shape = dataManager.getSelectedShape();
        if (shape instanceof DraggableText) {
            DraggableText modifyText = (DraggableText) shape;
            Font currentFont = workspace.getCurrentFontSettings();
            transaction = new EditTextFont_Transaction(app, modifyText, currentFont);
            dataManager.getjTPS().addTransaction(transaction);
        }
    }

    public void changeTextColor() {
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        Node shape = dataManager.getSelectedShape();
        if (shape instanceof DraggableText) {
            DraggableText text = (DraggableText) shape;
            String selectedText = text.getText();
            ColorPicker textColor = new ColorPicker();
            VBox showBox = new VBox();
            Alert alertBox = new Alert(AlertType.CONFIRMATION);
            alertBox.setTitle("Metro stops list");
            textColor.setValue((Color) text.getFill());
            alertBox.setHeaderText("Choose a color for " + selectedText);
            showBox.setPadding(new Insets(0, 0, 0, 15));
            showBox.getChildren().add(textColor);
            alertBox.getDialogPane().setContent(showBox);

            Optional<ButtonType> result = alertBox.showAndWait();
            if (result.get() == ButtonType.OK) {
                transaction = new EditTextFillColor_Transaction(app, text, textColor.getValue());
                dataManager.getjTPS().addTransaction(transaction);
            }
        } else {
            Alert alertBox = new Alert(AlertType.WARNING);
            alertBox.setTitle("Warning");
            alertBox.setHeaderText("Non Compatible Node");
            alertBox.setContentText("This node is not a text instance, so it cannot be modified");
        }
    }
    
    /**
     * This method processes a user request to take a snapshot of the
     * current scene.
     */
    public void processSnapshot() {
	m3Workspace workspace = (m3Workspace)app.getWorkspaceComponent();
	Pane canvas = workspace.getCanvas();
	WritableImage image = canvas.snapshot(new SnapshotParameters(), null);
	File file = new File("Logo.png");
	try {
	    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
	}
	catch(IOException ioe) {
	    ioe.printStackTrace();
	}
    }
}
