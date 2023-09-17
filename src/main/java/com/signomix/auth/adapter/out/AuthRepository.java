package com.signomix.auth.adapter.out;

import java.sql.DatabaseMetaData;

import org.eclipse.microprofile.config.ConfigProvider;
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

    void onStart(@Observes StartupEvent ev) {
        userDao = new UserDao();
        userDao.setDatasource(userDataSource);
        authDao = new AuthDao();
        authDao.setDatasource(authDataSource);
    }

    private boolean isPostgres() {
        String dbKind = ConfigProvider.getConfig().getValue("quarkus.datasource.db-kind", String.class);
        if (dbKind.equals("postgresql")) {
            return true;
        } else if (dbKind.equals("h2")) {
            return false;
        } else {
            throw new RuntimeException("Unknown database type: " + dbKind);
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
        return authDao.createTokenForUser(issuer, uid, lifetime, permanent);
    }

}
