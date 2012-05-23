package cornell.cloud.dropsomething.common.util;

import java.util.ArrayList;
import java.util.Arrays;

import cornell.cloud.dropsomething.common.IConstants;

public class Jarvis {


	private static final int OPCODE = 0;

	private static final int CHAINID = 1;

	ArrayList<String> messages = new ArrayList<String>();
	
	public Jarvis(String message){
		messages = new ArrayList<String>(Arrays.asList(message.trim().split(IConstants.DELIMITER)));
	}
	public int getOPCode(){
		return Integer.parseInt(messages.get(OPCODE));
	}
	public int getChainID(){
		return Integer.parseInt(messages.get(CHAINID));
	}
	private String getClientName(){
		return messages.get(2);
	}
}
