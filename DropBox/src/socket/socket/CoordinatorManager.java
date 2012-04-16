package socket;

import java.util.concurrent.ConcurrentHashMap;

public class CoordinatorManager {
	private static CoordinatorManager _instance  ;
	
	private static FileVersionTable fvTable = new FileVersionTable();
	
	private static FileServerTable fsTable = new FileServerTable();
	
	private static ServerTable sTable = new ServerTable();
	
	private static ClientFileTable cfTable = new ClientFileTable();
	
	private static ConcurrentHashMap<String, String> userMap = new ConcurrentHashMap<String, String>();
	
	private CoordinatorManager() {
		
	}
	public ClientFileTable getClientFileTable(){
		return cfTable;
	}
	public static CoordinatorManager getInstance(){
		if(_instance == null){
			_instance = new CoordinatorManager();
		}
		return _instance;
	}
	public FileVersionTable getFileVersionTable(){
		return fvTable;
	}
	public FileServerTable getFileServerTable(){
		return fsTable;
	}
		
	public ServerTable getServerTable(){
		return sTable;
	}
	public void addUser(String uname,String pwd){
		userMap.put(uname, pwd);
	}
	public String getPwd(String uname){
		return userMap.get(uname);
	}
	
	
}
