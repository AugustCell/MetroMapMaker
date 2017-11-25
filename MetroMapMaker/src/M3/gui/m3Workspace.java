package M3.gui;


import djf.AppTemplate;
import djf.components.AppDataComponent;
import djf.components.AppWorkspaceComponent;
import static djf.settings.AppPropertyType.REDO_TOOLTIP;
import static djf.settings.AppPropertyType.UNDO_TOOLTIP;
import djf.ui.AppGUI;
import djf.ui.AppMessageDialogSingleton;
import djf.ui.AppYesNoCancelDialogSingleton;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import jtps.jTPS_Transaction;
import static M3.css.m3Style.CLASS_COLOR_CHOOSER_CONTROL;
import static M3.css.m3Style.CLASS_EDIT_TOOLBAR;
import static M3.css.m3Style.CLASS_EDIT_TOOLBAR_ROW;
import static M3.css.m3Style.CLASS_RENDER_CANVAS;
import M3.data.m3Data;
import static M3.m3LanguageProperty.ADD_ELEMENT_ICON;
import static M3.m3LanguageProperty.ADD_IMAGE_TOOLTIP;
import static M3.m3LanguageProperty.ADD_LABEL_TOOLTIP;
import static M3.m3LanguageProperty.ADD_LINE_TOOLTIP;
import static M3.m3LanguageProperty.ADD_STATION_TOOLTIP;
import static M3.m3LanguageProperty.ADD_STATION_TO_LINE_TOOLTIP;
import static M3.m3LanguageProperty.BOLD_ICON;
import static M3.m3LanguageProperty.BOLD_TOOLTIP;
import static M3.m3LanguageProperty.EDIT_LINE_TOOLTIP;
import static M3.m3LanguageProperty.EDIT_STATION_TOOLTIP;
import static M3.m3LanguageProperty.EXPAND_MAP_ICON;
import static M3.m3LanguageProperty.EXPAND_MAP_TOOLTIP;
import static M3.m3LanguageProperty.ITALIC_ICON;
import static M3.m3LanguageProperty.ITALIC_TOOLTIP;
import static M3.m3LanguageProperty.LIST_ICON;
import static M3.m3LanguageProperty.LIST_TOOLTIP;
import static M3.m3LanguageProperty.MINIMIZE_MAP_ICON;
import static M3.m3LanguageProperty.MINIMIZE_MAP_TOOLTIP;
import static M3.m3LanguageProperty.MOVE_LABEL_TOOLTIP;
import static M3.m3LanguageProperty.REMOVE_ELEMENT_ICON;
import static M3.m3LanguageProperty.REMOVE_ELEMENT_TOOLTIP;
import static M3.m3LanguageProperty.REMOVE_LINE_TOOLTIP;
import static M3.m3LanguageProperty.REMOVE_STATION_FROM_LINE_TOOLTIP;
import static M3.m3LanguageProperty.REMOVE_STATION_TOOLTIP;
import static M3.m3LanguageProperty.ROTATE_LABEL_ICON;
import static M3.m3LanguageProperty.ROTATE_LABEL_TOOLTIP;
import static M3.m3LanguageProperty.ROUTE_ICON;
import static M3.m3LanguageProperty.ROUTE_TOOLTIP;
import static M3.m3LanguageProperty.SET_BACKGROUND_TOOLTIP;
import static M3.m3LanguageProperty.SET_IMAGE_BACKGROUND_TOOLTIP;
import static M3.m3LanguageProperty.SNAP_STATION_TOOLTIP;
import static M3.m3LanguageProperty.TEXT_COLOR_TOOLTIP;
import static M3.m3LanguageProperty.ZOOM_IN_ICON;
import static M3.m3LanguageProperty.ZOOM_IN_TOOLTIP;
import static M3.m3LanguageProperty.ZOOM_OUT_ICON;
import static M3.m3LanguageProperty.ZOOM_OUT_TOOLTIP;
import javafx.geometry.Insets;

/**
 * This class serves as the workspace component for this application, providing
 * the user interface controls for editing work.
 *
 * @author Richard McKenna
 * @author ?
 * @version 1.0
 */
public class m3Workspace extends AppWorkspaceComponent {
   
    // HERE'S THE APP
    AppTemplate app;

    // IT KNOWS THE GUI IT IS PLACED INSIDE
    AppGUI gui;
    
    m3Data dataManager;
    
    // HAS ALL THE CONTROLS FOR EDITING
    VBox editToolbar;
    
    Button undoButton;
    Button redoButton;
    
    // FIRST BOX
    VBox metroLineBox;
    
    //FIRST ROW FIRST BOX
    HBox row1Box;
    Label lineText;
    ComboBox<String> lineCombo;
    Button editLineButton;
    
    //SECOND ROW FIRST BOX
    HBox row2Box;
    Button addLineButton;
    Button removeLineButton;
    Button addStationToLineButton;
    Button removeStationFromLineButton;
    Button listStationsButton;
    
    //THIRD ROW FIRST BOX
    Slider lineThickness;
    
    //SECOND BOX
    VBox metroStationsBox;
    
    //FIRST ROW SECOND BOX
    HBox row3Box;
    Label stationText;
    ComboBox<String> stationCombo;
    Button editStationButton;
    
    //SECOND ROW SECOND BOX
    HBox row4Box;
    Button addStationButton;
    Button removeStationButton;
    Button snapToMapButton;
    Button moveLabelPosButton;
    Button rotateLabelButton;
    
    //THIRD ROW SECOND BOX
    Slider stationRadius;
    
    //THIRD BOX
    HBox routerBox;
    
    //FIRST COLUMN THIRD BOX
    VBox column1Box;
    ComboBox<String> originCombo;
    ComboBox<String> destCombo;
    
    //SECOND COLUMN THIRD BOX
    Button routerButton;
    
    //FOURTH BOX
    VBox decorBox;
    
    //ROW 1 FOURTH BOX
    HBox row5Box;
    Label decorText;
    ColorPicker setBackgroundColorButton;
    
    //ROW 2 FOURTH BOX
    HBox row6Box;
    Button setImageBackgroundButton;
    Button addImageButton;
    Button addLabelButton;
    Button removeElementButton;
    
    //FIFTH BOX
    VBox textEditorBox;
    
    //FIRST ROW FIFTH BOX
    HBox row7Box;
    Label fontText;
    Button textColorButton;
    
    //SECOND ROW FIFTH BOX
    HBox row8Box;
    Button boldButton;
    Button italicizeButton;
    ComboBox<String> sizeCombo;
    ComboBox<String> fontCombo;
    
    //SIXTH BOX
    VBox mapEditingBox;
    
    //FIRST ROW SIXTH BOX
    HBox row9Box;
    Label mapEditingText;
    CheckBox showGridCheckBox;
    
    //SECOND ROW SIXTH BOX
    HBox row10Box;
    Button zoomInButton;
    Button zoomOutButton;
    Button shrinkMapButton;
    Button expandMapButton;
    
    
    // THIS IS WHERE WE'LL RENDER OUR DRAWING, NOTE THAT WE
    // CALL THIS A CANVAS, BUT IT'S REALLY JUST A Pane
    Pane canvas;
    
    // HERE ARE THE CONTROLLERS
    CanvasController canvasController;
    MapEditController mapEditController;    

    // HERE ARE OUR DIALOGS
    AppMessageDialogSingleton messageDialog;
    AppYesNoCancelDialogSingleton yesNoCancelDialog;
    
    // FOR DISPLAYING DEBUG STUFF
    Text debugText;
    
    Node copiedNode;
  
    
    jTPS_Transaction transaction;
    
    public m3Workspace(AppTemplate initApp) {
	// KEEP THIS FOR LATER
	app = initApp;
        
	// KEEP THE GUI FOR LATER
	gui = app.getGUI();

      
        dataManager = (m3Data) app.getDataComponent();
        // LAYOUT THE APP
        initLayout();
        
        // HOOK UP THE CONTROLLERS
        initControllers();
        
        // AND INIT THE STYLE FOR THE WORKSPACE
        initStyle();  
        
        
    }
    
    
    public ComboBox<String> getLineBox(){
        return lineCombo;
    }
    
    public ComboBox<String> getStationBox(){
        return stationCombo;
    }
    
    public ColorPicker getBackgroundColorPicker() {
	return setBackgroundColorButton;
    }
    /**
     * Note that this is for displaying text during development.
     */
    public void setDebugText(String text) {
        debugText.setText(text);
    }
    
    /**
     * @return the lineThickness
     */
    public Slider getLineThickness() {
        return lineThickness;
    }

    /**
     * @return the sationRadius
     */
    public Slider getStationRadius() {
        return stationRadius;
    }
    
    public Pane getCanvas() {
	return canvas;
    }
    
    private void initLayout(){
        
        // THIS WILL GO IN THE LEFT SIDE OF THE WORKSPACE
	editToolbar = new VBox();
        
        undoButton = gui.initTextChildButton(gui.getCenterToolbar(), "Undo", UNDO_TOOLTIP.toString(), false);
        redoButton = gui.initTextChildButton(gui.getCenterToolbar(), "Redo", REDO_TOOLTIP.toString(), false);
        
        //BOX 1
        metroLineBox = new VBox();
        
        //FIRST ROW FIRST BOX
        row1Box = new HBox();
        lineText = new Label("Metro Line");
        lineCombo = new ComboBox<>();
        lineCombo.setEditable(true);
        lineCombo.setPromptText("Select a metro line");
        row1Box.getChildren().add(lineText);
        row1Box.getChildren().add(lineCombo);
        editLineButton = gui.initTextChildButton(row1Box, "Edit Line", EDIT_LINE_TOOLTIP.toString(), false);
        row1Box.setSpacing(10);
        
        //SECOND ROW FRIST BOX
        row2Box = new HBox();
        addLineButton = gui.initChildButton(row2Box, ADD_ELEMENT_ICON.toString(), ADD_LINE_TOOLTIP.toString(), false);
        removeLineButton = gui.initChildButton(row2Box, REMOVE_ELEMENT_ICON.toString(), REMOVE_LINE_TOOLTIP.toString(), false);
        addStationToLineButton = gui.initTextChildButton(row2Box, "Add Station", ADD_STATION_TO_LINE_TOOLTIP.toString(), false);
        removeStationFromLineButton = gui.initTextChildButton(row2Box, "Remove Station", REMOVE_STATION_FROM_LINE_TOOLTIP.toString(), false);
        listStationsButton = gui.initChildButton(row2Box, LIST_ICON.toString(), LIST_TOOLTIP.toString(), false);
        row2Box.setSpacing(10);
        
        //ADD ALL TO VBOX
        lineThickness = new Slider(0, 10, 1);
        metroLineBox.getChildren().add(row1Box);
        metroLineBox.getChildren().add(row2Box);
        metroLineBox.getChildren().add(lineThickness);
        
        
        //BOX 2
        metroStationsBox = new VBox();
        
        //FIRST ROW SECOND BOX
        row3Box = new HBox();
        stationText = new Label("Metro Stations");
        stationCombo = new ComboBox<>();
        stationCombo.setEditable(true);
        stationCombo.setPromptText("Pick a station");
        row3Box.getChildren().add(stationText);
        row3Box.getChildren().add(stationCombo);
        editStationButton = gui.initTextChildButton(row3Box, "Edit Station", EDIT_STATION_TOOLTIP.toString(), false);
        row3Box.setSpacing(10);
        
        //SECOND ROW SECOND BOX
        row4Box = new HBox();
        addStationButton = gui.initChildButton(row4Box, ADD_ELEMENT_ICON.toString(), ADD_STATION_TOOLTIP.toString(), false);
        removeStationButton = gui.initChildButton(row4Box, REMOVE_ELEMENT_ICON.toString(), REMOVE_STATION_TOOLTIP.toString(), false);
        snapToMapButton = gui.initTextChildButton(row4Box, "Snap", SNAP_STATION_TOOLTIP.toString(), false);
        moveLabelPosButton = gui.initTextChildButton(row4Box, "Move Label", MOVE_LABEL_TOOLTIP.toString(), false);
        rotateLabelButton = gui.initChildButton(row4Box, ROTATE_LABEL_ICON.toString(), ROTATE_LABEL_TOOLTIP.toString(), false);
        row4Box.setSpacing(10);
        
        //THIRD ROW SECOND BOX
        stationRadius = new Slider(0, 10, 1);
        metroStationsBox.getChildren().add(row3Box);
        metroStationsBox.getChildren().add(row4Box);
        metroStationsBox.getChildren().add(stationRadius);
        
        //THIRD BOX
        routerBox = new HBox();
        
        //FIRST ROW THIRD BOX
        column1Box = new VBox();
        originCombo = new ComboBox<>();
        originCombo.setPromptText("Origin");
        destCombo = new ComboBox<>();
        originCombo.setStyle("-fx-pref-width : 420;");
        destCombo.setPromptText("Destination");
        destCombo.setStyle("-fx-pref-width : 420;");
        column1Box.getChildren().add(originCombo);
        column1Box.getChildren().add(destCombo);
        column1Box.setSpacing(10);

        //ADD THE 2 COLUMNS INTO THE BOX
        routerBox.getChildren().add(column1Box);
        routerButton = gui.initChildButton(routerBox, ROUTE_ICON.toString(), ROUTE_TOOLTIP.toString(), false);
        routerButton.setStyle("-fx-pref_width : 80;");
        routerButton.setStyle("-fx-pref-height : 80");
        
        //FOURTH BOX
        decorBox = new VBox();

        //ROW 1 FOURTH BOX
        row5Box = new HBox();
        decorText = new Label("Decor");
        row5Box.getChildren().add(decorText);
        setBackgroundColorButton = new ColorPicker();
        row5Box.getChildren().add(setBackgroundColorButton);
        row5Box.setSpacing(285);
        
        //ROW 2 FOURTH BOX
        row6Box = new HBox();
        setImageBackgroundButton = gui.initTextChildButton(row6Box, "Set Background Image", SET_IMAGE_BACKGROUND_TOOLTIP.toString(), false);
        addImageButton = gui.initTextChildButton(row6Box, "Add Image", ADD_IMAGE_TOOLTIP.toString(), false);
        addLabelButton = gui.initTextChildButton(row6Box, "Add Label", ADD_LABEL_TOOLTIP.toString(), false);
        removeElementButton = gui.initTextChildButton(row6Box, "Remove Element", REMOVE_ELEMENT_TOOLTIP.toString(), false);
        row6Box.setSpacing(10);
        
        //ADD INTO BOX
        decorBox.getChildren().add(row5Box);
        decorBox.getChildren().add(row6Box);
        
        
        //FIFTH BOX
        textEditorBox = new VBox();
        
        //FIRST ROW FIFTH BOX
        row7Box = new HBox();
        fontText = new Label("Font");
        row7Box.getChildren().add(fontText);
        textColorButton = gui.initTextChildButton(row7Box, "Text Color", TEXT_COLOR_TOOLTIP.toString(), false);
        row7Box.setSpacing(360);
        
        //SECOND ROW FIFTH BOX
        row8Box = new HBox();
        boldButton = gui.initChildButton(row8Box, BOLD_ICON.toString(), BOLD_TOOLTIP.toString(), false);
        italicizeButton = gui.initChildButton(row8Box, ITALIC_ICON.toString(), ITALIC_TOOLTIP.toString(), false);
        Label sizeComboLabel = new Label("Text size");
        Label fontComboLabel = new Label("Text font");
        sizeCombo = new ComboBox<>();
        sizeCombo.setPromptText("Text size");
        fontCombo = new ComboBox<>();
        fontCombo.setPromptText("Text font");
        row8Box.getChildren().add(sizeCombo);
        row8Box.getChildren().add(fontCombo);
        row8Box.setSpacing(10);
        
        //ADD ALL INTO FIFTH BOX
        textEditorBox.getChildren().add(row7Box);
        textEditorBox.getChildren().add(row8Box);
        
        //SIXTH BOX
        mapEditingBox = new VBox();
        
        //FIRST ROW SIXTH BOX
        row9Box = new HBox();
        mapEditingText = new Label("Navigation");
        showGridCheckBox = new CheckBox();
        showGridCheckBox.setText("Show Grid");
        row9Box.getChildren().add(mapEditingText);
        row9Box.getChildren().add(showGridCheckBox);
        row9Box.setSpacing(285);
        
        //SECOND ROW SIXTH BOX
        row10Box = new HBox();
        zoomInButton = gui.initChildButton(row10Box, ZOOM_IN_ICON.toString(), ZOOM_IN_TOOLTIP.toString(), false);
        zoomOutButton = gui.initChildButton(row10Box, ZOOM_OUT_ICON.toString(), ZOOM_OUT_TOOLTIP.toString(), false);
        shrinkMapButton = gui.initChildButton(row10Box, MINIMIZE_MAP_ICON.toString(), MINIMIZE_MAP_TOOLTIP.toString(), false);
        expandMapButton = gui.initChildButton(row10Box, EXPAND_MAP_ICON.toString(), EXPAND_MAP_TOOLTIP.toString(), false);
        row10Box.setSpacing(10);
        
        //ADD ALL INTO SIXTH BOX
        mapEditingBox.getChildren().add(row9Box);
        mapEditingBox.getChildren().add(row10Box);
        
        // NOW ORGANIZE THE EDIT TOOLBAR
        editToolbar.getChildren().add(metroLineBox);
        editToolbar.getChildren().add(metroStationsBox);
        editToolbar.getChildren().add(routerBox);
        editToolbar.getChildren().add(decorBox);
        editToolbar.getChildren().add(textEditorBox);
        editToolbar.getChildren().add(mapEditingBox);
        
        // WE'LL RENDER OUR STUFF HERE IN THE CANVAS
        canvas = new Pane();
        debugText = new Text();
        canvas.getChildren().add(debugText);
        debugText.setX(100);
        debugText.setY(100);
        
        // AND MAKE SURE THE DATA MANAGER IS IN SYNCH WITH THE PANE
        m3Data data = (m3Data) app.getDataComponent();
        data.setShapes(canvas.getChildren());
       

        // AND NOW SETUP THE WORKSPACE
        workspace = new BorderPane();
        ((BorderPane) workspace).setLeft(editToolbar);
        ((BorderPane) workspace).setCenter(canvas);
        editToolbar.setMaxHeight(100);
        editToolbar.setMinHeight(100);
        editToolbar.setPrefHeight(100); 
        
        
        
    }
    
    
     private void initControllers() {
         mapEditController = new MapEditController(app);
              
         
         
         lineCombo.setOnAction(e -> {
            String lineName = lineCombo.getSelectionModel().getSelectedItem();
            
         });
         editLineButton.setOnAction(e -> {
             mapEditController.handleEditLineRequest();
         });
         addLineButton.setOnAction(e -> {
             mapEditController.handleAddLineRequest();
         });
         removeLineButton.setOnAction(e -> {
             mapEditController.handleRemoveLineRequest();
         });
         addStationToLineButton.setOnAction(e -> {
             mapEditController.handleAddStationLineRequest();
         });
         removeStationFromLineButton.setOnAction(e -> {
             
         });
         listStationsButton.setOnAction(e -> {
             
         });
         lineThickness.valueProperty().addListener(e -> {
             
         });
         stationCombo.setOnAction(e -> {
             
         });
         editStationButton.setOnAction(e -> {
             
         });
         addStationButton.setOnAction(e -> {
             mapEditController.handleAddStationRequest();
         });
         removeStationButton.setOnAction(e -> {
             mapEditController.handleRemoveStationRequest();
         });
         snapToMapButton.setOnAction(e -> {
             
         });
         moveLabelPosButton.setOnAction(e -> {
             
         });
         rotateLabelButton.setOnAction(e -> {
             
         });
         stationRadius.valueProperty().addListener(e -> {
             
         });
         originCombo.setOnAction(e -> {
             
         });
         destCombo.setOnAction(e -> {
             
         });
         routerButton.setOnAction(e -> {
             
         });
         setBackgroundColorButton.setOnAction(e -> {
             mapEditController.processSelectBackgroundColor();
         });
         addImageButton.setOnAction(e -> {
             
         });
         addLabelButton.setOnAction(e -> {
             
         });
         removeElementButton.setOnAction(e -> {
             
         });
         textColorButton.setOnAction(e -> {
             
         });
         boldButton.setOnAction(e -> {
             
         });
         italicizeButton.setOnAction(e -> {
             
         });
         sizeCombo.setOnAction(e -> {
             
         });
         fontCombo.setOnAction(e -> {
             
         });
         showGridCheckBox.setOnAction(e -> {
             
         });
         zoomInButton.setOnAction(e -> {
             
         });
         zoomOutButton.setOnAction(e -> {
             
         });
         shrinkMapButton.setOnAction(e -> {
             
         });
         expandMapButton.setOnAction(e -> {
             
         });
         undoButton.setOnAction(e -> {
             
         });
         redoButton.setOnAction(e -> {
             
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
             canvasController.processCanvasMouseDragged((int) e.getX(), (int) e.getY());
         });
     }
    
     // HELPER METHOD
    public void loadSelectedShapeSettings(Node shape) {
        if (shape != null) {
            Color fillColor = (Color) ((Shape) shape).getFill();
            Color strokeColor = (Color) ((Shape) shape).getStroke();
            double lineThicknessValue = ((Shape) shape).getStrokeWidth();
            lineThickness.setValue(lineThicknessValue);
            
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
	
	editToolbar.getStyleClass().add(CLASS_EDIT_TOOLBAR);
       
        metroLineBox.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
	lineText.getStyleClass().add(CLASS_COLOR_CHOOSER_CONTROL);
        
	
        metroStationsBox.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
        stationText.getStyleClass().add(CLASS_COLOR_CHOOSER_CONTROL);
        
        routerBox.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
        
        decorBox.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
        decorText.getStyleClass().add(CLASS_COLOR_CHOOSER_CONTROL);
        
        textEditorBox.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
        fontText.getStyleClass().add(CLASS_COLOR_CHOOSER_CONTROL);
        
        mapEditingBox.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
        mapEditingText.getStyleClass().add(CLASS_COLOR_CHOOSER_CONTROL);
   
    }
    
    /**
     * This function reloads all the controls for editing logos
     * the workspace.
     */
    @Override
    public void reloadWorkspace(AppDataComponent data) {
        System.out.println("This is an old functionality");
    }
    
    @Override
    public void resetWorkspace() {
        System.out.println("This is an old functionality");
    }
}