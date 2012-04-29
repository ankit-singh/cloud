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

import socket.FileDownloader;
import socket.FileUploader;
import socket.IConstants;
import socket.Logger;
import socket.MD5Checksum;

public class ServerHandler extends Thread { 
	DataInputStream reqStream; 
	DataOutputStream respStream; 
	Socket clientSocket; 
	final static int BUFFER_SIZE = 65536;
	String rootDir = "/users/ankitsingh/desktop/drop/sdef/";
	public ServerHandler (Socket aClientSocket,String dir) { 
		try { 
			this.rootDir = dir;
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
		ArrayList<String> requestList = Utilities.getMessage(request);
		String filePath = "/"+requestList.get(1)+requestList.get(2);
		FileDownloader.recieveFile(rootDir, filePath, requestList.get(3), requestList.get(1), clientSocket);
	}
	public String getMD5(String filePath){
		System.out.println("File Path :"+filePath);
		try {
			return MD5Checksum.getMD5Checksum(filePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
	public void sendFile(String request){
		System.out.println("ServerHandler.sendFile()");
		ArrayList<String> requestList = Utilities.getMessage(request);
		String filePath = "/"+requestList.get(1)+requestList.get(2);
		FileUploader.sendFile(rootDir, filePath, requestList.get(1), clientSocket);
		
	}
	private String createFileList(String fileListString,String folderName,String clientName){
		System.out.println("ServerHandler.createFileList() fileListString :"+fileListString);
		File folder = new File(folderName);
		Logger.Log("Client Foler :"+folder);
		File[] fileList = folder.listFiles();
		for (int j = 0; j < fileList.length; j++) {
			System.out.println("ServerHandler.createFileList()File Name :"+fileList[j].getAbsolutePath());
			if (fileList[j].isFile() && !fileList[j].isHidden()) {
				File file = fileList[j];
				//Send only the name as this the absolute path for the client
				String md5 = getMD5(file.getAbsolutePath());
				String filePath = file.getName();
				String userDir = rootDir+"/"+clientName;
				if(folder.equals(userDir)){
				fileListString += IConstants.DELIMITER+filePath+IConstants.DELIMITER+md5;
				System.out.println("ServerHandler.createFileList() fileListString :"+fileListString);
				}
				else{
					String absPath = file.getAbsolutePath();
					 filePath = absPath.substring(
							 userDir.length(), absPath.length());
					fileListString += IConstants.DELIMITER+filePath+IConstants.DELIMITER+md5;
					System.out.println("ServerHandler.createFileList() fileListString :"+fileListString);
					
				}
			}else if(fileList[j].isDirectory()){
				fileListString +=createFileList(fileListString, fileList[j].getAbsolutePath(),clientName);
				System.out.println("ServerHandler.createFileList() fileListString :"+fileListString);
			}

		}
		return fileListString;
	}
	public void sendFileList(String request){
		System.out.println("ConnectionHandler.sendFileList()"+request);
		try {
			ArrayList<String> response = Utilities.getMessage(request);
			String clientName = response.get(1);
			Logger.Log("Opening Client Folder :"+rootDir+"/"+clientName);
			
			String fileListString = new String();
			fileListString = IConstants.FILELIST+createFileList(fileListString, rootDir+"/"+clientName,clientName);
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
			}else if(opcode == IConstants.FILELIST){
				sendFileList(request);
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
