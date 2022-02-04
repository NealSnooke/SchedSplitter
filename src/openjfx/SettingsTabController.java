package openjfx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CheckBox;

import tagDataProcessor.TagDataProcessor;

//public class Controller implements ChangeListener<String> {
public class SettingsTabController {

	// the gui fxml elements with fx:id attributes 
	@FXML
	private Label label;    
 
	@FXML
	private TextField latCol, lngCol, dateCol, timeCol;
	@FXML
	private TextField intervalLower, intervalHigher;
	@FXML
	protected TextField startDate, endDate, startTime, endTime;
	@FXML
	private Label runLabel;
	@FXML
	protected Button runButton, fileButton;   
	@FXML
	protected TextField fileName;   

	private FileHandler fileHandler;
	private DateTimeHandler dateTimeHandler;
	
	@FXML
	protected ComboBox<String> dateSelector;
	@FXML
	protected TextField dateTimeFormat;
	
	@FXML
	protected ComboBox<String>errorSelector;
	
	@FXML
	private CheckBox timeOffsetCheckbox;
	@FXML
	private TextField timeOffsetTextField;
	
	@FXML
	protected ComboBox<String>separatorSelector;
	
	@FXML
	private CheckBox outputISOTimeOffsetCheckbox;
	
	private String errorLevel = "Messages";
	private String separator = "comma";

	/**
	 * 
	 */
	public void initialize() {
		fileHandler = new FileHandler(this);
		
		dateTimeHandler = new DateTimeHandler(this);
		
		//fileHandler.initialize(this);
		
		String javaVersion = System.getProperty("java.version");
		String javafxVersion = System.getProperty("javafx.version");
		label.setText("JavaFX " + javafxVersion + "   running on Java " + javaVersion + ".");

		//latCol.setPrefWidth(40); // better in the css
		//fileButton.setText("Browse");
		//intervalLower.textProperty().addListener(this);

		// prevent non integers being entered
		intervalLower.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					intOnly(intervalLower, oldValue, newValue);
				});

		// prevent non integers being entered
		intervalHigher.textProperty().addListener(
				(observable, oldValue, newValue) -> {					
					intOnly(intervalHigher, oldValue, newValue);	
				});

		latCol.textProperty().addListener(
				(observable, oldValue, newValue) -> {					
					intOnly(latCol, oldValue, newValue);	
				});

		lngCol.textProperty().addListener(
				(observable, oldValue, newValue) -> {					
					intOnly(lngCol, oldValue, newValue);	
				}); 

		dateCol.textProperty().addListener(
				(observable, oldValue, newValue) -> {					
					intOnly(dateCol, oldValue, newValue);	
				}); 

		timeCol.textProperty().addListener(
				(observable, oldValue, newValue) -> {					
					intOnly(timeCol, oldValue, newValue);	
				}); 
		
		startDate.textProperty().addListener(
				(observable, oldValue, newValue) -> {					
					dateTimeHandler.checkStartDate(startDate, oldValue, newValue);	
				}); 
		startTime.textProperty().addListener(
				(observable, oldValue, newValue) -> {					
					dateTimeHandler.checkStartDate(startTime, oldValue, newValue);	
				}); 
		
		endDate.textProperty().addListener(
				(observable, oldValue, newValue) -> {					
					dateTimeHandler.checkEndDate(endDate, oldValue, newValue);	
				}); 
		endTime.textProperty().addListener(
				(observable, oldValue, newValue) -> {					
					dateTimeHandler.checkEndDate(endTime, oldValue, newValue);	
				}); 

		/*errorSelector.setOnAction(event -> {
				System.out.println("hello");
				errorSelector.sel
			});*/
		
		errorSelector.valueProperty().addListener(
				(observable, oldValue, newValue) -> {	
				
					this.errorLevel = newValue;					
					//dateTimeHandler.checkEndDate(endTime, oldValue, newValue);	
				}); 
		
		separatorSelector.valueProperty().addListener(
				(observable, oldValue, newValue) -> {	
				
					this.separator = newValue;
					
				}); 
		
		timeOffsetCheckbox.setSelected(true);
		outputISOTimeOffsetCheckbox.setSelected(false);
		
		//System.out.println("x "+this.errorClass.toString());
		//timeCol.pseudoClassStateChanged(errorClass, true);
		//timeCol.getStyleClass().add("error");
	}
	
	

	/**
	 * restrict a text field to integers 
	 * @param tf
	 * @param oldValue
	 * @param newValue
	 */
	private void intOnly (TextField tf, String oldValue, String newValue) {
		try {	
			if (!newValue.isEmpty()) Integer.parseInt(newValue);
		} catch (Exception e) {					
			tf.setText(oldValue);
		}	
	}

	/**
	 * 
	 */
	public boolean processFiles() {
		//System.out.println("process here...");
		TagDataProcessor tdp = new TagDataProcessor();
		
		if (this.errorLevel.equals("log: errors only")) {
			tdp.getErrorHandler().show(false, false); // messages, debug
		}
		if (this.errorLevel.equals("log: messages + errors")) {
			tdp.getErrorHandler().show(true, false); // messages, debug
		}
		if (this.errorLevel.equals("log: debug")) {
			tdp.getErrorHandler().show(true, true); // messages, debug
		}
		
		File err = fileHandler.getSelectedFiles().get(0);
		//System.out.println("Log file path: "+err.getParent());
		
		// set up the log file
		String logFileName =err.getParent()+File.separator+"LOG-SchedSplitter.txt";
		tdp.getErrorHandler().setLogFile(logFileName);
		
		int minTimeDiff = 0;
		try {	
			minTimeDiff = Integer.parseInt(intervalLower.getText());
		} catch (Exception e) {					
			tdp.getErrorHandler().error("Invalid minumum time diff: "+intervalLower.getText());
			return false;
		}	

		int maxTimeDiff = 0;
		try {	
			maxTimeDiff = Integer.parseInt(intervalHigher.getText());
		} catch (Exception e) {					
			tdp.getErrorHandler().error("Invalid maximum time diff: "+intervalHigher.getText());
			return false;
		}	

		if (minTimeDiff>maxTimeDiff || minTimeDiff<0 || maxTimeDiff <0) {
			tdp.getErrorHandler().error("Invalid maximum time diff: "+intervalLower.getText()+":"+intervalHigher.getText());
			return false;
		}

		tdp.getErrorHandler().message("Select time period: "+minTimeDiff+":"+maxTimeDiff);
		
		Integer latc = null;
		Integer lngc = null;
		Integer datec = null;
		Integer timec = null;
		
		try {
			latc = Integer.parseInt(latCol.getText());
		} catch (Exception e) {					
			tdp.getErrorHandler().error("Invalid latitude column: "+latCol.getText());
			return false;
		}
		
		try {
			lngc = Integer.parseInt(lngCol.getText());
		} catch (Exception e) {					
			tdp.getErrorHandler().error("Invalid longititude column: "+lngCol.getText());
			return false;
		}
		
		try {
			datec = Integer.parseInt(dateCol.getText());
		} catch (Exception e) {					
			tdp.getErrorHandler().error("Invalid date column: "+dateCol.getText());
			return false;
		}
		
		try {
			timec = Integer.parseInt(timeCol.getText());
		} catch (Exception e) {}

		
		String StartDateTime = startDate.getText()+" "+startTime.getText();	
		String EndDateTime  = endDate.getText()+" "+endTime.getText();
		
		// add the timezone to the end of the datetime if the checkbox is selected	
		String timeZone	= "";
			
		if (timeOffsetCheckbox.isSelected()){	
			timeZone = timeOffsetTextField.getText();
			tdp.getErrorHandler().message("Adding timezone offset "+timeOffsetTextField.getText()+" to all date/times");		
		} else {		
			tdp.getErrorHandler().message("No timezone offset specified - it must be included in the file data to match "
					+ "the 'x' format specifier");
		}
		
		//set output format
		tdp.outputISOTimeFormat = outputISOTimeOffsetCheckbox.isSelected();
		
		for (File f : fileHandler.getSelectedFiles()) {
			//System.out.println("Processing file: "+f.getPath());
			
			tdp.processFile(f, 
					minTimeDiff, maxTimeDiff,
					latc, lngc, datec, timec,
					dateTimeFormat.getText(), timeZone,
					StartDateTime, EndDateTime,
					separator
					);
		}

		runLabel.setText(tdp.getErrorHandler().getStats());
		
		tdp.getErrorHandler().closeLogFile();
		
		// read and display log file
		File logFile = new File(logFileName);	
		
		MainApp.logController.setlogText(fetchFile(logFile).toString());
		
		//display the summary info
		MainApp.resultsController.setResultsText(tdp.getSummaryOutput());
	    
	    SingleSelectionModel<Tab> selectionModel = MainApp.tabPane.getSelectionModel();
	    selectionModel.select(1);
		
		return true;
	}
	
	/**
	 * loads up to 1000 lines from a text file into a string buffer.
	 * @return
	 */
	private StringBuffer fetchFile(File file) {
		
		StringBuffer sb = new StringBuffer();
		int linecount = 0;
		
		try {

			FileInputStream tagFileStream = new FileInputStream(file);	
			BufferedReader br = new BufferedReader(new InputStreamReader(tagFileStream));

			String line = br.readLine();

			while (line != null && linecount<1000) {
				sb.append(line+"\n");
				line = br.readLine();
				linecount++;
			}
			
			if (linecount>=500) {
				sb.append("\nToo many lines to display here. Look in the log file for complete output\n");
			}
			
			br.close();
			
		} catch (FileNotFoundException e1) {
			sb.append("Can't open file for reading: "+file.getName());
			return sb;

		} catch (IOException e) {
			sb.append("Can't close file "+file.getName());
			return sb;
		}	
		
		
		return sb;
	}

}
