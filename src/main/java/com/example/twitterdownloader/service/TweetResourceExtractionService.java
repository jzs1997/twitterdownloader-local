package com.example.twitterdownloader.service;

import java.util.List;

public interface TweetResourceExtractionService {
    public List<String> resourceExtraction(String statusId);
}
