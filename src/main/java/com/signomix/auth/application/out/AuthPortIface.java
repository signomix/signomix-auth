package com.signomix.auth.application.out;

import com.signomix.common.Token;
import com.signomix.common.User;

public interface AuthPortIface {
    //public String getSessionToken(String login, String password);
    //public Token createSessionToken(User user, long lifetime);
    public Token createTokenForUser(User issuer, String uid, long lifetime, boolean permanent, long sessionTokenLifetime, long permanentTokenLifetime);
    public User getUser(String token, long sessionTokenLifetime, long permanentTokenLifetime);
    public User getUserById(String uid);
    public void removeSession(String token);
    public Token getTokenById(String tokenId);
    public Token createApiToken(User issuer, long lifetimeMinutes);
    public Token getApiToken(User user);
}
