package socket;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ClientFileTable {
	ConcurrentHashMap<String, ArrayList<FileDetails>> cfTable = new ConcurrentHashMap<String, ArrayList<FileDetails>>();
	
	public void addFile(String clientName, FileDetails fileDetails){
		ArrayList<FileDetails> fileList;
		if(cfTable.get(clientName) == null){
			fileList = new ArrayList<FileDetails>();
		}else{
			fileList = cfTable.get(clientName);
		}
		fileList.add(fileDetails);
		cfTable.put(clientName, fileList);
	}
	public ArrayList<FileDetails> getFileList(String clientName){
		return cfTable.get(clientName);
	}
	
}
