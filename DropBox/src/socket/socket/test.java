package socket;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class test {

	static TreeMap<Long, String> tmap = new TreeMap<Long, String>();
	
	
	@SuppressWarnings("rawtypes")
	public static void main(String[] args){
		tmap.put(new Long(123), "india");
		tmap.put(new Long(3435), "ab");
		tmap.put(new Long(256256), "sd");
		tmap.put(new Long(24524), "da");
		tmap.put(new Long(4545), "eee");
		tmap.put(new Long(123), "Ankaait");
		tmap.put(new Long(223344), "Anksdsdit");
		tmap.put(new Long(111111), "wwww");
		tmap.put(new Long(33333), "Ansskit");
		tmap.put(new Long(56788), "Ankcccit");
		tmap.put(new Long(222), "Anwwwwkit");
		tmap.put(new Long(44556), "ss");
		Long in = new Long(220);
		// Get a set of the entries 
		Set set = tmap.entrySet(); 
		// Get an iterator 
		Iterator i = set.iterator(); 
		// Display elements 
		while(i.hasNext()) { 
		Map.Entry me = (Map.Entry)i.next(); 
		System.out.print(me.getKey() + ": "); 
		System.out.println(me.getValue()); 
		} 
		Map.Entry met = tmap.ceilingEntry(in);
		System.out.println("test.main()--------");
		System.out.print(met.getKey() + ": "); 
		System.out.println(met.getValue()); 
	}
}
