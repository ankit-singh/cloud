package socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPConncetion {
	public String sendFile(String fileData,ServerDetails destination){
		return null;
	}
	public  static String send(String request,ServerDetails destination)  {
		Socket tcpSocket = null;
		try {
			tcpSocket = new Socket(destination.getIp(), destination.getPort());
			DataOutputStream requestStream = new DataOutputStream(tcpSocket.getOutputStream());
			DataInputStream responsStream =  new DataInputStream(tcpSocket.getInputStream());
			Logger.Log("TCPService.send() Request length :"+request.length());
			requestStream.writeInt(request.length());
			Logger.Log("TCPService.send() Request : "+request);
			requestStream.writeBytes(request);

			int nb = responsStream.readInt();
			Logger.Log("TCPService.send() Response Length :"+ nb);
			byte[] digit = new byte[nb];

			for(int i = 0; i < nb; i++)
				digit[i] = responsStream.readByte(); 
			String resopnse = new String(digit);
			Logger.Log("TCPService.send() Response :"+resopnse);
			return resopnse.trim();
		} catch (IOException e) {
			e.printStackTrace();
			return new String("");
		}
		finally{
			if(tcpSocket!=null) 
				try {tcpSocket.close();
				} 
			catch (IOException e) {/*close failed*/}
		}
	}
}		

