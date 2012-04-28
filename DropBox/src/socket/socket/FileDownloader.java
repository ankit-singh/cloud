package socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import cornell.cloud.dropsomething.common.model.ServerDetails;

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
	public static void pullFIle(String rootDir,String filePath,String md5,String clientName,Socket tcpSocket){

		try {

			File f = getFile(rootDir, filePath, md5);
			if(f != null){
				DataOutputStream socketOutputStream = new DataOutputStream (tcpSocket.getOutputStream());
				DataInputStream socketInputStream  = new DataInputStream ( tcpSocket.getInputStream());
				long startTime = System.currentTimeMillis();
				String request =IConstants.PULL+IConstants.DELIMITER+clientName+IConstants.DELIMITER+filePath;
				socketOutputStream.writeInt(request.getBytes().length);
				socketOutputStream.write(request.getBytes());

				byte[] buffer = new byte[BUFFER_SIZE];
				FileOutputStream fileStream  = new FileOutputStream(f);
				int read;
				int totalRead = 0;
				while ((read = socketInputStream.read(buffer)) != -1) {
					fileStream.write(buffer,0,read);
					totalRead += read;
				}
				long endTime = System.currentTimeMillis();
				fileStream.close();
				System.out.println(totalRead + " bytes read in " + (endTime - startTime) + " ms.");
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
			tcpSocket = new Socket(dest.getIp(), dest.getPort());
			pullFIle(rootDir, filePath, md5, clientName, tcpSocket);
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
				file = new File(rootDir+"/"+filePath);
			}
		}else{
			String[] arr = filePath.split("/");
			String fileDir = filePath.substring(0,filePath.length() - arr[arr.length-1].length()-1);
			new File(fileDir).mkdirs();
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
		return new File(rootDir+"/"+filePath).exists();

	}
	/**
	 * @param rootDir
	 * @param filePath
	 * @param md5
	 * @return True if the file is same the new file
	 */
	private static boolean isSameFile(String rootDir,String filePath, String md5){
		try {
			String oldmd5 = MD5Checksum.getMD5Checksum(rootDir+"/"+filePath);
			if(oldmd5.endsWith(md5)){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}

