package socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import cornell.cloud.dropsomething.common.model.ServerDetails;

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
	
	
	public static boolean pushFile(String rootDir,String filePath,String clientName,Socket tcpSocket){
		boolean pushSuccess = false;
		try {
			File file = new File(rootDir+"/"+filePath);
			FileInputStream fileInputStream = new FileInputStream(file);
			DataOutputStream socketOutputStream = new DataOutputStream (tcpSocket.getOutputStream());
			DataInputStream socketInputStream  = new DataInputStream ( tcpSocket.getInputStream());
			long startTime = System.currentTimeMillis();
			String md5 = MD5Checksum.getMD5Checksum(rootDir+"/"+filePath);
			//Step 1: prepare the destination for upload
			String request =IConstants.PUSH+IConstants.DELIMITER+clientName+IConstants.DELIMITER+file.getName()+IConstants.DELIMITER+md5;
			socketOutputStream.writeInt(request.getBytes().length);
			socketOutputStream.write(request.getBytes());
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
				
				long endTime = System.currentTimeMillis();
				System.out.println(readTotal + " bytes written in " + (endTime - startTime) + " ms.");
				pushSuccess = true;
			}
			fileInputStream.close();
		} catch (Exception e) {
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
		Socket tcpSocket = null;
		boolean response = false;
		try {
			tcpSocket = new Socket(dest.getIp(), dest.getPort());
			response = pushFile(rootDir, filePath, clientName, tcpSocket);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
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
		return response;
	}
	
}
