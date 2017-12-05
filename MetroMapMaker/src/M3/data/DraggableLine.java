/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package M3.data;

import javafx.scene.shape.Line;

/**
 *
 * @author Augusto
 */
public class DraggableLine extends Line implements Draggable{

    double startX;
    double startY;
    double endX;
    double endY;
    String lineEnd;
    String lineStart;
    
    public DraggableLine(){
        setStartX(100.0);
        setStartY(100.0);
        setEndX(140.0);
        setEndY(100.0);
        setStrokeWidth(5);
        startX = 0.0;
        startY = 0.0;
        endX = 0.0;
        endY = 0.0;
        lineEnd = "";
        
        
    }
    @Override
    public m3State getStartingState() {
        return m3State.SELECTING_SHAPE;
    }

    @Override
    public void start(int x, int y) {
        startX = x;
        startY = y;
    }

    @Override
    public void drag(int x, int y) {
        double diffX = x - startX;
	double diffY = y - startY;
	double newX = getX() + diffX;
	double newY = getY() + diffY;
	setTranslateX(newX);
        setTranslateY(newY);
	startX = x;
	startY = y;
    }

    @Override
    public void undoDrag(int x, int y) {
        System.out.println("Undid drag");
    }

    @Override
    public void size(int x, int y) {
        System.out.println("Width");
    }

    @Override
    public double getX() {
        return startX;
    }

    @Override
    public double getY() {
        return startY;
    }
    
    @Override
    public double getWidth() {
        return getStrokeWidth();
    }

    @Override
    public double getHeight() {
        return getStrokeWidth();
    }

    @Override
    public void setLocationAndSize(double initX, double initY, double initWidth, double initHeight) {
        startXProperty().set(initX);
        startYProperty().set(initY);
        endXProperty().set(endX);
        endYProperty().set(endY);
        
    }

    @Override
    public String getShapeType() {
        return LINE;
    }
    
    public void setLineEnd(String stationName){
        lineEnd = stationName;
    }
    
    public void setLineStart(String stationName){
        lineStart = stationName;
    }
}
