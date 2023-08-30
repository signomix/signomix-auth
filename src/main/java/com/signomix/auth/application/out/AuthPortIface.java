package com.signomix.auth.application.out;

import com.signomix.common.User;

public interface AuthPortIface {
    public String getSessionToken(String login, String password);
    public String createSessionToken(User user);
    public User getUser(String token);
    public User getUserById(String uid);
    public void removeSession(String token);
}
