/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gol.data;

/**
 *
 * @author Augusto
 */
public class UndoRedoState {
    private String ADD_PICTURE = "Picture added";
    private String CREATE_RECTANGLE = "Rectangle created";
    private String CREATE_ELIPSE = "Elipse Created";
    private String CHANGE_FILL = "Fill color changed";
    private String CHANGE_OUTLINE_COLOR = "Outline color changed";
    private String ADD_TEXT = "Text added";

    /**
     * @return the ADD_PICTURE
     */
    public String getAddPictureString() {
        return ADD_PICTURE;
    }

    /**
     * @return the CREATE_RECTANGLE
     */
    public String getCreateRectangleString() {
        return CREATE_RECTANGLE;
    }

    /**
     * @return the CREATE_ELIPSE
     */
    public String getCreateElipseString() {
        return CREATE_ELIPSE;
    }

    /**
     * @return the CHANGE_FILL
     */
    public String getChangeFillString() {
        return CHANGE_FILL;
    }

    /**
     * @return the CHANGE_OUTLINE_COLOR
     */
    public String getChangeOutlineColorString() {
        return CHANGE_OUTLINE_COLOR;
    }

    /**
     * @return the ADD_TEXT
     */
    public String getAddedTextString() {
        return ADD_TEXT;
    }
}
