package djf;

import djf.ui.*;
import djf.components.*;
import djf.controller.AppFileController;
import javafx.application.Application;
import javafx.stage.Stage;
import properties_manager.PropertiesManager;
import static djf.settings.AppPropertyType.*;
import djf.settings.AppStartupConstants;
import static djf.settings.AppStartupConstants.*;
import static djf.ui.AppGUI.CLASS_BORDERED_PANE;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import static javafx.scene.paint.Color.color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import properties_manager.InvalidXMLFileFormatException;

/**
 * This is the framework's JavaFX application. It provides the start method that
 * begins the program initialization, which delegates component initialization
 * to the application-specific child class' hook function.
 *
 * @author Richard McKenna
 * @version 1.0
 */
public abstract class AppTemplate extends Application {

    public String linkFile = "";
    // THIS IS THE APP'S FULL JavaFX GUI. NOTE THAT ALL APPS WOULD
    // SHARE A COMMON UI EXCEPT FOR THE CUSTOM WORKSPACE
    protected AppGUI gui;

    // THIS CLASS USES A COMPONENT ARCHITECTURE DESIGN PATTERN, MEANING IT
    // HAS OBJECTS THAT CAN BE SWAPPED OUT FOR OTHER COMPONENTS
    // THIS APP HAS 4 COMPONENTS
    // THE COMPONENT FOR MANAGING CUSTOM APP DATA
    protected AppDataComponent dataComponent;

    // THE COMPONENT FOR MANAGING CUSTOM FILE I/O
    protected AppFileComponent fileComponent;

    // THE COMPONENT FOR THE GUI WORKSPACE
    protected AppWorkspaceComponent workspaceComponent;

    // The startup constants helper
    protected AppStartupConstants xmlConstants = new AppStartupConstants();
    
    public File workingFile;
    
    public String workingFileString;


    // THIS METHOD MUST BE OVERRIDDEN WHERE THE CUSTOM BUILDER OBJECT
    // WILL PROVIDE THE CUSTOM APP COMPONENTS
    /**
     * This function must be overridden, it should initialize all of the
     * components used by the app in the proper order according to the
     * particular app's dependencies.
     */
    public abstract void buildAppComponentsHook();

    // COMPONENT ACCESSOR METHODS
    /**
     * Accessor for the data component.
     */
    public AppDataComponent getDataComponent() {
        return dataComponent;
    }

    public File getWorkingfile(){
        return workingFile;
    }
    
    public void setWorkingFile(File file){
        workingFile = file;
    }
    public String getLinkFile(){
        return linkFile;
    }
    
    public void setWorkingFileString(String result){
        workingFileString = result;
    }
    
    public String getWorkingFileString(){
        return workingFileString;
    }
    
    /**
     * Accessor for the file component.
     */
    public AppFileComponent getFileComponent() {
        return fileComponent;
    }

    /**
     * Accessor for the workspace component.
     */
    public AppWorkspaceComponent getWorkspaceComponent() {
        return workspaceComponent;
    }

    /**
     * Accessor for the gui. Note that the GUI would contain the workspace.
     */
    public AppGUI getGUI() {
        return gui;
    }

    /**
     * This is where our Application begins its initialization, it will load the
     * custom app properties, build the components, and fully initialize
     * everything to get the app rolling.
     *
     * @param primaryStage This application's window.
     */
    @Override
    public void start(Stage primaryStage) {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        AppFileController helper = new AppFileController(this);

        // LET'S START BY INITIALIZING OUR DIALOGS
        AppMessageDialogSingleton messageDialog = AppMessageDialogSingleton.getSingleton();
        messageDialog.init(primaryStage);
        AppYesNoCancelDialogSingleton yesNoDialog = AppYesNoCancelDialogSingleton.getSingleton();
        yesNoDialog.init(primaryStage);

        try {
            // LOAD APP PROPERTIES, BOTH THE BASIC UI STUFF FOR THE FRAMEWORK
            // AND THE CUSTOM UI STUFF FOR THE WORKSPACE
            boolean success = loadProperties(APP_PROPERTIES_FILE_NAME);

            if (success) {
                // GET THE TITLE FROM THE XML FILE
                String appTitle = props.getProperty(APP_TITLE);

                // BUILD THE BASIC APP GUI OBJECT FIRST
                gui = new AppGUI(primaryStage, appTitle, this);

                // THIS BUILDS ALL OF THE COMPONENTS, NOTE THAT
                // IT WOULD BE DEFINED IN AN APPLICATION-SPECIFIC
                // CHILD CLASS
                buildAppComponentsHook();
                welcomeDialogWindow();
                primaryStage.setOnCloseRequest(e -> {
                    if (!helper.getSaved()) {
                        helper.handleExitRequest();
                    }
                });

                // NOW OPEN UP THE WINDOW
                primaryStage.show();
               

            }
        } catch (Exception e) {
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            dialog.show(props.getProperty(PROPERTIES_LOAD_ERROR_TITLE), props.getProperty(PROPERTIES_LOAD_ERROR_MESSAGE));
        }
    }

    public void welcomeDialogWindow(){
        //WE ARE GOING TO INITIALIZE THE WINDOW THAT WILL SHOW
        //THE WELCOME DIALOG
        AppFileController helper = new AppFileController(this);
        ArrayList recentMaps = new ArrayList();
        PropertiesManager props = PropertiesManager.getPropertiesManager();
      //  FileChooser directoryHelper = new FileChooser();
    //    directoryHelper.setInitialDirectory(new File("C:\\Users\\Augusto\\Netbeans projects\\CSE219-Homework2\\hw2\\MetroMapMaker\\export"));
       // String workDirectory = "C:\\Users\\Augusto\\Netbeans projects\\CSE219-Homework2\\hw2\\MetroMapMaker\\work";
        String exportDirectory = "./export/";
        String workDirectory = "./work/";
        String workDirectoryExtension = "/work/";
        File folder = new File(workDirectory);
        File[] files = folder.listFiles();
        for(int i = 0; i < files.length; i++){
            System.out.println("CONCENTRATE " + files[i].toString().substring(files[i].toString().indexOf("./work/") + 8, files[i].toString().length()));
            recentMaps.add(files[i].toString().substring(files[i].toString().indexOf("./work/") + 8, files[i].toString().length()));
        }
        
        Group root = new Group();
        Stage tempStage = new Stage();
        Scene tempScene = new Scene(root, 1200, 700);
        BorderPane tempPane = new BorderPane();
        VBox rightBox = new VBox(200);
        VBox leftBox = new VBox(20);
        ScrollPane leftPane = new ScrollPane();
        HBox topBox = new HBox();

        String imagePath = FILE_PROTOCOL + PATH_IMAGES + "Logo1.png";
        Image tempImg = new Image(imagePath);
        ImageView tempImage = new ImageView();
        tempImage.setImage(tempImg);

        Hyperlink newMapLink = new Hyperlink("Create a New Map");
        newMapLink.setBorder(Border.EMPTY);
        newMapLink.setStyle("-fx-font: 40 arial;");
        newMapLink.setPadding(new Insets(0, 0, 100, 180));

        newMapLink.setOnAction(e -> {
            try {
                handleWelcomeNewRequest();
                tempStage.close();
            } catch (IOException ex) {
                Logger.getLogger(AppTemplate.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        rightBox.setPadding(new Insets(0, 0, 0, 145));
        rightBox.getChildren().add(tempImage);
        rightBox.getChildren().add(newMapLink);

        Text leftText = new Text("Load Maps");
        leftText.setStyle("-fx-font: 20 arial;");
        leftBox.setPadding(new Insets(30, 50, 0, 50));
        leftBox.getChildren().add(leftText);
         if (!recentMaps.isEmpty()) {
            for (int i = 0; i < recentMaps.size(); i++) {
                Hyperlink recentLink = new Hyperlink(recentMaps.get(i).toString());
                String fileNameHelp = workDirectory + recentMaps.get(i).toString();
                workingFile = new File(fileNameHelp);
                System.out.println(fileNameHelp);
                recentLink.setBorder(Border.EMPTY);
                recentLink.setStyle("-fx-font: 20 arial;");
                recentLink.setOnAction(e -> {
                    String fileName = recentLink.getText();
                    linkFile = recentLink.getText();
                    setWorkingFileString(linkFile);
                    System.out.println("RECENT MAP NAME " + recentLink.getText().toString());
                    String fileNameSetter = workDirectory + fileName;
                    System.out.println("THIS IS THE FILE NAME SETTER : " + fileNameSetter);
                    File currentFile = new File(fileNameSetter);
                    System.out.println("WORKING CURRENT FILE " + currentFile.getAbsolutePath());
                    setWorkingFile(currentFile);
                   
                    helper.handleLoadFileRequest(fileNameHelp);
                    helper.setCurrentWorkFile(currentFile);
                    tempStage.close();

                });
                leftBox.getChildren().add(recentLink);
            }
        }
        leftPane.setContent(leftBox);

        Text topText = new Text("Welcome to Matro Map Maker");
        topText.setStyle("-fx-font: 16 arial;");
        topBox.getChildren().add(topText);
        topBox.setPadding(new Insets(0, 0, 5, 0));

        topBox.setStyle("-fx-background-radius: 5.0;");
        topBox.setStyle("-fx-padding: 15;");
        topBox.setStyle("-fx-spacing: 10;");
        topBox.setStyle("-fx-border-width: 2px;");
        topBox.setStyle("-fx-border-color: #000000;");
        topBox.setStyle("-fx-background-color : #1E90FF;");

        rightBox.setStyle("-fx-background-radius: 5.0;");
        rightBox.setStyle("-fx-padding: 15;");
        rightBox.setStyle("-fx-spacing: 10;");
        rightBox.setStyle("-fx-border-width: 2px;");
        rightBox.setStyle("-fx-border-color: #000000;");

        leftBox.setStyle("-fx-background-radius: 5.0;");
        leftBox.setStyle("-fx-padding: 15;");
        leftBox.setStyle("-fx-spacing: 10;");
        leftBox.setStyle("-fx-border-width: 2px;");
        leftBox.setStyle("-fx-border-color: #000000;");
        leftBox.setStyle("-fx-background-color : #FFDAB9;");
        
        leftPane.setStyle("-fx-background-radius: 5.0;");
        leftPane.setStyle("-fx-padding: 15;");
        leftPane.setStyle("-fx-spacing: 10;");
        leftPane.setStyle("-fx-border-width: 2px;");
        leftPane.setStyle("-fx-border-color: #000000;");
        leftPane.setStyle("-fx-background : #FFDAB9;");

        tempPane.setTop(topBox);
        tempPane.setCenter(rightBox);
        tempPane.setLeft(leftPane);

        tempPane.prefHeightProperty().bind(tempStage.heightProperty());
        tempPane.prefWidthProperty().bind(tempStage.widthProperty());

        root.getChildren().add(tempPane);
        tempStage.setScene(tempScene);
        tempStage.showAndWait();
    }
    
    /**
     * Loads this application's properties file, which has a number of settings
     * for initializing the user interface.
     *
     * @param propertiesFileName The XML file containing properties to be loaded
     * in order to initialize the UI.
     *
     * @return true if the properties file was loaded successfully, false
     * otherwise.
     */
    public boolean loadProperties(String propertiesFileName) {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        try {
            // LOAD THE SETTINGS FOR STARTING THE APP
            props.addProperty(PropertiesManager.DATA_PATH_PROPERTY, PATH_DATA);
            props.loadProperties(propertiesFileName, PROPERTIES_SCHEMA_FILE_NAME);
            return true;
        } catch (InvalidXMLFileFormatException ixmlffe) {
            // SOMETHING WENT WRONG INITIALIZING THE XML FILE
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            dialog.show(props.getProperty(PROPERTIES_LOAD_ERROR_TITLE), props.getProperty(PROPERTIES_LOAD_ERROR_MESSAGE));
            return false;
        }
    }

    public void handleWelcomeNewRequest() throws IOException {
        AppFileController helper = new AppFileController(this);
    
     
        String workDirectory = "./work/";
        File folder = new File(workDirectory);
        File[] files = folder.listFiles();
        ArrayList<String> fileNames = new ArrayList<String>();
      
        for (int i = 0; i < files.length; i++) {
            fileNames.add(files[i].toString().substring(files[i].toString().indexOf("./work/") + 8, files[i].toString().length()));
        }

        TextInputDialog dialog = new TextInputDialog();
        boolean existingFile = true;
        boolean equalityFlag = false;
        dialog.setTitle("Map Title");
        dialog.setContentText("Please enter your unique name for the map:");
        Text t = new Text();
        while (existingFile) {
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                t.setText(result.get());
                if (fileNames == null) {
                    handleDirectorySave(t.getText());
                    setWorkingFileString(t.getText());
                    workingFile = new File(workDirectory + t.getText());
                    linkFile = t.getText();
                    existingFile = false;
                } else {

                    for (int l = 0; l < fileNames.size(); l++) {
                        if (t.getText().equals(fileNames.get(l))) {
                            equalityFlag = true;
                        }
                    }
                    if (equalityFlag) {
                        Alert alert = new Alert(AlertType.WARNING);
                        alert.setTitle("Error");
                        alert.setContentText("The name already exists, please enter a unique name");
                        alert.showAndWait();
                        equalityFlag = false;
                    } else {
                        handleDirectorySave(t.getText());
                        workingFile = new File(workDirectory + t.getText());
                        setWorkingFileString(t.getText());
                        linkFile = t.getText();
                        existingFile = false;
                    }
                }
            } else {
                existingFile = false;
            }
        }
        helper.handleNewRequest();
    }

    /*
    This function will helpt o save the file and create
    a directory within the export directory, and within the work directory
    as well for the system.
     */
    public void handleDirectorySave(String file) throws IOException {
        FileChooser directoryHelper = new FileChooser();
        String workDirectory = "./work/";
        String exportDirectoryString = "./export/";
        directoryHelper.setInitialDirectory(new File(workDirectory));
        File projectFile = new File(directoryHelper.getInitialDirectory() + "/"+ file);
        System.out.println("WHEN SAVED FIRST TIME " + projectFile.getAbsolutePath().toString());
        BufferedWriter output = new BufferedWriter(new FileWriter(projectFile));
        directoryHelper.setInitialDirectory(new File(exportDirectoryString));
        File exportDirectory = new File(directoryHelper.getInitialDirectory() + "/" + file);
        System.out.println("WHEN SAVED FIRST EXPORT DIRECTORY NAME " + exportDirectory.getAbsolutePath().toString());
        exportDirectory.mkdir();
    }
}
