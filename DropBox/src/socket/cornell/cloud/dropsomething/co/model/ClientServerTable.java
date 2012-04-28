package cornell.cloud.dropsomething.co.model;

import java.util.Hashtable;

public class ClientServerTable {

	Hashtable<String, String> csTable = new Hashtable<String, String>();
	
	
	public String getServer(String client){
		return csTable.get(client);
	}
	public void addClient(String client,String server){
		csTable.put(client, server);
	}
	
	
	
}
