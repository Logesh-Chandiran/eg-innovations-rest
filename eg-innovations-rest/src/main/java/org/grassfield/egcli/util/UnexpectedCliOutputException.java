package org.grassfield.egcli.util;

/**
 * Exception to throw when unexpected output is received from cli
 * @author Ramaiah Murugapandian
 *
 */
public class UnexpectedCliOutputException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * @param string Exception related text
	 */
	public UnexpectedCliOutputException(String string) {
		super(string);
	}

}
