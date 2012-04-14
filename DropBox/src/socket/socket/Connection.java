package socket;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
	
public class Connection extends Thread { 
		DataInputStream input; 
		DataOutputStream output; 
		Socket clientSocket; 
		
		public Connection (Socket aClientSocket) { 
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
				  int nb = input.readInt();// read the number of bytes (length) eg. 4 bytes
				  System.out.println("Read Length"+ nb);
				  byte[] digit = new byte[nb];// digit will hold the content of the bytes (length) later. 4 bytes
				  //Step 2 read byte
				   System.out.println("Writing.......");

				  for(int i = 0; i < nb; i++)
					digit[i] = input.readByte(); // reads the bytes. eg. reads the 4 bytes one by one.
				   
				   String st = new String(digit);
				   System.out.println(st);
				   String delimiter =IConstants.DELIMITER;
				   String file [] = new String [100];
				   file = st.split(delimiter);
				//   System.out.println(file[0].length());
	
				   //int j = data.length()/2;
				   //while(data.length()/2 >0)
					 //  data.length()
					   
				   System.out.println ("receive from : " + 
							clientSocket.getInetAddress() + ":" +
							clientSocket.getPort() + " message - " + st);   
				   FileWriter out = new FileWriter("C:\\Users\\dallasdias\\workspace\\DropBox\\Server2\\" + file[0]+ ".txt");
				   BufferedWriter bufWriter = new BufferedWriter(out);
				   bufWriter.append(file[1]);
				   bufWriter.close();
					
					
				
				  //Step 1 send length
				  output.writeInt(st.length());
				  //Step 2 send length
				  output.writeBytes(file[1]); // UTF is a string encoding
			  //  output.writeUTF(data); 
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


