package com.signomix.auth.application.in;

import org.jboss.logging.Logger;

import com.signomix.auth.domain.AuthLogic;
import com.signomix.common.Token;
import com.signomix.common.User;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuthPort {
    @Inject
    Logger logger;

    @Inject
    AuthLogic authLogic;

     public String getSessionToken(String login, String password, String remoteAddress) {
        return authLogic.getSessionToken(login, password, remoteAddress);
    }

    public String getTokenForUser(User issuer, String uid, boolean permanent) {
        return authLogic.createTokenForUser(issuer, uid, permanent);
    }

    public User getUser(String token) {
        if(token!=null && token.endsWith("/")){
            token=token.substring(0,token.length()-1);
        }
        return authLogic.getUser(token);
    }

    public void removeSession(String token) {
        authLogic.removeSession(token);
    }

    public Token findToken(String tokenId){
        return authLogic.findToken(tokenId);
    }

    public Token getApiToken(User user) {
        return authLogic.getApiToken(user);
    }

    public Token createApiToken(User issuer, long lifetimeMinutes, String key) {
        return authLogic.createApiToken(issuer, lifetimeMinutes, key);
    }

    public void removeApiToken(User issuer) {
        authLogic.removeApiToken(issuer);
    }

    public String getUserSessionToken(String login, String adminSessionTokenID, String remoteAddress){
        return authLogic.getUserSessionToken(login, adminSessionTokenID, remoteAddress);
    }
    
    
}
