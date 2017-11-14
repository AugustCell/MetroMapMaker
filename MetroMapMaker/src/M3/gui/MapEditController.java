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
import M3.data.DraggableText;
import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
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
    

    
    public Color getImageFill(){
        return imageFill;
    }
    
    
    public MapEditController(AppTemplate initApp) {
	app = initApp;
	dataManager = (m3Data)app.getDataComponent();
    }
    
    public DraggableText getText(){
        return t;
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
