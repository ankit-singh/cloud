package cornell.cloud.dropsomething.common.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
public class FileDownloader {

	/**
	 * The constant buffer size used to define the number of bytes read in one packet
	 */
	private static final int BUFFER_SIZE = 65536;	
	/**
	 * NOTE: Socket will not be closed here
	 * @param rootDir Where the files need to be stored
	 * @param filePath fileName
	 * @param clientName
	 * @param tcpSocket
	 */
	public static void recieveFile(String rootDir,String filePath,String md5,String clientName,Socket tcpSocket){
		rootDir = Utilities.getFormattedDir(rootDir);
		filePath = Utilities.getFormattedFilePath(filePath);
		try {

			DataOutputStream socketOutputStream = new DataOutputStream (tcpSocket.getOutputStream());
			DataInputStream socketInputStream  = new DataInputStream ( tcpSocket.getInputStream());
			File f = getFile(rootDir, filePath, md5);
			if(f != null){
				long startTime = System.currentTimeMillis();
				System.out.println("FileDownloader.recieveFile() Sending OK");
				socketOutputStream.writeInt(IConstants.OK);
				byte[] buffer = new byte[BUFFER_SIZE];
				System.out.println("FileDownloader.recieveFile() Opeing fileStream");
				FileOutputStream fileStream  = new FileOutputStream(f);
				int read;
				int totalRead = 0;
				System.out.println("FileDownloader.recieveFile() Starting to download file");
				while ((read = socketInputStream.read(buffer)) != -1) {
					fileStream.write(buffer,0,read);
					totalRead += read;
					System.out.println("FileDownloader.recieveFile() Reading");
				}
				System.out.println("FileDownloader.recieveFile() Download Complete");
				long endTime = System.currentTimeMillis();
				System.out.println("FileDownloader.recieveFile() Closing fileStream");
				fileStream.close();
				System.out.println("FileDownloader.recieveFile() File Stream closed");
				System.out.println(totalRead + " bytes read in " + (endTime - startTime) + " ms.");
			}else{
				socketOutputStream.writeInt(IConstants.NO_ACTION);
			}
		} catch (IOException e) {
			System.out.println("FileDownloader.pullFIle() "+e.getMessage());
			e.printStackTrace();
		}


	}
	
	/**
	 * @param rootDir
	 * @param filePath
	 * @param md5
	 * @param clientName
	 * @param dest
	 */
	public static void pullFIle(String rootDir,String filePath,String md5,String clientName,ServerDetails dest){
		Socket tcpSocket = null;
		try {
			System.out.println("FileDownloader.pullFIle()");
			rootDir = Utilities.getFormattedDir(rootDir);
			filePath = Utilities.getFormattedFilePath(filePath);
			File f = getFile(rootDir, filePath, md5);
			SocketAddress address = new InetSocketAddress(dest.getIp(),dest.getPort());
			if (f != null) {
				Logger.Log("Opening a socket for pull Server : " + dest.getIp());
				Logger.Log("Opening a socket for pull Port : " + dest.getPort());
				tcpSocket = new Socket();
				tcpSocket.connect(address, 4000);
				DataOutputStream socketOutputStream = new DataOutputStream(
						tcpSocket.getOutputStream());
				DataInputStream socketInputStream = new DataInputStream(
						tcpSocket.getInputStream());
				long startTime = System.currentTimeMillis();
				String request = IConstants.PULL + IConstants.DELIMITER
						+ clientName + IConstants.DELIMITER + filePath;
				Logger.Log("Sending pull request :" + request);
				socketOutputStream.writeInt(request.getBytes().length);
				socketOutputStream.write(request.getBytes());
				byte[] buffer = new byte[BUFFER_SIZE];
				FileOutputStream fileStream = new FileOutputStream(f);
				int read;
				int totalRead = 0;
				while ((read = socketInputStream.read(buffer)) != -1) {
					fileStream.write(buffer, 0, read);
					totalRead += read;
				}
				long endTime = System.currentTimeMillis();
				fileStream.close();
				System.out.println(totalRead + " bytes read in "
						+ (endTime - startTime) + " ms.");
			}else{
				Logger.Log("File is upto date on the client");
			}
		}catch(ConnectException ce){
			System.out.println("FileDownloader.pullFIle() Connection Failed");
		}catch(SocketTimeoutException sto){
			System.out.println("FileDownloader.pullFIle() Socket Timed out");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			if(tcpSocket != null){
				try {
					tcpSocket.close();
				} catch (IOException e) {
					System.out.println("Socket Close failed");
					e.printStackTrace();
				}
			}
		}

	}
	/**
	 * @param rootDir
	 * @param filePath
	 * @param md5
	 * @return
	 */
	private  static File getFile(String rootDir,String filePath,String md5){
		File file  = null;
		if(fileExists(rootDir, filePath)){
			if(!isSameFile(rootDir, filePath, md5)){
				file = new File(rootDir+filePath);
			}
		}else{
			String[] arr = filePath.split("/");
			String fileDir = filePath.substring(0,filePath.length() - arr[arr.length-1].length()-1);
			fileDir = Utilities.getFormattedDir(fileDir);
			System.out.println("Creating directory :"+rootDir+fileDir);
			new File(rootDir+fileDir).mkdirs();
			System.out.println("Opening/Creating file :"+rootDir+filePath);
			file = new File(rootDir+filePath);
		}
		return file;
	}
	/**
	 * @param rootDir<String>
	 * @param filePath<String>
	 * @return true if file exists at rootDir/filePath
	 * 			else false
	 */
	private static boolean fileExists(String rootDir,String filePath){
		boolean fileExits = new File(rootDir+filePath).exists();
		System.out.println("FileDownloader.fileExists() "+rootDir+filePath+" :"+fileExits);
		return fileExits;

	}
	/**
	 * @param rootDir
	 * @param filePath
	 * @param md5
	 * @return True if the file is same the new file
	 */
	private static boolean isSameFile(String rootDir,String filePath, String md5){
		boolean isSameFile = false;
		try {
			String oldmd5 = MD5Checksum.getMD5Checksum(rootDir+filePath);
			if(oldmd5.endsWith(md5)){
				isSameFile = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		Logger.Log("Is Same File:"+isSameFile);
		return isSameFile;
	}
}

