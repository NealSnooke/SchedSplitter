/**
 * First created Neal Snooke 26/07/2018
 * 
 * simple public data container
 */
package tagDataProcessor;

import java.time.OffsetDateTime;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.TimeZone;


public class TagDataItem {

	private OffsetDateTime zdt = null; // the real date time object.

	protected GpsPosition pos;

	protected String date;
	protected String time;

	//public long timeSecs;

	public class TagItemInvalidException extends Exception { 
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public TagItemInvalidException(String errorMessage) {
			super(errorMessage);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public OffsetDateTime getDateTime() {
		return zdt;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getISOTimeFormat(){
		return zdt.toString();
	}

	/**
	 * 
	 * @param lat
	 * @param lon
	 * @param date
	 * @param time
	 * @param dateTimeFormat
	 * @param errhndlr
	 * 
	 * @throws DateTimeParseException - if date/time strings cannot be converted to a real date.
	 */
	public TagDataItem (String lat, String lon, 
			String date, String time, String timeZone, String dateTimeFormat,
			ErrorHandler errhndlr) throws TagItemInvalidException{

		this.date = date;
		this.time = time;

		try {

			pos = new GpsPosition(Double.parseDouble(lat), Double.parseDouble(lon));

		} catch (Exception e) {

			errhndlr.error( "Unable to parse '"+lat+"' or '"+lon+"' as double value");

			throw new TagItemInvalidException("Unable to create Tag Data Item from position specified.");

		}

		String dateTime;

		if (time == null) {
			//we have date and time as one field
			dateTime = date;
			// the date field will include the time in a combined situation so just set to empty string
			this.time = "";
		} else {
			// join date and time into a single item before parsing
			dateTime = date+" "+time;
		}

		String inputDateTime = dateTime;
		
		if (!timeZone.isBlank())
			dateTime = dateTime.concat(" "+timeZone); //will be "+00" or "" if none.
		
		errhndlr.debug("DateTime string: '"+dateTime+"'");

		//DateTimeFormatter f = DateTimeFormatter.ofPattern( "H:m:s x d.M.y " );
		//DateTimeFormatter dtformat = DateTimeFormatter.ofPattern(dateTimeFormat+" x");//now included in the format string from GUI
		DateTimeFormatter dtformat = null;
		try {
			dtformat = DateTimeFormatter.ofPattern(dateTimeFormat);
		} catch (IllegalArgumentException e) {
			errhndlr.error("Illegal date/time format: '"+dateTimeFormat+"'");
			throw new TagItemInvalidException("Illegal date/time format");
		}
		//String input = "16:02:37 14.03.2021 +00";
		//DateTimeFormatter f = DateTimeFormatter.ofPattern( "HH:mm:ss d.M.y x" );

		try {

			//ZonedDateTime zdt1 = ZonedDateTime.parse( dateTime , dtformat );
			zdt = OffsetDateTime.parse( dateTime , dtformat );
			
			// adjust the time by the amount of the offset
			// because the specified time in the file is a local time
			// Revision: actually OffsetDateTime adjusts the "Z" (epoch) time by the specified offset when it is created
			// so the UTC time is correct.
			//ZoneOffset zo = zdt.getOffset();
			/*
			long secsoffset = zo.getTotalSeconds();			
			if (secsoffset<0)
				zdt = zdt.minusSeconds(secsoffset);
			else
				zdt = zdt.plusSeconds(secsoffset);			
			*/
			errhndlr.debug("Date time string: '"+inputDateTime+" parsed into '"+zdt+"' UTC "+zdt.toEpochSecond() );
			//+zdt.atZoneSameInstant(TimeZone.getTimeZone("UTC")));

		} catch (DateTimeParseException e) {

			errhndlr.error( "Unable to parse '"+dateTime+"' using format ISO 6601 format specifiers: '"+dtformat+"'");
			throw new TagItemInvalidException("Unable to create Tag Data Item from date/time specified.");

		}

		/*
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

		/*
		Calendar c = Calendar.getInstance();

		String[] dt = date.split("/");
		String[] tp = time.split(":");
		 */
		/*
		for (String s : dt){	
			System.out.println(s+" ");
		}
		for (String s : tp){	
			System.out.println(s+" ");
		}*/

		//yy mm date hh, mm, ss
		/*c.set(Integer.parseInt(dt[2]),
				Integer.parseInt(dt[1])-1,
				Integer.parseInt(dt[0]),
				Integer.parseInt(tp[0]),
				Integer.parseInt(tp[1]),
				Integer.parseInt(tp[2])); //note month is 0-11 0 = jan
		 */
		//System.out.println(c.getTime());

		//timeSecs = c.getTimeInMillis()/1000;

		//System.out.println(timeSecs);
		/*
				Integer.parseInt(tp[0])*3600
				+ Integer.parseInt(tp[1])*60 
				+Integer.parseInt(tp[2]);
		 */
	}

	/**
	 * 
	 */
	public long getTimeInSeconds() {
		return zdt.toEpochSecond();
	}

	/**
	 * 
	 */
	public String toString(){
		StringBuffer sb = new StringBuffer();

		if (pos != null)
			sb.append(pos.lat+" "+pos.lon+" "+date+" epoch time "+getTimeInSeconds());
		else 
			sb.append("Null position!");

		return sb.toString();
	}

}
