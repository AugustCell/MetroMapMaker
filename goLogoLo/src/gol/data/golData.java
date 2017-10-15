package gol.data;

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
import static gol.data.golState.SELECTING_SHAPE;
import static gol.data.golState.SIZING_SHAPE;
import gol.gui.golWorkspace;
import djf.components.AppDataComponent;
import djf.AppTemplate;
import gol.transaction.AddShape_Transaction;
import java.util.Optional;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
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
public class golData implements AppDataComponent {
    // FIRST THE THINGS THAT HAVE TO BE SAVED TO FILES
    
    // THESE ARE THE SHAPES TO DRAW
    ObservableList<Node> shapes;
    
    ObservableList<Node> images;
    
    
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
    golState state;

    // THIS IS A SHARED REFERENCE TO THE APPLICATION
    AppTemplate app;
    
    // USE THIS WHEN THE SHAPE IS SELECTED
    Effect highlightedEffect;
    int rectOriginX;
    int rectOriginY;
    int ellipseOriginX;
    int ellipseOriginY;
    int textOriginX;
    int textOriginY;
    
    

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
    public golData(AppTemplate initApp) {
	// KEEP THE APP FOR LATER
	app = initApp;

	// NO SHAPE STARTS OUT AS SELECTED
	newShape = null;
	selectedShape = null;
        newImage = null;
        selectedImage = null;

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
    }
    
    public ObservableList<Node> getImages(){
        return images;
    }
    
    public ObservableList<Node> getShapes() {
	return shapes;
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
    
    public void setImages(ObservableList<Node> initImage){
        images = initImage;
    }
    
    public void setShapes(ObservableList<Node> initShapes) {
	shapes = initShapes;
    }
    
    public jTPS getjTPS(){
        return jTPS;
    }
    
    public int getRectOriginX(){
        return rectOriginX;
    }
    
    public int getRectOriginY(){
        return rectOriginY;
    }
    
    public int getEllipseOriginX(){
        return ellipseOriginX;
    }
    
    public int getEllipseOriginY(){
        return ellipseOriginY;
    }
    
    public int getTextOriginX(){
        return textOriginX;
    }
    
    public int getTextOriginY(){
        return textOriginY;
    }
    
    public void setBackgroundColor(Color initBackgroundColor) {
	backgroundColor = initBackgroundColor;
	golWorkspace workspace = (golWorkspace)app.getWorkspaceComponent();
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
    
    
    public void removeSelectedImage(){
        if(selectedImage != null){
            images.remove(selectedImage);
            selectedImage = null;
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

    public void moveSelectedImageToBack() {
        if (selectedImage != null) {
            images.remove(selectedImage);
            if (images.isEmpty()) {
                images.add(selectedImage);
            } else {
                ArrayList<Node> temp = new ArrayList<>();
                temp.add(selectedImage);
                for (Node node : images) {
                    temp.add(node);
                }
                images.clear();
                for (Node node : temp) {
                    images.add(node);
                }
            }
        }
    }

    public void moveSelectedShapeToFront() {
        if (selectedShape != null) {
            shapes.remove(selectedShape);
            shapes.add(selectedShape);
        }
    }

    public void moveSelectedImageToFront(){
        if(selectedImage != null){
            images.remove(selectedImage);
            images.add(selectedImage);
        }
    }
    /**
     * This function clears out the HTML tree and reloads it with the minimal
     * tags, like html, head, and body such that the user can begin editing a
     * page.
     */
    @Override
    public void resetData() {
	setState(SELECTING_SHAPE);
	newShape = null;
	selectedShape = null;

	// INIT THE COLORS
	currentFillColor = Color.web(WHITE_HEX);
	currentOutlineColor = Color.web(BLACK_HEX);
	
	shapes.clear();
	((golWorkspace)app.getWorkspaceComponent()).getCanvas().getChildren().clear();
    }

    public void selectSizedShape() {
	if (selectedShape != null)
	    unhighlightShape(selectedShape);
	selectedShape = newShape;
	highlightShape(selectedShape);
	newShape = null;
	if (state == SIZING_SHAPE) {
	    state = ((Draggable)selectedShape).getStartingState();
	}
    }
    
    public void selectSizedImage(){
        if(selectedImage != null)
            unhighlightImage(selectedImage);
        selectedImage = newImage;
        highlightImage(selectedImage);
        newImage = null;
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
    
    public void startNewRectangle(int x, int y) {
	DraggableRectangle newRectangle = new DraggableRectangle();
	newRectangle.start(x, y);
	newShape = newRectangle;
	initNewShape();
    }

    public void startNewEllipse(int x, int y) {
	DraggableEllipse newEllipse = new DraggableEllipse();
	newEllipse.start(x, y);
	newShape = newEllipse;
	initNewShape();
    }
    
  
    public void initNewImage(){
        if(selectedImage != null){
            unhighlightImage(selectedImage);
            selectedImage = null;
        }
        
        images.add(newImage);
        state = golState.DRAGGING_IMAGE;
    }
    
    public void initNewShape() {
	// DESELECT THE SELECTED SHAPE IF THERE IS ONE
	if (selectedShape != null) {
	    unhighlightShape(selectedShape);
	    selectedShape = null;
	}

	// USE THE CURRENT SETTINGS FOR THIS NEW SHAPE
	golWorkspace workspace = (golWorkspace)app.getWorkspaceComponent();
	((Shape)newShape).setFill(workspace.getFillColorPicker().getValue());
	((Shape)newShape).setStroke(workspace.getOutlineColorPicker().getValue());
	((Shape)newShape).setStrokeWidth(workspace.getOutlineThicknessSlider().getValue());
	
	// ADD THE SHAPE TO THE CANVAS
	//shapes.add(newShape);
	
	// GO INTO SHAPE SIZING MODE
	state = golState.SIZING_SHAPE;
        transaction = new AddShape_Transaction(app ,newShape);
        jTPS.addTransaction(transaction);
    }

    public Node getNewShape() {
	return newShape;
    }

    public ImageView getNewImage(){
        return newImage;
    }
    
    public Node getSelectedShape() {
	return selectedShape;
    }

    public ImageView getSelectedImage(){
        return selectedImage;
    }
    
    public void setSelectedShape(Node initSelectedShape) {
	selectedShape = initSelectedShape;
    }

    public void setSelectedImage(ImageView initImage){
        selectedImage = initImage;
    }
    
    public Node selectTopShape(int x, int y) {
	Node shape = getTopShape(x, y);
	if (shape == selectedShape)
	    return shape;
	
	if (selectedShape != null) {
	    unhighlightShape(selectedShape);
	}
	if (shape != null) {
	    highlightShape(shape);
	    golWorkspace workspace = (golWorkspace)app.getWorkspaceComponent();
	    workspace.loadSelectedShapeSettings(shape);
	}
	selectedShape = shape;
	if (shape != null) {
	    ((Draggable)(Node)shape).start(x, y);
	}
	return shape;
    }

    public ImageView getTopImage(int x, int y){
        for(int i = images.size() - 1; i >= 0; i--){
            ImageView img = (ImageView)images.get(i);
            if(img.contains(x, y)){
                return img;
            }
        }
        return null;
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

    public void addImage(ImageView imageToAdd){
        images.add(imageToAdd);
    }
    
    public void removeShape(Node shapeToRemove) {
	shapes.remove(shapeToRemove);
    }

    public void removeImage(ImageView imageToRemove){
        images.remove(imageToRemove);
    }
    
    public golState getState() {
	return state;
    }

    public void setState(golState initState) {
	state = initState;
    }

    public boolean isInState(golState testState) {
	return state == testState;
    }
}
