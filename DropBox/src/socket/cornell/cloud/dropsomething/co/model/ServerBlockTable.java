package cornell.cloud.dropsomething.co.model;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

import socket.IConstants;
import socket.MessageService;
import cornell.cloud.dropsomething.co.db.SimpleDBManager;
import cornell.cloud.dropsomething.common.model.ServerDetails;
import cornell.cloud.dropsomething.common.util.Utilities;

public class ServerBlockTable {
	
	ConcurrentHashMap<String,String> sbTable = new ConcurrentHashMap<String, String>();
	public ServerBlockTable() {
//		refresh();
	}
	public synchronized void addServer(String serverChainID,int blocks){
		SimpleDBManager.getInstance().addRecord(SimpleDBManager.sbTable, serverChainID,String.valueOf(blocks));
		sbTable.put(serverChainID, String.valueOf(blocks));
		
	}
	public int getAvailableBlocks(String serverChainID){
		return Integer.valueOf(sbTable.get(serverChainID));
	}
	public synchronized String getNextServer(){
		//FIX SIMPLE DB
//		refresh();
		int max = 0;
		String returnVal = null;
		Enumeration<String> serverlist = sbTable.keys();
		while(serverlist.hasMoreElements()){
			String server = serverlist.nextElement();
			System.out.println("ServerBlockTable.getNextServer()****** "+sbTable.get(server));
			int val = Integer.valueOf( sbTable.get(server));
			if(max < val){
				max = val;
				returnVal = server;
			}
		}
		if(max == 0){
			returnVal = null;
		}
		return returnVal;
	}
	private synchronized void refresh(){
		SimpleDBManager dbManager = SimpleDBManager.getInstance();
		sbTable.putAll(dbManager.getRecords(SimpleDBManager.sbTable));
	}

}
