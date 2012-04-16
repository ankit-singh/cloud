package socket;

import java.io.FileNotFoundException;
import java.util.concurrent.ConcurrentHashMap;

public class FileServerTable {
	ConcurrentHashMap<FileDetails, ServerDetails> fsTable ;
	
	public FileServerTable(){
		fsTable = new ConcurrentHashMap<FileDetails, ServerDetails>();
		
	}
	public void addFileDetails(FileDetails fileDetails,ServerDetails serverDetails){
		fsTable.put(fileDetails, serverDetails);
	}
	public ServerDetails getServerDetails(FileDetails fileDetails) throws FileNotFoundException{
		if(fsTable.get(fileDetails) == null){
			throw new FileNotFoundException();
		}else{
			return fsTable.get(fileDetails);
		}
	}
}
