package gol.gui;

import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import static gol.golLanguageProperty.ELLIPSE_ICON;
import static gol.golLanguageProperty.ELLIPSE_TOOLTIP;
import static gol.golLanguageProperty.MOVE_TO_BACK_ICON;
import static gol.golLanguageProperty.MOVE_TO_BACK_TOOLTIP;
import static gol.golLanguageProperty.MOVE_TO_FRONT_ICON;
import static gol.golLanguageProperty.MOVE_TO_FRONT_TOOLTIP;
import static gol.golLanguageProperty.RECTANGLE_ICON;
import static gol.golLanguageProperty.RECTANGLE_TOOLTIP;
import static gol.golLanguageProperty.REMOVE_ICON;
import static gol.golLanguageProperty.REMOVE_TOOLTIP;
import static gol.golLanguageProperty.SELECTION_TOOL_ICON;
import static gol.golLanguageProperty.SELECTION_TOOL_TOOLTIP;
import static gol.golLanguageProperty.SNAPSHOT_ICON;
import static gol.golLanguageProperty.SNAPSHOT_TOOLTIP;
import gol.data.golData;
import static gol.data.golData.BLACK_HEX;
import static gol.data.golData.WHITE_HEX;
import gol.data.golState;
import djf.ui.AppYesNoCancelDialogSingleton;
import djf.ui.AppMessageDialogSingleton;
import djf.ui.AppGUI;
import djf.AppTemplate;
import djf.components.AppDataComponent;
import djf.components.AppWorkspaceComponent;
import static djf.settings.AppStartupConstants.APP_PROPERTIES_FILE_NAME;
import static djf.settings.AppStartupConstants.APP_PROPERTIES_FILE_NAME_SPANISH;
import static djf.settings.AppStartupConstants.FILE_PROTOCOL;
import static djf.settings.AppStartupConstants.PATH_IMAGES;
import static gol.css.golStyle.*;
import gol.golLanguageProperty;
import static gol.golLanguageProperty.ADD_IMAGE_ICON;
import static gol.golLanguageProperty.ADD_IMAGE_TOOLTIP;
import static gol.golLanguageProperty.ADD_TEXT_ICON;
import static gol.golLanguageProperty.ADD_TEXT_TOOLTIP;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TextArea;

/**
 * This class serves as the workspace component for this application, providing
 * the user interface controls for editing work.
 *
 * @author Richard McKenna
 * @author ?
 * @version 1.0
 */
public class golWorkspace extends AppWorkspaceComponent {
    // HERE'S THE APP
    AppTemplate app;

    // IT KNOWS THE GUI IT IS PLACED INSIDE
    AppGUI gui;

    // HAS ALL THE CONTROLS FOR EDITING
    VBox editToolbar;
    
    // FIRST ROW
    HBox row1Box;
    Button selectionToolButton;
    Button removeButton;
    Button rectButton;
    Button ellipseButton;
    
    //Row for adding images and text boxes.
    HBox rowTempBox;
    Button imageButton;
    Button textBoxButton;
    
    // SECOND ROW
    HBox row2Box;
    Button moveToBackButton;
    Button moveToFrontButton;

    // THIRD ROW
    VBox row3Box;
    Label backgroundColorLabel;
    ColorPicker backgroundColorPicker;

    // FORTH ROW
    VBox row4Box;
    Label fillColorLabel;
    ColorPicker fillColorPicker;
    
    // FIFTH ROW
    VBox row5Box;
    Label outlineColorLabel;
    ColorPicker outlineColorPicker;
        
    // SIXTH ROW
    VBox row6Box;
    Label outlineThicknessLabel;
    Slider outlineThicknessSlider;
    
    // SEVENTH ROW
    HBox row7Box;
    Button snapshotButton;
    
    // THIS IS WHERE WE'LL RENDER OUR DRAWING, NOTE THAT WE
    // CALL THIS A CANVAS, BUT IT'S REALLY JUST A Pane
    Pane canvas;
    
    // HERE ARE THE CONTROLLERS
    CanvasController canvasController;
    LogoEditController logoEditController;    

    // HERE ARE OUR DIALOGS
    AppMessageDialogSingleton messageDialog;
    AppYesNoCancelDialogSingleton yesNoCancelDialog;
    
    // FOR DISPLAYING DEBUG STUFF
    Text debugText;
    
    private Button copyButton;
    private Button pasteButton;
    private Button cutButton;
    private Button changeLanguageButton;
    private Button aboutButton;

    /**
     * Constructor for initializing the workspace, note that this constructor
     * will fully setup the workspace user interface for use.
     *
     * @param initApp The application this workspace is part of.
     *
     * @throws IOException Thrown should there be an error loading application
     * data for setting up the user interface.
     */
    public golWorkspace(AppTemplate initApp) {
	// KEEP THIS FOR LATER
	app = initApp;

	// KEEP THE GUI FOR LATER
	gui = app.getGUI();

        // LAYOUT THE APP
        initLayout();
        
        // HOOK UP THE CONTROLLERS
        initControllers();
        
        // AND INIT THE STYLE FOR THE WORKSPACE
        initStyle();    
    }
    
    /**
     * Note that this is for displaying text during development.
     */
    public void setDebugText(String text) {
	debugText.setText(text);
    }
    
    // ACCESSOR METHODS FOR COMPONENTS THAT EVENT HANDLERS
    // MAY NEED TO UPDATE OR ACCESS DATA FROM
    
    public ColorPicker getFillColorPicker() {
	return fillColorPicker;
    }
    
    public ColorPicker getOutlineColorPicker() {
	return outlineColorPicker;
    }
    
    public ColorPicker getBackgroundColorPicker() {
	return backgroundColorPicker;
    }
    
    public Slider getOutlineThicknessSlider() {
	return outlineThicknessSlider;
    }

    public Pane getCanvas() {
	return canvas;
    }
    
    public Button getCopyButton(){
        return copyButton;
    }
    
    public Button getPasteButton(){
        return pasteButton;
    }
    
    public Button getCutButton(){
        return cutButton;
    }
    
    public Button getChangeLanguageButton(){
        return changeLanguageButton;
    }
    
    public Button getAboutButton(){
        return aboutButton;
    }
        
    // HELPER SETUP METHOD
    private void initLayout() {
    /*    
    //Add copy and paste button into toolbar
        
        copyButton = gui.initChildButton(gui.getFileToolbar(), LanguageProperty.COPY_ICON.toString(), LanguageProperty.COPY_TOOLTIP.toString(), true);
        copyButton.setMaxWidth(30);
        copyButton.setMinWidth(30);
        copyButton.setPrefWidth(30);
        copyButton.setMaxHeight(25);
        copyButton.setMinHeight(25);
        copyButton.setPrefHeight(25);

        pasteButton = gui.initChildButton(gui.getFileToolbar(), LanguageProperty.PASTE_ICON.toString(), LanguageProperty.PASTE_TOOLTIP.toString(), true);
        pasteButton.setMaxWidth(30);
        pasteButton.setMinWidth(30);
        pasteButton.setPrefWidth(30);
        pasteButton.setMaxHeight(25);
        pasteButton.setMinHeight(25);
        pasteButton.setPrefHeight(25);
        
         cutButton = gui.initChildButton(gui.getFileToolbar(), LanguageProperty.CUT_ICON.toString(), LanguageProperty.CUT_TOOLTIP.toString(), true);
        cutButton.setMaxWidth(30);
        cutButton.setMinWidth(30);
        cutButton.setPrefWidth(30);   
        cutButton.setMaxHeight(25);
        cutButton.setMinHeight(25);
        cutButton.setPrefHeight(25);
        */
    
        changeLanguageButton = gui.initChildButton(gui.getFileToolbar(), golLanguageProperty.CHANGE_LANGUAGE_ICON.toString(), golLanguageProperty.CHANGE_LANGUAGE_TOOLTIP.toString(), true);
        changeLanguageButton.setMaxWidth(30);
        changeLanguageButton.setMinWidth(30);
        changeLanguageButton.setPrefWidth(30);   
        changeLanguageButton.setMaxHeight(25);
        changeLanguageButton.setMinHeight(25);
        changeLanguageButton.setPrefHeight(25);
        
        aboutButton = gui.initChildButton(gui.getFileToolbar(), golLanguageProperty.ABOUT_ICON.toString(), golLanguageProperty.ABOUT_TOOLTIP.toString(), true);
        aboutButton.setMaxWidth(30);
        aboutButton.setMinWidth(30);
        aboutButton.setPrefWidth(30);   
        aboutButton.setMaxHeight(25);
        aboutButton.setMinHeight(25);
        aboutButton.setPrefHeight(25);
    

	// THIS WILL GO IN THE LEFT SIDE OF THE WORKSPACE
	editToolbar = new VBox();
	
	// ROW 1
	row1Box = new HBox();
	selectionToolButton = gui.initChildButton(row1Box, SELECTION_TOOL_ICON.toString(), SELECTION_TOOL_TOOLTIP.toString(), true);
	removeButton = gui.initChildButton(row1Box, REMOVE_ICON.toString(), REMOVE_TOOLTIP.toString(), true);
	rectButton = gui.initChildButton(row1Box, RECTANGLE_ICON.toString(), RECTANGLE_TOOLTIP.toString(), false);
	ellipseButton = gui.initChildButton(row1Box, ELLIPSE_ICON.toString(), ELLIPSE_TOOLTIP.toString(), false);
      
        //Row which consists of adding images, and text
        rowTempBox = new HBox();
        imageButton = gui.initChildButton(rowTempBox, ADD_IMAGE_ICON.toString(), ADD_IMAGE_TOOLTIP.toString(), true);
        textBoxButton = gui.initChildButton(rowTempBox, ADD_TEXT_ICON.toString(), ADD_TEXT_TOOLTIP.toString(), true);
        imageButton.setMaxWidth(95);
        imageButton.setMinWidth(95);
        imageButton.setPrefWidth(95);
        textBoxButton.setMaxWidth(95);
        textBoxButton.setMinWidth(95);
        textBoxButton.setPrefWidth(95);

	// ROW 2
	row2Box = new HBox();
	moveToBackButton = gui.initChildButton(row2Box, MOVE_TO_BACK_ICON.toString(), MOVE_TO_BACK_TOOLTIP.toString(), true);
	moveToFrontButton = gui.initChildButton(row2Box, MOVE_TO_FRONT_ICON.toString(), MOVE_TO_FRONT_TOOLTIP.toString(), true);

        // ROW 3
        row3Box = new VBox();

        if (app.getPreferredLanguage().equals(APP_PROPERTIES_FILE_NAME)) {
            backgroundColorLabel = new Label("Background Color");
        } else if (app.getPreferredLanguage().equals(APP_PROPERTIES_FILE_NAME_SPANISH)) {
            backgroundColorLabel = new Label("Color de fondo");
        }

        backgroundColorPicker = new ColorPicker(Color.valueOf(WHITE_HEX));
        row3Box.getChildren().add(backgroundColorLabel);
        row3Box.getChildren().add(backgroundColorPicker);

        // ROW 4
        row4Box = new VBox();

        if (app.getPreferredLanguage().equals(APP_PROPERTIES_FILE_NAME)) {
            fillColorLabel = new Label("Fill Color");
        } else if (app.getPreferredLanguage().equals(APP_PROPERTIES_FILE_NAME_SPANISH)) {
            fillColorLabel = new Label("Color de Fondo");
        }

        fillColorPicker = new ColorPicker(Color.valueOf(WHITE_HEX));
        row4Box.getChildren().add(fillColorLabel);
        row4Box.getChildren().add(fillColorPicker);

        // ROW 5
        row5Box = new VBox();

        if (app.getPreferredLanguage().equals(APP_PROPERTIES_FILE_NAME)) {
            outlineColorLabel = new Label("Outline Color");
        } else if (app.getPreferredLanguage().equals(APP_PROPERTIES_FILE_NAME_SPANISH)) {
            outlineColorLabel = new Label("Color del Contorno");
        }

        outlineColorPicker = new ColorPicker(Color.valueOf(BLACK_HEX));
        row5Box.getChildren().add(outlineColorLabel);
        row5Box.getChildren().add(outlineColorPicker);

        // ROW 6
        row6Box = new VBox();

        if (app.getPreferredLanguage().equals(APP_PROPERTIES_FILE_NAME)) {
            outlineThicknessLabel = new Label("Outline Color");
        } else if (app.getPreferredLanguage().equals(APP_PROPERTIES_FILE_NAME_SPANISH)) {
            outlineThicknessLabel = new Label("Grosor del Contorno");
        }

        outlineThicknessSlider = new Slider(0, 10, 1);
        row6Box.getChildren().add(outlineThicknessLabel);
        row6Box.getChildren().add(outlineThicknessSlider);

        // ROW 7
        row7Box = new HBox();
        snapshotButton = gui.initChildButton(row7Box, SNAPSHOT_ICON.toString(), SNAPSHOT_TOOLTIP.toString(), false);

        // NOW ORGANIZE THE EDIT TOOLBAR
        editToolbar.getChildren().add(row1Box);
        editToolbar.getChildren().add(rowTempBox);
        editToolbar.getChildren().add(row2Box);
        editToolbar.getChildren().add(row3Box);
        editToolbar.getChildren().add(row4Box);
        editToolbar.getChildren().add(row5Box);
        editToolbar.getChildren().add(row6Box);
        editToolbar.getChildren().add(row7Box);

        // WE'LL RENDER OUR STUFF HERE IN THE CANVAS
        canvas = new Pane();
        debugText = new Text();
        canvas.getChildren().add(debugText);
        debugText.setX(100);
        debugText.setY(100);

        // AND MAKE SURE THE DATA MANAGER IS IN SYNCH WITH THE PANE
        golData data = (golData) app.getDataComponent();
        data.setShapes(canvas.getChildren());

        
        //Handle requests for change language and about 
        //request
        changeLanguageButton.setOnAction(e -> {
            logoEditController.handleChangeLanguageRequest();
        });

        aboutButton.setOnAction(e -> {
            try {
                logoEditController.handleAboutRequest();
            } catch (IOException ex) {
                Logger.getLogger(golWorkspace.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        // AND NOW SETUP THE WORKSPACE
        workspace = new BorderPane();
        ((BorderPane) workspace).setLeft(editToolbar);
        ((BorderPane) workspace).setCenter(canvas);
        /*editToolbar.prefHeightProperty().bind(((BorderPane) workspace));
        editToolbar.prefWidthProperty().bind(workspace.widthProperty());
        canvas.prefHeightProperty().bind(workspace.);
        canvas.prefWidthProperty().bind(workspace.widthProperty());*/

    }

    // HELPER SETUP METHOD
    private void initControllers() {
        // MAKE THE EDIT CONTROLLER
        logoEditController = new LogoEditController(app);

        // NOW CONNECT THE BUTTONS TO THEIR HANDLERS
        selectionToolButton.setOnAction(e -> {
            logoEditController.processSelectSelectionTool();
        });
        removeButton.setOnAction(e -> {
            logoEditController.processRemoveSelectedShape();
        });
        rectButton.setOnAction(e -> {
            logoEditController.processSelectRectangleToDraw();
        });
        ellipseButton.setOnAction(e -> {
            logoEditController.processSelectEllipseToDraw();
        });

        moveToBackButton.setOnAction(e -> {
            logoEditController.processMoveSelectedShapeToBack();
        });
        moveToFrontButton.setOnAction(e -> {
            logoEditController.processMoveSelectedShapeToFront();
        });

        backgroundColorPicker.setOnAction(e -> {
            logoEditController.processSelectBackgroundColor();
        });
        fillColorPicker.setOnAction(e -> {
            logoEditController.processSelectFillColor();
        });
        outlineColorPicker.setOnAction(e -> {
            logoEditController.processSelectOutlineColor();
        });
        outlineThicknessSlider.valueProperty().addListener(e -> {
            logoEditController.processSelectOutlineThickness();
        });
        snapshotButton.setOnAction(e -> {
            logoEditController.processSnapshot();
        });

        imageButton.setOnAction(e -> {
            logoEditController.handleAddImageRequest();
        });
        textBoxButton.setOnAction(e -> {
            logoEditController.handleAddTextRequets();
        });

        // MAKE THE CANVAS CONTROLLER	
        canvasController = new CanvasController(app);
        canvas.setOnMousePressed(e -> {
            canvasController.processCanvasMousePress((int) e.getX(), (int) e.getY());
        });
        canvas.setOnMouseReleased(e -> {
            canvasController.processCanvasMouseRelease((int) e.getX(), (int) e.getY());
        });
        canvas.setOnMouseDragged(e -> {
	    canvasController.processCanvasMouseDragged((int)e.getX(), (int)e.getY());
	});
    }

    // HELPER METHOD
    public void loadSelectedShapeSettings(Shape shape) {
	if (shape != null) {
	    Color fillColor = (Color)shape.getFill();
	    Color strokeColor = (Color)shape.getStroke();
	    double lineThickness = shape.getStrokeWidth();
	    fillColorPicker.setValue(fillColor);
	    outlineColorPicker.setValue(strokeColor);
	    outlineThicknessSlider.setValue(lineThickness);	    
	}
    }

    /**
     * This function specifies the CSS style classes for all the UI components
     * known at the time the workspace is initially constructed. Note that the
     * tag editor controls are added and removed dynamicaly as the application
     * runs so they will have their style setup separately.
     */
    public void initStyle() {
	// NOTE THAT EACH CLASS SHOULD CORRESPOND TO
	// A STYLE CLASS SPECIFIED IN THIS APPLICATION'S
	// CSS FILE
	canvas.getStyleClass().add(CLASS_RENDER_CANVAS);
	
	// COLOR PICKER STYLE
	fillColorPicker.getStyleClass().add(CLASS_BUTTON);
	outlineColorPicker.getStyleClass().add(CLASS_BUTTON);
	backgroundColorPicker.getStyleClass().add(CLASS_BUTTON);
	
	editToolbar.getStyleClass().add(CLASS_EDIT_TOOLBAR);
	row1Box.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
        rowTempBox.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
	row2Box.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
	row3Box.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
	backgroundColorLabel.getStyleClass().add(CLASS_COLOR_CHOOSER_CONTROL);
	
	row4Box.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
	fillColorLabel.getStyleClass().add(CLASS_COLOR_CHOOSER_CONTROL);
	row5Box.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
	outlineColorLabel.getStyleClass().add(CLASS_COLOR_CHOOSER_CONTROL);
	row6Box.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
	outlineThicknessLabel.getStyleClass().add(CLASS_COLOR_CHOOSER_CONTROL);
	row7Box.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
    }

    /**
     * This function reloads all the controls for editing logos
     * the workspace.
     */
    @Override
    public void reloadWorkspace(AppDataComponent data) {
	golData dataManager = (golData)data;
        changeLanguageButton.setDisable(false);
        aboutButton.setDisable(false);
        imageButton.setDisable(false);
        textBoxButton.setDisable(false);
        
	if (dataManager.isInState(golState.STARTING_RECTANGLE)) {
	    selectionToolButton.setDisable(false);
	    removeButton.setDisable(true);
	    rectButton.setDisable(true);
	    ellipseButton.setDisable(false);
	}
	else if (dataManager.isInState(golState.STARTING_ELLIPSE)) {
	    selectionToolButton.setDisable(false);
	    removeButton.setDisable(true);
	    rectButton.setDisable(false);
	    ellipseButton.setDisable(true);
	}
	else if (dataManager.isInState(golState.SELECTING_SHAPE) 
		|| dataManager.isInState(golState.DRAGGING_SHAPE)
		|| dataManager.isInState(golState.DRAGGING_NOTHING)) {
	    boolean shapeIsNotSelected = dataManager.getSelectedShape() == null;
	    selectionToolButton.setDisable(true);
	    removeButton.setDisable(shapeIsNotSelected);
	    rectButton.setDisable(false);
	    ellipseButton.setDisable(false);
	    moveToFrontButton.setDisable(shapeIsNotSelected);
	    moveToBackButton.setDisable(shapeIsNotSelected);
	}
	
	removeButton.setDisable(dataManager.getSelectedShape() == null);
	backgroundColorPicker.setValue(dataManager.getBackgroundColor());
    }
    
    @Override
    public void resetWorkspace() {
        // WE ARE NOT USING THIS, THOUGH YOU MAY IF YOU LIKE
    }
}