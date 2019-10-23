package org.grassfield.egcli;

/**
 * Exception class for CLI failures
 * @author Ramaiah Murugapandian
 *
 */
public class CliFailedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CliFailedException(String output) {
		super(output);
	}

}
