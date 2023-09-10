package com.signomix.auth.application.out;

import org.jboss.logging.Logger;

import com.signomix.auth.adapter.out.AuthRepository;
import com.signomix.common.Token;
import com.signomix.common.User;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuthRepositoryPort implements AuthPortIface {

    @Inject
    AuthRepository authRepository;
    @Inject
    Logger logger;

    @Override
    public Token createSessionToken(User user, long lifetime) {
        logger.info("createSessionToken: " + user.uid + " " + lifetime);
        Token token = authRepository.createTokenForUser(user, user.uid, lifetime, false);
        
        logger.info("created user: " + authRepository.getUser(token.getToken()));
        return token;
    }

    @Override
    public void removeSession(String token) {
        authRepository.removeSession(token);
    }

    @Override
    public User getUser(String token) {
        return authRepository.getUser(token);
    }

    @Override
    public User getUserById(String uid) {
        return authRepository.getUserById(uid);
    }


    @Override
    public Token createTokenForUser(User issuer, String uid, long lifetime, boolean permanent) {
        User user = authRepository.getUserById(uid);
        if (user != null) {
            Token token = authRepository.createTokenForUser(issuer, user.uid, lifetime, permanent);
            token.setIssuer(issuer.uid);
            return token;
        } else {
            return null;
        }
        
    }


    
}
