package org.grassfield.egcli;

/**
 * Exception class for permission related eG CLI errors
 * @author Ramaiah Murugapandian
 *
 */
public class CliPermissionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * @param string exception message
     */
    public CliPermissionException(String string) {
        super(string);
    }

}
