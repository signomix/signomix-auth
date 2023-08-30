package com.signomix.auth.adapter.out;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.signomix.auth.application.out.AuthPortIface;
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
public class AuthRepository implements AuthPortIface{

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

    //TODO: throwing exceptions

    void onStart(@Observes StartupEvent ev) {
        userDao = new UserDao();
        userDao.setDatasource(userDataSource);
        authDao = new AuthDao();
        authDao.setDatasource(authDataSource);
    }

    @Override
    public String getSessionToken(String login, String password) {
        User user;
        try {
            user = userDao.getUser(login, password);
        } catch (IotDatabaseException e) {
            throw new RuntimeException(userNotAuthorizedException);
        }
        if(user==null){
            throw new RuntimeException(userNotAuthorizedException);
        }
        return authDao.createSession(user);
    }

    @Override
    public void removeSession(String token) {
        authDao.removeSession(token);
    }

    @Override
    public User getUser(String token) {
        try {
            return userDao.getUser(authDao.getUser(token));
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
    public String createSessionToken(User user) {
        return authDao.createSession(user);
    }
    
}
