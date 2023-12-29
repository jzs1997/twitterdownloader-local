package com.example.twitterdownloader.requests;

import java.io.Serializable;
import java.sql.Timestamp;

public class UserRequest implements Serializable {
    private String sessionId;
    private Integer usageCount;
    private Timestamp expireTime;
}
