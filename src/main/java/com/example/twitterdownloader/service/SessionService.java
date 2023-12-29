package com.example.twitterdownloader.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.session.Session;

public interface SessionService {
    public Session findSessionById(String sessionId);
    public void saveSession(Session session);
    public void deleteSessionById(String sessionId);
}
