package com.example.twitterdownloader.controller;

import com.example.twitterdownloader.model.User;
import com.example.twitterdownloader.service.TweetResourceExtractionService;
import com.example.twitterdownloader.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Enumeration;
import java.util.HashSet;

import static com.example.twitterdownloader.globals.GlobalVars.*;
import static com.example.twitterdownloader.globals.SessionAttrs.*;



import static java.util.UUID.randomUUID;

@RequestMapping("/api/v1")
@RestController
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;
    private final HttpSession session;
    private final TweetResourceExtractionService extractionService;
    private static final Logger logger = LogManager.getLogger();

    @GetMapping("")
    public ResponseEntity<String> initialize(HttpServletRequest request, HttpServletResponse response){

        String sessionId = session.getId();
        session.setMaxInactiveInterval(SESSION_MAX_INTERVAL);
        logger.info("Session Id: " + session.getId());

        session.setAttribute(TOTAL_USAGE_COUNT, 0);
        session.setAttribute(EXTRACTED_FILES, new HashSet<String>());
        session.setAttribute(CONVERTED_FILES, new HashSet<String>());

        Enumeration<String> enu = session.getAttributeNames();
        while(enu.hasMoreElements()){
            System.out.println(enu.nextElement());
        }

        Cookie cookie = retrieveCookieFromRequest(request);
//        HttpHeaders headers = new HttpHeaders();
//        headers.setAccessControlAllowOrigin("*");
        if(cookie != null && cookie.getValue() != null){
            System.out.println("cookie value: " + cookie.getValue());
            User user = userService.findByUserId(cookie.getValue());
            if(user != null){
                String userId = user.getUserId();
                logger.info("Logged in user: " + userId);
                return ResponseEntity.ok().build();
            }else{
                String userId = createNewUser(cookie.getValue(), 0);
                return ResponseEntity.ok().build();
            }
        }

        String userId = createNewUser();
        logger.info("New User, id: " + userId);
        session.setAttribute(USER_ID, userId);

        cookie = new Cookie(NAME_COOKIE, userId);
        cookie.setPath("/");
        cookie.setMaxAge(1 * 24 * 60 *60);

        // only for development mode
        cookie.setAttribute("SameSite", "None; Secure");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
//        header.setAccessControlAllowCredentials(true);
        return ResponseEntity.ok().build();
    }

    private String retrieveSessionIdFromCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        String sessionId = null;
        if(cookies != null){
            for(Cookie cookie: cookies){
                if(cookie.getName().equals(NAME_COOKIE)){
                    sessionId = cookie.getValue();
                }
            }
        }
        return  sessionId;
    }

    private Cookie retrieveCookieFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(NAME_COOKIE)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    private String createNewUser(){
        String userId = randomUUID().toString();
        User newUser = User.builder()
                .userId(userId)
                .usageCount(0)
                .build();
        userService.saveUser(newUser);
        return userId;
    }

    private String createNewUser(String userId, int usageCount){
        User newUser = User.builder()
                .userId(userId)
                .usageCount(usageCount)
                .build();
        userService.saveUser(newUser);
        return userId;
    }
}
