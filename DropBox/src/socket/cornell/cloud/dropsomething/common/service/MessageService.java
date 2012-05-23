package cornell.cloud.dropsomething.common.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import cornell.cloud.dropsomething.common.model.ServerDetails;
import cornell.cloud.dropsomething.common.util.Logger;

public class MessageService {
	final static int timeout = 4*1000;

	/** 
	 * Sends a request on the socket 
	 * NOTE: this method does not close the socket
	 * @param request<String> The request to be sent as is
	 * @param tcpSocket<Socket> The Socket on which the request and response will be
	 * 		  Received and sent respectively
	 * @return
	 * 		Response
	 */
	private static String send(String request,Socket tcpSocket){
		try {
			DataOutputStream requestStream = new DataOutputStream(tcpSocket.getOutputStream());
			DataInputStream responsStream =  new DataInputStream(tcpSocket.getInputStream());
			Logger.Log("Destination Server :"+tcpSocket.getInetAddress().getHostAddress());
			Logger.Log("Destination Port :"+tcpSocket.getPort());
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
		}catch(ConnectException ce){
			System.out.println("The Coonection cannot be established , the server failed to respond!");
			return "";
		}
		catch (EOFException e) {
			System.out.println("MessageService.send() Response not recived");
			return new String("");
		} catch (IOException e) {
			System.out.println("MessageService.send() IO EXCeption");
			return new String("");
		}
	}


	public  static String send(String request,ServerDetails destination)  {
		Socket tcpSocket = null;
		String response = "";
		try {
			SocketAddress address = new InetSocketAddress(destination.getIp(),destination.getPort());
			tcpSocket = new Socket();
			tcpSocket.connect(address, timeout);
			response = send(request, tcpSocket);
		}catch(ConnectException ce){
			System.out.println("The Coonection cannot be established , the server failed to respond!");
			return "";
		}
		catch(SocketTimeoutException ce){
			System.out.println("The Coonection cannot be established , the server failed to respond!");
			return "";
		}
		catch (UnknownHostException e) {
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

