package com.example.twitterdownloader.config;

import com.example.twitterdownloader.service.UserService;
import com.example.twitterdownloader.utils.FileResolver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.session.Session;
import org.springframework.session.events.SessionCreatedEvent;
import org.springframework.session.events.SessionDeletedEvent;
import org.springframework.session.events.SessionDestroyedEvent;
import org.springframework.session.events.SessionExpiredEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

import static com.example.twitterdownloader.globals.SessionAttrs.*;
import static com.example.twitterdownloader.globals.GlobalVars.*;

@Component
public class MySpringHttpSessionListener {

    private static final Logger logger = LogManager.getLogger();
    @EventListener
    public void processSessionCreatedEvent(SessionCreatedEvent event) {
        System.out.println("created: " + event.getSessionId());
    }

    @EventListener
    public void processSessionDeletedEvent(SessionDeletedEvent event) {
        System.out.println("deleted: " + event.getSessionId());
    }

    @EventListener
    public void processSessionDestroyedEvent(SessionDestroyedEvent event) {
        Session s = event.getSession();
        if(s == null) return ;

        Set<String> extractedToDelete = (Set<String>)s.getAttribute(EXTRACTED_FILES);
        Set<String> convertedToDelete = (Set<String>)s.getAttribute(CONVERTED_FILES);

        try {
            System.out.println("Deleting files1");
            FileResolver.removeFiles(FILEPATH_EXTRACTED, extractedToDelete);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            System.out.println("Deleting files2");
            FileResolver.removeFiles(FILEPATH_CONVERTED, convertedToDelete);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String userId = (String) s.getAttribute(USER_ID);
        int usage_cnt = (int)s.getAttribute(TOTAL_USAGE_COUNT);
        System.out.println(extractedToDelete);
        System.out.println(convertedToDelete);
        System.out.println(userId);
        System.out.println(usage_cnt);

        if(userId != null){
            System.out.println("Adding count");
            UserService userService = SpringContext.getBean(UserService.class);
            userService.updateUser(usage_cnt, userId);
        }
        System.out.println("destroyed: " + event.getSessionId());
    }

    @EventListener
    public void processSessionExpiredEvent(SessionExpiredEvent event){
        logger.info("Session Expired: " + event.getSessionId());

    }
}
