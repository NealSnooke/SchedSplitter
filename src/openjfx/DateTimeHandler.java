package openjfx;
import java.time.*;
import java.time.format.*;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.scene.control.TextField;

public class DateTimeHandler {
	// the error style for text fields (.text-input:error)
	private final PseudoClass errorClass = PseudoClass.getPseudoClass("error");

	//the parent that contains the fxml classes
	private SettingsTabController controller;

	private String[] options = {
			"d/M/y H:m:s xxx", 
			"M/d/y H:m:s xxx", 
			"d.M.y H:m:s xxx", 
			"d-M-y H:m:s xxx", 
			"y-M-d H:m:s xxx", 
			"H:m:s xxx d/M/y",
			"enter custom format"
	};

	/**
	 * transfer drop down date time to its text field
	 * @param parent
	 */
	public DateTimeHandler(SettingsTabController parent) {
		controller = parent;

		//controller.dateTimeFormat.textProperty().bind(
		//		controller.dateSelector.getSelectionModel().selectedItemProperty());

		controller.dateSelector.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<String>() {
					public void changed(ObservableValue<? extends String> selected, String oldval, String newval) {				
						//System.out.println("changed ");

						controller.dateTimeFormat.textProperty().set(
								options[controller.dateSelector.getSelectionModel().getSelectedIndex()]);
					}
				}
				);

		//https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html

		//String input = "Mon Mar 14 16:02:37 GMT 2011";
		//DateTimeFormatter f = DateTimeFormatter.ofPattern( "E MMM d HH:mm:ss z uuuu" );

		//String input = "14/03/2021 16:02:37 GMT";
		//DateTimeFormatter f = DateTimeFormatter.ofPattern( "d/M/y HH:mm:ss z" );

		//String input = "16:02:37 +00 14.03.2021 ";
		//DateTimeFormatter f = DateTimeFormatter.ofPattern( "H:m:s x d.M.y " );

		//String input = "16:02:37 +00 14.03.2021 ";
		//DateTimeFormatter f = DateTimeFormatter.ofPattern( "HH:mm:ss x d.M.y " );

		//ZonedDateTime zdt = ZonedDateTime.parse( input , f );
		/*
		OffsetDateTime zdt = OffsetDateTime.parse( input , f );

		System.out.println( "input: " + input );
		System.out.println( "zdt: " + zdt );
		//System.out.println( "ym: " + ym );

		System.out.println("year "+zdt.getYear());
		System.out.println("year "+zdt.getMonth());
		System.out.println("day of month "+zdt.getDayOfMonth());

		System.out.println("hour "+zdt.getHour());
		System.out.println("min "+zdt.getMinute());
		System.out.println("sec "+zdt.getSecond());
		 */
	}

	/**
	 * check the date end time fields
	 * allow empty date and time
	 * if the time is empty then insert 0:0:0 once a valid date is entered
	 * if the date or time is badly formatted then apply error style
	 * if the time is OK but no date then apply error style to date. 
	 * @param tf
	 * @param oldValue
	 * @param newValue
	 */
	public void checkStartDate(TextField tf, String oldValue, String newValue) {
		//System.out.println("Check start date: "+tf.getText()+ " +00");

		checkDateTime(controller.startDate, controller.startTime, "00:00:00");

	}

	/**
	 * check the date field 
	 * @param tf
	 * @param oldValue
	 * @param newValue
	 */
	public void checkEndDate(TextField tf, String oldValue, String newValue) {
		//System.out.println("Check end date");

		checkDateTime(controller.endDate, controller.endTime, "23:59:59");
	}

	/**
	 * check the date end time fields
	 * allow empty date and time
	 * if the time is empty then insert 0:0:0 once a valid date is entered
	 * if the date or time is badly formatted then apply error style
	 * if the time is OK but no date then apply error style to date. 
	 * @param tf
	 * @param oldValue
	 * @param newValue
	 */
	public void checkDateTime(TextField date, TextField time, String defaultTime) {

		// reset textfields style to non error
		date.pseudoClassStateChanged(errorClass, false); //text field error style
		time.pseudoClassStateChanged(errorClass, false); //text field error style

		DateTimeFormatter dtformat = DateTimeFormatter.ofPattern("H:m:s x d/M/y");

		//check the date
		boolean dateIsValid = true;

		if (!date.getText().isBlank()) {

			try {
				OffsetDateTime zdt = OffsetDateTime.parse( 
						"0:0:0 +00 "+date.getText(), dtformat );	
				// force 4 digit dates 
				if (zdt.getYear() < 1000) {
					date.pseudoClassStateChanged(errorClass, true);
				}

			} catch (DateTimeParseException e) {		
				date.pseudoClassStateChanged(errorClass, true); //text field error style
				dateIsValid = false;
				//System.out.println("date format error"+e);
			}	
		} else {
			// if there is no date but a time then signal the date as an error
			if (!time.getText().isBlank())
				date.pseudoClassStateChanged(errorClass, true);
		}

		//check the time
		if (!time.getText().isBlank()) { 

			try {
				OffsetDateTime.parse( 
						time.getText()+" +00 1/1/2000", dtformat );	

			} catch (DateTimeParseException e) {		
				time.pseudoClassStateChanged(errorClass, true); //text field error style
				//System.out.println("date format error"+e);
			}	
		} else {
			//fill in default start of day for a real valid date
			if (dateIsValid && !date.getText().isBlank())
				time.setText(defaultTime);
		}

	}

}
