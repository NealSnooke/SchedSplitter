/**
 * First created Neal Snooke 26/07/2018
 */

package tagDataProcessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Vector;

public class TagDataProcessor {

	private Vector<TagDataItem> rowData;
	private String outputFileName;
	private int minTimeDiff;
	private int maxTimeDiff;
	private String dateTimeFormat;
	private Integer latCol, lngCol, dateCol, timeCol;

	private ErrorHandler errhndlr = new ErrorHandler();
	
	public boolean outputISOTimeFormat = false;
	
	private StringBuffer summaryOutput;
	private int dayItemCount = 0; //the number of valid items for the current day being processed

	/**
	 * 
	 * Entry point for command line version
	 * 
	 * @param args CL parameters
	 */
	public static void main(String args[]){
		System.out.println("Tag Data Processor");
		System.out.println("Working Directory = " + System.getProperty("user.dir"));

		/*
		try {
			System.out.println("Working Directory = "+ new java.io.File(".").getCanonicalPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 */

		TagDataProcessor tdp = new TagDataProcessor();

		if (args.length < 3){
			System.out.println("Please specify path/name of file to read!");
			System.out.println("Please specify minimum time difference between samples");
			System.out.println("Please specify maximum time difference between samples");
			return;
		}

		File file = new File(args[0]);	

		System.out.println(file.getName());
		System.out.println(file.getParentFile());

		//tdp.outputFileName = args[1];
		tdp.outputFileName = tdp.makeOutputFilename(file);

		tdp.minTimeDiff = Integer.parseInt(args[1]);
		tdp.maxTimeDiff = Integer.parseInt(args[2]);

		tdp.loadFile(file, ",", null, null, "+00"); // TODO start/end date timezone

		//tdp.listData();

		FileOutputStream filewriter = null;
		try {
			filewriter = new FileOutputStream(tdp.outputFileName);

			tdp.saveFile(filewriter);

		} catch (FileNotFoundException e) {
			System.out.println("File write error! - "+ tdp.outputFileName);
			return;
		}

	}

	/**
	 * 
	 */
	public TagDataProcessor() {
		rowData = new Vector<TagDataItem>();
		dayItemCount = 0;
		
		summaryOutput = new StringBuffer();
	}

	/**
	 * 
	 * @return
	 */
	public ErrorHandler getErrorHandler() {
		return errhndlr;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getSummaryOutput() {
		return summaryOutput.toString();
	}

	/**
	 * process an individual file
	 */
	public void processFile(File infile, int minTimeDiff, int maxTimeDiff, 
			Integer latc, Integer lngc, Integer datec, Integer timec,
			String dateTimeFormat, String timeZone,
			String stateDateTime, String endDateTime,
			String separator
			){

		//this();
		this.dateTimeFormat = dateTimeFormat;
		this.minTimeDiff = minTimeDiff;
		this.maxTimeDiff = maxTimeDiff;
		latCol = latc;
		lngCol =lngc; 
		dateCol = datec; 
		timeCol = timec;
		
		getErrorHandler().message("Date/Time format: '"+dateTimeFormat+'"');

		rowData = new Vector<TagDataItem>();
		dayItemCount = 0;
		System.out.println("file to process"+infile.getName());
		loadFile(infile, separator, stateDateTime, endDateTime, timeZone);	

		outputFileName = makeOutputFilename(infile);

		//listData(); // debugging

		FileOutputStream filewriter = null;

		try {
			filewriter = new FileOutputStream(outputFileName);

			saveFile(filewriter);

		} catch (FileNotFoundException e) {
			errhndlr.error("File write error! - "+ outputFileName);
			return;
		}
	}	

	/**
	 * 
	 * @param inputFile
	 * @return
	 */
	private String makeOutputFilename(File inputFile) {
		// add the time selection to the existing filename
		String baseName =  stripExtension(inputFile.getName());
		String extension = inputFile.getName().substring(baseName.length());
		String outputFileName = inputFile.getParent()+File.separator
				+baseName+"_"+minTimeDiff+"-"+maxTimeDiff+extension;
		errhndlr.debug("Generated output file name "+outputFileName);

		return outputFileName;
	}

	/**
	 * s
	 * @param s
	 * @return
	 */
	public static String stripExtension(final String s)
	{
		return s != null && s.lastIndexOf(".") > 0 ? s.substring(0, s.lastIndexOf(".")) : s;
	}

	/**
	 * 
	 */
	public void listData(){
		for (TagDataItem i : rowData){
			System.out.println(i);
		}
	}

	/**
	 * 
	 */
	public boolean loadFile( File file, String separator, String startDateTime, String endDateTime, String timeZone){
		// start collecting info for a new file
		errhndlr.newFile(file);

		// check the validity of the date/time specification
		DateTimeFormatter dtformat = DateTimeFormatter.ofPattern("d/M/y H:m:s x");
		OffsetDateTime zdt_s = null, zdt_e = null;
		//String input = "16:02:37 14.03.2021 +00";
		//DateTimeFormatter f = DateTimeFormatter.ofPattern( "HH:mm:ss d.M.y x" );
		//System.out.println("xx"+ startDateTime+" "+endDateTime);

		if (!startDateTime.isBlank()) {
			try {		
				zdt_s = OffsetDateTime.parse( startDateTime+" +00" , dtformat );
				//zdt_s = OffsetDateTime.parse( startDateTime, dtformat );	
				errhndlr.debug("Date time string: '"+startDateTime+" parsed into '"+zdt_s+"' UTC");		
			} catch (DateTimeParseException e) {		
				errhndlr.error( "Unable to parse '"+startDateTime+"' using format ISO 6601 format specifiers: '"+dtformat+"'");
				return false;
			}
		}

		if (!endDateTime.isBlank()) {
			try {		
				//ZonedDateTime zdt = ZonedDateTime.parse( dateTime , dtformat );
				zdt_e = OffsetDateTime.parse( endDateTime+" +00" , dtformat );	
				//zdt_e = OffsetDateTime.parse( endDateTime, dtformat );	
				errhndlr.debug("Date time string: '"+endDateTime+" parsed into '"+zdt_e+"' UTC");		
			} catch (DateTimeParseException e) {		
				errhndlr.error( "Unable to parse '"+endDateTime+"' using format ISO 6601 format specifiers: '"+dtformat+"'");
				return false;
			}
		}

		try {

			FileInputStream tagFileStream = new FileInputStream(file);	
			BufferedReader br = new BufferedReader(new InputStreamReader(tagFileStream));
			
			summaryOutput.append("File: "+file.getName()+"\n");

			String line = br.readLine();

			while (line != null) {

				processDataline(line, separator, zdt_s, zdt_e, timeZone);
				
				line = br.readLine();
				errhndlr.nextline();
				
			}

			//add the summary info for the very last date
			if (!rowData.isEmpty()) {
					OffsetDateTime zdt = rowData.lastElement().getDateTime();
					summaryOutput.append(zdt.toLocalDate().toString()+" \t"+dayItemCount+" items read"+"\n\n");	
			} else {
				summaryOutput.append("No data to summarise.");
			}
			tagFileStream.close();

			errhndlr.message(errhndlr.getFileStats());
			errhndlr.message(errhndlr.getStats());
		}

		catch (FileNotFoundException e1) {
			errhndlr.error("Can't open file for reading: "+file.getName());
			return false;

		} catch (IOException e) {
			errhndlr.error("Can't close file "+file.getName());
			return false;
		}	

		return true;
	}

	/**
	 * read and parse a line from the file
	 * any lines that do not contain gps data according to the specified format are dropped
	 * any lines that fall outside the specifies dates/times are dropped
	 * 
	 * @param line
	 * @param startDateTime - select only entries after this time, no restriction if null
	 * @param endDateTime - select only entries before this time, no restriction if null
	 * @return
	 */
	private void processDataline(String line, String separator,
			OffsetDateTime startDateTime, OffsetDateTime endDateTime, 
			String timeZone){

		//split by comma or comma space
		String[] result = null;
		
		switch (separator) {
		case "comma" : {
			
			result = line.split(",");
			break;
		}

		case "space" : {
			result = line.split(" ");
			break;
		}

		case "tab" : {
			result = line.split("\t");
			break;
		}
		default:
			errhndlr.error("Unknown separator type: "+ separator);
			return;
		}
		
		// remove any leading or trailing spaces
		for (int i = 0; i< result.length; i++) {
			//System.out.println("'"+result[i]+"'");
			result[i] = result[i].trim();
		}
		
		//for (String s : result) {
		//	System.out.println("x'"+s+"'");
		//}

		errhndlr.debug("Read "+result.length+" items for columns: "
				+latCol+" "+lngCol+" "+dateCol+" "+timeCol);

		int maxcolumn = Math.max(Math.max(latCol,lngCol), dateCol);
		int mincolumn = Math.min(Math.min(latCol,lngCol), dateCol);	

		if (mincolumn < 1) {
			errhndlr.error("Cannot process lat, lng, date column numbers < 1");
		}

		if (timeCol != null && timeCol >= 1) {
			maxcolumn = Math.max(maxcolumn, timeCol);
		}

		//errhndlr.debug("maxcol " +maxcolumn+ " mincol "+mincolumn);
		if (maxcolumn>(result.length+1)) {
			errhndlr.debug("Not enough columns ("+result.length+") in data! \""+line+"\"");
		}

		if (result.length >= maxcolumn){
			String lat = result[latCol-1];
			String lng = result[lngCol-1];
			String date = result[dateCol-1];
			String time = null;		

			if (timeCol != null && timeCol > 0) {
				time = result[timeCol-1];
			}

			//TagDataItem item = new TagDataItem(result[0], result[1], result[2], result[3]);
			errhndlr.debug("Split data items: lat "+lat+", lng "+lng+", date "+date+" time "+time);			

			try {

				TagDataItem item = new TagDataItem(lat, lng, date, time, timeZone, dateTimeFormat, errhndlr);

				errhndlr.debug(item.toString());

				//check the dates and add if within required period
				//System.out.println("Date Time selection: "+startDateTime+" -> "+endDateTime);

				if (startDateTime != null){

					//System.out.println("QQQ "+item.getTimeInSeconds() +" "+ startDateTime.toEpochSecond());
					//if (endDateTime!=null) System.out.println("QQQ "+item.getTimeInSeconds() +" "+ endDateTime.toEpochSecond());

					//check start
					if (item.getTimeInSeconds() >= startDateTime.toEpochSecond()) {

						if (endDateTime != null) {
							if (item.getTimeInSeconds() <= endDateTime.toEpochSecond())
								// falls with specified dates
								addItem(item);
							else 
								errhndlr.debug("Item outside required date/time - skipped");
						} else 
							// start date but no end date so add item
							addItem(item);					
					} else 
						errhndlr.debug("Item outside required date/time - skipped");

				} else {
					//if (startDateTime!=null) System.out.println("RRR "+item.getTimeInSeconds() +" "+ startDateTime.toEpochSecond());
					//System.out.println("RRR "+item.getTimeInSeconds() +" "+ endDateTime.toEpochSecond());


					// no start date so just check end date
					if (endDateTime != null) {
						if (item.getTimeInSeconds()  <= endDateTime.toEpochSecond() ) 
							// falls with specified dates
							addItem(item); 

						else 
							errhndlr.debug("Item outside required date/time - skipped");
					} else {
						// no start or end date specified so add it
						addItem(item);
					}
				}

				//for (int x=0; x<result.length; x++){
				//	System.out.println(result[x]);
				//}			

			} catch (TagDataItem.TagItemInvalidException e) {

				errhndlr.error("Item cannot be created - skipped ("+"lat "+lat+", lng "+lng+", date "+date+" time "+time);
			}

		} 

	}

	/**
	 * 
	 */
	private void addItem(TagDataItem item) {
		//check if this is the same day as the previous item and increase the count if so
		if (rowData.isEmpty()) {
			dayItemCount++;
		} else {
			
			OffsetDateTime zdt = rowData.lastElement().getDateTime();
			
			//if dates match
			if (zdt.toLocalDate().toString().equals(
				 item.getDateTime().toLocalDate().toString())) {
			 
				dayItemCount++;
				
			} else {
				
				//add message and reset count
				summaryOutput.append(zdt.toLocalDate().toString()+" \t"+dayItemCount+" items read"+"\n");	
				dayItemCount = 1;
			}
		}
				
		rowData.add(item); 
		errhndlr.validLine();	
	}
	
	/**
	 * 
	 */
	private void dayValidItem(TagDataItem previousItem, TagDataItem currentItem) {
		// increase the valid items count for day
		// output summary if same day as the previous item 
		
		dayItemCount++;
		
		//daySummary(previousItem, currentItem);
		/*if (!previousItem.getDateTime().toLocalDate().toString().equals(
				currentItem.getDateTime().toLocalDate().toString())) {
			//add message and reset count
			summaryOutput.append(previousItem.getDateTime().toLocalDate().toString()
					+" \t"+dayItemCount+" items within specification"+"\n");	
			dayItemCount = 0;
		}*/
	}
	
	/**
	 * 
	 */
	private void daySummary(TagDataItem previousItem, TagDataItem currentItem) {
		//check if this is the same day as the previous item and increase the count if so

		//if dates match				
		if (!previousItem.getDateTime().toLocalDate().toString().equals(
				currentItem.getDateTime().toLocalDate().toString())) {
			//add message and reset count
			summaryOutput.append(previousItem.getDateTime().toLocalDate().toString()
					+" \t"+dayItemCount+" items within specification"+"\n");	
			dayItemCount = 0;
		}
	}

	/**
	 * 
	 * @param lastItem
	 * @throws IOException 
	 */
	private void writeLine(OutputStreamWriter writer, TagDataItem lastItem) throws IOException{
		
		writer.write(lastItem.pos.lat+", ");
		writer.write(lastItem.pos.lon+", ");
		
		if (this.outputISOTimeFormat) {
			writer.write(lastItem.getISOTimeFormat());
	
		} else {
			writer.write(lastItem.date);
			
			// add the time if the date and time were separate items in the input
			if (!lastItem.time.isBlank()) {				
				writer.write(", "+lastItem.time);
			}
		}
		writer.write("\n");
	}

	/**
	 * 
	 */
	public void saveFile(FileOutputStream filewriter){
		dayItemCount = 0;
		errhndlr.reset();
		
		try {
			OutputStreamWriter writer = new OutputStreamWriter(filewriter,"UTF8");
			// the writer doesn't seem to add the UTF8 magic numbers...
			// so add them manually
			//byte [] magicUTF8 = {(byte)0x0ef, (byte)0x0bb, (byte)0x0bf};
			//filewriter.write(magicUTF8);

			TagDataItem lastItem = null;
			long timediff = 0;

			boolean lastLineIncluded = false;
			long lastTimeIncluded = 0;

			for (TagDataItem item : rowData){
				errhndlr.nextline();
				
				if (lastItem != null){// can't do anything with first item.		
					
					daySummary(lastItem, item); //reset summary if day changed.
					//daySummary(lastItem, item); //summary if day changed.

					// time between two adjacent items
					timediff = Math.abs(item.getTimeInSeconds() - lastItem.getTimeInSeconds());
					//System.out.println("Time Diff "+timediff);

					if (item.getTimeInSeconds() < lastItem.getTimeInSeconds()){ // out of order data
						//lastTimeIncluded = item.getTimeInSeconds(); // negative
						errhndlr.debug("Time order problem:"+"date "+item.date+" time "+item.time);

					}

					if (timediff >= minTimeDiff && timediff <= maxTimeDiff)	{
						// time between 2 samples is what we are looking for

						if (!lastLineIncluded){
							writeLine(writer, lastItem);
							
							errhndlr.debug("Included last-line "+"(elapsed "+timediff+"):"
									+ "date "+item.date+" time "+item.time
									+", lat" +item.pos.lat+", lng "+item.pos.lon);	
							
							dayItemCount++; //need to count the item as written
						}

						writeLine(writer, item);
						errhndlr.debug("Included "+"(elapsed "+timediff+"):"
								+ "date "+item.date+" time "+item.time
								+", lat" +item.pos.lat+", lng "+item.pos.lon);	
						
						lastLineIncluded = true;
						lastTimeIncluded = item.getTimeInSeconds();
						
						dayValidItem(lastItem, item); //add valid item to summary output if day changed

					} else {
						// time between last two items is too big or small
						long timediffIncluded = Math.abs(item.getTimeInSeconds() - lastTimeIncluded);
						
						// have we reached a sample that satisfies the constraints with last included?
						if (timediffIncluded > minTimeDiff
								&& Math.abs(timediffIncluded) <= maxTimeDiff) {
							
							errhndlr.debug("Included (elapsed "+timediffIncluded+"): "+"date "+item.date+" time "+item.time);
							
							//pick first item from 'busy' periods that is greater than minimum time
							writeLine(writer, item);
							lastLineIncluded = true; //nns added 22/02/2022
							lastTimeIncluded = item.getTimeInSeconds();
							
							dayValidItem(lastItem, item); //add valid item to summary output if day changed
							
						} else { //replaced nns added 22/02/2022 was unconditional
							errhndlr.debug("Excluded (elapsed "+timediffIncluded+"):" +"date "+item.date+" time "+item.time);
							lastLineIncluded = false;
						}
					}
					

				} //if
				else {
					//if last item is null then set time for last item
					lastTimeIncluded = item.getTimeInSeconds();
				}
				
				lastItem = item; 
			} //for	

			//output last item
			if (lastItem != null ) {
				summaryOutput.append(lastItem.getDateTime().toLocalDate().toString()
					+" \t"+dayItemCount+" items within specification"+"\n");
			}
			
			writer.close();

		} catch (IOException e) {
			System.out.println("File write error! - "+ outputFileName);
		}	

	}
}