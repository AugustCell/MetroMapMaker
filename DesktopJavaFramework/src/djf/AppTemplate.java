package djf;

import djf.ui.*;
import djf.components.*;
import javafx.application.Application;
import javafx.stage.Stage;
import properties_manager.PropertiesManager;
import static djf.settings.AppPropertyType.*;
import djf.settings.AppStartupConstants;
import static djf.settings.AppStartupConstants.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import properties_manager.InvalidXMLFileFormatException;

/**
 * This is the framework's JavaFX application. It provides the start method
 * that begins the program initialization, which delegates component
 * initialization to the application-specific child class' hook function.
 * 
 * @author Richard McKenna
 * @version 1.0
 */
public abstract class AppTemplate extends Application {

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
    
    public String preferredLanguage = "";
    
    public String filename = "language.ser";
    
        
    // THIS METHOD MUST BE OVERRIDDEN WHERE THE CUSTOM BUILDER OBJECT
    // WILL PROVIDE THE CUSTOM APP COMPONENTS

    /**
     * This function must be overridden, it should initialize all
     * of the components used by the app in the proper order according
     * to the particular app's dependencies.
     */
    public abstract void buildAppComponentsHook();
    
    // COMPONENT ACCESSOR METHODS

    /**
     *  Accessor for the data component.
     */
    public AppDataComponent getDataComponent() { return dataComponent; }

    /**
     *  Accessor for the file component.
     */
    public AppFileComponent getFileComponent() { return fileComponent; }

    /**
     *  Accessor for the workspace component.
     */
    public AppWorkspaceComponent getWorkspaceComponent() { return workspaceComponent; }
    
    /**
     *  Accessor for the gui. Note that the GUI would contain the workspace.
     */
    public AppGUI getGUI() { return gui; }
    
    /**
     * Accessor for the app preffered language. 
     */

    public String getPrefferedLanguage(){ return preferredLanguage;  }
    
    /**
     * This is where our Application begins its initialization, it will load
     * the custom app properties, build the components, and fully initialize
     * everything to get the app rolling.
     *
     * @param primaryStage This application's window.
     */
    @Override
    public void start(Stage primaryStage) {
        
        //This set of code will check if the file exists or not.
        //If the file exists, the reder will make preferredLanguage to the object found in the file.
        //If it is not found, the poopup window opens up, explaining to pick a preferred language.
        File f = new File("C:\\Users\\Augusto\\Netbeans projects\\CSE219-Homework2\\hw2\\goLogoLo\\language.ser");
        
        if (f.exists()) {
            System.out.println("File exists");
            FileInputStream fis = null;
            ObjectInputStream in = null;
            try {
                fis = new FileInputStream(filename);
                in = new ObjectInputStream(fis);
                preferredLanguage = (String) in.readObject();
                in.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            Alert alert = new Alert(AlertType.NONE);
            alert.setTitle("Language picker");
            alert.setContentText("Choose your preferred language");

            ButtonType englishButton = new ButtonType("English");
            ButtonType spanishButton = new ButtonType("Espanol");

            alert.getButtonTypes().setAll(englishButton, spanishButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == englishButton) {
                englishButtonRequest();
            } else if (result.get() == spanishButton) {
                spanishButtonRequest();
            };
        }
        
        // LET'S START BY INITIALIZING OUR DIALOGS
        AppMessageDialogSingleton messageDialog = AppMessageDialogSingleton.getSingleton();
        messageDialog.init(primaryStage);
        AppYesNoCancelDialogSingleton yesNoDialog = AppYesNoCancelDialogSingleton.getSingleton();
        yesNoDialog.init(primaryStage);
        PropertiesManager props = PropertiesManager.getPropertiesManager();

        try {
            // LOAD APP PROPERTIES, BOTH THE BASIC UI STUFF FOR THE FRAMEWORK
            // AND THE CUSTOM UI STUFF FOR THE WORKSPACE
            boolean success = loadProperties(preferredLanguage);

            if (success) {
                // GET THE TITLE FROM THE XML FILE
                String appTitle = props.getProperty(APP_TITLE);

                // BUILD THE BASIC APP GUI OBJECT FIRST
                gui = new AppGUI(primaryStage, appTitle, this);

                // THIS BUILDS ALL OF THE COMPONENTS, NOTE THAT
                // IT WOULD BE DEFINED IN AN APPLICATION-SPECIFIC
                // CHILD CLASS
                buildAppComponentsHook();

                // NOW OPEN UP THE WINDOW
                primaryStage.show();
            }
        } catch (Exception e) {
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            dialog.show(props.getProperty(PROPERTIES_LOAD_ERROR_TITLE), props.getProperty(PROPERTIES_LOAD_ERROR_MESSAGE));
        }
    }

    
    /**
     * This will handle if the English button is pressed
     * This will also output to a file called language.ser
     * so it can be read later on in the program.
     * This will be done if no file is found
     */
    public void englishButtonRequest() {
        preferredLanguage = xmlConstants.getEnglish();
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = new FileOutputStream(filename);
            out = new ObjectOutputStream(fos);
            out.writeObject(preferredLanguage);

            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

     /**
     * This will handle if the Spanish button is pressed
     * This will also output to a file called language.ser
     * so it can be read later on in the program.
     */
    public void spanishButtonRequest() {
        preferredLanguage = xmlConstants.getSpanish();
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = new FileOutputStream(filename);
            out = new ObjectOutputStream(fos);
            out.writeObject(preferredLanguage);

            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } 
    }


    /**
     * Loads this application's properties file, which has a number of settings
     * for initializing the user interface.
     *
     * @param propertiesFileName The XML file containing properties to be
     * loaded in order to initialize the UI.
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
}