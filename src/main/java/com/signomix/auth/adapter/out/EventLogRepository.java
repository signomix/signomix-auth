package com.signomix.auth.adapter.out;

import org.jboss.logging.Logger;

import com.signomix.common.User;
import com.signomix.common.db.EventLogDaoIface;

import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@ApplicationScoped
public class EventLogRepository {

    @Inject
    Logger logger;

    @Inject
    @DataSource("oltp")
    AgroalDataSource mainDataSource;

    EventLogDaoIface eventLogDao;

    void onStart(@Observes StartupEvent ev) {
        eventLogDao = new com.signomix.common.tsdb.EventLogDao();
        eventLogDao.setDatasource(mainDataSource);
    }

    public void saveLoginEvent(User user, String remoteAddress) {
        eventLogDao.saveLoginEvent(user, remoteAddress, 0);
    }

    public void saveLoginFailure(String login, String remoteAddress, int resultCode) {
        eventLogDao.saveLoginFailure(login, remoteAddress, resultCode);
    }
}