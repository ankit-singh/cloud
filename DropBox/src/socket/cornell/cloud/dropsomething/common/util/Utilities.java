package cornell.cloud.dropsomething.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import socket.IConstants;
import socket.MessageService;
import cornell.cloud.dropsomething.common.model.ServerDetails;

/**
 * @author ankitsingh
 *
 */
public class Utilities {
	private static ArrayList<ServerDetails> coList = new ArrayList<ServerDetails>();
	static{
		coList.add(ServerDetails.create(IConstants.COORD_IP, 9876));
		coList.add(ServerDetails.create(IConstants.COORD_IP, 8765));
	}
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
	public static String getFormattedDir(String dir){
		dir = dir.trim();
		char seperator = IConstants.DIRSEP;
		if(!dir.equals("") && dir.charAt(dir.length() -1) == seperator){
			dir = dir.substring(0, dir.length()-1);
		}
		return dir;
	}
	public static String getFormattedFilePath(String filePath){
		filePath = filePath.trim();
		char seperator = IConstants.DIRSEP;
		if(!filePath.equals("")&&filePath.charAt(0) != seperator){
			filePath = seperator+filePath;
		}
		return filePath;
	}
	private  static boolean ping(ServerDetails dest) {
		String pingServer = MessageService.send(""+ IConstants.PING,dest);
		if (pingServer.equals("" + IConstants.OK)) {
			return true;
		} else {
			return false;
		}
	}
	public static ServerDetails getCoordinator(){
		boolean alive = false;
		Iterator<ServerDetails> itr = coList.iterator();
		ServerDetails sd = coList.get(1);
		while(!alive && itr.hasNext()){
			 sd = itr.next();
			if(ping(sd)){
				alive = true;
				break;
			}
		}
		return sd;
		
	}
}
