package org.grassfield.egcli;

import java.util.ArrayList;
import java.util.List;

import org.grassfield.egcli.entity.Agent;
import org.grassfield.egcli.entity.Component;
import org.grassfield.egcli.entity.MaintenancePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ResultParser.
 * This parses the result from eG CLI to REST friendly entities
 * @author Murugapandian Ramaiah
 */
public class ResultParser {
	
	/** The logger. */
	static Logger logger = LoggerFactory.getLogger(ResultParser.class);

	/**
	 * Gets the agents.
	 *
	 * @param al the list of string in AgentName~Hash~HostIP format
	 * @return the agents
	 * @since 1.0
	 */
	public static List<Agent> getAgents(List<String> al) {
		List<Agent> result = new ArrayList<Agent>();
		if (al==null) {
			logger.warn("Given agent list is null");
			return result;
		}
		
		if (al.isEmpty()) {
			logger.warn("Given agent list is empty");
			return result;
		}
		
		for (String line:al) {
			if (line.trim().startsWith("AgentName")) {
				logger.info("Ignore title "+line);
				continue;
			}
			
			result.add(new Agent(line, "~Hash~"));
		}
		
		return result;
	}

	public static List<Component> getComponents(String type, List<String> al) {
		System.out.println("al:"+al);
		List<Component> result = new ArrayList<Component>();
		for (String line:al) {
			if (line.startsWith("HostIP~Hash~")) {
				logger.info("skipping header line "+line);
				continue;
			}
			result.add(new Component(type, line, "~Hash~"));
		}
		return result;
	}

	public static List<MaintenancePolicy> getMaintenancePolicyNames(List<String> al) {
		List<MaintenancePolicy> result = new ArrayList<MaintenancePolicy>();
		for (String line:al) {
			if (line.startsWith("Policy Names~Hash~")) {
				logger.info("skipping header line "+line);
				continue;
			}
			result.add(new MaintenancePolicy(line));
		}
		return result;
	}

	

}
