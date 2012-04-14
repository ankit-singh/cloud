package socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;


public class RequestHandler extends Thread {
	DataInputStream input; 
	DataOutputStream output; 
	Socket clientSocket; 
	public RequestHandler (Socket aClientSocket) { 
		try { 
					clientSocket = aClientSocket; 
					input = new DataInputStream( clientSocket.getInputStream()); 
					output = new DataOutputStream( clientSocket.getOutputStream()); 
					this.start(); 
		} 
			catch(IOException e) {
			System.out.println("Connection:"+e.getMessage());
			} 
	  } 

	 public void run() { 
			try { 					   
				  //Step 1 read length
				  int nb = input.readInt();
				  System.out.println("Read Length"+ nb);
				  byte[] digit = new byte[nb];
			      System.out.println("Reached Request Handler! Request is as follows: \n");

				  for(int i = 0; i < nb; i++)
					digit[i] = input.readByte(); 
				   
				   String st = new String(digit);
				   
				   System.out.println(st); // Printing Request
				   
				   String packet [];
				   packet = st.split(IConstants.DELIMITER);
				   System.out.println(packet[0]);
				//   System.out.println(file[0].length());
				   System.out.println("Split happened");
	
				   //int j = data.length()/2;
				   //while(data.length()/2 >0)
					 //  data.length()
				   if(packet[0].equals("Register"))
				   {
						SessionManager sessMgr = SessionManager.getInstance();
						String IP = packet[1];
						String Port = packet[2];
						String sDet = packet[3];
						System.out.println("Inside IF");
						if(sessMgr.getServerMap(IP+IConstants.DELIMITER+Port)==null)
						{
							
							sessMgr.setServerMap(IP+IConstants.DELIMITER+Port, sDet);
							System.out.println("/n Controller registered Server IP: " + IP +" @ Port: " +Port);
							System.out.println("/nServer space available for write: " +sessMgr.getServerMap(IP+IConstants.DELIMITER+Port)+" bytes");
						}
						else
						{
							if(sessMgr.getServerMap(IP+Port)!=null)
							{
								System.out.println("Server is already Registered on Controller!");
							}
							else
							{
								System.out.println("Registration unsuccessful");
							}
				
						}
						
				   }else if(packet[0].equals("NEWUSER")){
					   if(SessionManager.getInstance().userNameExists(packet[1])){
						   //TODO User name already taken
						   System.out.println("User name already exists");
					   }else{
						   	String uName = packet[1];
						   	String pwd = packet[2];
						   	SessionManager.getInstance().addUserDetails(uName, pwd);
						   	System.out.println("New User Added ->UserName : "+uName);
						   	System.out.println("New User Added ->Pasword : "+uName);
						   	String reply = SessionManager.getInstance().getPotentialServer();
						   	String s = new String();
						   	if(reply!= null){
						   		 s ="serverip__"+reply;
						   	}else{
						   		s ="fail";
						   	}
						   	System.out.println("RequestHandler.run() Sending reply:"+s);
						   	output.writeInt(s.length());
						   	output.writeBytes(s);
						   	
					   }
					   
				   }
//				   
//				   if(packet[1].equals("Y"))
//				   {
//					   
//				   }
//					   
//				   System.out.println ("receive from : " + 
//							clientSocket.getInetAddress() + ":" +
//							clientSocket.getPort() + " message - " + st);   
//				   FileWriter out = new FileWriter("C:\\Users\\dallasdias\\workspace\\DropBox\\Server2\\" + file[0]+ ".txt");
//				   BufferedWriter bufWriter = new BufferedWriter(out);
//				   bufWriter.append(file[1]);
//				   bufWriter.close();
//					
//					
//				
//				  //Step 1 send length
//				  output.writeInt(st.length());
//				  //Step 2 send length
//				  output.writeBytes(file[1]); // UTF is a string encoding
//			  //  output.writeUTF(data); 
				} 
				catch(EOFException e) {
				System.out.println("EOF:"+e.getMessage()); } 
				catch(IOException e) {
				System.out.println("IO:"+e.getMessage());}  
	   
				finally { 
				  try { 
					  clientSocket.close();
				  }
				  catch (IOException e){/*close failed*/}
				}
	 }

}
