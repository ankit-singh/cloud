package socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class FileDownloader {

	public static void pullFile(String filePath, ServerDetails dest,String dir){
		try {
			final int BUFFER_SIZE = 65536;
			Socket socket = new Socket(dest.getIp(), dest.getPort());
			DataOutputStream socketOutputStream = new DataOutputStream (socket.getOutputStream());
			DataInputStream socketInputStream  = new DataInputStream ( socket.getInputStream());
			long startTime = System.currentTimeMillis();
			byte[] buffer = new byte[BUFFER_SIZE];
			int uname = filePath.split("_")[0].length();
			String fn = filePath.substring(uname+1,filePath.length());			
			File f = new File(dir+"/"+fn);
			String request =IConstants.PULL+IConstants.DELIMITER+filePath;
			socketOutputStream.writeInt(request.getBytes().length);
			socketOutputStream.write(request.getBytes());
			FileOutputStream fileStream  = new FileOutputStream(f);
			int read;
			int totalRead = 0;
			while ((read = socketInputStream.read(buffer)) != -1) {
				fileStream.write(buffer,0,read);
				totalRead += read;
			}
			long endTime = System.currentTimeMillis();
			fileStream.close();
			socket.close();
			System.out.println(totalRead + " bytes read in " + (endTime - startTime) + " ms.");
		} catch (IOException e) {
		}
	}

}
