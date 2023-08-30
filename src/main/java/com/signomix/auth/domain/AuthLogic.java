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

    public String getSessionToken(String login, String password) {
        User user = authRepositoryPort.getUser(login);
        if (user != null && user.checkPassword(password) && (user.authStatus == User.IS_ACTIVE || user.authStatus==User.IS_CREATED)) {
            return authRepositoryPort.createSessionToken(user);
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
}
