/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gol.data;

import java.util.Stack;

/**
 *
 * @author Augusto
 */
public class UndoRedoStack {
    Stack<String> undoStack = new Stack<String>();
    Stack<String> redoStack = new Stack<String>();
    UndoRedoState helper = new UndoRedoState();
    
    public Stack<String> getUndoStack(){
        return undoStack;
    }
    
    public Stack<String> getRedoStack(){
        return redoStack;
    }
    
    public void inverse(){
        undoStack.push(helper.getAddPictureString());
    }
}
