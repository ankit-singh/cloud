package cornell.cloud.dropsomething.co.model;

import java.util.Hashtable;

import cornell.cloud.dropsomething.co.db.SimpleDBManager;

public class ClientServerTable {

	Hashtable<String, String> csTable = new Hashtable<String, String>();
	
	public ClientServerTable(){
		simpleRefresh();
	}
	public String getServer(String client){
		simpleRefresh();
		return csTable.get(client);
	}
	public void addClient(String client,String server){
		csTable.put(client, server);
		SimpleDBManager.getInstance().addRecord(SimpleDBManager.csTable, client, server);
	}
	
	private void simpleRefresh(){
		SimpleDBManager dbManager = SimpleDBManager.getInstance();
		//Get the current table from the DB 
		csTable.putAll(dbManager.getRecords(SimpleDBManager.csTable));
	}
	
}
