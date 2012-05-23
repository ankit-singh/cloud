package cornell.cloud.dropsomething.common.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import cornell.cloud.dropsomething.common.IConstants;
import cornell.cloud.dropsomething.common.model.ServerDetails;
import cornell.cloud.dropsomething.common.util.Logger;
import cornell.cloud.dropsomething.common.util.MD5Checksum;
import cornell.cloud.dropsomething.common.util.Utilities;

/**
 * @author ankitsingh
 *
 */
public class FileUploader {
	/**
	 * 
	 */
	private  final static int BUFFER_SIZE = 65536;
	/**
	 * @param rootDir
	 * @param filePath
	 * @param clientName
	 * @param tcpSocket
	 * @return
	 */
	
	
	public static boolean sendFile(String rootDir,String filePath,String clientName,Socket tcpSocket){
		boolean pushSuccess = false;
		Logger.Log("Root Dir :"+rootDir);
		Logger.Log("File Path:"+filePath);
		Logger.Log("Client Name :"+clientName);
		rootDir = Utilities.getFormattedDir(rootDir);
		filePath = Utilities.getFormattedFilePath(filePath);
		try {
			File file = new File(rootDir+filePath);
			FileInputStream fileInputStream = new FileInputStream(file);
			DataOutputStream socketOutputStream = new DataOutputStream (tcpSocket.getOutputStream());
			long startTime = System.currentTimeMillis();
			//Step 1: prepare the destination for upload
			//Step 2: Confirm and upload file
				byte[] buffer = new byte[BUFFER_SIZE];
				int read;
				int readTotal = 0;
				while ((read = fileInputStream.read(buffer)) != -1) {
					socketOutputStream.write(buffer, 0, read);
					readTotal += read;
				}
				
				long endTime = System.currentTimeMillis();
				System.out.println(readTotal + " bytes written in " + (endTime - startTime) + " ms.");
				pushSuccess = true;
			fileInputStream.close();
		} catch (Exception e) {
			Logger.Log(e.getMessage());
			e.printStackTrace();
			
		}
		return pushSuccess;
	}
	/**
	 * @param rootDir
	 * @param filePath
	 * @param clientName
	 * @param dest
	 * @return
	 */
	public static boolean pushFile(String rootDir,String filePath,String clientName,ServerDetails dest){
		boolean pushSuccess = false;
		Logger.Log("Root Dir :"+rootDir);
		Logger.Log("File Path:"+filePath);
		Logger.Log("Client Name :"+clientName);
		rootDir = Utilities.getFormattedDir(rootDir);
		filePath = Utilities.getFormattedFilePath(filePath);
		try {
			Socket tcpSocket = new Socket(dest.getIp(), dest.getPort());
			File file = new File(rootDir+filePath);
			FileInputStream fileInputStream = new FileInputStream(file);
			DataOutputStream socketOutputStream = new DataOutputStream (tcpSocket.getOutputStream());
			DataInputStream socketInputStream  = new DataInputStream ( tcpSocket.getInputStream());
			long startTime = System.currentTimeMillis();
			String md5 = MD5Checksum.getMD5Checksum(rootDir+filePath);
			//Step 1: prepare the destination for upload
			String request =IConstants.PUSH+IConstants.DELIMITER+clientName+IConstants.DELIMITER+filePath+IConstants.DELIMITER+md5;
			Logger.Log("Upload Request :"+request);
			socketOutputStream.writeInt(request.length());
			socketOutputStream.writeBytes(request);
			//Step 2: Confirm and upload file
			int opcode = socketInputStream.readInt();
			if(opcode == IConstants.OK){
				byte[] buffer = new byte[BUFFER_SIZE];
				int read;
				int readTotal = 0;
				while ((read = fileInputStream.read(buffer)) != -1) {
					socketOutputStream.write(buffer, 0, read);
					readTotal += read;
				}
				socketInputStream.close();
				socketOutputStream.close();
				long endTime = System.currentTimeMillis();
				System.out.println(readTotal + " bytes written in " + (endTime - startTime) + " ms.");
				pushSuccess = true;
			}else if(opcode == IConstants.NO_ACTION){
				Logger.Log("UPLOAD NOT REQUIRED");
			}
			fileInputStream.close();
		} catch (Exception e) {
			Logger.Log(e.getMessage());
			e.printStackTrace();
			
		}
		return pushSuccess;
	}
	public static boolean replicate(String rootDir,String filePath,String clientName,ServerDetails dest) throws ConnectException{
		boolean pushSuccess = false;
		Logger.Log("Root Dir :"+rootDir);
		Logger.Log("File Path:"+filePath);
		Logger.Log("Client Name :"+clientName);
		rootDir = Utilities.getFormattedDir(rootDir);
		filePath = Utilities.getFormattedFilePath(filePath);
		try {
			SocketAddress address  = new InetSocketAddress(dest.getIp(), dest.getPort());
			Socket tcpSocket = new Socket();
			tcpSocket.connect(address,4000);
			File file = new File(rootDir+filePath);
			FileInputStream fileInputStream = new FileInputStream(file);
			DataOutputStream socketOutputStream = new DataOutputStream (tcpSocket.getOutputStream());
			DataInputStream socketInputStream  = new DataInputStream ( tcpSocket.getInputStream());
			long startTime = System.currentTimeMillis();
			String md5 = MD5Checksum.getMD5Checksum(rootDir+filePath);
			//Step 1: prepare the destination for upload
			String request =IConstants.REPLICATE+IConstants.DELIMITER+clientName+IConstants.DELIMITER+filePath+IConstants.DELIMITER+md5;
			Logger.Log("Upload Request :"+request);
			socketOutputStream.writeInt(request.length());
			socketOutputStream.writeBytes(request);
			//Step 2: Confirm and upload file
			int opcode = socketInputStream.readInt();
			if(opcode == IConstants.OK){
				byte[] buffer = new byte[BUFFER_SIZE];
				int read;
				int readTotal = 0;
				while ((read = fileInputStream.read(buffer)) != -1) {
					socketOutputStream.write(buffer, 0, read);
					readTotal += read;
				}
				socketInputStream.close();
				socketOutputStream.close();
				long endTime = System.currentTimeMillis();
				System.out.println(readTotal + " bytes written in " + (endTime - startTime) + " ms.");
				pushSuccess = true;
			}else if(opcode == IConstants.NO_ACTION){
				Logger.Log("UPLOAD NOT REQUIRED");
			}
			fileInputStream.close();
		} catch(ConnectException ce){
			ce.printStackTrace();
			System.out.println("FileUploader.replicate() Connection Failed");
			throw ce;
		}catch(SocketTimeoutException sto){
			throw new ConnectException();
			
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pushSuccess;
	}
	
}
