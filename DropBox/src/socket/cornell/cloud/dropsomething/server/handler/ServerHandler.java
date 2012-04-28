package cornell.cloud.dropsomething.server.handler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import cornell.cloud.dropsomething.common.util.Utilities;

import socket.IConstants;
import socket.Logger;

public class ServerHandler extends Thread { 
	DataInputStream reqStream; 
	DataOutputStream respStream; 
	Socket clientSocket; 
	final static int BUFFER_SIZE = 65536;
	String dir = "/users/ankitsingh/desktop/drop/sdef/";
	public ServerHandler (Socket aClientSocket,String dir) { 
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
	private boolean fileExits(String filePath){
		return false;//TODO
	}
	private boolean isSameFile(String filePath, String md5){
		return false;
	}
	private File getFile(String filePath,String clieString){
		new File(dir+"/"+clieString).mkdir();
		File f = new File(filePath);
		return f;
		
	}
	public void reciveFile(String request){
		try {
			String clientDir = request.split(IConstants.DELIMITER)[1];
			String filePath = dir+"/"+clientDir+"/"+request.split(IConstants.DELIMITER)[2];
			String md5 = request.split(IConstants.DELIMITER)[3];
			if (!fileExits(filePath) && !isSameFile(filePath, md5)) {
				long startTime = System.currentTimeMillis();
				byte[] buffer = new byte[BUFFER_SIZE];
				respStream.writeInt(IConstants.OK);
				FileOutputStream fileStream = new FileOutputStream(getFile(
						filePath, clientDir));
				int read;
				int totalRead = 0;
				while ((read = reqStream.read(buffer)) != -1) {
					fileStream.write(buffer, 0, read);
					totalRead += read;
				}
				long endTime = System.currentTimeMillis();
				System.out.println(totalRead + " bytes read in "
						+ (endTime - startTime) + " ms.");
				fileStream.close();
			}
		} catch (IOException e) {
		}

	}
	public String getMD5(String filePath){
		return null;//TODO
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
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("ConnectionHandler.sendFile() IO");
			e.printStackTrace();
		}
	}
	public void sendFileList(String request){
		System.out.println("ConnectionHandler.sendFileList()"+request);
		try {
			ArrayList<String> response = Utilities.getMessage(request);
			String clientName = response.get(0);
			File folder = new File(dir+"/"+clientName);
			File[] fileList = folder.listFiles();
			Logger.Log("Send file No of Files"+fileList.length);
			String fileListString = new String(IConstants.FILELIST+IConstants.DELIMITER);
			for (int j = 0; j < fileList.length; j++) {
				if (fileList[j].isFile() && !fileList[j].isHidden()) {
					File file = fileList[j];
					//Send only the name as this the absolute path for the client
					String md5 = getMD5(file.getAbsolutePath());
					String filePath = file.getName();
					fileListString += filePath+IConstants.DELIMITER+md5;
					
				}

			}
			respStream.writeInt(fileListString.getBytes().length);
			respStream.writeBytes(fileListString);
			respStream.close();
			reqStream.close();
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("ConnectionHandler.sendFile() Something went wrong :"+request);
		} catch (IOException e) {
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
			}else if(opcode == IConstants.PING){
				String reply =String.valueOf(IConstants.OK);
				respStream.writeInt(reply.length());
				respStream.writeBytes(reply);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			if(clientSocket != null){
				try {
					
					clientSocket.close();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
