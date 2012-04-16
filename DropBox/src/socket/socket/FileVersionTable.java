package socket;

import java.io.FileNotFoundException;
import java.util.concurrent.ConcurrentHashMap;

public class FileVersionTable {

	ConcurrentHashMap<String, Integer> fvTable;
	
	
	public FileVersionTable(){
		fvTable = new ConcurrentHashMap<String, Integer>();
	}
	public void addFile(String fileName, int version ){
		fvTable.put(fileName, Integer.valueOf(version));
	
	}
	public int getVersion(String fileName) throws FileNotFoundException{
		Integer	ver = fvTable.get(fileName);
		if(ver == null){
			throw new FileNotFoundException("File does not exist in version table");
		}
		else{
			return ver.intValue();
		}
	}
}
