package com.signomix.auth.adapter.out;

import java.sql.DatabaseMetaData;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.signomix.auth.application.out.AuthPortIface;
import com.signomix.common.Token;
import com.signomix.common.User;
import com.signomix.common.db.AuthDao;
import com.signomix.common.db.AuthDaoIface;
import com.signomix.common.db.IotDatabaseException;
import com.signomix.common.db.UserDao;
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

    @Inject
    @DataSource("user")
    AgroalDataSource userDataSource;

    @Inject
    @DataSource("auth")
    AgroalDataSource authDataSource;

    UserDaoIface userDao;
    AuthDaoIface authDao;

    @ConfigProperty(name = "signomix.exception.api.unauthorized")
    String userNotAuthorizedException;

    // TODO: throwing exceptions

    void onStart(@Observes StartupEvent ev) {
        userDao = new UserDao();
        userDao.setDatasource(userDataSource);
        authDao = new AuthDao();
        authDao.setDatasource(authDataSource);
    }

    /*
     * @Override
     * public String getSessionToken(String login, String password) {
     * User user;
     * try {
     * user = userDao.getUser(login, password);
     * } catch (IotDatabaseException e) {
     * throw new RuntimeException(userNotAuthorizedException);
     * }
     * if(user==null){
     * throw new RuntimeException(userNotAuthorizedException);
     * }
     * return authDao.createSession(user);
     * }
     */

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

    /*
     * @Override
     * public Token createSessionToken(User user) {
     * return authDao.createSession(user, 0L);
     * }
     */

/*     @Override
    public Token createSessionToken(User user, long lifetime) {
        logger.info("createSessionToken: " + user.uid);
        return authDao.createSession(user, lifetime);
    } */

    @Override
    public Token createTokenForUser(User issuer, String uid, long lifetime, boolean permanent, long sessionTokenLifetime, long permanentTokenLifetime) {
/*         try {
            DatabaseMetaData metadata = authDao.getDataSource().getConnection().getMetaData();
            System.out.println("Connected to " + metadata.getDatabaseProductName() + " " + metadata.getDatabaseProductVersion());
            System.out.println(metadata.getDriverName() + " " + metadata.getDriverVersion());
            System.out.println(metadata.getURL());
            System.out.println(metadata.getUserName());
        } catch (Exception ex) {
            logger.error("DB connection problem.");
            ex.printStackTrace();
        } */
        return authDao.createTokenForUser(issuer, uid, lifetime, permanent);
    }

}
