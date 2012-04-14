package socket;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;


public class SessionManager {
	
	public static SessionManager _Instance; 
	//public static DaemonTimer daemonInstance;
	private static ConcurrentHashMap<String,String> ServerList= new ConcurrentHashMap<String, String>(); 
	
	private static ConcurrentHashMap<String,String> userDetails = new ConcurrentHashMap<String, String>();
	
	
	private static ConcurrentHashMap<String,String> UserList= new ConcurrentHashMap<String, String>(); 
	
	private static TreeMap<Long, String> serverMap = new TreeMap<Long, String>();
	
	public static SessionManager getInstance()
	{
		if(_Instance == null)
		{
			_Instance = new SessionManager();
		}
		
		return _Instance;
	}
	
	public String getUserDetails(String key){
		return userDetails.get(key);
	}
	
	public void addUserDetails(String userName,String pwd){
		userDetails.put(userName, pwd);
	}
	
	public boolean userNameExists(String key){
		return (getUserDetails(key) != null);
	}
	public void setServerMap(String Server, String sDet)
	{
		ServerList.put(Server, sDet);
		serverMap.put(Long.valueOf(sDet), Server);
		
	}
	
	public String getServerMap(String Server)
	{
		return ServerList.get(Server);
//		return serverMap.get
		
	}
	

	public String removeServerFromMap(String Server)
	{
		return ServerList.remove(Server);
		
	}
	
	
	
	public void setUserMap(String User, String Server)
	{
		UserList.put(User, Server);
		
	}
	
	public String getUserMap(String User)
	{
		return UserList.get(User);
		
	}
	public String getPotentialServer(){
		Map.Entry<Long, String> mapEntry = serverMap.lastEntry();
		if(mapEntry != null){
		return mapEntry.getValue();
		}else{
			return null;
		}
	}

	public String removeUserFromMap(String User)
	{
		return UserList.remove(User);
		
	}	
}
