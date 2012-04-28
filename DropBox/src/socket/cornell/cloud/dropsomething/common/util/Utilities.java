package cornell.cloud.dropsomething.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import socket.IConstants;
import cornell.cloud.dropsomething.common.model.ServerDetails;

/**
 * @author ankitsingh
 *
 */
public class Utilities {
	private static ArrayList<ServerDetails> coList = new ArrayList<ServerDetails>();
	/**
	 * Extracts the messages from the message based on the delimiter
	 * @param message
	 * @return ArrayList of Strings 
	 */
	public static ArrayList<String> getMessage(String message){
		return new ArrayList<String>(Arrays.asList(message.trim().split(IConstants.DELIMITER)));
	}
	/**
	 * @param message
	 * @return
	 */
	public static boolean isValidMessage(String message){
		if(message != null && !message.trim().equals("")){
			return true;
		}
		else{
			return false;
		}
	}
	public static ServerDetails getCoordinator(){
		Random randomGen = new Random();
		int rnd = randomGen.nextInt(coList.size()-1);
		return coList.get(rnd);
	}
}