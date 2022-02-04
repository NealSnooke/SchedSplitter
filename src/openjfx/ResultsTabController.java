package openjfx;

import javafx.fxml.FXML;

import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

//public class Controller implements ChangeListener<String> {
public class ResultsTabController {

@FXML
private TextArea resultsTextArea;
@FXML
private BorderPane resultsPane;

	/**
	 * 
	 */
	public void initialize() {
		//System.out.println("Init results tab controller");
			
		//resultsTextArea.setPrefWidth(800);
		//resultsTextArea.setPrefHeight(800);	
	}
	
	/**
	 * 
	 */
	public void setResultsText(String s) {
		resultsTextArea.setText(s);
	}

}
