package org.grassfield.egcli;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.grassfield.egcli.entity.Agent;
import org.grassfield.egcli.entity.Component;
import org.grassfield.egcli.entity.MaintenancePolicy;
import org.grassfield.egcli.entity.MaintenancePolicyDetails;
import org.grassfield.egcli.entity.EnableDisableTests;
import org.grassfield.egcli.entity.ManagedHosts;
import org.grassfield.egcli.entity.Response;
import org.grassfield.egcli.entity.TestDetails;
import org.grassfield.egcli.entity.TestType;
import org.grassfield.egcli.entity.TestsDetails;
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
    
    /**
     * Gets the components for given types.
     *
     * @param al the list of string
     * @return the Component
     * @since 1.0
     */
    public static List<Component> getComponents(String type, List<String> al) {
        System.out.println("al:" + al);
        List<Component> result = new ArrayList<Component>();
        for (String line:al) {
            if (line.startsWith("HostIP~Hash~")) {
                logger.info("skipping header "+line);
                continue;
            }
            result.add(new Component(type, line, "~Hash~"));
        }
        return result;
    }

    /**
     * Gets the maintenance policy names.
     *
     * @param al the list of string
     * @return the MaintenancePolicy
     * @since 1.0
     */
    public static List<MaintenancePolicy> getMaintenancePolicyNames(List<String> al) {
        List<MaintenancePolicy> result = new ArrayList<MaintenancePolicy>();
        MaintenancePolicy policyName = new MaintenancePolicy();
        List<String> list = new ArrayList<String>();
        for (String line:al) {
            if (line.startsWith("Policy Names~Hash~")) {
                logger.info("skipping header line "+line);
                continue;
            }
            list.add(line);
        }
        policyName.setPolicyName(list);
        result.add(policyName);
        return result;
    }
    
   /**
    * Gets the list of maintenance policy details.
    *
    * @param al the list of string
    * @return the MaintenancePolicy
    * @since 1.0
    */
    public static List<MaintenancePolicyDetails> getMaintenancePolicyDetails(List<String> al) {
        List<MaintenancePolicyDetails> list = new ArrayList<MaintenancePolicyDetails>();
        MaintenancePolicyDetails details = null;
        for(String line : al) {
            line = line.trim();
            if(line.equals("~Hash~newLine_CSV") || line.equals("newLine_CSV~Hash~") || line.equals("newLine")) {
                continue;
            } else if(line.contains("Policy Name")) {
                if(details != null) {
                    list.add(details);
                }
                details = new MaintenancePolicyDetails();
                String name = line.substring(line.indexOf("~Hash~") + 6);
                details.setPolicyName(name);
            } else if(line.contains("Policy Status")) {
                String status = line.substring(line.indexOf("~Hash~") + 6);
                details.setPolicyStatus(status);
            } else if(line.contains("NextScheduleDate")) {
                String nextScheduleDate = line.substring(line.indexOf("~Hash~") + 6);
                details.setNextScheduleDate(nextScheduleDate.trim());
            }
        }
        if(details != null) {
            list.add(details);
        }
        return list;
    }
    
    static List<String> enabledTest = null;
    static List<String> disabledTest = null;
    
    /**
     * Gets the list of maintenance policy names.
     *
     * @param al the list of string in AgentName~Hash~HostIP format
     * @return the MaintenancePolicy
     * @since 1.0
     */
    public static void separateTest(List<String> al) {
        if(al.indexOf("Enabled Tests~Hash~") != -1 && al.indexOf("Disabled Tests") != -1) {
            enabledTest = al.subList(0, al.indexOf("Disabled Tests"));
            disabledTest = al.subList(al.indexOf("Disabled Tests"), al.size());
        } else if(al.indexOf("Enabled Tests~Hash~") != -1) {
            enabledTest = al;
            disabledTest = null;
        } else if(al.indexOf("Disabled Tests~Hash~") != -1) {
            disabledTest = al;
            enabledTest = null;
        }
    }

    /**
     * Gets the list of enabled & disabled tests for given component type.
     *
     * @return the EnableDisableTests
     * @since 1.0
     */
    public static List<EnableDisableTests> getEnableDisableTests() {
        List<EnableDisableTests> result = new ArrayList<EnableDisableTests>();
        EnableDisableTests test = new EnableDisableTests();
        if(enabledTest != null) {
            test.setEnabledTests(getEnabledTests());
        }
        if(disabledTest != null) {
            test.setDisabledTests(getDisabledTests());
        }
        result.add(test);
        return result;
    }

    /**
     * Gets the list of enabled tests for given component type.
     *
     * @param al the list of string
     * @return the EnableDisableTests
     * @since 1.0
     */
    public static List<String> getEnabledTests() {
        List<String> enableTests = new ArrayList<String>();
        for(String line : enabledTest) {
            if(line.startsWith("Enabled") || line.startsWith("newLine") || line.startsWith("dash")) {
                logger.info("skipping keyword : " + line);
                continue;
            }
            enableTests.add(line);
        }
        return enableTests;
    }

    /**
     * Gets the list of disabled tests for given component type.
     *
     * @param al the list of string
     * @return the EnableDisableTests
     * @since 1.0
     */
    public static List<String> getDisabledTests() {
        List<String> disableTests = new ArrayList<String>();
        for(String line : disabledTest) {
            if(line.startsWith("Disabled") || line.startsWith("newLine") || line.startsWith("dash")) {
                logger.info("skipping keyword : " + line);
                continue;
            }
            disableTests.add(line);
        }
        return disableTests;
    }
    
    /**
     * Gets the list of managed hosts by agent.
     *
     * @param al the list of string
     * @return the ManagedHosts
     * @since 1.0
     */
    public static List<ManagedHosts> getManagedHosts(List<String> al) {
        List<ManagedHosts> managedHostsList = new ArrayList<ManagedHosts>();
        ManagedHosts managedHost = new ManagedHosts();
        List<String> temp = new ArrayList<String>();
        String keyword = al.get(0);
        if(keyword.startsWith("Hosts Managed By")) {
            String agentName = keyword.substring(keyword.indexOf("(") + 1, keyword.lastIndexOf(")"));
            managedHost.setAgentName(agentName);
        }
        for(String line : al) {
            if(line.startsWith("Hosts Managed By") || line.startsWith("newLine") || line.equalsIgnoreCase("dash")) {
                logger.info("skipping keyword : " + line);
                continue;
            }
            temp.add(line);
        }
        managedHost.setManagedHost(temp);
        managedHostsList.add(managedHost);
        return managedHostsList;
    }
    
    /**
     * Gets the test details.
     *
     * @param al the list of string
     * @return the TestsDetails
     * @since 1.0
     */
    public static List<TestsDetails> getTestDetails(List<String> al) {
        List<TestsDetails> list = new ArrayList<TestsDetails>();
        TestsDetails testsDetails = null;
        TestType testType = null;
        TestDetails testDetails = null;
        String type = null;
        Map<String, String> tempMap = null;
        for(String line : al) {
            if(line.trim().equals("~Hash~newLine_CSV") || line.equals("newLine")) {
                continue;
            } else if(line.startsWith("Component name")) {
                logger.info("***component name : " + line);
                if(tempMap != null && testType != null && testsDetails != null) {
                    testDetails.setDetails(tempMap);
                    if(type.equalsIgnoreCase("Performance")) {
                        testType.addPerformanceTest(testDetails);
                    } else {
                        testType.addConfigurationTest(testDetails);
                    }
                    tempMap = null;
                    testsDetails.setTestType(testType);
                    list.add(testsDetails);
                }
                testsDetails = new TestsDetails();
                testType = new TestType();
                String name = line.substring(line.indexOf(": ") + 2, line.indexOf("~Hash~"));
                testsDetails.setComponentName(name);
            } else if(line.startsWith("Component type")) {
                logger.info("***component type : " + line);
                String componentType = line.substring(line.indexOf(": ") + 2, line.indexOf("~Hash~"));
                testsDetails.setComponentType(componentType);
            } else if(line.startsWith("Test type")) {
                logger.info("***test type : " + line);
                if(tempMap != null && testDetails != null) {
                    testDetails.setDetails(tempMap);
                    if(type.equalsIgnoreCase("Performance")) {
                        testType.addPerformanceTest(testDetails);
                    } else {
                        testType.addConfigurationTest(testDetails);
                    }
                    tempMap = null;
                }
                type = line.substring(line.indexOf(": ") + 2);
            } else if(line.startsWith("Test name")) {
                if(tempMap != null && testDetails != null) {
                    testDetails.setDetails(tempMap);
                    if(type.equalsIgnoreCase("Performance")) {
                        testType.addPerformanceTest(testDetails);
                    } else {
                        testType.addConfigurationTest(testDetails);
                    }
                }
                testDetails = new TestDetails();
                tempMap = new LinkedHashMap<String, String>();
                String testName = line.substring(line.indexOf(": ") + 2);
                testDetails.setTestName(testName);
            } else {
                String[] tokens = line.split("~Hash~");
                tempMap.put(tokens[0], tokens[1]);
            }
        }
        if(tempMap != null && testDetails != null && testType != null && testsDetails != null) {
            testDetails.setDetails(tempMap);
            if(type.equalsIgnoreCase("Performance")) {
                testType.addPerformanceTest(testDetails);
            } else {
                testType.addConfigurationTest(testDetails);
            }
            testsDetails.setTestType(testType);
            list.add(testsDetails);
        }
        return list;
    }
    
    /**
     * Gets the success response in format messageStatus & message.
     *
     * @param al the list of string
     * @return the Response
     * @since 1.0
     */
    public static List<Response> parseCliResponse(List<String> list) {
        List<Response> response = new ArrayList<Response>();
        String line = list.get(0);
        if(line.startsWith("Result") || line.startsWith("Error")) {
            String[] tokens = line.split(":");
            Response resp = new Response();
            tokens[0] = tokens[0].trim().equalsIgnoreCase("Result") ? "Succeed" : "Failed";
            resp.setMessageStatus(tokens[0].trim());
            resp.setMessage(tokens[1].trim());
            response.add(resp);
        }
        return response;
    }

}