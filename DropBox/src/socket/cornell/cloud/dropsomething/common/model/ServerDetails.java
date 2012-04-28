package cornell.cloud.dropsomething.common.model;

import socket.IConstants;
import cornell.cloud.dropsomething.common.util.Utilities;

/**
 * Captures the properties of the server such as available disk space, 
 */

public class ServerDetails {
	
	private String ip;
	private int port;
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	@Override
	public String toString() {
		return ip+IConstants.DELIMITER+port;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + port;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServerDetails other = (ServerDetails) obj;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (port != other.port)
			return false;
		return true;
	}
	
	public static ServerDetails create(String ip,int port){
		ServerDetails serverDetails = new ServerDetails();
		serverDetails.setIp(ip);
		serverDetails.setPort(port);
		return serverDetails;
	}
	public static ServerDetails coOrdinator(){
		return Utilities.getCoordinator();
	}
	
	
	
}
