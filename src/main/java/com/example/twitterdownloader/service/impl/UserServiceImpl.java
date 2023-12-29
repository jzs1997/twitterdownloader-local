package com.example.twitterdownloader.service.impl;

import com.example.twitterdownloader.model.User;
import com.example.twitterdownloader.repository.UserRepository;
import com.example.twitterdownloader.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUser(Integer usageCountToAdd, String userId) {
        userRepository.updateUser(usageCountToAdd, userId);
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAllInBatch();
    }

}
