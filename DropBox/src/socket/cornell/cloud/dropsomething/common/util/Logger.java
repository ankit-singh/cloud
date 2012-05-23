package cornell.cloud.dropsomething.common.util;

import java.sql.Timestamp;

public class Logger {
	public static boolean DEBUG = true;
	
	public static void Log(String message){
		if(DEBUG){
			java.util.Date today = new java.util.Date();
		    System.out.println(new Timestamp(today.getTime())+" : "+message);

			
		}
	}
}
