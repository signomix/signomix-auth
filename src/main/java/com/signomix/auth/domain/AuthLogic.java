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

    // TODO: move to config
    private long sessionTokenLifetime = 30; // minutes
    private long permanentTokenLifetime = 10* 365 * 24 * 60; // 10 years in minutes

     public String getSessionToken(String login, String password) {
        logger.info("getSessionToken: " + login + " " + password);
        User user = authRepositoryPort.getUserById(login);
        if(user==null){
            logger.info("user not found: " + login);
            return null;
        }
        if(!user.checkPassword(password)){
            logger.info("wrong password: " + login);
            return null;
        }
        if (user.authStatus == User.IS_ACTIVE || user.authStatus==User.IS_CREATED) {
            //return authRepositoryPort.createSessionToken(user, sessionTokenLifetime).getToken();
            return authRepositoryPort.createTokenForUser(user, user.uid, sessionTokenLifetime, false, sessionTokenLifetime, permanentTokenLifetime).getToken();
        } else {
            logger.info("user not active: " + login + " status: " + user.authStatus);
            return null;
        }
    }

    public User getUser(String token) {
        return authRepositoryPort.getUser(token, sessionTokenLifetime, permanentTokenLifetime);
    }

    public void removeSession(String token) {
        authRepositoryPort.removeSession(token);
    }

    public String createTokenForUser(User issuer, String uid, boolean permanent) {
        String token = authRepositoryPort.createTokenForUser(issuer, uid, permanent?permanentTokenLifetime:sessionTokenLifetime, permanent, sessionTokenLifetime, permanentTokenLifetime).getToken();
        User user=authRepositoryPort.getUser(token, sessionTokenLifetime, permanentTokenLifetime);
        logger.info("created token: "+user.uid+" "+user.authStatus);
        
        return token;
    }
}
