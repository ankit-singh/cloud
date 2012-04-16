package socket;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ServerTable {
	HashMap<ServerDetails,Long> serverMap = new HashMap<ServerDetails,Long>();
	SizeComparator bvc =  new SizeComparator(serverMap);
	TreeMap<ServerDetails,Long> sorted_map = new TreeMap(bvc);
	/**
	 * @param server
	 * @param size
	 */
	public void addServer(ServerDetails server,Long size){
		Logger.Log("ServerTable.addServer() Server :"+server.toString());
		Logger.Log("ServerTable.addServer() size :"+size);
		serverMap.put(server, size);
	}
	/**
	 * @param details
	 * @return
	 */
	public Long getServerSpace(ServerDetails details){
		return serverMap.get(details);
	}
	/**
	 * @param fileSize
	 * @return
	 */
	public ServerDetails getPotentialServer(Long fileSize){
		Logger.Log("ServerTable.getPotentialServer() fileSize : "+fileSize);
		ServerDetails result = null;
		sorted_map.clear();
		sorted_map.putAll(serverMap);
		Long bestDistanceFoundYet =Long.MAX_VALUE;
		for (ServerDetails key : sorted_map.keySet()) {
			// if we found the desired number, we return it.
			if (sorted_map.get(key) == fileSize) {
				result = key;
			} else if(fileSize< sorted_map.get(key)){
				// else, we consider the difference between the desired number and the current number in the array.
				long diff = Math.abs(fileSize - sorted_map.get(key));
				if (diff < bestDistanceFoundYet) {
					result = key;
					bestDistanceFoundYet = diff;
				}
			}
		}
		return result;
	}


}
class SizeComparator implements Comparator {

	Map base;
	public SizeComparator(Map base) {
		this.base = base;
	}

	public int compare(Object a, Object b) {

		if((Long)base.get(a) < (Long)base.get(b)) {
			return 1;
		} else if((Long)base.get(a) == (Long)base.get(b)) {
			return 0;
		} else {
			return -1;
		}
	}
}


