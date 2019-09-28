package org.grassfield.egcli.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.grassfield.egcli.DbCliController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class DBUtilities.
 */
public class DBUtilities {
	static Logger logger = LoggerFactory.getLogger(DbCliController.class);

	/**
	 * Parses the output of custom sql query.
	 *
	 * @param clioutput the clioutput
	 * @return the list
	 */
	public static List<?> parseSqlQuery(@SuppressWarnings("rawtypes") ArrayList clioutput) {
		List<Map<String,String>> result = new ArrayList<Map<String,String>>();
		String header = (String) clioutput.get(0);
		List<String> headerTokens = tokenize(header, "  ");
		for (int i=1;i<clioutput.size(); i++) {
			String record = (String) clioutput.get(i);
			if (record.trim().equals(""))
				continue;
			
			String [] tokens = record.split(" \\|\\| ");
			Map<String,String> recordMap = new Hashtable<String,String>();
			int j=0;
			for (String token:headerTokens) {
				try {
					recordMap.put(token, tokens[j]);
				} catch (IndexOutOfBoundsException e) {
					logger.error("Error while searching for '"+token+ "' in "+tokens, e);
					throw new UnexpectedCliOutputException("Error while searching for '"+token+ "' in "+tokens);
				}
				j++;
			}
			result.add(recordMap);
		}
		return result;
	}
	public static List<String> tokenize(String header, String delimiter) {
		
		StringTokenizer st = new StringTokenizer(header, delimiter);
		List<String> tokens = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			tokens.add(st.nextToken());
		}
		return tokens;
	}

}
