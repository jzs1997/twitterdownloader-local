package com.example.twitterdownloader.repository;

import com.example.twitterdownloader.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findByUserId(String userId);

    @Modifying
    @Query("update User u set u.usageCount = u.usageCount + ?1 where u.userId = ?2")
    public void updateUser(Integer cnt, String userId);
}
