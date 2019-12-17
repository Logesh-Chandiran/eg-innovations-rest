package org.grassfield.egcli;

import java.util.Base64;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.grassfield.egcli.entity.Agent;
import org.grassfield.egcli.entity.Component;
import org.grassfield.egcli.entity.MaintenancePolicy;
import org.grassfield.egcli.entity.MaintenancePolicyDetails;
import org.grassfield.egcli.entity.EnableDisableTests;
import org.grassfield.egcli.entity.ManagedHosts;
import org.grassfield.egcli.entity.Response;
import org.grassfield.egcli.entity.TestsDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.eg.cli.CLIClientBase;

/**
 * REST controller for eG Admin CLI
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
            @RequestBody (required = false) Command command) throws Exception {
        logger.info("Received request");
        
        //PathValidator validator = new PathValidator();
        //action = validator.validateAction(action);
        //element = validator.validateElement(action, element);
        
        if (authorization == null) {
            throw new CliPermissionException("Authorization is not provided");
        }
        logger.info("Auth received");
        if (authorization.startsWith("Basic ")) {
            authorization = authorization.substring(6);
        }
        
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
        if (command != null) {
            parameters = command.getParameters();
            if (parameters != null && !parameters.isEmpty()) {
                for (String key:parameters.keySet()) {
                    ht.put(key, parameters.get(key));
                }
            }
        } else {
            logger.info("No command parameters received");
        }

        ht.put("username",userName);
        ht.put("password",password);
        ht.put("managerip",managerHost);
        ht.put("managerport",managerPort);
        ht.put("ssl",managerSsl);

        logger.info("Prepared the request map:"+ht);

        CLIClientBase cli = new CLIClientBase();
        List<String> al = cli.doExecuteForREST(ht);
        System.out.println("al:" + al);
        if (al == null) {
            logger.error("received no response from CLI");
            throw new CliFailedException("Unknown Error");
        } else if (!al.isEmpty()) {
            String output = al.get(0).trim();
            String outputlc = output.toLowerCase();
            if (outputlc.indexOf("user does not have privilege") != -1) {
                throw new CliPermissionException(output);
            }
            if (outputlc.startsWith("error"))
                throw new CliFailedException(output);
        }
        System.out.println("al size:" + al.size());
        switch(action + element) {
        case "showExternalAgent":
        case "showRemoteAgent":
            List<Agent> result = ResultParser.getAgents(al);
            return result;
        case "addRemoteAgent" :
        case "addExternalAgent" :
        case "deleteRemoteAgent" :
        case "deleteExternalAgent" : {
            List<Response> response = ResultParser.parseCliResponse(al);
            return response;
        }
        case "showComponent":
            if (parameters!=null) {
                List<Component> cList = ResultParser.getComponents(parameters.get("componenttype"),al);
                return cList;
            } else {
                throw new CliFailedException("componenttype parameter is missing");
            }
        case "addComponent" :
        case "modifyComponent" :
        case "deleteComponent" :
        case "manageComponent" :
        case "unmanageComponent" : {
            List<Response> response = ResultParser.parseCliResponse(al);
            return response;
        }
        case  "addQuickPolicyMaintenancePolicy" :
        case "modifyQuickPolicyMaintenancePolicy" :
        case "deleteQuickPolicyMaintenancePolicy": {
            List<Response> response = ResultParser.parseCliResponse(al);
            return response;
        }
        case "showMaintenancePolicy":
            List<MaintenancePolicy> cList = ResultParser.getMaintenancePolicyNames(al);
            return cList;
        case "showPolicyDetailsMaintenancePolicy" : {
            List<MaintenancePolicyDetails> list = ResultParser.getMaintenancePolicyDetails(al);
            return list;
        }
        case "showEnableDisableTests" : {
            ResultParser.separateTest(al);
            List<EnableDisableTests> list = ResultParser.getEnableDisableTests();
            return list;
        }
        case "enableEnableDisableTests" :
        case "disableEnableDisableTests" : {
            List<Response> response = ResultParser.parseCliResponse(al);
            return response;
        }
        case "showCLIShowManagedHosts" : {
            if(parameters != null) {
                List<ManagedHosts> list = ResultParser.getManagedHosts(al);
                return list;
            }
            throw new CliFailedException("Agent name parameter is missing.");
        }
        case "showShowTestDetails" : {
            List<TestsDetails> list = ResultParser.getTestDetails(al);
            return list;
        }
        case "includeTestsIncludeExcludeComponents" :    //IncludeTestsForComponent
        case "excludeTestsIncludeExcludeComponents" :    //ExcludeTestsForComponent
        case "includeIncludeExcludeComponents" :         //IncludeComponentsForTest
        case "excludeIncludeExcludeComponents" : {       //ExcludeComponentsForTest
            List<Response> response = ResultParser.parseCliResponse(al);
            return response;
        }
        case "AddUserMgmt" :
        case "DeleteUserMgmt" :
        case "ModifyUserMgmt" : 
        case "UserAssociateComponentUserMgmt" : {
            List<Response> response = ResultParser.parseCliResponse(al);
            return response;
        }
        case "addZone" :
        case "renameZone" :
        case "modifyZone" :
        case "deleteZone" : {
            List<Response> response = ResultParser.parseCliResponse(al);
            return response;
        }
        case "addGroup" :
        case "renameGroup" :
        case "modifyGroup" :
        case "deleteGroup" : {
            List<Response> response = ResultParser.parseCliResponse(al);
            return response;
        }
        }
        return null;
    }
}