package com.example.twitterdownloader.controller;


import com.example.twitterdownloader.advices.RateLimited;
import com.example.twitterdownloader.exceptions.ExcessiveUsageException;
import com.example.twitterdownloader.globals.GlobalVars;
import com.example.twitterdownloader.requests.VideoConvertRequest;
import com.example.twitterdownloader.service.FileSystemService;
import com.example.twitterdownloader.service.TweetResourceExtractionService;
import com.example.twitterdownloader.service.UserService;
import com.example.twitterdownloader.service.VideoFormatConversionService;
import com.example.twitterdownloader.utils.FileResolver;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.twitterdownloader.globals.GlobalVars.FILEPATH_EXTRACTED;
import static com.example.twitterdownloader.globals.SessionAttrs.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/video")
public class VideoController {

    private final TweetResourceExtractionService extractionService;
    private final FileSystemService fileSystemService;
    private final VideoFormatConversionService videoFormatService;
    private final UserService userService;
    private final HttpSession session;

    @GetMapping("/extract/{statusId}")
    @RateLimited
    public ResponseEntity<Resource> extractVideo(@PathVariable String statusId, @CookieValue("twdownloaduuid") String userId) throws ExcessiveUsageException {

        System.out.println("User " + userId + " calling image fetch");
        System.out.println("Session: " + session.getId());
        setSession(session, userId);

        int userUsageCount = userService.findByUserId(userId).getUsageCount();

        if(userUsageCount >= MAX_USAGE){
            throw new ExcessiveUsageException();
        }

        List<String> filenames = extractionService.resourceExtraction(statusId);
        if(filenames == null || filenames.size() > 1){
            return ResponseEntity.badRequest().build();
        }

        String filename = filenames.get(0);
        Resource resource = FileResolver.fileToResource(FILEPATH_EXTRACTED, filename);

        Set<String> extractedFiles = (Set<String>) session.getAttribute(EXTRACTED_FILES);
        extractedFiles.add(filename);
        session.setAttribute(EXTRACTED_FILES, extractedFiles);

        if(resource.exists() && resource.isReadable()){
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("video", "mp4"));
            headers.setContentDispositionFormData("attachment", filename);
            headers.add("Returned-Filename", filename);
            List<String> arr = new ArrayList<>();
            arr.add("Returned-Filename");
            headers.setAccessControlExposeHeaders(arr);

            session.setAttribute(TOTAL_USAGE_COUNT, (int)session.getAttribute(TOTAL_USAGE_COUNT) + 1);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping(value = "/convert", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @RateLimited
    public ResponseEntity<String> postFileToConvert(@RequestPart("file") MultipartFile file, @RequestPart("params") VideoConvertRequest videoRequest, @CookieValue("twdownloaduuid") String userId) throws ExcessiveUsageException {

        System.out.println("User " + userId + " calling video fetch");
        System.out.println("Session: " + session.getId());
        setSession(session, userId);

        int userUsageCount = userService.findByUserId(userId).getUsageCount();

        if(userUsageCount >= MAX_USAGE){
            throw new ExcessiveUsageException();
        }

        if(fileSystemService.store(file)){
            String filename = videoRequest.getFilename();
            String width = videoRequest.getWidth();
            String height = videoRequest.getHeight();
            String flag = videoRequest.getFlag();
            String fps = videoRequest.getFps();
            String duration = videoRequest.getDuration();
            System.out.println(filename + ' ' + width + ' ' + height + ' ' + flag + ' ' + fps + ' ' + duration + ' ');

            String filePath = Paths.get(FILEPATH_EXTRACTED, filename).toAbsolutePath().toString();
            String newFilename = videoFormatService.baseVideoToGIF(filePath, width, height, flag, fps, duration);

            Set<String> extractedFiles = (Set<String>) session.getAttribute(EXTRACTED_FILES);
            extractedFiles.add(file.getOriginalFilename());
            session.setAttribute(EXTRACTED_FILES, extractedFiles);

            return ResponseEntity.created(URI.create(newFilename)).build();
        }
        return ResponseEntity.notFound().build();
    }


    @GetMapping("/converted/{filename}")
    @RateLimited
    public ResponseEntity<Resource> getConvertedGIF(@PathVariable String filename, @CookieValue("twdownloaduuid") String userId) {

        System.out.println("User " + userId + " calling image fetch");
        System.out.println("Session: " + session.getId());
        setSession(session, userId);

        Set<String> convertedFiles = (Set<String>)session.getAttribute(CONVERTED_FILES);
        convertedFiles.add(filename);
        session.setAttribute(CONVERTED_FILES, convertedFiles);
        session.setAttribute(TOTAL_USAGE_COUNT, (int)session.getAttribute(TOTAL_USAGE_COUNT) + 1);

        if(Files.exists(Paths.get(GlobalVars.FILEPATH_CONVERTED, filename))) {
            Resource resource = FileResolver.fileToResource(GlobalVars.FILEPATH_CONVERTED, filename);
            if (resource.exists() && resource.isReadable()) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(new MediaType("image", "gif"));
                headers.setContentDispositionFormData("attachment", filename);
                headers.add("Returned-Filename", filename);
                List<String> arr = new ArrayList<>();
                arr.add("Returned-Filename");
                headers.setAccessControlExposeHeaders(arr);
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(resource);
            }
        }
        return ResponseEntity.notFound().build();
    }

    private void setSession(HttpSession session, String userId){
        session.setAttribute(USER_ID, userId);
        if(session.getMaxInactiveInterval() != SESSION_MAX_INTERVAL) session.setMaxInactiveInterval(SESSION_MAX_INTERVAL);
        if(session.getAttribute(EXTRACTED_FILES) == null) session.setAttribute(EXTRACTED_FILES, new HashSet<String>());
        if(session.getAttribute(CONVERTED_FILES) == null) session.setAttribute(CONVERTED_FILES, new HashSet<String>());
        if(session.getAttribute(TOTAL_USAGE_COUNT) == null) session.setAttribute(TOTAL_USAGE_COUNT, 0);
    }
}
