package com.example.twitterdownloader.service.impl;


import com.example.twitterdownloader.globals.GlobalVars;
import com.example.twitterdownloader.service.VideoFormatConversionService;
import com.example.twitterdownloader.utils.FileResolver;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class VideoFormatConversionServiceImpl implements VideoFormatConversionService {
    @Override
    public String baseVideoToGIF(String filepath, String width, String height, String flag, String fps, String duration) {
        try{
//            String pythonScript = "src/main/java/com/example/twitterdownloader/externals/video_converter.py";
            String pythonScript = "/video_converter.py";
            String convertedFilePath = GlobalVars.FILEPATH_CONVERTED;
            System.out.println(pythonScript + " " + filepath + " " + width + " " + height + " " + flag + " " + duration + " " + convertedFilePath);
            ProcessBuilder processBuilder = new ProcessBuilder("python3", pythonScript, filepath, width, height, flag, fps, duration, convertedFilePath);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader((new InputStreamReader(process.getInputStream())));
            String line;
            while((line = reader.readLine()) != null){
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("Exit code:" + exitCode);
        }catch (IOException | InterruptedException e){
            e.printStackTrace();
        }

        String fullFilename = FileResolver.extractFilename(filepath);
        String filename = fullFilename.split("\\.")[0];
        String newFilename = filename + ".gif";

        return newFilename;
    }
}
