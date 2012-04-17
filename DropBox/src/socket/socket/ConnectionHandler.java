package socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionHandler extends Thread { 
	DataInputStream reqStream; 
	DataOutputStream respStream; 
	Socket clientSocket; 
	final static int BUFFER_SIZE = 65536;
	String dir = "/users/ankitsingh/desktop/drop/sdef/";
	public ConnectionHandler (Socket aClientSocket,String dir) { 
		try { 
			this.dir = dir;
			clientSocket = aClientSocket; 
			reqStream = new DataInputStream( clientSocket.getInputStream()); 
			respStream = new DataOutputStream( clientSocket.getOutputStream()); 
			this.start(); 
		} 
		catch(IOException e) {
			System.out.println("Connection:"+e.getMessage());
		} 
	} 
	public void reciveFile(String request){
		try {
			long startTime = System.currentTimeMillis();
			byte[] buffer = new byte[BUFFER_SIZE];
			File f = new File(dir+"/"+request.split("__")[1]);
			respStream.writeInt(IConstants.OK);
			FileOutputStream fileStream  = new FileOutputStream(f);
			int read;
			int totalRead = 0;
			while ((read = reqStream.read(buffer)) != -1) {
				fileStream.write(buffer,0,read);
				totalRead += read;
			}
			long endTime = System.currentTimeMillis();
			fileStream.close();
			System.out.println(totalRead + " bytes read in " + (endTime - startTime) + " ms.");
		} catch (IOException e) {
		}

	}
	public void sendFile(String request){
		System.out.println("ConnectionHandler.sendFile()"+request);
		try {
			String fileName = request.split(IConstants.DELIMITER)[1];
			System.out.println("ConnectionHandler.sendFile() fileName :"+fileName);
			FileInputStream fileInputStream = new FileInputStream(dir+"/"+fileName);
			long startTime = System.currentTimeMillis();
			byte[] buffer = new byte[BUFFER_SIZE];
			int read;
			int readTotal = 0;
			while ((read = fileInputStream.read(buffer)) != -1) {
				respStream.write(buffer, 0, read);
				readTotal += read;
				System.out
				.println("UploadED BYTES :"+readTotal);
			}
			respStream.close();
			fileInputStream.close();
			long endTime = System.currentTimeMillis();
			System.out.println(readTotal + " bytes written in " + (endTime - startTime) + " ms.");
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("ConnectionHandler.sendFile() Something went wrong :"+request);
		} catch (FileNotFoundException e) {
			System.out.println("ConnectionHandler.sendFile() FNE");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("ConnectionHandler.sendFile() IO");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		//Close the sockets
		int size;
		try {
			size = reqStream.readInt();
			byte[] digit = new byte[size];
			for(int i = 0; i < size; i++){
				digit[i] = reqStream.readByte();
			}
			String request = new String(digit);
			int opcode = Integer.parseInt(request.split(IConstants.DELIMITER)[0]);

			if(opcode == IConstants.PUSH){
				reciveFile(request);
			}else if(opcode == IConstants.PULL){
				sendFile(request);
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if(clientSocket != null){
				try {
					clientSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
