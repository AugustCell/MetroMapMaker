package M3.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import djf.components.AppDataComponent;
import djf.components.AppFileComponent;
import M3.data.m3Data;
import M3.data.DraggableImage;
import M3.data.Draggable;
import static M3.data.Draggable.LINE;
import static M3.data.Draggable.RECTANGLE;
import static M3.data.Draggable.STATION;
import static M3.data.Draggable.TEXT;
import M3.data.DraggableStation;
import M3.data.DraggableText;
import M3.data.LineGroups;
import M3.data.StationEnds;
import M3.data.StationTracker;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;

/**
 * This class serves as the file management component for this application,
 * providing all I/O services.
 *
 * @author Richard McKenna
 * @author ?
 * @version 1.0
 */
public class m3Files implements AppFileComponent {
    // FOR JSON LOADING

    static final String JSON_BG_COLOR = "background_color";
    static final String JSON_NAME = "name";
    static final String JSON_LINES_DECLARATION = "lines";
    static final String JSON_LINE_NAME = "line_names";
    static final String JSON_CIRCULAR_STATION = "circular";
    static final String JSON_STATION_NAME = "station_names";
    public final String JSON_LINE_COLOR = "color";
    public final String JSON_STATIONS_DECLARATION = "stations";
    static final String JSON_RED = "red";
    static final String JSON_GREEN = "green";
    static final String JSON_BLUE = "blue";
    static final String JSON_ALPHA = "alpha";
    static final String JSON_SHAPES = "shapes";
    static final String JSON_SHAPE = "shape";
    static final String JSON_TYPE = "type";
    static final String JSON_X = "x";
    static final String JSON_Y = "y";
    static final String JSON_LINE_WIDTH = "line_width";
    static final String JSON_START_X = "start x";
    public final String JSON_START_Y = "start y";
    public final String JSON_END_X = "end x";
    public final String JSON_END_Y = "end y";
    static final String JSON_WIDTH = "width";
    static final String JSON_HEIGHT = "height";
    static final String JSON_RADIUS = "radius";
    static final String JSON_LEFT_END = "left_end";
    static final String JSON_RIGHT_END = "right_end";
    static final String JSON_LEFT_END_TYPE = "left_end_type";
    static final String JSON_RIGHT_END_TYPE = "right_end_type";
    static final String JSON_FIRST_LINE_BOOLEAN = "First _line_boolean";
    static final String JSON_LAST_LINE_BOOLEAN = "Last_line_boolean";
    static final String JSON_FILL_COLOR = "fill_color";
    static final String JSON_OUTLINE_COLOR = "outline_color";
    static final String JSON_OUTLINE_THICKNESS = "outline_thickness";
    static final String JSON_IMAGE_PATTERN = "Image_pattern";
    static final String JSON_TEXT_STRING = "Text";
    static final String JSON_TEXT_SIZE = "Size";
    static final String JSON_TEXT_FONT = "Font";

    static final String DEFAULT_DOCTYPE_DECLARATION = "<!doctype html>\n";
    static final String DEFAULT_ATTRIBUTE_VALUE = "";

    /**
     * This method is for saving user work, which in the case of this
     * application means the data that together draws the logo.
     *
     * @param data The data management component for this application.
     *
     * @param filePath Path (including file name/extension) to where to save the
     * data to.
     *
     * @throws IOException Thrown should there be an error writing out data to
     * the file.
     */
    @Override
    public void saveData(AppDataComponent data, String filePath) throws IOException {
        // GET THE DATA
        m3Data dataManager = (m3Data) data;

        // FIRST THE BACKGROUND COLOR
        Color bgColor = dataManager.getBackgroundColor();
        JsonObject bgColorJson = null;
        if (bgColor != null) {
            bgColorJson = makeJsonColorObject(bgColor);
        }
        else{
            bgColorJson = makeJsonWhiteObject();
        }
        // NOW BUILD THE JSON OBJCTS TO SAVE
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        ObservableList<Node> shapes = dataManager.getShapes();
        JsonArray shapesArray = null;
        for (Node node : shapes) {
            Node shape = (Node) node;
            
            if (shape instanceof DraggableText) {
                DraggableText draggableShape = (DraggableText) shape;
                JsonObject fillColorJson = null;
                System.out.println(draggableShape.getClass().toString());
                String type = draggableShape.getShapeType();
                double x = draggableShape.getX();
                double y = draggableShape.getY();
                double width = draggableShape.getWidth();
                double height = draggableShape.getHeight();
                
                String text = draggableShape.getText();
                if (draggableShape.getFill() != null) {
                    fillColorJson = makeJsonColorObject((Color) draggableShape.getFill());
                }
                JsonObject shapeJson = Json.createObjectBuilder()
                        .add(JSON_TYPE, type)
                        .add(JSON_X, x)
                        .add(JSON_Y, y)
                        .add(JSON_TEXT_STRING, text)
                        .add(JSON_FILL_COLOR, fillColorJson)
                        .add(JSON_WIDTH, width)
                        .add(JSON_HEIGHT, height).build();
                arrayBuilder.add(shapeJson);
            } 
            
            else if (shape instanceof DraggableStation) {
                DraggableStation draggableShape = (DraggableStation) shape;
                JsonObject fillColorJson = null;
                String type = draggableShape.getShapeType();
                double x = draggableShape.getCenterX();
                double y = draggableShape.getCenterY();
                double radius = draggableShape.getRadius();
                String name = draggableShape.getStationName();
                ArrayList<String> leftEnd = new ArrayList<String>();
                ArrayList<String> rightEnd = new ArrayList<String>();
                ArrayList<String> lineNames = new ArrayList<String>();
                ArrayList<StationEnds> stationEnds = new ArrayList<StationEnds>();
                //IF ADDED SEQUENTIALLY THEN EACH NAME WILL HAVE ITS RESPECTIVE LEFT ADN RIGHT END
                if(!draggableShape.getStationEnds().isEmpty()){
                    stationEnds = draggableShape.getStationEnds();
                    for(int i = 0; i < stationEnds.size(); i++){
                        leftEnd.add(stationEnds.get(i).getLeftEnd());
                        rightEnd.add(stationEnds.get(i).getRightEnd());
                        lineNames.add(stationEnds.get(i).getLineName());
                    }
                }
                if(draggableShape.getFill() != null){
                    fillColorJson = makeJsonColorObject((Color) draggableShape.getFill());
                }
                else{
                    fillColorJson = makeJsonWhiteObject();
                }
                
                JsonArrayBuilder leftEndArray = Json.createArrayBuilder();
                JsonArrayBuilder rightEndArray = Json.createArrayBuilder();
                JsonArrayBuilder lineNameArray = Json.createArrayBuilder();
                if (!draggableShape.getStationEnds().isEmpty()) {
                    for (int p = 0; p < leftEnd.size(); p++) {
                        leftEndArray.add(leftEnd.get(p));
                        rightEndArray.add(rightEnd.get(p));
                        lineNameArray.add(lineNames.get(p));
                    }
                }

                JsonObject shapeJson = Json.createObjectBuilder()
                        .add(JSON_TYPE, type)
                        .add(JSON_X, x)
                        .add(JSON_Y, y)
                        .add(JSON_RADIUS, radius)
                        .add(JSON_NAME, name).build();
                arrayBuilder.add(shapeJson);
                
                if(leftEndArray != null){
                    JsonObject arr = Json.createObjectBuilder()
                            .add(JSON_LEFT_END, leftEndArray)
                            .add(JSON_RIGHT_END, rightEndArray)
                            .add(JSON_LINE_NAME, lineNameArray).build();
                    arrayBuilder.add(arr);
                }
                
            }
            
            else if (shape instanceof LineGroups) {
                LineGroups draggableShape = (LineGroups) shape;
                JsonObject fillColorJson = null;
                String name = draggableShape.getLineName();
                ArrayList<String> stationNames = new ArrayList<String>();
                ArrayList<StationTracker> tracker = dataManager.getStationTracker();
                for (int i = 0; i < tracker.size(); i++){
                    if(tracker.get(i).getName().equals(name)){
                        stationNames = tracker.get(i).getStationNames();
                        break;
                    }
                }
                String type = draggableShape.getShapeType();
                double startX = draggableShape.getStartX();
                double startY = draggableShape.getStartY();
                double endX = draggableShape.getEndX();
                double endY = draggableShape.getEndY();
                String leftEnd = draggableShape.getLeftEnd();
                String rightEnd = draggableShape.getRightEnd();
                boolean firstLine = draggableShape.getFirstLine();
                boolean lastLine = draggableShape.getLastLine();
                double strokeWidth = draggableShape.getStrokeWidth();
                String leftEndType = draggableShape.getLeftElementType();
                String rightEndType = draggableShape.getRightElementType();
                System.out.println("left element type :" + leftEndType);
                System.out.println("right element type:" + rightEndType);
           
                        
                
                if (draggableShape.getFill() != null) {
                    fillColorJson = makeJsonColorObject((Color) draggableShape.getStroke());
                }
                else{
                    fillColorJson = makeJsonBlackObject();
                }
           
                JsonArrayBuilder stationArray = null;
                if (!stationNames.isEmpty()) {
                    stationArray = Json.createArrayBuilder();
                    for (int p = 0; p < stationNames.size(); p++) {
                        stationArray.add(stationNames.get(p));
                    }
                }
                JsonObject shapeJson = Json.createObjectBuilder()
                        .add(JSON_TYPE, type)
                        .add(JSON_NAME, name)
                        .add(JSON_START_X, startX)
                        .add(JSON_START_Y, startY)
                        .add(JSON_END_X, endX)
                        .add(JSON_END_Y, endY)
                        .add(JSON_LEFT_END, leftEnd)
                        .add(JSON_RIGHT_END, rightEnd)
                        .add(JSON_FIRST_LINE_BOOLEAN, firstLine)
                        .add(JSON_LAST_LINE_BOOLEAN, lastLine)
                        .add(JSON_LEFT_END_TYPE, leftEndType)
                        .add(JSON_RIGHT_END_TYPE, rightEndType)
                        .add(JSON_LINE_WIDTH, strokeWidth)
                        .add(JSON_FILL_COLOR, fillColorJson)
                        .build();
                
                arrayBuilder.add(shapeJson);
                
                if(stationArray != null){
                    JsonObject arr = Json.createObjectBuilder()
                            .add(JSON_STATION_NAME, stationArray).build();
                            arrayBuilder.add(arr);
                }
            }
            
            else if (shape instanceof DraggableImage) {
                DraggableImage draggableShape = ((DraggableImage) shape);
                JsonObject fillColorJson = null;
                String type = draggableShape.getShapeType();
                double x = draggableShape.getX();
                double y = draggableShape.getY();
                double width = draggableShape.getWidth();
                double height = draggableShape.getHeight();
                fillColorJson = makeJsonImageObject(draggableShape.getPathString());
                JsonObject outlineColorJson = makeJsonColorObject((Color) ((Shape) shape).getStroke());
                double outlineThickness = ((Shape) shape).getStrokeWidth();

                JsonObject shapeJson = Json.createObjectBuilder()
                        .add(JSON_TYPE, type)
                        .add(JSON_X, x)
                        .add(JSON_Y, y)
                        .add(JSON_WIDTH, width)
                        .add(JSON_HEIGHT, height)
                        .add(JSON_FILL_COLOR, fillColorJson)
                        .add(JSON_OUTLINE_COLOR, outlineColorJson)
                        .add(JSON_OUTLINE_THICKNESS, outlineThickness).build();
                arrayBuilder.add(shapeJson);
            }

        }
        shapesArray = arrayBuilder.build();

        // THEN PUT IT ALL TOGETHER IN A JsonObject
        JsonObject dataManagerJSO = Json.createObjectBuilder()
                .add(JSON_BG_COLOR, bgColorJson)
                .add(JSON_SHAPES, shapesArray)
                .build();

        // AND NOW OUTPUT IT TO A JSON FILE WITH PRETTY PRINTING
        Map<String, Object> properties = new HashMap<>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
        StringWriter sw = new StringWriter();
        JsonWriter jsonWriter = writerFactory.createWriter(sw);
        jsonWriter.writeObject(dataManagerJSO);
        jsonWriter.close();

        // INIT THE WRITER
        OutputStream os = new FileOutputStream(filePath);
        JsonWriter jsonFileWriter = Json.createWriter(os);
        jsonFileWriter.writeObject(dataManagerJSO);
        String prettyPrinted = sw.toString();
        PrintWriter pw = new PrintWriter(filePath);
        pw.write(prettyPrinted);
        pw.close();
    }
    
    private JsonObject makeJsonColorObject(Color color) {
	JsonObject colorJson = Json.createObjectBuilder()
		.add(JSON_RED, color.getRed())
		.add(JSON_GREEN, color.getGreen())
		.add(JSON_BLUE, color.getBlue())
		.add(JSON_ALPHA, color.getOpacity()).build();
	return colorJson;
    }
    
    private JsonObject makeJsonWhiteObject(){
        JsonObject colorJson = Json.createObjectBuilder()
                .add(JSON_RED, 1.0)
                .add(JSON_GREEN, 1.0)
                .add(JSON_BLUE, 1.0)
                .add(JSON_ALPHA, 1.0).build();
        return colorJson;
    }
    
    private JsonObject makeJsonBlackObject(){
        JsonObject colorJson = Json.createObjectBuilder()
                .add(JSON_RED, 0.0)
                .add(JSON_GREEN, 0.0)
                .add(JSON_BLUE, 0.0)
                .add(JSON_ALPHA, 1.0).build();
        return colorJson;
    }
    
    private JsonObject makeJsonImageObject(String pattern){
        JsonObject colorJson = Json.createObjectBuilder()
                .add(JSON_IMAGE_PATTERN, pattern).build();
        return colorJson;
    }
     
    public void loadDataFile(AppDataComponent data, String filePath) throws IOException{
        // CLEAR THE OLD DATA OUT
        m3Data dataManager = (m3Data) data;
        dataManager.resetData();

        // LOAD THE JSON FILE WITH ALL THE DATA
        JsonObject json = loadJSONFile(filePath);

        // LOAD THE BACKGROUND COLOR
        Color bgColor = loadColor(json, JSON_BG_COLOR);
        dataManager.setBackgroundColor(bgColor);

        // AND NOW LOAD ALL THE SHAPES
        JsonArray jsonShapeArray = json.getJsonArray(JSON_SHAPES);
        for (int i = 0; i < jsonShapeArray.size(); i++) {
            JsonObject jsonShape = jsonShapeArray.getJsonObject(i);
            Node shape;

            System.out.println(jsonShape.get(JSON_FILL_COLOR));
            shape = loadShape(jsonShape);

            dataManager.addShape(shape);
        }
    }
    /**
     * This method loads data from a JSON formatted file into the data 
     * management component and then forces the updating of the workspace
     * such that the user may edit the data.
     * 
     * @param data Data management component where we'll load the file into.
     * 
     * @param filePath Path (including file name/extension) to where
     * to load the data from.
     * 
     * @throws IOException Thrown should there be an error reading
     * in data from the file.
     */
    @Override
    public void loadData(AppDataComponent data, String filePath) throws IOException {
        // CLEAR THE OLD DATA OUT
        m3Data dataManager = (m3Data) data;
        dataManager.resetData();

        if (new File(filePath).length() != 0) {

            // LOAD THE JSON FILE WITH ALL THE DATA
            JsonObject json = loadJSONFile(filePath);

            // LOAD THE BACKGROUND COLOR
            Color bgColor = loadColor(json, JSON_BG_COLOR);
            dataManager.setBackgroundColor(bgColor);

            // AND NOW LOAD ALL THE SHAPES
            JsonArray jsonShapeArray = json.getJsonArray(JSON_SHAPES);
            for (int i = 0; i < jsonShapeArray.size(); i++) {
                JsonObject jsonShape = jsonShapeArray.getJsonObject(i);
                Node shape;

                System.out.println(jsonShape.get(JSON_FILL_COLOR));
                shape = loadShape(jsonShape);

                dataManager.addShape(shape);
            }
        }
    }

    private double getDataAsDouble(JsonObject json, String dataName) {
        JsonValue value = json.get(dataName);
        JsonNumber number = (JsonNumber) value;
        return number.bigDecimalValue().doubleValue();
    }

    private Node loadShape(JsonObject jsonShape) throws IOException {
        // FIRST BUILD THE PROPER SHAPE TYPE
        String type = jsonShape.getString(JSON_TYPE);
        Node shape = null ;
        if (type.equals(RECTANGLE)) {
            shape = new DraggableImage();
        } else if (type.equals(TEXT)) {
            shape = new DraggableText();
        } else if(type.equals(LINE)){
            shape = new LineGroups();
        } else if(type.equals(STATION)){
            shape = new DraggableStation();
        }
      // THEN LOAD ITS FILL AND OUTLINE PROPERTIES
        if (type.equals(RECTANGLE)) {
            if (jsonShape.get(JSON_FILL_COLOR).toString().contains("Image pattern")) {
                String prefix = "Image pattern:     ";
                String noPrefix = jsonShape.get(JSON_FILL_COLOR).toString().substring(jsonShape.get(JSON_FILL_COLOR).toString().indexOf(prefix) + prefix.length());
                noPrefix = noPrefix.substring(0, noPrefix.length() - 2);
                String finalPath = "file:///" + noPrefix;
                URL url = new URL(finalPath);
                Color outlineColor = loadColor(jsonShape, JSON_OUTLINE_COLOR);
                double outlineThickness = getDataAsDouble(jsonShape, JSON_OUTLINE_THICKNESS);
                ((Shape) shape).setFill(new ImagePattern(new Image(finalPath)));
                ((Shape) shape).setStroke(outlineColor);
                ((Shape) shape).setStrokeWidth(outlineThickness);
                double x = getDataAsDouble(jsonShape, JSON_X);
                double y = getDataAsDouble(jsonShape, JSON_Y);
                double width = getDataAsDouble(jsonShape, JSON_WIDTH);
                double height = getDataAsDouble(jsonShape, JSON_HEIGHT);
                Draggable draggableShape = (Draggable) shape;
                draggableShape.setLocationAndSize(x, y, width, height);
            } else {
                Color fillColor = loadColor(jsonShape, JSON_FILL_COLOR);
                Color outlineColor = loadColor(jsonShape, JSON_OUTLINE_COLOR);
                double outlineThickness = getDataAsDouble(jsonShape, JSON_OUTLINE_THICKNESS);
                ((Shape) shape).setFill(fillColor);
                ((Shape) shape).setStroke(outlineColor);
                ((Shape) shape).setStrokeWidth(outlineThickness);
                double x = getDataAsDouble(jsonShape, JSON_X);
                double y = getDataAsDouble(jsonShape, JSON_Y);
                double width = getDataAsDouble(jsonShape, JSON_WIDTH);
                double height = getDataAsDouble(jsonShape, JSON_HEIGHT);
                Draggable draggableShape = (Draggable) shape;
                draggableShape.setLocationAndSize(x, y, width, height);
            }
        } 
        
        else if (type.equals(TEXT)) {
            String text = jsonShape.get(JSON_TEXT_STRING).toString();
            text = text.substring(1, text.length() - 1);
            ((DraggableText) shape).setText(text);
            double x = getDataAsDouble(jsonShape, JSON_X);
            double y = getDataAsDouble(jsonShape, JSON_Y);
            double width = getDataAsDouble(jsonShape, JSON_WIDTH);
            double height = getDataAsDouble(jsonShape, JSON_HEIGHT);
            Draggable draggableShape = (Draggable) shape;
            draggableShape.setLocationAndSize(x, y, width, height);
        } 
        
        else if (type.equals(LINE)){
            Color fillColor = loadColor(jsonShape, JSON_FILL_COLOR);
            double strokeWidth = getDataAsDouble(jsonShape, JSON_LINE_WIDTH);
            ((Shape) shape).setFill(fillColor);
            ((Shape) shape).setStrokeWidth(strokeWidth);
            double startX = getDataAsDouble(jsonShape, JSON_START_X);
            double startY = getDataAsDouble(jsonShape, JSON_START_Y);
            double endX = getDataAsDouble(jsonShape, JSON_END_X);
            double endY = getDataAsDouble(jsonShape, JSON_END_Y);
            LineGroups draggableShape = (LineGroups) shape;
    //        draggableShape.setStartX(startX);
    //        draggableShape.setStartY(startY);
    //        draggableShape.setEndX(endX);
    //        draggableShape.setEndY(endY);

        }
        
        else if(type.equals(STATION)){
            double x = getDataAsDouble(jsonShape, JSON_X);
            double y = getDataAsDouble(jsonShape, JSON_Y);
            double width = getDataAsDouble(jsonShape, JSON_WIDTH);
            double height = getDataAsDouble(jsonShape, JSON_HEIGHT);
            Draggable draggableShape = (Draggable) shape;
            draggableShape.setLocationAndSize(x, y, width, height);
        }
        
        else {
            Color fillColor = loadColor(jsonShape, JSON_FILL_COLOR);
            Color outlineColor = loadColor(jsonShape, JSON_OUTLINE_COLOR);
            double outlineThickness = getDataAsDouble(jsonShape, JSON_OUTLINE_THICKNESS);
            ((Shape) shape).setFill(fillColor);
            ((Shape) shape).setStroke(outlineColor);
            ((Shape) shape).setStrokeWidth(outlineThickness);
            double x = getDataAsDouble(jsonShape, JSON_X);
            double y = getDataAsDouble(jsonShape, JSON_Y);
            double width = getDataAsDouble(jsonShape, JSON_WIDTH);
            double height = getDataAsDouble(jsonShape, JSON_HEIGHT);
            Draggable draggableShape = (Draggable) shape;
            draggableShape.setLocationAndSize(x, y, width, height);
        }
	
	// ALL DONE, RETURN IT
	return shape;
    }
    
    private Color loadColor(JsonObject json, String colorToGet) {
	JsonObject jsonColor = json.getJsonObject(colorToGet);
	double red = getDataAsDouble(jsonColor, JSON_RED);
	double green = getDataAsDouble(jsonColor, JSON_GREEN);
	double blue = getDataAsDouble(jsonColor, JSON_BLUE);
	double alpha = getDataAsDouble(jsonColor, JSON_ALPHA);
	Color loadedColor = new Color(red, green, blue, alpha);
	return loadedColor;
    }
    
    
    // HELPER METHOD FOR LOADING DATA FROM A JSON FORMAT
    private JsonObject loadJSONFile(String jsonFilePath) throws IOException {
	InputStream is = new FileInputStream(jsonFilePath);
	JsonReader jsonReader = Json.createReader(is);
	JsonObject json = jsonReader.readObject();
	jsonReader.close();
	is.close();
	return json;
    }
    
    /**
     * This method is provided to satisfy the compiler, but it
     * is not used by this application.
     */
    @Override
    public void exportData(AppDataComponent data, String filePath) throws IOException {
        // WE ARE NOT USING THIS, THOUGH PERHAPS WE COULD FOR EXPORTING
        // IMAGES TO VARIOUS FORMATS, SOMETHING OUT OF THE SCOPE
        // OF THIS ASSIGNMENT
    }
    
    /**
     * This method is provided to satisfy the compiler, but it
     * is not used by this application.
     */
    @Override
    public void importData(AppDataComponent data, String filePath) throws IOException {
	// AGAIN, WE ARE NOT USING THIS IN THIS ASSIGNMENT
    }
}
