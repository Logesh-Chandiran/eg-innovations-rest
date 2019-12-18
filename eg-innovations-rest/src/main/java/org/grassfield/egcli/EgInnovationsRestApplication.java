package org.grassfield.egcli;

import org.grassfield.egcli.util.PathValidator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot application
 * @author Ramaiah Murugapandian
 *
 */
@SpringBootApplication
public class EgInnovationsRestApplication {

    public static void main(String[] args) throws Throwable {
        SpringApplication.run(EgInnovationsRestApplication.class, args);
        PathValidator.readeGOperations();
    }

}