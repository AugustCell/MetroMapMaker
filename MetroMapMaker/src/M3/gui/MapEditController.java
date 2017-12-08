package M3.gui;

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
import M3.graph.Station;
import M3.graph.StationGraph;
import M3.transaction.AddLine_Transaction;
import M3.transaction.AddStation_Transaction;
import M3.transaction.EditLine_Transaction;
import M3.transaction.EditStationFillColor_Transaction;
import M3.transaction.EditTextFillColor_Transaction;
import M3.transaction.EditTextFont_Transaction;
import M3.transaction.RemoveLine_Transaction;
import M3.transaction.RemoveShape_Transaction;
import M3.transaction.RemoveStation_Transaction;
import M3.transaction.RemoveText_Transaction;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
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
                    transaction = new AddStation_Transaction(app, workspace, newStation, t, result.get());
                    dataManager.getjTPS().addTransaction(transaction);
                }
            } else {
                transaction = new AddStation_Transaction(app, workspace, newStation, t, result.get());
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
            transaction = new RemoveStation_Transaction(app, workspace, nodeToRemoveString, stationController);
            dataManager.getjTPS().addTransaction(transaction);
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
            transaction = new EditStationFillColor_Transaction(app, tempStation, stationColor.getValue());
            dataManager.getjTPS().addTransaction(transaction);
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
                    transaction = new AddLine_Transaction(app, workspace, newLineGroup, originText, endText, textField.getText(), newStationTracker);
                    dataManager.getjTPS().addTransaction(transaction);
                    tempStage.close();
                }
            } 
            else {
                if (textField.getText() != null && !textField.getText().isEmpty()) {
                    transaction = new AddLine_Transaction(app, workspace, newLineGroup, originText, endText, textField.getText(), newStationTracker);
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
            transaction = new RemoveLine_Transaction(app, workspace, nodeToRemoveString);
            dataManager.getjTPS().addTransaction(transaction);
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
            transaction = new EditLine_Transaction(app, workspace, lineColor.getValue(), textField.getText(), editedText, tempTracker);
            dataManager.getjTPS().addTransaction(transaction);
            tempStage.close();

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
        Background background = workspace.getCanvas().getBackground();
        FileChooser fc = new FileChooser();
        fc.setTitle("Open Resource File");
        fc.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File selectedFile = fc.showOpenDialog(null);
        System.out.println(selectedFile.getPath());
       
        URL url = selectedFile.toURI().toURL();
        Image image = new Image(url.toExternalForm(), workspace.getCanvas().getPrefWidth(), workspace.getCanvas().getPrefHeight(), false, false);
        
        
        if(selectedFile != null){
            workspace.getCanvas().setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        }
       
        
        dataManager.setState(m3State.SELECTING_SHAPE);

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
        double width = 4000;
        double height = 4000;
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
        double width = 4000;
        double height = 4000;
        double i = 0;
        double j = 0;
        if(shape instanceof DraggableText){
            DraggableText tempText = (DraggableText) shape;
            double xCord = tempText.getX(); 
            double yCord = tempText.getY(); 
            double offset = 0; 
            double snapPointX = 0; 
            double snapPointY = 0; 
            double newXcord = 0; 
            double newYcord = 0; 
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
    
    public void processRoute() {
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        String origin = workspace.getOriginBox().getSelectionModel().getSelectedItem();
        String destination = workspace.getDestinationBox().getSelectionModel().getSelectedItem();
        Iterator<Station> it = null;
        ArrayList<StationTracker> tracker = dataManager.getStationTracker();
        ArrayList<String> trackerElements = new ArrayList<String>();
        for (int i = 0; i < tracker.size(); i++) {
            ArrayList<String> stations = tracker.get(i).getStationNames();
            Iterator itr = stations.iterator();
            it = new Iterator<Station>() {
                @Override
                public boolean hasNext() {
                    return itr.hasNext();
                }

                @Override
                public Station next() {
                    return new Station((String) itr.next());
                }
            };
            
        }
        StationGraph sg = new StationGraph(it);
        Station start = new Station(origin);
        Station goal = new Station(destination);
        List<Station> path = sg.minimumWeightPath(start, goal);
        if(path == null){
            System.out.println("No path found");
        }
        else{
            for(Station station : path){
                System.out.print(station + " ");
            }
            System.out.println();
        }
    }

    public void removeMapElement() {
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        StationController stationController = new StationController(app);
        Node shape = dataManager.getSelectedShape();
        
        if (shape instanceof DraggableStation) {
            String nodeToRemoveString = ((DraggableStation) shape).getStationName();
            transaction = new RemoveStation_Transaction(app, workspace, nodeToRemoveString, stationController);
            dataManager.getjTPS().addTransaction(transaction);
        }
      
        else if(shape instanceof DraggableText){
            DraggableText tempText = (DraggableText) shape;
            boolean isLine = false;
            for(int i = 0; i < dataManager.getShapes().size(); i++){
                if(dataManager.getShapes().get(i) instanceof LineGroups){
                    LineGroups tempLine = (LineGroups) dataManager.getShapes().get(i);
                    if(tempLine.getLineName().equals(tempText.getText())){
                        isLine = true;
                        break;
                    }
                }
            }
            if(isLine){
                String nodeToRemoveString = tempText.getText();
                transaction = new RemoveLine_Transaction(app, workspace, nodeToRemoveString);
                dataManager.getjTPS().addTransaction(transaction);
            }
            else{
                transaction = new RemoveText_Transaction(app, workspace, tempText);
                dataManager.getjTPS().addTransaction(transaction);
            }
        }
        
        else if(shape instanceof DraggableImage){
            transaction = new RemoveShape_Transaction(app, shape);
            dataManager.getjTPS().addTransaction(transaction);
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
