package org.grassfield.egcli;

import java.util.Map;

/**
 * Pojo representing inpput command parameters
 * @author Ramaiah Murugapandian
 *
 */
public class Command {
	/**
	 * type of element chosen to interact with eG manager
	 * for e.g. RemoteAgent
	 */
	private String element;
	/**
	 * type of action 
	 * for e.g. show
	 */
	private String action;
	private Map<String, String> parameters;
	public String getElement() {
		return element;
	}
	public void setElement(String element) {
		this.element = element;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public Map<String, String> getParameters() {
		return parameters;
	}
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	@Override
	public String toString() {
		return "Command [element=" + element + ", action=" + action + ", parameters=" + parameters + "]";
	}

}
