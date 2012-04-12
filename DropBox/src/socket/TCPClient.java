package socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class TCPClient{ 
	public static void main (String args[])  throws IOException
	{
		
		Socket s = null; 
		try{ 
				int serverPort = 7778;
				String ip = "localhost";
				
				//String data = "Hello, How are you?. Whats up !!"; 
			
				File folder = new File("C:/Users/dallasdias/workspace/DropBox/Client1");
				File[] listOfFiles = folder.listFiles();
				System.out.println(listOfFiles.length);
				
				//String [] Names = folder.list();
				//System.out.println(Names[0]);
			  
				for (int j = 0; j < listOfFiles.length; j++) {
					if (listOfFiles[j].isFile()) {
					
						//System.out.println(Names[0]);
						//String data = "Hello! ujhuhu";
						
				        String data = listOfFiles[j].getName() + "___" + FileUtils.readFileToString(listOfFiles[j]);
				
						
						System.out.println("File content: " + data);
				 
			    
			  
				        s = new Socket(ip, serverPort); 
				        DataInputStream input = new DataInputStream( s.getInputStream()); 
				        DataOutputStream output = new DataOutputStream( s.getOutputStream()); 
		  	  
				        System.out.println("Client now sending to Server");
		     
				        //Step 1 send length
				        System.out.println("Length"+ data.length());
				        output.writeInt(data.length());
				        //Step 2 send length
				        System.out.println("Writing.......");
				        output.writeBytes(data); // UTF is a string encoding
				  
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
			  }
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
}

