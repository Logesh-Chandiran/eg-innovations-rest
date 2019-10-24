package org.grassfield.egcli;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.grassfield.egcli.util.DBUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.eg.cli.eGDBCLI;

/**
 * The Class DbCliController.
 * @author Ramaiah Murugapandian
 */
@RestController
public class DbCliController {
	static Logger logger = LoggerFactory.getLogger(DbCliController.class);
	 /**
	 * Execute a custom sql query on eG database.
	 * 
	 * curl -X POST -H 'manager-host: eGInnovations' -H 'manager-port: 9099' -H 'manager-ssl: true' -H 'Content-Type: application/json;charset=UTF-8' -H 'Authorization: Basic YWRtaW46YWRtaW4=' -i http://localhost:8080/rest/v1/data/query --data '{"query":"select * from measure_last"}'
	 *
	 * @param authorization the authorization
	 * @param managerHost the manager host
	 * @param managerPort the manager port
	 * @param managerSsl the manager ssl
	 * @param paramMap the param map
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	@PostMapping("/rest/v1/data/query")
    List<?> query(
    		@RequestHeader("Authorization") String authorization,
    		@RequestHeader("manager-host") String managerHost,
    		@RequestHeader("manager-port") String managerPort,
    		@RequestHeader("manager-ssl") String managerSsl,
    		@RequestBody  Map<String, String> paramMap
    		) throws IOException, InterruptedException {
		logger.info("Received request /rest/v1/data/query");
		System.out.println("Received request");
		
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
		
		logger.info("Parsed credentials");
		
		System.out.println("Received request");
		
		eGDBCLI cli = new eGDBCLI();
		cli.execute(new String[] {
				managerHost,
				managerPort,
				managerSsl,
				userName,
				password,
				"-query", paramMap.get("query")});
		@SuppressWarnings("rawtypes")
		ArrayList clioutput = cli.getOutput();
		System.out.println("clioutput:"+clioutput);
		return DBUtilities.parseSqlQuery(clioutput);
	}
}
