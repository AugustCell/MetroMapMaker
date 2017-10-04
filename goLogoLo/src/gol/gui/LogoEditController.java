package gol.gui;

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
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import static javafx.scene.input.KeyCode.C;
import javafx.scene.layout.VBox;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.filechooser.FileNameExtensionFilter;

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
    golWorkspace workspaceManager;
    
    public LogoEditController(AppTemplate initApp) {
	app = initApp;
	dataManager = (golData)app.getDataComponent();
    }
    
   
    public void handleAddImageRequest(){
        Scene scene = app.getGUI().getPrimaryScene();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("jpg", "png", "jpeg");
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(filter);
        ImageView image = new ImageView();
        int result = fc.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                image = new ImageView(file.toURI().toURL().toExternalForm());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        scene.setCursor(Cursor.DEFAULT);
        double xPos = ((workspaceManager.getCanvas().getWidth()) / 2);
        double yPos = ((workspaceManager.getCanvas().getHeight()) / 2);
        dataManager.setState(golState.ADD_IMAGE);
        image.relocate(xPos, yPos);
       
        workspaceManager.getCanvas().getChildren().add(image);


    }

    public void handleAddTextRequets(){
        
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
	dataManager.removeSelectedShape();
	
	// ENABLE/DISABLE THE PROPER BUTTONS
	golWorkspace workspace = (golWorkspace)app.getWorkspaceComponent();
	workspace.reloadWorkspace(dataManager);
	app.getGUI().updateToolbarControls(false);
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
	dataManager.moveSelectedShapeToBack();
	app.getGUI().updateToolbarControls(false);
    }
    
    /**
     * This method processes a user request to move the selected shape
     * up to the front layer.
     */
    public void processMoveSelectedShapeToFront() {
	dataManager.moveSelectedShapeToFront();
	app.getGUI().updateToolbarControls(false);
    }
        
    /**
     * This method processes a user request to select a fill color for
     * a shape.
     */
    public void processSelectFillColor() {
	golWorkspace workspace = (golWorkspace)app.getWorkspaceComponent();
	Color selectedColor = workspace.getFillColorPicker().getValue();
	if (selectedColor != null) {
	    dataManager.setCurrentFillColor(selectedColor);
	    app.getGUI().updateToolbarControls(false);
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
