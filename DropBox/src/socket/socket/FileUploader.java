package socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

public class FileUploader {
	ClientDetails client;
	final static int BUFFER_SIZE = 65536;

	public static void pushFile(final File file , final ServerDetails dest,String uname){

		try {
			Socket socket = new Socket(dest.getIp(), dest.getPort());
			FileInputStream fileInputStream = new FileInputStream(file);
			DataOutputStream socketOutputStream = new DataOutputStream (socket.getOutputStream());
			DataInputStream socketInputStream  = new DataInputStream ( socket.getInputStream());
			long startTime = System.currentTimeMillis();
			String request =IConstants.PUSH+IConstants.DELIMITER+uname+"_"+file.getName();
			socketOutputStream.writeInt(request.getBytes().length);
			socketOutputStream.write(request.getBytes());
			int opcode = socketInputStream.readInt();
			if(opcode == IConstants.OK){
				byte[] buffer = new byte[BUFFER_SIZE];
				int read;
				int readTotal = 0;
				while ((read = fileInputStream.read(buffer)) != -1) {
					socketOutputStream.write(buffer, 0, read);
					readTotal += read;
//					System.out
//					.println("UploadED BYTES :"+readTotal);
				}
				socketOutputStream.close();
				fileInputStream.close();
				socket.close();
				long endTime = System.currentTimeMillis();
				System.out.println(readTotal + " bytes written in " + (endTime - startTime) + " ms.");
			}
		} catch (Exception e) {
		}
	}
}
