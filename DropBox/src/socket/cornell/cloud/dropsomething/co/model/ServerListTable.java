package cornell.cloud.dropsomething.co.model;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import socket.IConstants;
import socket.MessageService;
import cornell.cloud.dropsomething.co.db.SimpleDBManager;
import cornell.cloud.dropsomething.common.model.ServerDetails;
import cornell.cloud.dropsomething.common.util.Utilities;

public class ServerListTable   {

	ConcurrentHashMap<String, String> serverListTable = new ConcurrentHashMap<String, String>();
	
	
	public synchronized void addServerList(String serverChainId,String serverlist){
		try{
			if(serverChainId.equals("ankit"))
			throw new Exception();
		}catch(Exception e){
			System.out.println("ServerListTable.addServerList() Bazinga");
			e.printStackTrace();
			System.out.println("ServerListTable.addServerList() Bazinga");
			
		}
		serverListTable.put(serverChainId, serverlist);
		SimpleDBManager.getInstance().addRecord(SimpleDBManager.slTable, serverChainId, serverlist);
		printServerListTable();
	}
	public  synchronized String getServerList(String serverChainId){
		simpleRefresh();
		return serverListTable.get(serverChainId);
	}
	public String newServerChain(){
		simpleRefresh();
		Enumeration<String> chainIds = serverListTable.keys();
		String chainId = null;
		while(chainIds.hasMoreElements()){
			 chainId = chainIds.nextElement();
			String listString = serverListTable.get(chainId); 
			if(getSize(listString) < IConstants.CHAIN_SIZE){
				 break;
			}else{
				chainId = null;
			}
		}
		return chainId;
	}
	private void printServerListTable(){
		Enumeration<String> chainIds = serverListTable.keys();
		String chainId = null;
		while(chainIds.hasMoreElements()){
			 chainId = chainIds.nextElement();
			 String listString = serverListTable.get(chainId);
			 System.out.println("Chain ID :"+chainId);
			 System.out.println("ServerList :"+listString);
		}
		
	}
	private int getSize(String listString){
		int length = listString.trim().split(IConstants.DELIMITER).length;
		return (length/2);
	}
	private void simpleRefresh(){
		SimpleDBManager dbManager = SimpleDBManager.getInstance();
		//Get the current table from the DB 
		serverListTable.putAll(dbManager.getRecords(SimpleDBManager.slTable));
	}
	//Do not use refresh now
	private void refresh(){
		SimpleDBManager dbManager = SimpleDBManager.getInstance();
		//Get the current table from the DB 
		serverListTable.putAll(dbManager.getRecords(SimpleDBManager.slTable));
		Enumeration<String> keys = serverListTable.keys();
		while(keys.hasMoreElements()){
			String chainId = keys.nextElement();
			String oldChain = getServerList(chainId);
			String newChain = new String();
			String list[]= oldChain.split(IConstants.DELIMITER);
			for(int i=0; i<list.length; i=i+2)
			{
				String request = String.valueOf(IConstants.PING);
				String response = MessageService.send(request, ServerDetails.create(list[i], Integer.parseInt(list[i+1])));
				System.out.println("received response" +response);
				if(Utilities.isValidMessage(response)){
					if(response.split(IConstants.DELIMITER)[0].equals(IConstants.OK)){
							newChain += IConstants.DELIMITER + list[i] + IConstants.DELIMITER + list[i+1];

					}
				}
				else{
					System.out.println("Server : " +list[i] +"/"+ list[i+1] +" is down, removed from Server chain");
				}
			}
			System.out.println("Replying with newchain = : " + newChain);	
			dbManager.addRecord(SimpleDBManager.slTable, chainId, newChain);
			addServerList(chainId, newChain);
		}
	}
	
}
