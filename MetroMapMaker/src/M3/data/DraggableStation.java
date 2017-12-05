/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package M3.data;

import java.util.ArrayList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author Augusto
 */
public class DraggableStation extends Circle implements Draggable {

    double startCenterX;
    double startCenterY;
    boolean connectTwoLines;
    boolean connectThreeLines;
    String stationName;
    public ArrayList<StationEnds> stationEnds = new ArrayList<StationEnds>();
    public String leftElementType;
    public String rightElementType;
    boolean topRight;
    boolean topLeft;
    boolean bottomRight;
    boolean bottomLeft;
    boolean visited;
    boolean straightened;
    
    public DraggableStation(){
        setCenterX(50.0);
	setCenterY(50.0);
	setRadius(10.0);
	setOpacity(1.0);
        setFill(Color.TRANSPARENT);
        setStroke(Color.BLACK);
	startCenterX = 50.0;
	startCenterY = 50.0;
        connectTwoLines = false;
        connectThreeLines = false;
        stationName = "";
        leftElementType = "";
        rightElementType = "";
        topRight = false;
        topLeft = false;
        bottomRight = false;
        bottomLeft = false;
        visited = false;
        straightened = false;
    }
    
    @Override
    public m3State getStartingState() {
        return m3State.SELECTING_SHAPE;
    }

    @Override
    public void start(int x, int y) {
        startCenterX = x;
	startCenterY = y;
    }

    @Override
    public void drag(int x, int y) {
        double diffX = x - startCenterX;
	double diffY = y - startCenterY;
	double newX = getCenterX() + diffX;
	double newY = getCenterY() + diffY;
	setCenterX(newX);
	setCenterY(newY);
	startCenterX = x;
	startCenterY = y;
    }

    @Override
    public void undoDrag(int x, int y) {
        System.out.println("This is undo function");
    }

    @Override
    public void size(int x, int y) {
        double width = x - startCenterX;
	double height = y - startCenterY;
	double centerX = startCenterX + (width / 2);
	double centerY = startCenterY + (height / 2);
	setCenterX(centerX);
	setCenterY(centerY);
	setRadius(width / 2);
    }
    
    @Override
    public double getX() {
	return getCenterX() - getRadius();
    }

    @Override
    public double getY() {
	return getCenterY() - getRadius();
    }

    @Override
    public double getWidth() {
	return getRadius() * 2;
    }

    @Override
    public double getHeight() {
	return getRadius() * 2;
    }

    @Override
    public void setLocationAndSize(double initX, double initY, double initWidth, double initHeight) {
        setCenterX(initX + (initWidth/2));
	setCenterY(initY + (initHeight/2));
	setRadius(initWidth/2);
    }

    @Override
    public String getShapeType() {
        return STATION;
    }    
    
    public void setTwoLines(boolean choice){
        connectTwoLines = choice;
    }
    public void setThreeLines(boolean choice){
        connectThreeLines = choice;
    }
    
    public String getStationName(){
        return stationName;
    }
    
    public void setStationName(String name){
        stationName = name;
    }
    public ArrayList<StationEnds> getStationEnds(){
        return stationEnds;
    }
   
    public void setLeftElementType(String type){
        leftElementType = type;
    }
    public String getLeftElementType(){
        return leftElementType;
    }
    public void setRightElementType(String type){
        rightElementType = type;
    }
    public String getRightElementType(){
        return rightElementType;
    }
    public boolean getTopRight(){
        return topRight;
    }
    public void setTopRight(boolean result){
        topRight = result;
    }
    public boolean getTopLeft(){
        return topLeft;
    }
    public void setTopLeft(boolean result){
        topLeft = result;
    }
    public boolean getBottomRight(){
        return bottomRight;
    }
    public void setBottomRight(boolean result){
        bottomRight = result;
    }
    public boolean getBottomLeft(){
        return bottomLeft;
    }
    public void setBottomLeft(boolean result){
        bottomLeft = result;
    }
    public boolean getVisited(){
        return visited;
    }
    public void setVisited(boolean result){
        visited = result;
    }
    public boolean getStraightened(){
        return straightened;
    }
    public void setStraightened(boolean result){
        straightened = result;
    }
}
