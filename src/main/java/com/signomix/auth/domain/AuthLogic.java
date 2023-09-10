package com.signomix.auth.domain;

import org.jboss.logging.Logger;

import com.signomix.auth.application.out.AuthRepositoryPort;
import com.signomix.common.User;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuthLogic {

    @Inject
    Logger logger;

    @Inject
    AuthRepositoryPort authRepositoryPort;

    private long sessionTokenLifetime = 30; // minutes
    private long permanentTokenLifetime = 10* 365 * 24 * 60; // 10 years in minutes

    public String getSessionToken(String login, String password) {
        logger.info("getSessionToken: " + login + " " + password);
        User user = authRepositoryPort.getUserById(login);
        if (user != null && user.checkPassword(password) && (user.authStatus == User.IS_ACTIVE || user.authStatus==User.IS_CREATED)) {
            return authRepositoryPort.createSessionToken(user, sessionTokenLifetime).getToken();
        } else {
            return null;
        }
    }

    public User getUser(String token) {
        return authRepositoryPort.getUser(token);
    }

    public void removeSession(String token) {
        authRepositoryPort.removeSession(token);
    }

    public String createTokenForUser(User issuer, String uid, boolean permanent) {
        String token = authRepositoryPort.createTokenForUser(issuer, uid, permanent?permanentTokenLifetime:sessionTokenLifetime, permanent).getToken();
        User user=authRepositoryPort.getUser(token);
        logger.info("created token: "+user.uid+" "+user.authStatus);
        
        return token;
    }
}
