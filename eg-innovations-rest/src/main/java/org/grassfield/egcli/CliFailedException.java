package org.grassfield.egcli;

public class CliFailedException extends RuntimeException {

	public CliFailedException(String output) {
		super(output);
	}

}
