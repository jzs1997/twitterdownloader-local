package com.example.twitterdownloader.service.impl;

import com.example.twitterdownloader.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;
    @Override
    public Session findSessionById(String sessionId) {
        return sessionRepository.findById(sessionId);
    }

    @Override
    public void saveSession(Session session) {
        sessionRepository.save(session);
    }

    @Override
    public void deleteSessionById(String sessionId){
        sessionRepository.deleteById(sessionId);
    }
}
