package gol.file;

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
import gol.data.golData;
import gol.data.DraggableEllipse;
import gol.data.DraggableRectangle;
import gol.data.Draggable;
import static gol.data.Draggable.RECTANGLE;

/**
 * This class serves as the file management component for this application,
 * providing all I/O services.
 *
 * @author Richard McKenna
 * @author ?
 * @version 1.0
 */
public class golFiles implements AppFileComponent {
    // FOR JSON LOADING
    static final String JSON_BG_COLOR = "background_color";
    static final String JSON_RED = "red";
    static final String JSON_GREEN = "green";
    static final String JSON_BLUE = "blue";
    static final String JSON_ALPHA = "alpha";
    static final String JSON_SHAPES = "shapes";
    static final String JSON_SHAPE = "shape";
    static final String JSON_TYPE = "type";
    static final String JSON_X = "x";
    static final String JSON_Y = "y";
    static final String JSON_WIDTH = "width";
    static final String JSON_HEIGHT = "height";
    static final String JSON_FILL_COLOR = "fill_color";
    static final String JSON_OUTLINE_COLOR = "outline_color";
    static final String JSON_OUTLINE_THICKNESS = "outline_thickness";
    
    static final String DEFAULT_DOCTYPE_DECLARATION = "<!doctype html>\n";
    static final String DEFAULT_ATTRIBUTE_VALUE = "";
    
 
    /**
     * This method is for saving user work, which in the case of this
     * application means the data that together draws the logo.
     * 
     * @param data The data management component for this application.
     * 
     * @param filePath Path (including file name/extension) to where
     * to save the data to.
     * 
     * @throws IOException Thrown should there be an error writing 
     * out data to the file.
     */
    @Override
    public void saveData(AppDataComponent data, String filePath) throws IOException {
	// GET THE DATA
	golData dataManager = (golData)data;
	
	// FIRST THE BACKGROUND COLOR
	Color bgColor = dataManager.getBackgroundColor();
	JsonObject bgColorJson = makeJsonColorObject(bgColor);

	// NOW BUILD THE JSON OBJCTS TO SAVE
	JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
	ObservableList<Node> shapes = dataManager.getShapes();
	for (Node node : shapes) {
	    Shape shape = (Shape)node;
	    Draggable draggableShape = ((Draggable)shape);
	    String type = draggableShape.getShapeType();
	    double x = draggableShape.getX();
	    double y = draggableShape.getY();
	    double width = draggableShape.getWidth();
	    double height = draggableShape.getHeight();
	    JsonObject fillColorJson = makeJsonColorObject((Color)shape.getFill());
	    JsonObject outlineColorJson = makeJsonColorObject((Color)shape.getStroke());
	    double outlineThickness = shape.getStrokeWidth();
	    
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
	JsonArray shapesArray = arrayBuilder.build();
	
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
	golData dataManager = (golData)data;
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
	    Shape shape = loadShape(jsonShape);
	    dataManager.addShape(shape);
	}
    }
    
    private double getDataAsDouble(JsonObject json, String dataName) {
	JsonValue value = json.get(dataName);
	JsonNumber number = (JsonNumber)value;
	return number.bigDecimalValue().doubleValue();	
    }
    
    private Shape loadShape(JsonObject jsonShape) {
	// FIRST BUILD THE PROPER SHAPE TYPE
	String type = jsonShape.getString(JSON_TYPE);
	Shape shape;
	if (type.equals(RECTANGLE)) {
	    shape = new DraggableRectangle();
	}
	else {
	    shape = new DraggableEllipse();
	}
	
	// THEN LOAD ITS FILL AND OUTLINE PROPERTIES
	Color fillColor = loadColor(jsonShape, JSON_FILL_COLOR);
	Color outlineColor = loadColor(jsonShape, JSON_OUTLINE_COLOR);
	double outlineThickness = getDataAsDouble(jsonShape, JSON_OUTLINE_THICKNESS);
	shape.setFill(fillColor);
	shape.setStroke(outlineColor);
	shape.setStrokeWidth(outlineThickness);
	
	// AND THEN ITS DRAGGABLE PROPERTIES
	double x = getDataAsDouble(jsonShape, JSON_X);
	double y = getDataAsDouble(jsonShape, JSON_Y);
	double width = getDataAsDouble(jsonShape, JSON_WIDTH);
	double height = getDataAsDouble(jsonShape, JSON_HEIGHT);
	Draggable draggableShape = (Draggable)shape;
	draggableShape.setLocationAndSize(x, y, width, height);
	
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
