package openjfx;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.css.PseudoClass;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/*
 * looks after the file chooser
 * there is a text entry field for filenames and a file chooser button
 * 
 * either can be used to select files
 * the file chooser puts a message in the text field 
 * 
 * if the field is typed in then that is used as the specified folder or file
 * if the file chooser is cancelled
 */

public class FileHandler {
	//the parent that contains the fxml classes
	private SettingsTabController controller;

	//popup filechooser
	private FileChooser fileChooser;

	// the current file(s) to process
	private List<File> selectedFiles = null;

	// the error style for text fields (.text-input:error)
	private final PseudoClass errorClass = PseudoClass.getPseudoClass("error");

	/**
	 * 
	 */
	public FileHandler(SettingsTabController parent) {
		controller = parent;

		controller.runButton.setOnAction(event -> parent.processFiles());
		controller.fileButton.setOnAction(event -> this.fileChooser());
		controller.runButton.setDisable(true);

		fileChooser = new FileChooser();
		//fileChooser = new DirectoryChooser();
		fileChooser.setTitle("Open Resource File(s)");	

		// set file chooser to working directory by default
		//try {
		//	System.out.println("Working Directory = "+ new java.io.File(".").getCanonicalPath());
		//} catch (IOException e) {}

		// the filechooser default folder
		fileChooser.setInitialDirectory(new java.io.File("."));

		/*
		   controller.fileName.textProperty().addListener(
				(observable, oldValue, newValue) -> {					
					fileNameChanged(newValue);	
				}); 
		 */

		//detect typing into file text area
		controller.fileName.setOnKeyTyped(event -> {
			//if (event.getCode() == KeyCode.A) 			
			fileNameChanged(controller.fileName.getText());
		});
	}

	/**
	 * update the currently selected list of valid files
	 * 
	 * @param files
	 * @param chooser If true then the filename is recreated in the text input field
	 */
	private void updateSelected(List<File> files, boolean chooser) {
		//System.out.println("Update selected list "+chooser);

		if (files != null) {
			controller.runButton.setDisable(false);
			controller.fileName.pseudoClassStateChanged(errorClass, false); //text field error style
			selectedFiles = files;

			//System.out.println("File: "+f.getAbsolutePath());
			//fileName.setText(f.getName());

			String name = null;
			for (File f : selectedFiles) {
				if (name == null) name = f.getName();

				//System.out.println(f.getName());
				//System.out.println(f.getParentFile());
			}

			// fill in same info to the text area
			if (chooser) {
				if (selectedFiles.size() > 1) {
					controller.fileName.setText(name+", ...  ("+selectedFiles.size()+") files");
				} else {
					controller.fileName.setText(name);
					//System.out.println("chooser update name");
				}
			}
		} else {
			// highlight error and disable run button
			controller.runButton.setDisable(true);
			
			controller.fileName.pseudoClassStateChanged(errorClass, true); //text field error style
			selectedFiles = null;
		} 
	}

	/**
	 * 
	 * @return
	 */
	public List<File> getSelectedFiles() {
		return selectedFiles;
	}

	/**
	 * 
	 * @return
	 */
	public String fileChooser () {
		System.out.println("file chooser...");

		Stage stage = (Stage)controller.fileButton.getScene().getWindow();

		//File f = fileChooser.showDialog(stage);
		List<File> f = fileChooser.showOpenMultipleDialog(stage);

		if (f != null) updateSelected(f, true);

		return null;
	}

	/**
	 * restrict a text field to file 
	 * @param tf
	 * @param oldValue
	 * @param newValue
	 */
	private void fileNameChanged (String newValue) {

		File f = new java.io.File(newValue);

		if (f.exists()){
			//System.out.println("File exists: "+f.getPath());
			//System.out.println("File exists: "+f.getAbsolutePath());

			ArrayList<File> files = new ArrayList<File>();
			
			if (f.isDirectory()) {
				//System.out.println("isDirectory ");
				
				// get all the files in the folder
				for (String s : f.list()) {
					//System.out.println("file name "+f.getPath()+File.separator+s);
					
					try {
						//System.out.println(f1.getCanonicalPath());
						//System.out.println(f1.getAbsolutePath());
						
						File f1 = new java.io.File(f.getCanonicalPath()+File.separator+s);
						
						// skip any hidden files that start with '.'
						if ( f1.exists() && (!s.startsWith("."))) files.add(f1);

					} catch (IOException e) {
						//nothing to do if file can't be found
					}	
				}
				
			} else {	
				files.add(f);
			}
			
			updateSelected(files, false);
			
		} else {
			updateSelected(null, false);
		}


	}

}
