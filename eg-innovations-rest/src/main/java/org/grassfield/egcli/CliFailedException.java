package org.grassfield.egcli;

public class CliFailedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CliFailedException(String output) {
		super(output);
	}

}
