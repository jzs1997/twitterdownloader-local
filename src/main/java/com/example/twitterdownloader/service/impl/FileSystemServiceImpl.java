package com.example.twitterdownloader.service.impl;

import com.example.twitterdownloader.globals.GlobalVars;
import com.example.twitterdownloader.service.FileSystemService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileSystemServiceImpl implements FileSystemService {
    @Override
    public boolean store(MultipartFile file) {
        if(file.isEmpty()){
            System.out.println("No file posted");
            return false;
        }

        Path destPath = Paths.get(GlobalVars.FILEPATH_EXTRACTED, file.getOriginalFilename())
                .toAbsolutePath();
        if(Files.exists(destPath)){
            System.out.println("File Already Exists");
            return true;
        }
        try(InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destPath, StandardCopyOption.REPLACE_EXISTING);
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
