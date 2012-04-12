package socket;

import java.util.concurrent.ConcurrentHashMap;


public class SessionManager {
	
	public static SessionManager _Instance; 
	//public static DaemonTimer daemonInstance;
	private static ConcurrentHashMap<String,String> ServerList= new ConcurrentHashMap<String, String>(); 
	private static ConcurrentHashMap<String,String> UserList= new ConcurrentHashMap<String, String>(); 
	
	public static SessionManager getInstance()
	{
		if(_Instance == null)
		{
			_Instance = new SessionManager();
		}
		
		return _Instance;
	}
	
	
	public void setServerMap(String Server, String sDet)
	{
		ServerList.put(Server, sDet);
		
	}
	
	public String getServerMap(String Server)
	{
		return ServerList.get(Server);
		
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
	

	public String removeUserFromMap(String User)
	{
		return UserList.remove(User);
		
	}	
}
