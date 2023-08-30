package com.signomix.auth.application.in;

import com.signomix.auth.domain.AuthLogic;
import com.signomix.common.User;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuthPort {

    @Inject
    AuthLogic authLogic;

    public String getSessionToken(String login, String password) {
        return authLogic.getSessionToken(login, password);
    }

    public User getUser(String token) {
        return authLogic.getUser(token);
    }

    public void removeSession(String token) {
        authLogic.removeSession(token);
    }
    
    
}
