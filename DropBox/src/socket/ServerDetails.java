package socket;

import java.io.File;
import java.net.ServerSocket;

/**
 * Captures the properties of the server such as available disk space, 
 */

public class ServerDetails {
	private long TotalSpace;
	private long FreeSpace;
	String MyIP;
	String MyPort;
	
	public ServerDetails(ServerSocket s)
	{
		this.MyIP = s.getInetAddress() + "_" + s.getLocalPort();
		this.MyPort =
		File file = new File("C:");
    	//long usableSpace = file.getUsableSpace(); ///unallocated / free disk space in bytes.
    	this.FreeSpace = file.getFreeSpace(); //unallocated / free disk space in bytes.
    	this.TotalSpace = file.getTotalSpace();  //total disk space in bytes.
		
	}
	
	public long getAvailableSpace()
	{
    	return this.FreeSpace;

	}
	
	
	public long getTotalSpace()
	{
    	return this.TotalSpace;

	}
	
	public String getMyIPP()
	{
		return MyIPP;
	}
	
}
