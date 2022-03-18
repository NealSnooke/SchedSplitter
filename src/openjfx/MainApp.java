package openjfx;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.Tab;


// Note the fx:id label in the fxml file provides the name that is used to refer
// to the #element in the css file

public class MainApp extends Application {
	private static String version = "1.0";

	BorderPane settingsPane;
	BorderPane resultsPane;
	BorderPane logPane;
	
	// quick hack to allow access between the controllers.
	// we only want one instance of Main App anyway so not a problem.
	static SettingsTabController settingsController;
	static ResultsTabController resultsController;
	static LogTabController logController;
	static TabPane tabPane;
	
    @Override
    public void start(Stage primaryStage) throws Exception{
    	//FXMLLoader loader = new FXMLLoader(MainApp.class.getClass().getResource("openjfx.fxml"));
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("settingspage.fxml"));
        settingsPane = (BorderPane)loader.load();
        settingsController = (SettingsTabController) loader.getController();
        
        FXMLLoader loader1 = new FXMLLoader(getClass().getResource("resultspage.fxml"));
        resultsPane =  (BorderPane)loader1.load();
        resultsController = (ResultsTabController) loader1.getController();
        
        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("logpage.fxml"));
        logPane =  (BorderPane)loader2.load();
        logController = (LogTabController) loader2.getController();
        
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

        Tab tab1 = new Tab("Settings", settingsPane);
        Tab tab2 = new Tab("Result summary"  , resultsPane);
        Tab tab3 = new Tab("Log summary"  , logPane);
        
        tabPane.getTabs().add(tab1);
        tabPane.getTabs().add(tab2);
        tabPane.getTabs().add(tab3);
        
        //VBox vBox = new VBox(tabPane);      
        //Scene scene = new Scene(page, 400, 300);      
        //Scene scene = new Scene(page);
        //Scene scene = new Scene(vBox);
        Scene scene = new Scene(tabPane);
              
        // set up css styling
        scene.getStylesheets().add( getClass().getResource("blueStyle.css").toExternalForm() );
        
        primaryStage.setTitle("Schedule Splitter (version "+version+")");      
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // controller needs this for the filechooser
        //Controller c = (Controller)loader.getController();
    }

    public static void main(String[] args) {
    	System.out.println("Schedule splitter v"+version);
        launch(args);

    }
}