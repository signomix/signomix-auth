package com.signomix.auth.application.in;

import org.jboss.logging.Logger;

import com.signomix.auth.domain.AuthLogic;
import com.signomix.common.User;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuthPort {
    @Inject
    Logger logger;

    @Inject
    AuthLogic authLogic;

    public String getSessionToken(String login, String password) {
        logger.info("getSessionToken: " + login + " " + password);
        return authLogic.getSessionToken(login, password);
    }

    public String getTokenForUser(User issuer, String uid, boolean permanent) {
        return authLogic.createTokenForUser(issuer, uid, permanent);
    }

    public User getUser(String token) {
        return authLogic.getUser(token);
    }

    public void removeSession(String token) {
        authLogic.removeSession(token);
    }
    
    
}
