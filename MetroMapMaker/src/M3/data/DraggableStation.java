/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package M3.data;

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
    public String leftEnd;
    public String rightEnd;
    public String leftElementType;
    public String rightElementType;
    
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
        leftEnd = "";
        rightEnd = "";
        leftElementType = "";
        rightElementType = "";
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
    public String getLeftEnd(){
        return leftEnd;
    }
    public void setLeftEnd(String name){
        leftEnd = name;
    }
    public String getRightEnd(){
        return rightEnd;
    }
    public void setRightend(String name){
        rightEnd = name;
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
}
