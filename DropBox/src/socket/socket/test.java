package socket;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;



public class test {

	public static void main(String[] args) {

		HashMap<String,Long> map = new HashMap<String,Long>();
		ValueComparator bvc =  new ValueComparator(map);
		TreeMap<String,Long> sorted_map = new TreeMap(bvc);

		map.put("A",(long) 99);
		map.put("B",(long) 673);
		map.put("C",(long) 677);
		map.put("D",(long) 675);

		System.out.println("unsorted map");
		for (String key : map.keySet()) {
			System.out.println("key/value: " + key + "/"+map.get(key));
		}

		sorted_map.putAll(map);
		String result ="not found";
		for (String key : sorted_map.keySet()) {
			System.out.println("key/value: " + key + "/"+sorted_map.get(key));
		}
		Long bestDistanceFoundYet =Long.MAX_VALUE;
		Long d = Long.valueOf(678);
		// We iterate on the array...
		for (String key : sorted_map.keySet()) {
			// if we found the desired number, we return it.
			if (sorted_map.get(key) == d) {
				result = key;
			} else if(d < sorted_map.get(key)){
				// else, we consider the difference between the desired number and the current number in the array.
				long diff = Math.abs(d - sorted_map.get(key));
				if (diff < bestDistanceFoundYet) {
					result = key;
					bestDistanceFoundYet = diff;
				}
			}
		}
		System.out.println("test.main() Result :"+result);
	}


}
class ValueComparator implements Comparator {

	Map base;
	public ValueComparator(Map base) {
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
