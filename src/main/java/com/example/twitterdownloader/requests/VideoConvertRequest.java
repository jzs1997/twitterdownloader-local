package com.example.twitterdownloader.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
public class VideoConvertRequest {
    private final String filename;
    private final String width;
    private final String height;
    private final String flag;
    private final String fps;
    private final String duration;
}
