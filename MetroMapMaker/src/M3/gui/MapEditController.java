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
import M3.data.LineGroups;
import M3.transaction.AddLine_Transaction;
import M3.transaction.AddStation_Transaction;
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
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
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
                    t.setText(result.get());
                    dataManager.getStations().add(result.get());
                    workspace.getStationBox().getItems().add(result.get());
                    workspace.getStationBox().valueProperty().set(t.getText());
                    newStation.setStationName(t.getText());
                    transaction = new AddStation_Transaction(app, newStation, t);
                    dataManager.getjTPS().addTransaction(transaction);
                }
            } else {
                t.setText(result.get());
                dataManager.getStations().add(result.get());
                workspace.getStationBox().getItems().add(result.get());
                workspace.getStationBox().valueProperty().set(t.getText());
                newStation.setStationName(t.getText());
                transaction = new AddStation_Transaction(app, newStation, t);
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
        String nodeToRemoveString = workspace.getStationBox().getSelectionModel().getSelectedItem();
        Alert alertBox = new Alert(AlertType.CONFIRMATION);
        alertBox.setTitle("Confirmation");
        alertBox.setContentText("Are you sure you want to remove this metro station?");
        Optional<ButtonType> result = alertBox.showAndWait();
        if (result.get() == ButtonType.OK) {
            dataManager.removeStationName(nodeToRemoveString);
            workspace.getStationBox().getItems().remove(nodeToRemoveString);
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
    
    public void handleAddLineRequest() {
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        Group tempRoot = new Group();
        Stage tempStage = new Stage();
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
    
    public void handleRemoveLineRequest(){
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
            workspace.getLineBox().getItems().remove(nodeToRemoveString);
           for(int i = dataManager.getShapes().size() - 1 ; i >= 0; i--){
               if(dataManager.getShapes().get(i) instanceof LineGroups){
                   LineGroups tempGroup = (LineGroups) dataManager.getShapes().get(i);
                   if(tempGroup.getLineName().equals(nodeToRemoveString)){
                       dataManager.getShapes().remove(tempGroup);
                   }
               }
               else if(dataManager.getShapes().get(i) instanceof DraggableText){
                   DraggableText tempText = (DraggableText) dataManager.getShapes().get(i);
                   if(tempText.getText().equals(nodeToRemoveString)){
                       dataManager.getShapes().remove(tempText);
                   }
               }
           }
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
     
      for(int i = dataManager.getShapes().size() - 1; i >= 0; i--){
          if(dataManager.getShapes().get(i) instanceof LineGroups){
              LineGroups tempGroup = (LineGroups) dataManager.getShapes().get(i);
              if(tempGroup.getLineName().equals(editedText)){
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
                } 
                else if (textField.getText() != null && !textField.getText().isEmpty()) {
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
                    for(int i = dataManager.getShapes().size() - 1; i >= 0; i--){
                        if(dataManager.getShapes().get(i) instanceof DraggableStation){
                            DraggableStation tempStation = (DraggableStation) dataManager.getShapes().get(i);
                            if(tempStation.getLeftEnd().equals(editedText)){
                                tempStation.setLeftEnd(textField.getText());
                            }
                            if(tempStation.getRightEnd().equals(editedText)){
                                tempStation.setRightend(textField.getText());
                            }
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
                                workspace.getLineBox().valueProperty().set(textField.getText());
                            }
                        }
                    }
                    for (int i = dataManager.getShapes().size() - 1; i >= 0; i--) {
                        if (dataManager.getShapes().get(i) instanceof DraggableStation) {
                            DraggableStation tempStation = (DraggableStation) dataManager.getShapes().get(i);
                            if (tempStation.getLeftEnd().equals(editedText)) {
                                tempStation.setLeftEnd(textField.getText());
                            } else if (tempStation.getRightEnd().equals(editedText)) {
                                tempStation.setRightend(textField.getText());
                            }
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
        ArrayList<String> stationElements = new ArrayList<String>();
        VBox showBox = new VBox();
        Alert alertBox = new Alert(AlertType.NONE);
        alertBox.setTitle("Metro stops list");
        alertBox.getDialogPane().getButtonTypes().add(ButtonType.OK);
        for(int i = 0; i < dataManager.getShapes().size(); i++){
            if (dataManager.getShapes().get(i) instanceof Group) {
                Group newGroup = (Group) dataManager.getShapes().get(i);
                for (int l = 0; l < newGroup.getChildren().size(); l++) {
                    if (newGroup.getChildren().get(l) instanceof LineGroups) {
                        LineGroups newLineGroup = (LineGroups) newGroup.getChildren().get(l);
                        if (newLineGroup.getLineName().equals(selectedLine)) {
                            if (!newLineGroup.getMetroStations().isEmpty()) {
                                stationElements.add(newLineGroup.getMetroStations().get(0));
                            }
                        }
                    }
                }
            } 
        }
        Text t = new Text(selectedLine + " metro stops: ");
        showBox.getChildren().add(t);
        for (int i = 0; i < stationElements.size(); i++){
            Text text = new Text();
            text.setText(stationElements.get(i));
            showBox.getChildren().add(text);
        }
        alertBox.getDialogPane().setContent(showBox);
        alertBox.showAndWait();
       
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
    
    public Node grabMetroShape(String compareLine) {
        for (int i = dataManager.getShapes().size() - 1; i >= 0; i--) {
            Node shape = (Node) dataManager.getShapes().get(i);
            if (shape instanceof Group) {
                for (Node children : ((Group) shape).getChildren()) {
                    if (((DraggableText) children).getText().equals(compareLine)) {
                        return shape;
                    }
                    break;
                }
            }
        }
        return null;
    }
  
    
 //PAST THIS LINE IS FUNCTIONALITY MEANT FOR FINAL PROJECT HOMEWORK
 //ABOVE THIS POINT IS CODE MEANT FOR ADDING/REMOVING STATIONS/LINES
 //AND ADDING/REMOVING STATIONS FROM LINES. tHIS ALSO INCLUDES EDITING
 //LINES BUT NOT STATIONS YET. 
    
    
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
          //  ((Shape)temp).setStroke(workspace.getOutlineColorPicker().getValue());
          //  ((Shape)temp).setStrokeWidth(workspace.getOutlineThicknessSlider().getValue());
            m3Workspace workspaceManager = (m3Workspace) app.getWorkspaceComponent();
            //FIX THIS ASAP
            //transaction = new AddShape_Transaction(app, temp);
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
        //FIX THIS ASAP
        //transaction = new AddShape_Transaction(app, t);
        dataManager.getjTPS().addTransaction(transaction);
         
    }
    
    public void handleEditTextRequests(){
        TextInputDialog dialog = new TextInputDialog();
        final Button ok = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        final Button cancel = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        dialog.setTitle("Edit text");
        dialog.setHeaderText("Your original text was: " + ((DraggableText)dataManager.getSelectedShape()).getText());
        dialog.setContentText("Please enter your replaced text: ");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            tempText.setText(result.get());  
            //FIX ASAP THIS PROBLEM
           // transaction = new EditText_Transaction(app, dataManager.getSelectedShape(), tempText);
            dataManager.getjTPS().addTransaction(transaction);
        }
        dataManager.setState(m3State.SELECTING_SHAPE);

    }

    /**
     * This method handles a user request to remove the selected shape.
     */
    public void processRemoveSelectedShape() {
	// REMOVE THE SELECTED SHAPE IF THERE IS ONE
        //FIX THIS  ASAP AND REMOVE THIS LINE
       // transaction = new Remove_Transaction(app, dataManager.getSelectedShape());
        dataManager.getjTPS().addTransaction(transaction);
	
    }
   
    
    
    /**
     * This method processes a user request to select a fill color for
     * a shape.
     */
    /*public void processSelectFillColor() {
	m3Workspace workspace = (m3Workspace)app.getWorkspaceComponent();
        
	Color selectedColor = workspace.getFillColorPicker().getValue();
        Node currentNode = dataManager.getSelectedShape();
	if (selectedColor != null) {
            //FIX THESE ASAP THESE ARE NECESSARY FOR THE CODE
           //transaction = new FillColor_Transaction(app, (Color) ((Shape)currentNode).getFill(), selectedColor);
           //dataManager.getjTPS().addTransaction(transaction);
	   
	}
    }*/
    /**
     * This method processes a user request to select the outline
     * color for a shape.
     */
    /*public void processSelectOutlineColor() {
	m3Workspace workspace = (m3Workspace)app.getWorkspaceComponent();
	Color selectedColor = workspace.getOutlineColorPicker().getValue();
	if (selectedColor != null) {
	    dataManager.setCurrentOutlineColor(selectedColor);
	    app.getGUI().updateToolbarControls(false);
	}    
    }*/
    
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
    
    public void processDefaultBackgroundColor(Color color){
        dataManager.setBackgroundColor(color);
        app.getGUI().updateToolbarControls(false);
    }
    
    
    /**
     * This method processes a user request to select the outline
     * thickness for shape drawing.
     */
   /* public void processSelectOutlineThickness() {
	m3Workspace workspace = (m3Workspace)app.getWorkspaceComponent();
	int outlineThickness = (int)workspace.getOutlineThicknessSlider().getValue();
	dataManager.setCurrentOutlineThickness(outlineThickness);
	app.getGUI().updateToolbarControls(false);
    }*/
    
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
