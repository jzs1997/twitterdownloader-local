package com.example.twitterdownloader.service;

import com.example.twitterdownloader.model.User;
import com.example.twitterdownloader.requests.UserRequest;
import jakarta.servlet.http.HttpSession;

public interface UserService {
    public User findByUserId(String userId);
    public void saveUser(User user);
    public void updateUser(Integer usageCountToAdd, String userId);
    public void deleteAll();
}
