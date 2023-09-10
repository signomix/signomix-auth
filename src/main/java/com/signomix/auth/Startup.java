package com.signomix.auth;

import java.sql.DatabaseMetaData;

import org.jboss.logging.Logger;

import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@ApplicationScoped
public class Startup {

    @Inject
    Logger logger;

    @Inject
    @DataSource("user")
    AgroalDataSource userDataSource;

    @Inject
    @DataSource("auth")
    AgroalDataSource authDataSource;

    void onStart(@Observes StartupEvent ev) {
        logger.info("Starting up...");
        int counter = 0;
        int maxTries = 10;
        while (null == authDataSource && counter < maxTries) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            counter++;
        }
        if (null == authDataSource) {
            logger.error("DB connection failed. Shutting down.");
            Quarkus.asyncExit(1);
            return;
        }
        try {
            DatabaseMetaData metadata = authDataSource.getConnection().getMetaData();
            System.out.println("Connected to " + metadata.getDatabaseProductName() + " " + metadata.getDatabaseProductVersion());
            System.out.println(metadata.getDriverName() + " " + metadata.getDriverVersion());
            System.out.println(metadata.getURL());
            System.out.println(metadata.getUserName());
        } catch (Exception ex) {
            logger.error("DB connection problem.");
            ex.printStackTrace();
        }
        logger.info("DB connection OK");
    }
    
}
