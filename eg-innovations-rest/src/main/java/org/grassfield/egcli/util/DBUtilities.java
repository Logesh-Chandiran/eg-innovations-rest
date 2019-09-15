package org.grassfield.egcli.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DBUtilities {

	public static List<Map<String, String>> format(List<String> out) {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		String header = out.get(0);
		String[] headerArray = header.split(",");
		for (int i=1; i<out.size(); i++) {
			Map<String, String> record = new TreeMap<String, String>();
			
			String line = out.get(i);
			if (line.trim().equals("")) {
				continue;
			}
			String[] split = line.split(",");
			if (split.length!=headerArray.length) {
				throw new RuntimeException("Length do not match "+headerArray.length +"<->"+split.length);
			}
			for (int j = 0; j<headerArray.length; j++) {
				record.put(headerArray[j].trim(), split[j].trim());
			}
			result.add(record);
		}
		return result;
	}

}
