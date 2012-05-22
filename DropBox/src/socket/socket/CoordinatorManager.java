package socket;

import java.util.concurrent.ConcurrentHashMap;

import cornell.cloud.dropsomething.co.db.SimpleDBManager;
import cornell.cloud.dropsomething.co.model.ClientServerTable;
import cornell.cloud.dropsomething.co.model.ServerBlockTable;
import cornell.cloud.dropsomething.co.model.ServerListTable;

public class CoordinatorManager {
	
	
	private static CoordinatorManager _instance  ;
	
	private ClientServerTable csTable = new ClientServerTable();
	
	private ServerBlockTable sbTable = new ServerBlockTable();
	
	private ServerListTable slTable  = new ServerListTable();
	
	private int port = 9756;
	
	private  ConcurrentHashMap<String, String> userMap = new ConcurrentHashMap<String, String>();
	private CoordinatorManager() {
		
		
	}
	public void setPort(int port){
		this.port = port;
	}
	public int getPOrt(){
		return this.port;
	}
	public static CoordinatorManager getInstance(){
		if(_instance == null){
			_instance = new CoordinatorManager();
		}
		return _instance;
	}
	public synchronized void addUser(String uname,String pwd){
		userMap.put(uname, pwd);
		SimpleDBManager.getInstance().addRecord(SimpleDBManager.uTable, uname, pwd);
	}
	public synchronized String getPwd(String uname){
		userMap.putAll(SimpleDBManager.getInstance().getRecords(SimpleDBManager.uTable));
		return userMap.get(uname);
	}
	/**
	 * @return the csTable
	 */
	public ClientServerTable getCsTable() {
		return csTable;
	}
	/**
	 * @param csTable the csTable to set
	 */
	public void setCsTable(ClientServerTable csTable) {
		this.csTable = csTable;
	}
	/**
	 * @return the sbTable
	 */
	public ServerBlockTable getSbTable() {
		return sbTable;
	}
	/**
	 * @param sbTable the sbTable to set
	 */
	public void setSbTable(ServerBlockTable sbTable) {
		this.sbTable = sbTable;
	}
	/**
	 * @return the slTable
	 */
	public ServerListTable getSlTable() {
		return slTable;
	}
	/**
	 * @param slTable the slTable to set
	 */
	public void setSlTable(ServerListTable slTable) {
		this.slTable = slTable;
	}
	

	
	
}
