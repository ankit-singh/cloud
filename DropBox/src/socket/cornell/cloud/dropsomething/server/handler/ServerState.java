package cornell.cloud.dropsomething.server.handler;

import java.util.ArrayList;

import cornell.cloud.dropsomething.common.model.ServerDetails;

public class ServerState {

	public  static final int STABLE = 101;
	
	public static final int INIT = 102;
	
	public static final int READ = 103;
	public static final int WRITE = 104;
	
	private static int chainId = -1;
	
	private static int itr = -1;
	
	private static ArrayList<ServerDetails> serverList =  new ArrayList<ServerDetails>();
	
	private static int state = -1;
	
	private ServerState(){
		
	}
	/**
	 * @return the state
	 */
	public static int getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public static void setState(int state) {
		ServerState.state = state;
	}

	/**
	 * @return the serverList
	 */
	public static ArrayList<ServerDetails> getServerList() {
		return serverList;
	}

	/**
	 * @param serverList the serverList to set
	 */
	public static void setServerList(ArrayList<ServerDetails> serverList) {
		ServerState.serverList = serverList;
	}

	/**
	 * @return the chainId
	 */
	public static int getChainId() {
		return chainId;
	}

	/**
	 * @param chainId the chainId to set
	 */
	public static void setChainId(int chainId) {
		ServerState.chainId = chainId;
	}

	/**
	 * @return the itr
	 */
	public static int getItr() {
		return itr;
	}

	/**
	 * @param itr the itr to set
	 */
	public static void setItr(int itr) {
		ServerState.itr = itr;
	}
}
