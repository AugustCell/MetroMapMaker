package M3.data;

import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import static M3.data.m3State.SELECTING_SHAPE;
import M3.gui.m3Workspace;
import djf.components.AppDataComponent;
import djf.AppTemplate;
import M3.transaction.AddShape_Transaction;
import java.util.Optional;
import javafx.scene.Group;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import jtps.jTPS;
import jtps.jTPS_Transaction;

/**
 * This class serves as the data management component for this application.
 *
 * @author Richard McKenna
 * @author ?
 * @version 1.0
 */
public class m3Data implements AppDataComponent {
    // FIRST THE THINGS THAT HAVE TO BE SAVED TO FILES
    
    // THESE ARE THE SHAPES TO DRAW
    ObservableList<Node> shapes;
    
    
    ObservableList<Node> lines;
    
    ObservableList<Node> stations;
    
    ArrayList<String> lineNames = new ArrayList<String>();
    
    ArrayList<String> stationNames = new ArrayList<String>();
    
    ArrayList<LineGroups> lineStationGroups = new ArrayList<LineGroups>();
    
    ArrayList<StationTracker> stationTracker = new ArrayList<StationTracker>();
    
    Rectangle clip = new Rectangle(4000, 4000);
    
    
   
    boolean boxChecked;


    // THE BACKGROUND COLOR
    Color backgroundColor;
    
    // AND NOW THE EDITING DATA

    // THIS IS THE SHAPE CURRENTLY BEING SIZED BUT NOT YET ADDED
    Node newShape;

    // THIS IS THE SHAPE CURRENTLY SELECTED
    Node selectedShape;
    
    ImageView newImage;
    
    ImageView selectedImage;

    // FOR FILL AND OUTLINE
    Color currentFillColor;
    Color currentOutlineColor;
    double currentBorderWidth;

    // CURRENT STATE OF THE APP
    m3State state;

    // THIS IS A SHARED REFERENCE TO THE APPLICATION
    AppTemplate app;
    
    // USE THIS WHEN THE SHAPE IS SELECTED
    Effect highlightedEffect;
    
    //ORIGINS FOR DRAGGING AND IMPLEMENTING UNDO/REDO
    double imgOriginX;
    double imgOriginY;
    double stationOriginX;
    double stationOriginY;
    double textOriginX;
    double textOriginY;
    double lineStartOriginX;
    double lineStartOriginY;
    double lineEndOriginX;
    double lineEndOriginY;
  
    
    public static final String WHITE_HEX = "#FFFFFF";
    public static final String BLACK_HEX = "#000000";
    public static final String YELLOW_HEX = "#EEEE00";
    public static final Paint DEFAULT_BACKGROUND_COLOR = Paint.valueOf(WHITE_HEX);
    public static final Paint HIGHLIGHTED_COLOR = Paint.valueOf(YELLOW_HEX);
    public static final int HIGHLIGHTED_STROKE_THICKNESS = 3;
    
    static jTPS jTPS = new jTPS();
    jTPS_Transaction transaction;
    
    /**
     * THis constructor creates the data manager and sets up the
     *
     *
     * @param initApp The application within which this data manager is serving.
     */
    public m3Data(AppTemplate initApp) {
	// KEEP THE APP FOR LATER
	app = initApp;

	// NO SHAPE STARTS OUT AS SELECTED
	newShape = null;
	selectedShape = null;

	// INIT THE COLORS
	currentFillColor = Color.web(WHITE_HEX);
	currentOutlineColor = Color.web(BLACK_HEX);
	currentBorderWidth = 1;
	
	// THIS IS FOR THE SELECTED SHAPE
	DropShadow dropShadowEffect = new DropShadow();
	dropShadowEffect.setOffsetX(0.0f);
	dropShadowEffect.setOffsetY(0.0f);
	dropShadowEffect.setSpread(1.0);
	dropShadowEffect.setColor(Color.YELLOW);
	dropShadowEffect.setBlurType(BlurType.GAUSSIAN);
	dropShadowEffect.setRadius(15);
	highlightedEffect = dropShadowEffect;
        
        
        boxChecked = false;
        
        
    }

    
    public ObservableList<Node> getShapes() {
	return shapes;
    }
    
    public ArrayList<String> getLines(){
        return lineNames;
    }
    
    public ArrayList<String> getStations(){
        return stationNames;
    }
    public ArrayList<LineGroups> getLineStationGroups(){
        return lineStationGroups;
    }
    public ArrayList<StationTracker> getStationTracker(){
        return stationTracker;
    }

    public Color getBackgroundColor() {
	return backgroundColor;
    }
    
    public Color getCurrentFillColor() {
	return currentFillColor;
    }

    public Color getCurrentOutlineColor() {
	return currentOutlineColor;
    }

    public double getCurrentBorderWidth() {
	return currentBorderWidth;
    }
    
    public void setShapes(ObservableList<Node> initShapes) {
	shapes = initShapes;
    }
    
    public jTPS getjTPS(){
        return jTPS;
    }
    
    public double getImageOriginX(){
        return imgOriginX;
    }
    
    public double getRectOriginY(){
        return imgOriginY;
    }
    
    public double getStationOriginX(){
        return stationOriginX;
    }
    
    public double getStationOriginY(){
        return stationOriginY;
    }
    
    public double getTextOriginX(){
        return textOriginX;
    }
    
    public double getTextOriginY(){
        return textOriginY;
    }
    
    public double getLineStartOriginX(){
        return lineStartOriginX;
    }
    
    public double getLineStartOriginY(){
        return lineStartOriginY;
    }
    
    public double getLineEndOriginX(){
        return lineEndOriginX;
    }
    
    public double getLineOriginY(){
        return lineEndOriginY;
    }
    
    public Rectangle getClip(){
        return clip;
    }
    public void setClipWidth(double value){
        clip.setWidth(value);
    }
    public void setClipHeight(double value){
        clip.setHeight(value);
    }
    public boolean getChecked(){
        return boxChecked;
    }
    public void setChecked(boolean result){
        boxChecked = result;
    }
    
    
    public void setBackgroundColor(Color initBackgroundColor) {
	backgroundColor = initBackgroundColor;
	m3Workspace workspace = (m3Workspace)app.getWorkspaceComponent();
	Pane canvas = workspace.getCanvas();
	BackgroundFill fill = new BackgroundFill(backgroundColor, null, null);
	Background background = new Background(fill);
	canvas.setBackground(background);
    }

    public void setCurrentFillColor(Color initColor) {
	currentFillColor = initColor;
	if (selectedShape != null)
	    ((Shape)selectedShape).setFill(currentFillColor);
    }

    public void setCurrentOutlineColor(Color initColor) {
	currentOutlineColor = initColor;
	if (selectedShape != null) {
	    ((Shape)selectedShape).setStroke(initColor);
	}
    }

    public void setCurrentOutlineThickness(int initBorderWidth) {
	currentBorderWidth = initBorderWidth;
	if (selectedShape != null) {
	    ((Shape)selectedShape).setStrokeWidth(initBorderWidth);
	}
    }
    
    public void removeSelectedShape() {
	if (selectedShape != null) {
	    shapes.remove(selectedShape);
	    selectedShape = null;
	}
    }
    
    public void moveSelectedShapeToBack() {
        if (selectedShape != null) {
            shapes.remove(selectedShape);
            if (shapes.isEmpty()) {
                shapes.add(selectedShape);
            } else {
                ArrayList<Node> temp = new ArrayList<>();
                temp.add(selectedShape);
                for (Node node : shapes) {
                    temp.add(node);
                }
                shapes.clear();
                for (Node node : temp) {
                    shapes.add(node);
                }
            }
        }
    }
    
    
    
    @Override
    public void resetData() {
        setState(SELECTING_SHAPE);
	newShape = null;
	selectedShape = null;

	// INIT THE COLORS
	currentFillColor = Color.web(WHITE_HEX);
	currentOutlineColor = Color.web(BLACK_HEX);
	
	shapes.clear();
	((m3Workspace)app.getWorkspaceComponent()).getCanvas().getChildren().clear();
    }
    
    public void unhighlightShape(Node shape) {
	selectedShape.setEffect(null);
    }
    public void unhighlightImage(ImageView image){
        selectedImage.setEffect(null);
    }
    
    public void highlightShape(Node shape) {
	shape.setEffect(highlightedEffect);
    }

    public void highlightImage(ImageView image){
        image.setEffect(highlightedEffect);
    }
    
    public void initNewShape() {
	// DESELECT THE SELECTED SHAPE IF THERE IS ONE
	if (selectedShape != null) {
	    unhighlightShape(selectedShape);
	    selectedShape = null;
	}

	// USE THE CURRENT SETTINGS FOR THIS NEW SHAPE
	m3Workspace workspace = (m3Workspace)app.getWorkspaceComponent();
        //FIX THIS PROBLEM ASAP ************************************************
	//((Shape)newShape).setFill(workspace.getFillColorPicker().getValue());
	//((Shape)newShape).setStroke(workspace.getOutlineColorPicker().getValue());
	//((Shape)newShape).setStrokeWidth(workspace.getOutlineThicknessSlider().getValue());
	
	// ADD THE SHAPE TO THE CANVAS
	//shapes.add(newShape);
	
	// GO INTO SHAPE SIZING MODE
	state = m3State.SELECTING_SHAPE;
        //FIX THIS PROBLEM ASAP*************************************************
        //transaction = new AddShape_Transaction(app ,newShape);
        jTPS.addTransaction(transaction);
    }

    public Node getNewShape() {
	return newShape;
    }
    
    public Node getSelectedShape() {
	return selectedShape;
    }
    
    public void setSelectedShape(Node initSelectedShape) {
	selectedShape = initSelectedShape;
    }
    
    public Node getStationName(int x, int y) {
        Node shape = getTopShape(x, y);
        if (shape != null) {
            if (shape instanceof Group) {
                return shape;
            }
        }
        if(shape == null){
            return null;
        }
        return shape;
    }

    public Node selectTopShape(int x, int y) {
        Node shape = getTopShape(x, y);
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();

        if (shape == selectedShape)
	    return shape;

        if (selectedShape != null) {
            unhighlightShape(selectedShape);
        }

        if (shape instanceof GridLine || shape instanceof LineGroups) {
            unhighlightShape(selectedShape);
        }
        else if(shape instanceof DraggableText){
            DraggableText tempText = (DraggableText) shape;
            for(int i = 0; i < shapes.size(); i++){
                if(shapes.get(i) instanceof LineGroups){
                    LineGroups tempGroup = (LineGroups) shapes.get(i);
                    if(tempGroup.getLineName().equals(tempText.getText())){
                        workspace.getLineBox().getSelectionModel().select(tempText.getText());
                        highlightShape(shape);
                        break;
                    }
                }
            }
        }
        
        else if(shape instanceof DraggableStation){
            workspace.getStationBox().getSelectionModel().select(((DraggableStation) shape).getStationName());
            highlightShape(shape);
        }
        
        else if (shape != null) {
            if (shape instanceof DraggableStation) {
                highlightShape(shape);
            } else if (shape instanceof Group) {
                Group group = (Group) shape;
                for (int i = 0; i < group.getChildren().size(); i++) {
                    if (group.getChildren().get(i).contains(x, y)) {
                        if (group.getChildren().get(i) instanceof DraggableStation) {
                            highlightShape(group.getChildren().get(i));
                            shape = group.getChildren().get(i);
                            break;
                        } else if (group.getChildren().get(i) instanceof DraggableText) {
                            highlightShape(group.getChildren().get(i));
                            shape = group.getChildren().get(i);
                            break;
                        }
                    }
                }
            } else {
                highlightShape(shape);
            }

            if (!(shape instanceof DraggableImage)) {
                workspace.loadSelectedShapeSettings(shape);
            }
        }
        selectedShape = shape;
        if (shape != null) {
            ((Draggable) (Node) shape).start(x, y);
        }
        return shape;
    }

    public Node getTopShape(int x, int y) {
	for (int i = shapes.size() - 1; i >= 0; i--) {
	    Node shape = (Node)shapes.get(i);
	    if (shape.contains(x, y)) {
		return shape;
	    }
	}
	return null;
    }
    
    public void addShape(Node shapeToAdd) {
	shapes.add(shapeToAdd);
    }

    public void removeShape(Node shapeToRemove) {
	shapes.remove(shapeToRemove);
    }
    
    public void addLineName(String name){
        lineNames.add(name);
    }
    
    public void removeLineName(String name){
        lineNames.remove(name);
    }
    
    public void addStationNames(String name){
        stationNames.add(name);
    }
    
    public void removeStationName(String name){
        stationNames.remove(name);
    }
    
    public void addLineGroupName(LineGroups group){
        lineStationGroups.add(group);
    }
    
    public void removeLineGroupName(LineGroups group){
        lineStationGroups.remove(group);
    }
    
    public m3State getState() {
	return state;
    }

    public void setState(m3State initState) {
	state = initState;
    }

    public boolean isInState(m3State testState) {
	return state == testState;
    }
}
