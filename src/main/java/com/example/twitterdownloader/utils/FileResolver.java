package com.example.twitterdownloader.utils;

import com.example.twitterdownloader.globals.GlobalVars;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class FileResolver {
    public static String extractFilename(String filePath){
        String[] split = filePath.split(GlobalVars.SLASH);
        return split[split.length-1];
    }

    public static String makeGifName(String filename){
        String[] split = filename.split("\\.");
        return split[0] + ".gif";
    }

    public static String changeDir(String originFilePath, String targetDir){
        String filename = extractFilename(originFilePath);
        return Paths.get(targetDir, filename).toAbsolutePath().toString();
    }
    public static Path makeFilePath(String fileDir, String filename){
        return Paths.get(fileDir, filename);
    }

    public static String makeFilePathString(String fileDir, String filename){
        return makeFilePath(fileDir, filename).toAbsolutePath().toString();
    }
    public static Resource fileToResource(String dirname, String filename){
        Path filePath = Paths.get(dirname, filename);
        Resource resource = UrlResource.from(filePath.toUri());
        try {
            System.out.println("Content-Length: " + resource.contentLength());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return resource;
    }

    public static void removeFiles(String fileDir, Iterable<String> filenames) throws IOException {
        if(filenames == null) return;
        for(String filename : filenames){
            Path fullPath = Paths.get(fileDir, filename);
            System.out.println("Deleting: " + fullPath.toAbsolutePath().toString());
            Files.deleteIfExists(fullPath);
        }
        return ;
    }
}
