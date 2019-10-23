package org.grassfield.egcli;

import java.util.Base64;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.grassfield.egcli.entity.Agent;
import org.grassfield.egcli.entity.Component;
import org.grassfield.egcli.entity.MaintenancePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.eg.cli.CLIClientBase;

/**
 * @author Ramaiah Murugapandian
 *
 */	
@RestController
public class AdminCliController {
	static Logger logger = LoggerFactory.getLogger(AdminCliController.class);

	@SuppressWarnings("unchecked")
	@PostMapping("/rest/v1/{action}/{element}")
    List<?> findAll(
    		@RequestHeader("Authorization") String authorization,
    		@RequestHeader("manager-host") String managerHost,
    		@RequestHeader("manager-port") String managerPort,
    		@RequestHeader("manager-ssl") String managerSsl,
    		@PathVariable String action,
    		@PathVariable String element,
    		@RequestBody (required=false) Command command) throws Exception {
		logger.info("Received request");
		if (authorization==null) {
			throw new CliPermissionException("Authorization is not provided");
		}
		logger.info("Auth received");
		
		if (authorization.startsWith("Basic "))
			authorization = authorization.substring(6);
		
		byte[] decode = Base64.getDecoder().decode(authorization);
		authorization = new String (decode);
		String[] split = authorization.split(":");
		String userName = split[0];
		String password = split[1];
		
		logger.info("Parsed credentials");
		
		@SuppressWarnings("rawtypes")
		Hashtable ht = new Hashtable();
		ht.put("element", element);
		ht.put("action", action);
		Map<String, String> parameters = null;
		if (command!=null) {
			parameters = command.getParameters();
			if (parameters!=null && !parameters.isEmpty()) {
				for (String key:parameters.keySet()) {
					ht.put(key, parameters.get(key));
				}
			}
		}else {
			logger.info("No command parameters received");
		}
		
		ht.put("username",userName);
		ht.put("password",password);
		ht.put("managerip",managerHost);
		ht.put("port",managerPort);
		ht.put("ssl",managerSsl);
		
		logger.info("Prepared the request map:"+ht);

		CLIClientBase cli = new CLIClientBase();
		List<String> al = cli.doExecuteForREST(ht);
		System.out.println("al:"+al);
		if (al==null) {
			logger.error("received no response from CLI");
			throw new CliFailedException("Unknown Error");
		}else if (!al.isEmpty()) {
			String output = al.get(0).trim();
			String outputlc = output.toLowerCase();
			if (outputlc.indexOf("user does not have privilege")!=-1) {
				throw new CliPermissionException(output);
			}
			if (outputlc.startsWith("error"))
				throw new CliFailedException(output);
		}
		switch(action+element) {
		case "showExternalAgent":
			//reuse showRemoteAgent
		case "showRemoteAgent":
			List<Agent> result = ResultParser.getAgents(al);
			return result;
		case "showComponent":
			if (parameters!=null) {
				List<Component> cList = ResultParser.getComponents(parameters.get("componenttype"),al);
				return cList;
			}else
				throw new CliFailedException("componenttype parameter is missing");
    	case "showMaintenancePolicy":
				List<MaintenancePolicy> cList = ResultParser.getMaintenancePolicyNames(al);
				return cList;
		}
		return null;
    }
}
