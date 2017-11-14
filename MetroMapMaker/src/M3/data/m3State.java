package M3.data;

/**
 * This enum has the various possible states of the logo maker app
 * during the editing process which helps us determine which controls
 * are usable or not and what specific user actions should affect.
 * 
 * @author Richard McKenna
 * @author ?
 * @version 1.0
 */
public enum m3State {
    SELECTING_SHAPE,
    DRAGGING_SHAPE,
    DRAGGING_NOTHING,
    SELECT_LINE_END,
    
    DRAGGING_LINE_END,
    DRAGGING_TEXT

}
