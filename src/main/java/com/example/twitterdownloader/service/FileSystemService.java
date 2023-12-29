package com.example.twitterdownloader.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileSystemService {
    public boolean store(MultipartFile file);
}
