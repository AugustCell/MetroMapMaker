/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gol.data;

import javafx.scene.text.Text;

/**
 *
 * @author Augusto
 */
public class DraggableText extends Text implements Draggable{
    double startX;
    double startY;
    double width;
    double height;
    
    public DraggableText() {
	setX(0.0);
	setY(0.0);
	setWidth(0.0);
	setHeight(0.0);
	setOpacity(1.0);
	startX = 0.0;
	startY = 0.0;
    }
    
    @Override
    public golState getStartingState() {
	return golState.STARTING_RECTANGLE;
    }
    
    @Override
    public void start(int x, int y) {
	startX = x;
	startY = y;
	setX(x);
	setY(y);
    }
    
    @Override
    public void drag(int x, int y) {
	double diffX = x - (getX() + (getWidth()/2));
	double diffY = y - (getY() + (getHeight()/2));
	double newX = getX() + diffX;
	double newY = getY() + diffY;
	xProperty().set(newX);
	yProperty().set(newY);
	startX = x;
	startY = y;
    }
    
    @Override
    public void undoDrag(int x, int y) {
        xProperty().set(x);
        yProperty().set(y);
        startX = x;
        startY = y;
    }

    public String cT(double x, double y) {
	return "(x,y): (" + x + "," + y + ")";
    }
    
    @Override
    public void size(int x, int y) {
	double width = x - getX();
	this.width = width;
	double height = y - getY();
	this.height = height;	
    }
    
    @Override
    public void setLocationAndSize(double initX, double initY, double initWidth, double initHeight) {
	xProperty().set(initX);
	yProperty().set(initY);
	this.width = initWidth;
        this.height = initHeight;
    }
    
    @Override
    public String getShapeType() {
	return TEXT;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    private void setWidth(double d) {
        width = d;
    }

    private void setHeight(double d) {
        height = d;
    }
}