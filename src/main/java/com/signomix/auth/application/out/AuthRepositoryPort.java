package com.signomix.auth.application.out;

import com.signomix.auth.adapter.out.AuthRepository;
import com.signomix.common.User;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuthRepositoryPort implements AuthPortIface {

    @Inject
    AuthRepository authRepository;

    @Override
    public String createSessionToken(User user) {
        return authRepository.createSessionToken(user);
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
    public String getSessionToken(String login, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSessionToken'");
    }
    
}
