package com.example.twitterdownloader.config;

import com.example.twitterdownloader.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ScheduledDataCleaning {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private UserService userService;

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteAllFromUserTable(){
        logger.info("Deleteing user data at: " + new Date());
        userService.deleteAll();
    }
}
