package socket;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class TCPServer {

	
	  public static void main (String args[]) throws IOException 
	  { 
		try{    Scanner scan = new Scanner(System.in);
				System.out.print("Server to start on Port?: ");
				String port = scan.next();
				System.out.println("About to send packet Debug1");
				
		    	System.out.println("About to send packet Debug3");
		    	
		        File file = new File("C:");	
		        System.out.println("TCPServer.main() Enter Server Size :");
		        String size = scan.next();
		    	String FreeSpace = String.valueOf(size);
		    	System.out.println("TCPServer.main() Enter Server Dir :");
		        String dir = scan.next();
		    	System.out.println("About to send packet Debug4");
		    	
				Socket s = new Socket(IConstants.COORD_IP, IConstants.COOR_PORT);//USE TO AUTOMATE
		        DataOutputStream output = new DataOutputStream( s.getOutputStream()); 
		        
		        String packet = IConstants.NEW_SERVER+IConstants.DELIMITER+InetAddress.getLocalHost().getHostAddress().toString()+IConstants.DELIMITER+port+IConstants.DELIMITER+FreeSpace;
		        System.out.println("TCPServer.main() ->"+packet);
		        output.writeInt(packet.length());
		        output.writeBytes(packet);
		        System.out.println("Registration Packet Sent! Initiating server socket to listen");
		        
		        ServerSocket listenSocket = new ServerSocket(Integer.valueOf(port));
				//DONE Please send server details to co-ordinator and code the co-ordinator to register your server and start accepting requests. 
		  
				System.out.println("Server start listening... ... ...");
			
				while(true) { 
					System.out.println("\n Now listening @ Port : " + listenSocket.getLocalPort());
					Socket clientSocket = listenSocket.accept(); 
					new ConnectionHandler(clientSocket,dir); 
				} 
		} 
		catch(IOException e) {
			System.out.println("Listen :"+e.getMessage());} 
	  }
	}

	




