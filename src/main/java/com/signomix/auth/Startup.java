package com.signomix.auth;

import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Startup {

    @Inject
    Logger logger;

}
