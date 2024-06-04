package com.signomix.auth.adapter.out;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.signomix.auth.application.out.AuthPortIface;
import com.signomix.common.Token;
import com.signomix.common.TokenType;
import com.signomix.common.User;
import com.signomix.common.db.AuthDaoIface;
import com.signomix.common.db.IotDatabaseException;
import com.signomix.common.db.UserDaoIface;

import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuthRepository implements AuthPortIface {

    @Inject
    Logger logger;

    /*
     * @Inject
     * 
     * @DataSource("user")
     * AgroalDataSource userDataSource;
     * 
     * @Inject
     * 
     * @DataSource("auth")
     * AgroalDataSource authDataSource;
     */

    @Inject
    @DataSource("oltp")
    AgroalDataSource mainDataSource;

    UserDaoIface userDao;
    AuthDaoIface authDao;

    @ConfigProperty(name = "signomix.exception.api.unauthorized")
    String userNotAuthorizedException;
    @ConfigProperty(name = "signomix.database.type")
    String databaseType;

    void onStart(@Observes StartupEvent ev) {
        if (databaseType.equalsIgnoreCase("postgresql")) {
            userDao = new com.signomix.common.tsdb.UserDao();
            userDao.setDatasource(mainDataSource);
            authDao = new com.signomix.common.tsdb.AuthDao();
            authDao.setDatasource(mainDataSource);
        } /*
           * else {
           * userDao = new com.signomix.common.db.UserDao();
           * userDao.setDatasource(userDataSource);
           * authDao = new com.signomix.common.db.AuthDao();
           * authDao.setDatasource(authDataSource);
           * }
           */
        boolean ok = false;
        int counter = 0;
        int maxTries = 30;
        while (!ok && counter < maxTries) {
            logger.info("DB connection try: " + counter);
            try {
                userDao.createStructure();
                authDao.createStructure();
                ok = true;
            } catch (Exception e) {
                logger.error("DB connection problem.");
                e.printStackTrace();
            }
            if (!ok) {
                counter++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void removeSession(String token) {
        authDao.removeSession(token);
    }

    @Override
    public User getUser(String token, long sessionTokenLifetime, long permanentTokenLifetime) {
        try {
            String uid = authDao.getUserId(token, sessionTokenLifetime, permanentTokenLifetime);
            logger.info("getUser: " + uid);
            return userDao.getUser(uid);
        } catch (IotDatabaseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public User getUserById(String uid) {
        try {
            return userDao.getUser(uid);
        } catch (IotDatabaseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Token createTokenForUser(User issuer, String uid, long lifetime, boolean permanent,
            long sessionTokenLifetime, long permanentTokenLifetime) {
        return authDao.createTokenForUser(issuer, uid, lifetime, permanent, TokenType.SESSION, null);
    }

    @Override
    public Token getTokenById(String tokenId) {
        return authDao.findTokenById(tokenId);
    }

    @Override
    public Token createApiToken(User issuer, long lifetimeMinutes, String key) {
        try {
            return authDao.createApiToken(issuer, lifetimeMinutes, key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Token getApiToken(User user) {
        try {
            return authDao.getApiToken(user);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void removeApiToken(User user) {
        authDao.removeApiToken(user);
    }

}
