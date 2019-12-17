package org.grassfield.egcli.util;

import java.io.InputStream;
import java.util.Properties;

import org.grassfield.egcli.InvalidPathException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This PathValidator class validates {action} & {element} in URL
 * @author Logesh
 * 
 */

public class PathValidator {
    
    private static Logger logger = LoggerFactory.getLogger(PathValidator.class);
    private static Properties prop = new Properties();
    InputStream is = this.getClass().getResourceAsStream("/eGOperations.properties");
    
    public String validateAction(String action) throws Exception {
        action = action.toLowerCase();
        if(is != null) {
            prop.load(is);
        }
        if(prop.containsKey(action)) {
            logger.info("Valid : " + action);
            return action;
        }
        throw new InvalidPathException("Invalid path {" + action + "}");
    }
    
    public String validateElement(String action, String element) throws Exception {
        element = element.toLowerCase();
        String line = prop.getProperty(action);
        String[] elements = line.split(",");
        for(String value : elements) {
            if(element.equalsIgnoreCase(value)) {
                element = value;
                logger.info("Valid : " + element);
                return element;
            }
        }
        throw new InvalidPathException("Invalid path {" + element + "}");
    }
}