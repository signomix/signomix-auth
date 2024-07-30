package com.signomix.auth.application.out;

import com.signomix.auth.adapter.out.EventLogRepository;
import com.signomix.common.User;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;


@ApplicationScoped
public class EventLogRepositoryPort {

    @Inject
    EventLogRepository eventLogRepository;
    

    public void saveLoginEvent(User user, String remoteAddress) {
        eventLogRepository.saveLoginEvent(user, remoteAddress);
    }

    public void saveLoginFailure(String login, String remoteAddress, int resultCode) {
        eventLogRepository.saveLoginFailure(login, remoteAddress, resultCode);
    }
}