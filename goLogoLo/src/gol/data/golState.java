package gol.data;

/**
 * This enum has the various possible states of the logo maker app
 * during the editing process which helps us determine which controls
 * are usable or not and what specific user actions should affect.
 * 
 * @author Richard McKenna
 * @author ?
 * @version 1.0
 */
public enum golState {
    SELECTING_SHAPE,
    DRAGGING_SHAPE,
    STARTING_RECTANGLE,
    STARTING_ELLIPSE,
    SIZING_SHAPE,
    DRAGGING_NOTHING,
    SIZING_NOTHING
}
