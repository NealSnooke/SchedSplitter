package tagDataProcessor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/*
 * captures and displays various messages:
 * 
 * messages - summary information for the user to confirm success
 * errors - issues that need user investigation
 * info - more detailed information for the use such as statistics about processing etc.
 * debug - low level information to help debug issues
 */
public class ErrorHandler {
	
	private File currentFile;
	private int currentLine; //in currentfile
	private int totalCurrentValidlines = 0; //in currentfile
	
	private int totalLines = 0; // for all files
	private int totalValidlines = 0; // for all files
	private int totalFiles = 0; // for all files
	
	private boolean showMessages = true;
	//private boolean showInfo = false;
	private boolean showDebug = false;
	
	OutputStreamWriter logWriter = null;
	
	/**
	 * 
	 * @param s
	 */
	public void setLogFile(String outputFileName){
		try {
			FileOutputStream filewriter = new FileOutputStream(outputFileName);
			logWriter = new OutputStreamWriter(filewriter,"UTF8");

		} catch (Exception e) {
			error("Problem creating log file! - "+ outputFileName);
			return;
		}
	}
	
	public void closeLogFile() {
		try {
			logWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param file
	 */
	public void newFile(File file) {
		currentFile = file;
		currentLine = 0;
		totalCurrentValidlines = 0;
		totalFiles++;
		
		try {
			message("Processing file: "+file.getCanonicalPath());
		} catch (IOException e) {
			error("Can't find file/path"+file.getAbsolutePath());
		}
	}
	
	/**
	 * select what to show
	 * 
	 * @param messages
	 * @param info
	 * @param debug
	 */
	public void show(boolean messages, boolean debug) {
		showMessages = messages;
		//showInfo = info;
		showDebug = debug;		
	}
	
	/**
	 * only show errors
	 */
	public void silence() {
		showMessages = false;
		showDebug = false;
		//showInfo = false;
	}
	
	/**
	 * 
	 */
	public int nextline() {
		currentLine++;	
		totalLines++;
		
		return currentLine;
	}
	
	/**
	 * indicate the line was valid
	 */
	public void validLine() {
		totalCurrentValidlines++;
		totalValidlines++;
	}
	
	public String getFileStats() {
		return currentLine+" lines read from '"+currentFile.getName()+"' File; "+totalCurrentValidlines+" lines contain valid coordinates.";
	}
	
	public String getStats() {
		return totalLines+" lines read from "+totalFiles+" Files; "+totalValidlines+" lines contain valid coordinates.";
	}
	
	/**
	 * 
	 * @param reason
	 */
	public void error(String reason) {
		
		write("Error: "+reason+" Line "+currentLine+"("+currentFile.getName()+")");
	}
	
	private void write(String s) {
		if (logWriter != null) {
			try {
				logWriter.write(s+"\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println(s);
		}
	}
	
	/**
	 * 
	 * @param reason
	 */
	public void message(String reason) {
		if (showMessages) {
			write(reason);
		}
	}
	
	/**
	 * 
	 * @param reason
	 
	public void info(String reason) {
		if (showInfo) {
			write("Line "+currentLine+" Info: "+reason);
		}
	}*/
	
	/**
	 * 
	 * @param reason
	 */
	public void debug(String reason) {
		if (showDebug) {
			write("["+currentLine+"]: "+reason);
		}
	}

}
