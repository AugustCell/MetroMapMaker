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
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

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
    
    
    /**
     * This will handle changing the language of the UI
     * to make it more readable for the user
     */
    public void handleChangeLanguageRequest(){
        Alert alert = new Alert(Alert.AlertType.NONE);
        if (app.getPreferredLanguage().equals(APP_PROPERTIES_FILE_NAME)) {
            alert.setTitle("Language picker");
            alert.setContentText("Choose your preferred language");
        } else if (app.getPreferredLanguage().equals(APP_PROPERTIES_FILE_NAME_SPANISH)) {
            alert.setTitle("Selector de idioma");
            alert.setContentText("Elija su idioma preferido");
        }
        ButtonType englishButton = new ButtonType("English");
        ButtonType spanishButton = new ButtonType("Espanol");

        alert.getButtonTypes().setAll(englishButton, spanishButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == englishButton) {
            app.englishButtonRequest();
        } else if (result.get() == spanishButton) {
            app.spanishButtonRequest();
        };
        
        
    }
    /**
     * This will show a box with about information about the program including 
     * its title, and image of the app.
     * 
     * @throws IOException Throws exception if the image is not found.
     */
    public void handleAboutRequest() throws IOException{
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information about goLogoLo");
        alert.setHeaderText(null);
        BufferedImage img = ImageIO.read(new File("C:\\Users\\Augusto\\Netbeans projects\\CSE219-Homework2\\hw2\\goLogoLo\\Logo.png"));
        ImageIcon icon = new ImageIcon(img);
        //alert.setGraphic(icon);
        // Image image = new Image(getClass().getResource("C:\\Users\\Augusto\\Netbeans projects\\CSE219-Homework2\\hw2\\goLogoLo\\Logo.png").toExternalForm());
        // ImageView imageView = new ImageView(image);
        // alert.setGraphic(imageView);
        if (app.getPreferredLanguage().equals(APP_PROPERTIES_FILE_NAME)) {
            alert.setContentText("Welcome to goLogoLo!!!\n"
                    + "Credits for work on this project go to Richard McKenna, and co editor Augusto Celis\n"
                    + "The project has been worked on during 2017");
        } else if (app.getPreferredLanguage().equals(APP_PROPERTIES_FILE_NAME_SPANISH)) {
            alert.setContentText("Bienvenido a goLogoLo !!!\n"
                    + "Los créditos para trabajar en este proyecto van a Richard McKenna, y el co editor Augusto Celis\n"
                    + "El proyecto se ha trabajado durante 2017");
        }
        alert.showAndWait();
    }
    
    public void handleAddImageRequest(){
        Scene scene = app.getGUI().getPrimaryScene();
        JFileChooser fc = new JFileChooser();
        JLabel image = new JLabel();
        int result = fc.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                image.setIcon(new ImageIcon(ImageIO.read(file)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        workspaceManager.getCanvas().setOnMouseClicked(e -> {
           // workspaceManager.getCanvas().getChildren().add(image);
        });
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
