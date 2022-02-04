package openjfx;

import javafx.fxml.FXML;

import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

//public class Controller implements ChangeListener<String> {
public class LogTabController {

@FXML
private TextArea logTextArea;
@FXML
private BorderPane logPane;

	/**
	 * 
	 */
	public void initialize() {
		//System.out.println("Init log tab controller");	
	}
	
	/**
	 * 
	 */
	public void setlogText(String s) {
		logTextArea.setText(s);
	}

}
