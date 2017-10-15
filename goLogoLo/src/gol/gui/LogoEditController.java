package gol.gui;

import gol.transaction.Remove_Transaction;
import gol.transaction.MoveFowards_Transaction;
import gol.transaction.MoveBackwards_Transaction;
import gol.transaction.EditText_Transaction;
import gol.transaction.AddShape_Transaction;
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
import gol.data.golData;
import gol.data.golState;
import djf.AppTemplate;
import static djf.settings.AppStartupConstants.APP_PROPERTIES_FILE_NAME;
import static djf.settings.AppStartupConstants.APP_PROPERTIES_FILE_NAME_SPANISH;
import static djf.settings.AppStartupConstants.FILE_PROTOCOL;
import static djf.settings.AppStartupConstants.PATH_IMAGES;
import gol.data.DraggableRectangle;
import gol.data.DraggableText;

import gol.data.UndoRedoState;
import gol.transaction.FillColor_Transaction;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import static javafx.scene.input.KeyCode.C;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.filechooser.FileNameExtensionFilter;
import jtps.jTPS_Transaction;

/**
 * This class responds to interactions with other UI logo editing controls.
 * 
 * @author Richard McKenna
 * @author ?
 * @version 1.0
 */
public class LogoEditController {
    AppTemplate app;
    golData dataManager;
    Color imageFill;
    UndoRedoState stateHelper;
    jTPS_Transaction transaction;
    DraggableText t = new DraggableText();
    DraggableText tempText = new DraggableText();
    String oldText = new String();
    

    
    public Color getImageFill(){
        return imageFill;
    }
    
    
    public LogoEditController(AppTemplate initApp) {
	app = initApp;
	dataManager = (golData)app.getDataComponent();
    }
    
    public DraggableText getText(){
        return t;
    }
   
    public void handleAddImageRequest(){
        Scene scene = app.getGUI().getPrimaryScene();
        golWorkspace workspace = (golWorkspace)app.getWorkspaceComponent();
        FileChooser fc = new FileChooser();
        fc.setTitle("Open Resource File");
        fc.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
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

            dataManager.setState(golState.SELECTING_SHAPE);
            DraggableRectangle temp = new DraggableRectangle();
            temp.setFill(new ImagePattern(img));
            temp.setPathString(imagePath);
            temp.setHeight(heightImg);
            temp.setWidth(widthImg);
            ((Shape)temp).setStroke(workspace.getOutlineColorPicker().getValue());
            ((Shape)temp).setStrokeWidth(workspace.getOutlineThicknessSlider().getValue());
            golWorkspace workspaceManager = (golWorkspace) app.getWorkspaceComponent();
            transaction = new AddShape_Transaction(app, temp);
            dataManager.getjTPS().addTransaction(transaction);
            
        } catch (IOException ex) {
            Logger.getLogger(LogoEditController.class.getName()).log(Level.SEVERE, null, ex);
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
        dataManager.setState(golState.SELECTING_SHAPE);
        transaction = new AddShape_Transaction(app, t);
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
            transaction = new EditText_Transaction(app, dataManager.getSelectedShape(), tempText);
            dataManager.getjTPS().addTransaction(transaction);
        }
        dataManager.setState(golState.SELECTING_SHAPE);

    }


    /**
     * This method handles the response for selecting either the
     * selection or removal tool.
     */
    public void processSelectSelectionTool() {
	// CHANGE THE CURSOR
	Scene scene = app.getGUI().getPrimaryScene();
	scene.setCursor(Cursor.DEFAULT);
	
	// CHANGE THE STATE
	dataManager.setState(golState.SELECTING_SHAPE);	
	
	// ENABLE/DISABLE THE PROPER BUTTONS
	golWorkspace workspace = (golWorkspace)app.getWorkspaceComponent();
	workspace.reloadWorkspace(dataManager);
    }
    
    /**
     * This method handles a user request to remove the selected shape.
     */
    public void processRemoveSelectedShape() {
	// REMOVE THE SELECTED SHAPE IF THERE IS ONE
        transaction = new Remove_Transaction(app, dataManager.getSelectedShape());
        dataManager.getjTPS().addTransaction(transaction);
	
    }
   
  
    
    /**
     * This method processes a user request to start drawing a rectangle.
     */
    public void processSelectRectangleToDraw() {
	// CHANGE THE CURSOR
	Scene scene = app.getGUI().getPrimaryScene();
	scene.setCursor(Cursor.CROSSHAIR);
	
	// CHANGE THE STATE
	dataManager.setState(golState.STARTING_RECTANGLE);

	// ENABLE/DISABLE THE PROPER BUTTONS
	golWorkspace workspace = (golWorkspace)app.getWorkspaceComponent();
	workspace.reloadWorkspace(dataManager);
    }
    
    /**
     * This method provides a response to the user requesting to start
     * drawing an ellipse.
     */
    public void processSelectEllipseToDraw() {
	// CHANGE THE CURSOR
	Scene scene = app.getGUI().getPrimaryScene();
	scene.setCursor(Cursor.CROSSHAIR);
	
	// CHANGE THE STATE
	dataManager.setState(golState.STARTING_ELLIPSE);

	// ENABLE/DISABLE THE PROPER BUTTONS
	golWorkspace workspace = (golWorkspace)app.getWorkspaceComponent();
	workspace.reloadWorkspace(dataManager);
    }
    
    /**
     * This method processes a user request to move the selected shape
     * down to the back layer.
     */
    public void processMoveSelectedShapeToBack() {
	 transaction = new MoveBackwards_Transaction(app);
        dataManager.getjTPS().addTransaction(transaction);
	app.getGUI().updateToolbarControls(false);
    }
    
    /**
     * This method processes a user request to move the selected shape
     * up to the front layer.
     */
    public void processMoveSelectedShapeToFront() {
        transaction = new MoveFowards_Transaction(app);
        dataManager.getjTPS().addTransaction(transaction);
	app.getGUI().updateToolbarControls(false);
    }
      
    
    /**
     * This method processes a user request to select a fill color for
     * a shape.
     */
    public void processSelectFillColor() {
	golWorkspace workspace = (golWorkspace)app.getWorkspaceComponent();
	Color selectedColor = workspace.getFillColorPicker().getValue();
        Node currentNode = dataManager.getSelectedShape();
	if (selectedColor != null) {
           transaction = new FillColor_Transaction(app, (Color) ((Shape)currentNode).getFill(), selectedColor);
           dataManager.getjTPS().addTransaction(transaction);
	   //dataManager.setCurrentFillColor(selectedColor);
	   // app.getGUI().updateToolbarControls(false);
	}
    }
    
    /**
     * This method processes a user request to select the outline
     * color for a shape.
     */
    public void processSelectOutlineColor() {
	golWorkspace workspace = (golWorkspace)app.getWorkspaceComponent();
	Color selectedColor = workspace.getOutlineColorPicker().getValue();
	if (selectedColor != null) {
	    dataManager.setCurrentOutlineColor(selectedColor);
	    app.getGUI().updateToolbarControls(false);
	}    
    }
    
    /**
     * This method processes a user request to select the 
     * background color.
     */
    public void processSelectBackgroundColor() {
	golWorkspace workspace = (golWorkspace)app.getWorkspaceComponent();
	Color selectedColor = workspace.getBackgroundColorPicker().getValue();
	if (selectedColor != null) {
	    dataManager.setBackgroundColor(selectedColor);
	    app.getGUI().updateToolbarControls(false);
	}
    }
    
    /**
     * This method processes a user request to select the outline
     * thickness for shape drawing.
     */
    public void processSelectOutlineThickness() {
	golWorkspace workspace = (golWorkspace)app.getWorkspaceComponent();
	int outlineThickness = (int)workspace.getOutlineThicknessSlider().getValue();
	dataManager.setCurrentOutlineThickness(outlineThickness);
	app.getGUI().updateToolbarControls(false);
    }
    
    /**
     * This method processes a user request to take a snapshot of the
     * current scene.
     */
    public void processSnapshot() {
	golWorkspace workspace = (golWorkspace)app.getWorkspaceComponent();
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
