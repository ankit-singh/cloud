package socket;

import java.net.*; 
import java.util.Scanner;
import java.io.*; 
import java.lang.Object;
import org.apache.commons.io.FileUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class TCPClient{ 
	public static void main (String args[])  throws IOException
	{
		
		Socket s = null; 
		try{ 
				Scanner scan = new Scanner(System.in);
				System.out.print("\nNew User?: Reply (Y/N): ");
				String reply = scan.next();
				
				System.out.println("About to send packet Debug1");
				
        	
				s = new Socket(IConstants.COORD_IP, IConstants.COOR_PORT);
		        DataOutputStream output = new DataOutputStream( s.getOutputStream());
		        DataInputStream input = new DataInputStream(s.getInputStream());
		    	System.out.println("About to send packet Debug3");
		        
		        if(reply.equals("Y"))
		        {
		        	
		        	System.out.print("\nCreate a user name: ");
					String user = scan.next();
					System.out.print("\nCreate a password: ");
					String pwd = scan.next();
					System.out.print("\nRe-enter password: ");
					String pwd2 = scan.next();
					
					while(!pwd.equals(pwd2))
					{   System.out.println("\nPasswords dont match, please re-enter information");
						System.out.print("\nCreate a password: ");
						pwd = scan.next();
						System.out.print("\nRe-enter password: ");
						pwd2 = scan.next();
					}
					
					
//					String filePath;
//					System.out.print("\nCreate Folder Path?: ");
//					filePath = scan.next();
		        	
		        	String packet = IConstants.NEW_CLIENT+IConstants.DELIMITER+user+IConstants.DELIMITER+pwd;
			        output.writeInt(packet.length());
			        output.writeBytes(packet);
			        System.out.println("Registration Packet Sent! Waiting for co-ordinator to reply with available servers");
			      //Step 1 read length
					int nb = input.readInt();// read the number of bytes (length) eg. 4 bytes
					System.out.println("Read Length"+ nb);
					byte[] digit = new byte[nb];// digit will hold the content of the bytes (length) later. 4 bytes
					  
					for(int i = 0; i < nb; i++)
					digit[i] = input.readByte(); // reads the bytes. eg. reads the 4 bytes one by one.
					   
					String st = new String(digit);
					System.out.println("Co-ordinator replied with: "+st);
					String delimiter =IConstants.DELIMITER;
					String tcpreply [] = st.trim().split(delimiter);
			        PushToServer(null, tcpreply[1], tcpreply[2]);
			        
		        }


		        //*******************************************************
			
				
				  
//			  //Step 1 read length
//			  int nb = input.readInt();
//			  byte[] digit = new byte[nb];
//			  //Step 2 read byte
//			  for(int i = 0; i < nb; i++)
//			  digit[i] = input.readByte();
//		  
//			  String st = new String(digit);
//			  System.out.println("Received: "+ st); 
//			  //s.close();
//		  
			    
			  
		}
	//	catch (UnknownHostException e){ 
		//	System.out.println("Sock:"+e.getMessage());}
		catch (EOFException e){
			System.out.println("EOF:"+e.getMessage()); }
		catch (IOException e){
			System.out.println("IO:"+e.getMessage());} 
		finally {
			  if(s!=null) 
				  try {s.close();
				  } 
				  catch (IOException e) {/*close failed*/}
		}
}
	
		
	
	public static void PushToServer(String filePath, String ServerIP, String Port) throws IOException
	{
		Socket spush = null;
		try{
		
		File folder = new File("C:\\Users\\dallasdias\\workspace\\DropBox\\Client1");
		File[] listOfFiles = folder.listFiles();
		System.out.println(listOfFiles.length);
		
		//String [] Names = folder.list();
		//System.out.println(Names[0]);
	  
		for (int j = 0; j < listOfFiles.length; j++) {
			if (listOfFiles[j].isFile()) {
				
		        String data = listOfFiles[j].getName() + IConstants.DELIMITER + FileUtils.readFileToString(listOfFiles[j]);
		
				
				System.out.println("File content: " + data);
		 
	    
	  
		        spush = new Socket(ServerIP, Integer.parseInt(Port)); 
		        DataInputStream input = new DataInputStream( spush.getInputStream()); 
		        DataOutputStream output = new DataOutputStream( spush.getOutputStream()); 
  	  
		        System.out.println("Client now sending to Server");
     
		        //Step 1 send length
		        System.out.println("Length"+ data.length());
		        output.writeInt(data.length());
		        //Step 2 send length
		        System.out.println("Writing.......");
		        output.writeBytes(data); // UTF is a string encoding
			}
		}
			
			}catch (EOFException e){
				System.out.println("EOF:"+e.getMessage()); }
			catch (IOException e){
				System.out.println("IO:"+e.getMessage());} 
			finally {
				  if(spush!=null) 
					  try {spush.close();
					  } 
					  catch (IOException e) {/*close failed*/}
			}
	}
}
