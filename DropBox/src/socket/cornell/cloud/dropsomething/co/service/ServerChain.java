package cornell.cloud.dropsomething.co.service;


import socket.IConstants;
import socket.MessageService;
import cornell.cloud.dropsomething.common.model.ServerDetails;


public class ServerChain {


	public static String updateChain(String ServerChain)
	{	

		String newChain = "";
		String list[]= ServerChain.split(IConstants.DELIMITER);

		for(int i=0; i<list.length; i=i+2)
		{
			String request = String.valueOf(IConstants.PING);
			String response = MessageService.send(request, ServerDetails.create(list[i], Integer.parseInt(list[i+1])));
			System.out.println("received response" +response);
			if(isValidMessage(response)){
				if(response.split(IConstants.DELIMITER)[0].equals(IConstants.OK)){

					if(newChain.equals(""))
					{
						newChain = list[i] + IConstants.DELIMITER + list[i+1];
					}
					else
					{
						newChain = newChain + IConstants.DELIMITER + list[i] + IConstants.DELIMITER + list[i+1];
					}

				}
			}
			else{
				System.out.println("Server : " +list[i] +"/"+ list[i+1] +" is down, removed from Server chain");
			}
		}
		System.out.println("Replying with newchain = : " + newChain);	
		return newChain;
	}

	public static boolean isValidMessage(String request){
		if(request != null && !request.trim().equals("")){
			return true;
		}
		else{
			return false;
		}
	}
	public static void main (String args[])
	{
		String oldChain = "192.168.2.2__3370__192.168.2.2__3371__192.168.2.2__3372__192.168.2.2__3373__192.168.2.2__3374";
		String newChain = updateChain(oldChain);

		System.out.println("NewChain = " + newChain);


	}

}


