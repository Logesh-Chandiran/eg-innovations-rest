package org.grassfield.egcli;

import java.util.Base64;
import java.util.Hashtable;
import java.util.List;

import org.grassfield.egcli.entity.Agent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.eg.cli.CLIClientBase;

@RestController
public class RemoteAgentController {

	@SuppressWarnings("unchecked")
	@PostMapping("/rest/v1/remoteagents")
    List<Agent> findAll(
    		@RequestHeader("Authorization") String authorization,
    		@RequestHeader("manager-host") String managerHost,
    		@RequestHeader("manager-port") String managerPort,
    		@RequestHeader("manager-ssl") String managerSsl,
    		@RequestBody Command command) throws CliPermissionException {
		if (authorization==null) {
			throw new CliPermissionException("Authorization is not provided");
		}
		
		if (authorization.startsWith("Basic "))
			authorization = authorization.substring(6);
		
		byte[] decode = Base64.getDecoder().decode(authorization);
		authorization = new String (decode);
		String[] split = authorization.split(":");
		String userName = split[0];
		String password = split[1];
		
		@SuppressWarnings("rawtypes")
		Hashtable ht = new Hashtable();
		ht.put("element", command.getElement());
		ht.put("action", command.getAction());
		ht.put("componenttype", command.getParameters().get("componenttype"));
		ht.put("username",userName);
		ht.put("password",password);
		ht.put("managerip",managerHost);
		ht.put("port",managerPort);
		ht.put("ssl",managerSsl);

		CLIClientBase cli = new CLIClientBase();
		List<String> al = cli.doExecuteForREST(ht);
		List<Agent> result = ResultParser.getAgents(al);
		return result;
    }
}
