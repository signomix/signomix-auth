package com.signomix.auth.application.out;

import com.signomix.common.Token;
import com.signomix.common.User;

public interface AuthPortIface {
    //public String getSessionToken(String login, String password);
    public Token createSessionToken(User user, long lifetime);
    public Token createTokenForUser(User issuer, String uid, long lifetime, boolean permanent);
    public User getUser(String token);
    public User getUserById(String uid);
    public void removeSession(String token);
}
