package com.signomix.auth.domain;

import java.time.temporal.ChronoUnit;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.signomix.auth.application.out.AuthRepositoryPort;
import com.signomix.common.Token;
import com.signomix.common.User;

import io.questdb.client.Sender;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuthLogic {

    @Inject
    Logger logger;

    @Inject
    AuthRepositoryPort authRepositoryPort;

    @ConfigProperty(name = "questdb.client.config")
    String questDbConfig;

    // TODO: move to config
    private long sessionTokenLifetime = 30; // minutes
    private long permanentTokenLifetime = 10 * 365 * 24 * 60; // 10 years in minutes

    public String getSessionToken(String login, String password, String remoteAddress) {
        logger.info("getSessionToken: " + login + " " + password);
        User user = authRepositoryPort.getUserById(login);
        if (user == null) {
            logger.info("user not found: " + login);
            saveLoginFailure(login, remoteAddress, 1);
            return null;
        }
        if (!user.checkPassword(password)) {
            logger.info("wrong password: " + login);
            saveLoginFailure(login, remoteAddress, 2);
            return null;
        }
        if (user.authStatus == User.IS_ACTIVE || user.authStatus == User.IS_CREATED) {
            // return authRepositoryPort.createSessionToken(user,
            // sessionTokenLifetime).getToken();
            saveLoginEvent(user, remoteAddress);
            return authRepositoryPort.createTokenForUser(user, user.uid, sessionTokenLifetime, false,
                    sessionTokenLifetime, permanentTokenLifetime).getToken();
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
        String token = authRepositoryPort
                .createTokenForUser(issuer, uid, permanent ? permanentTokenLifetime : sessionTokenLifetime, permanent,
                        sessionTokenLifetime, permanentTokenLifetime)
                .getToken();
        User user = authRepositoryPort.getUser(token, sessionTokenLifetime, permanentTokenLifetime);
        logger.info("created token: " + user.uid + " " + user.authStatus);

        return token;
    }

    public Token findToken(String tokenId){
        return authRepositoryPort.getTokenById(tokenId);
    }

    public Token getApiToken(User user) {
        return authRepositoryPort.getApiToken(user);
    }

    public Token createApiToken(User issuer, long lifetimeMinutes, String key) {
        return authRepositoryPort.createApiToken(issuer, lifetimeMinutes, key);
    }

    public void removeApiToken(User issuer) {
        authRepositoryPort.removeApiToken(issuer);
    }

    private void saveLoginEvent(User user, String remoteAddress) {
        try (/* Sender sender = Sender.builder().address("quest:9009").build() */
        Sender sender=Sender.fromConfig(questDbConfig)) {
            sender.table("user_events")
                    .symbol("login", user.uid)
                    .symbol("event_type","login_ok")
                    .symbol("client_ip", remoteAddress)
                    .longColumn("organization_id", user.organization)
                    .longColumn("error_code", 0)
                    .at(System.currentTimeMillis(), ChronoUnit.MILLIS);
        } catch (Exception e) {
            logger.error("saveLoginEvent: " + e.getMessage());
        }
    }
    private void saveLoginFailure(String login, String remoteAddress, int reason) {
        try (/* Sender sender = Sender.builder().address("quest:9009").build() */
        Sender sender=Sender.fromConfig(questDbConfig)) {
            sender.table("user_events")
                    .symbol("login", login)
                    .symbol("event_type","login_failure")
                    .symbol("client_ip", remoteAddress)
                    .longColumn("organization_id", -1)
                    .longColumn("error_code", reason)
                    .at(System.currentTimeMillis(), ChronoUnit.MILLIS);
        } catch (Exception e) {
            logger.error("saveLoginEvent: " + e.getMessage());
        }
    }
}
