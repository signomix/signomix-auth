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

    /*
     * @Override
     * public Token createSessionToken(User user, long lifetime) {
     * logger.info("createSessionToken: " + user.uid + " " + lifetime);
     * Token token = authRepository.createTokenForUser(user, user.uid, lifetime,
     * false);
     * 
     * logger.info("created user: " + authRepository.getUser(token.getToken()));
     * return token;
     * }
     */

    @Override
    public void removeSession(String token) {
        authRepository.removeSession(token);
    }

    @Override
    public User getUser(String token, long sessionTokenLifetime, long permanentTokenLifetime) {
        return authRepository.getUser(token, sessionTokenLifetime, permanentTokenLifetime);
    }

    @Override
    public User getUserById(String uid) {
        return authRepository.getUserById(uid);
    }

    @Override
    public Token createTokenForUser(User issuer, String uid, long lifetime, boolean permanent,
            long sessionTokenLifetime,
            long permanentTokenLifetime) {
        User user = authRepository.getUserById(uid);
        if (user != null) {
            Token token = authRepository.createTokenForUser(issuer, user.uid, lifetime, permanent, sessionTokenLifetime,
                    permanentTokenLifetime);
            token.setIssuer(issuer.uid);
            return token;
        } else {
            logger.info("user not found: " + uid);
            return null;
        }

    }

    @Override
    public Token getTokenById(String tokenId) {
        return authRepository.getTokenById(tokenId);
    }

    @Override
    public Token createApiToken(User issuer, long lifetimeMinutes, String key) {
        return authRepository.createApiToken(issuer, lifetimeMinutes, key);
    }

    @Override
    public Token getApiToken(User user) {
        return authRepository.getApiToken(user);
    }

    @Override
    public void removeApiToken(User user) {
        authRepository.removeApiToken(user);
    }

}
