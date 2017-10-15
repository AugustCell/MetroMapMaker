package gol.gui;

import gol.transaction.ItalicizeFont_Transaction;
import gol.transaction.EditFont_Transaction;
import gol.transaction.EditFontSize_Transaction;
import gol.transaction.BoldenFont_Transaction;
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
import static djf.settings.AppPropertyType.COPY_ICON;
import static djf.settings.AppPropertyType.COPY_TOOLTIP;
import static djf.settings.AppPropertyType.CUT_ICON;
import static djf.settings.AppPropertyType.CUT_TOOLTIP;
import static djf.settings.AppPropertyType.PASTE_ICON;
import static djf.settings.AppPropertyType.PASTE_TOOLTIP;
import static djf.settings.AppStartupConstants.APP_PROPERTIES_FILE_NAME;
import static djf.settings.AppStartupConstants.APP_PROPERTIES_FILE_NAME_SPANISH;
import static djf.settings.AppStartupConstants.FILE_PROTOCOL;
import static djf.settings.AppStartupConstants.PATH_IMAGES;
import static gol.css.golStyle.*;
import gol.data.DraggableEllipse;
import gol.data.DraggableRectangle;
import gol.data.DraggableText;
import gol.golLanguageProperty;
import static gol.golLanguageProperty.ADD_IMAGE_ICON;
import static gol.golLanguageProperty.ADD_IMAGE_TOOLTIP;
import static gol.golLanguageProperty.ADD_TEXT_ICON;
import static gol.golLanguageProperty.ADD_TEXT_TOOLTIP;
import static gol.golLanguageProperty.BOLD_ICON;
import static gol.golLanguageProperty.BOLD_TOOLTIP;
import static gol.golLanguageProperty.ITALIC_ICON;
import static gol.golLanguageProperty.ITALIC_TOOLTIP;
import static gol.golLanguageProperty.REDO_ICON;
import static gol.golLanguageProperty.REDO_TOOLTIP;
import static gol.golLanguageProperty.UNDO_ICON;
import static gol.golLanguageProperty.UNDO_TOOLTIP;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import jtps.jTPS;
import jtps.jTPS_Transaction;

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
    
    golData dataManager;
    
    // HAS ALL THE CONTROLS FOR EDITING
    VBox editToolbar;
    
    
    private Button copyButton;
    private Button pasteButton;
    private Button cutButton;
    private Button undoButton;
    private Button redoButton;
    
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
    
    
    //Row for editing text and stuff
    HBox editTextBox;
    Button boldButton;
    Button italicButton;
    ComboBox<String> font;
    ComboBox<String> size;
    
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
    
    Node copiedNode;
  
    
    jTPS_Transaction transaction;
    

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

      
        dataManager = (golData) app.getDataComponent();
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
      
     public Button getCopyButton() {
        return copyButton;
    }

    public Button getPasteButton() {
        return pasteButton;
    }
    
    public Button getCutButton(){
        return cutButton;
    }
    
    public Button getUndoButton(){
        return undoButton;
    }
    
    public Button getRedoButton(){
        return redoButton;
    }
    
    // HELPER SETUP METHOD
    private void initLayout() {  
 
	// THIS WILL GO IN THE LEFT SIDE OF THE WORKSPACE
	editToolbar = new VBox();
	
        copyButton = gui.initChildButton(gui.getCenterToolbar(), COPY_ICON.toString(), COPY_TOOLTIP.toString(), false); 
        pasteButton = gui.initChildButton(gui.getCenterToolbar(), PASTE_ICON.toString(), PASTE_TOOLTIP.toString(), false); 
        cutButton = gui.initChildButton(gui.getCenterToolbar(), CUT_ICON.toString(), CUT_TOOLTIP.toString(), false);
        undoButton = gui.initChildButton(gui.getCenterToolbar(), UNDO_ICON.toString(), UNDO_TOOLTIP.toString(), false);
        redoButton = gui.initChildButton(gui.getCenterToolbar(), REDO_ICON.toString(), REDO_TOOLTIP.toString(), false);
        
        
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

        
        //Row for modifying text and stuff
        editTextBox = new HBox();
        VBox editHolder = new VBox();
        font = new ComboBox<>();
        size = new ComboBox<>();
        ObservableList<String> fontOptions
                = FXCollections.observableArrayList(
                        "Times New Roman",
                        "Forte",
                        "Elephant",
                        "Verdana",
                        "Helvetica",
                        "Comic Sans MS"
                );
        ObservableList<String> sizeOptions
                = FXCollections.observableArrayList(
                        "10", "12", "14", "16", "18", "20", "24", "28", "32", "36", "40", "44", "48", "50", "52", "54", "56", "58", "60", "70"
                );
        font.setItems(fontOptions);
        
        size.setItems(sizeOptions);
        if (app.getPreferredLanguage().equals(APP_PROPERTIES_FILE_NAME)) {
            font.setPromptText("Change font");
            size.setPromptText("Change text size");
        }
        if (app.getPreferredLanguage().equals(APP_PROPERTIES_FILE_NAME_SPANISH)) {
            font.setPromptText("Cambiar fuente");
            size.setPromptText("Cambiar el tamaño del texto");
        }
        editHolder.getChildren().addAll(font, size);

        VBox boldItalic = new VBox();
        boldButton = gui.initChildButton(boldItalic, BOLD_ICON.toString(), BOLD_TOOLTIP.toString(), false);
        italicButton = gui.initChildButton(boldItalic, ITALIC_ICON.toString(), ITALIC_TOOLTIP.toString(), false);
        
        editTextBox.getChildren().addAll(editHolder, boldItalic);
        
        
        
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
        editToolbar.getChildren().add(editTextBox);
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
       

        // AND NOW SETUP THE WORKSPACE
        workspace = new BorderPane();
        ((BorderPane) workspace).setLeft(editToolbar);
        ((BorderPane) workspace).setCenter(canvas);
        editToolbar.setMaxHeight(100);
        editToolbar.setMinHeight(100);
        editToolbar.setPrefHeight(100); 

        
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
        font.setOnAction(e -> {

            Node node = dataManager.getSelectedShape();
            if (node != null) {
                String s = font.getSelectionModel().getSelectedItem();
                if (s != null) {
                    transaction = new EditFont_Transaction(app, node, s, ((DraggableText) node).getFont().getFamily(), ((DraggableText) node).getFont().getSize());
                    dataManager.getjTPS().addTransaction(transaction);
                }
            }
        });
        size.setOnAction(e -> {

            Node node = dataManager.getSelectedShape();
            if (node != null) {
                String i = size.getSelectionModel().getSelectedItem();
                double val = Double.parseDouble(i);
                if (i != null) {
                    transaction = new EditFontSize_Transaction(app, node, val, ((DraggableText) node).getFont().getFamily(), ((DraggableText) node).getFont().getSize());
                    dataManager.getjTPS().addTransaction(transaction);
                }
            }
        });
        
        boldButton.setOnAction(e -> {
            Node node = dataManager.getSelectedShape();
            if (node != null) {
                String i = size.getSelectionModel().getSelectedItem();
                if (i != null) {
                    transaction = new BoldenFont_Transaction(app, node, ((DraggableText) node).getFont().getFamily(), ((DraggableText) node).getFont().getSize(), FontWeight.NORMAL);
                    dataManager.getjTPS().addTransaction(transaction);
                   // ((DraggableText) node).setFont(Font.font(((DraggableText) node).getFont().getFamily(), FontWeight.BOLD, ((DraggableText) node).getFont().getSize()));
                }
            }
        });
        
        italicButton.setOnAction(e -> {
            Node node = dataManager.getSelectedShape();
            if (node != null) {
                String i = size.getSelectionModel().getSelectedItem();
                if (i != null) {
                    transaction = new ItalicizeFont_Transaction(app, node, ((DraggableText) node).getFont().getFamily(), ((DraggableText) node).getFont().getSize(), FontPosture.ITALIC);
                    dataManager.getjTPS().addTransaction(transaction);
                  //  ((DraggableText) node).setFont(Font.font(((DraggableText) node).getFont().getFamily(), FontPosture.ITALIC, ((DraggableText) node).getFont().getSize()));
                }
            }
        });
        
        copyButton.setOnAction(e -> {
            Node node = dataManager.getSelectedShape();
            if (node instanceof DraggableRectangle) {
                DraggableRectangle tempRect = new DraggableRectangle();
                tempRect.setWidth(((DraggableRectangle) node).getWidth());
                tempRect.setHeight(((DraggableRectangle) node).getHeight());
                tempRect.setX(((DraggableRectangle) node).getX());
                tempRect.setY(((DraggableRectangle) node).getY());
                tempRect.setFill(((DraggableRectangle) node).getFill());
                tempRect.setEffect(node.getEffect());
                copiedNode = tempRect;
            } else if(node instanceof DraggableEllipse){
                DraggableEllipse tempEllipse = new DraggableEllipse();
                tempEllipse.setCenterX(((DraggableEllipse) node).getCenterX());
                tempEllipse.setCenterY(((DraggableEllipse) node).getCenterY());
                tempEllipse.setRadiusX(((DraggableEllipse) node).getRadiusX());
                tempEllipse.setRadiusY(((DraggableEllipse) node).getRadiusY());
                tempEllipse.setFill(((DraggableEllipse) node).getFill());
                tempEllipse.setEffect(node.getEffect());
                copiedNode = tempEllipse;
            } else if(node instanceof DraggableText){
                DraggableText tempText = new DraggableText();
                tempText.setText(((DraggableText) node).getText());
                tempText.setEffect(node.getEffect());
                tempText.setFont(((DraggableText) node).getFont());
                copiedNode = tempText;
            }
        });
        
        pasteButton.setOnAction(e -> {
            canvas.getChildren().add(copiedNode);
        });
        
        cutButton.setOnAction(e -> {
             Node node = dataManager.getSelectedShape();
            if (node instanceof DraggableRectangle) {
                DraggableRectangle tempRect = new DraggableRectangle();
                tempRect.setWidth(((DraggableRectangle) node).getWidth());
                tempRect.setHeight(((DraggableRectangle) node).getHeight());
                tempRect.setX(((DraggableRectangle) node).getX());
                tempRect.setY(((DraggableRectangle) node).getY());
                tempRect.setFill(((DraggableRectangle) node).getFill());
                tempRect.setEffect(node.getEffect());
                copiedNode = tempRect;
            } else if(node instanceof DraggableEllipse){
                DraggableEllipse tempEllipse = new DraggableEllipse();
                tempEllipse.setCenterX(((DraggableEllipse) node).getCenterX());
                tempEllipse.setCenterY(((DraggableEllipse) node).getCenterY());
                tempEllipse.setRadiusX(((DraggableEllipse) node).getRadiusX());
                tempEllipse.setRadiusY(((DraggableEllipse) node).getRadiusY());
                tempEllipse.setFill(((DraggableEllipse) node).getFill());
                tempEllipse.setEffect(node.getEffect());
                copiedNode = tempEllipse;
            } else if(node instanceof DraggableText){
                DraggableText tempText = new DraggableText();
                tempText.setText(((DraggableText) node).getText());
                tempText.setEffect(node.getEffect());
                tempText.setFont(((DraggableText) node).getFont());
                copiedNode = tempText;
            }
            logoEditController.processRemoveSelectedShape();
        });
        
        undoButton.setOnAction(e -> {
            dataManager.getjTPS().undoTransaction();
        });
        
        redoButton.setOnAction(e -> {
            dataManager.getjTPS().doTransaction();
        });
        
        // MAKE THE CANVAS CONTROLLER	
        canvasController = new CanvasController(app);
        canvas.setOnMousePressed(e -> {
            canvasController.processCanvasMousePress((int) e.getX(), (int) e.getY());
            if (dataManager.getSelectedShape() instanceof DraggableText) {
                if (e.getClickCount() == 2) {
                    logoEditController = new LogoEditController(app);
                    logoEditController.handleEditTextRequests();
                }
            }
        });
        canvas.setOnMouseReleased(e -> {
            canvasController.processCanvasMouseRelease((int) e.getX(), (int) e.getY());
        });
        canvas.setOnMouseDragged(e -> {
	    canvasController.processCanvasMouseDragged((int)e.getX(), (int)e.getY());
	});
    }

    // HELPER METHOD
    public void loadSelectedShapeSettings(Node shape) {
        if (shape != null) {
           // Color fillColor = (Color) ((Shape) shape).getFill();
            Color strokeColor = (Color) ((Shape) shape).getStroke();
            double lineThickness = ((Shape) shape).getStrokeWidth();
           // fillColorPicker.setValue(fillColor);
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
        editTextBox.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
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
        else if(dataManager.isInState(golState.ADD_IMAGE))          {
            selectionToolButton.setDisable(false);
            removeButton.setDisable(false);
            rectButton.setDisable(true);
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