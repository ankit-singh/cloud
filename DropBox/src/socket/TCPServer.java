package socket;

import java.net.*; 
import java.util.Scanner;
import java.io.*; 
import java.net.Socket;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.Object;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;

public class TCPServer {


	  public static void main (String args[]) throws IOException 
	  { 
		try{    Scanner scan = new Scanner(System.in);
				System.out.print("Server to start on Port?: ");
				String serverPort = scan.next();
				System.out.println("About to send packet Debug1");
				
//				System.out.print("Co-ordinator @ IP?: ");//USE TO AUTOMATE
//				String IPAddress = scan.next();//USE TO AUTOMATE
//				System.out.println("Co-ordinator @ Port?: ");//USE TO AUTOMATE
//				String CPort = scan.next();//USE TO AUTOMATE
				String IPLocal = "127.0.0.1";//HARDCODED
				String CPort = "5554";//HARDCODED
				System.out.println("About to send packet Debug2");
		    	System.out.println("About to send packet Debug3");
		    	
		        File file = new File("C:");	
		    	//long usableSpace = file.getUsableSpace(); //unallocated / free disk space in bytes.
		    	//long FreeSpace = file.getFreeSpace(); 	//unallocated / free disk space in bytes.
		    	String FreeSpace = String.valueOf(file.getFreeSpace());
		    	System.out.println("About to send packet Debug4");
		    	
//				Socket s = new Socket(IPAddress, Integer.parseInt(CPort));//USE TO AUTOMATE
				Socket s = new Socket(IPLocal,Integer.parseInt(CPort));//HARDCODED
		        DataOutputStream output = new DataOutputStream( s.getOutputStream()); 
		        String packet = "Register__"+"127.0.0.1"+"__"+serverPort+"__"+FreeSpace;
		        output.writeInt(packet.length());
		        output.writeBytes(packet);
		        System.out.println("Registration Packet Sent! Initiating server socket to listen");
								
		        ServerSocket listenSocket = new ServerSocket(Integer.parseInt(serverPort));
				//DONE Please send server details to co-ordinator and code the co-ordinator to register your server and start accepting requests. 
		  
				System.out.println("Server start listening... ... ...");
			
				while(true) { 
					System.out.println("\n Now listening @ Port : " + listenSocket.getLocalPort());
					Socket clientSocket = listenSocket.accept(); 
					Connection c = new Connection(clientSocket); 
				} 
		} 
		catch(IOException e) {
			System.out.println("Listen :"+e.getMessage());} 
	  }
	}

	




