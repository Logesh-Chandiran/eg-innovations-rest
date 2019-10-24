package org.grassfield.egcli;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.grassfield.egcli.entity.UnrecognizedAgentException;
import org.grassfield.egcli.entity.UnrecognizedComponentException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * CustomGlobalExceptionHandler for REST calls
 * @author Ramaiah Murugapandian
 *
 */
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
	// Let Spring handle the exception, we just override the status code
    @ExceptionHandler(CliPermissionException.class)
    public void springHandleNotFound(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.UNAUTHORIZED.value());
    }
    
    @ExceptionHandler(UnrecognizedAgentException.class)
    public void unrecognizedAgentException(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
    
    @ExceptionHandler(UnrecognizedComponentException.class)
    public void unrecognizedComponentException(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
    
}
