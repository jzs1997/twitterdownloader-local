package com.example.twitterdownloader.service;

import java.io.IOException;

public interface VideoFormatConversionService {
    public String baseVideoToGIF(String filename, String width, String height, String flag, String fps, String duration);
}
