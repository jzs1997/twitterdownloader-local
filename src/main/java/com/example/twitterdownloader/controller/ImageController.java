package com.example.twitterdownloader.controller;

import com.example.twitterdownloader.advices.RateLimited;
import com.example.twitterdownloader.exceptions.ExcessiveUsageException;
import com.example.twitterdownloader.globals.GlobalVars;
import com.example.twitterdownloader.service.*;
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
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.HashSet;


import static com.example.twitterdownloader.globals.GlobalVars.FILEPATH_EXTRACTED;
import static com.example.twitterdownloader.globals.SessionAttrs.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/img")
public class ImageController {
    private final HttpSession session;
    private final TweetResourceExtractionService extractionService;
    private final FileSystemService fileSystemService;
    private final ImageFormatConversionService imageFormatService;
    private final UserService userService;

    /**
     * Extract target image from the giving url, save it and return it to the frontend
     * TODO: Do not save image in disk, save it in memory and return
     * Attention: Extracted Resource should be removed afterward
     * @param statusId
     * @return
     */
    @GetMapping("/extract/{statusId}")
    @RateLimited
    public ResponseEntity<Resource> extractImage(@PathVariable String statusId, @CookieValue("twdownloaduuid") String userId) throws ExcessiveUsageException {

        System.out.println("User " + userId + " calling image fetch");
        System.out.println("Session: " + session.getId());
        setSession(session, userId);

        int userUsageCount = userService.findByUserId(userId).getUsageCount();

        if(userUsageCount >= MAX_USAGE){
            throw new ExcessiveUsageException();
        }

        System.out.println("Extract Image, UserId: " + userId);

        Enumeration e = session.getAttributeNames();
        while(e.hasMoreElements()){
            System.out.println(e.nextElement());
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
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentDispositionFormData("attachment", filename);
            headers.add("Returned-Filename", filename);
            List<String> arr = new ArrayList<>();
            arr.add("Returned-Filename");
            headers.setAccessControlExposeHeaders(arr);

            session.setAttribute(TOTAL_USAGE_COUNT, (int)session.getAttribute(TOTAL_USAGE_COUNT) + 1);
            System.out.println(session.getAttribute(EXTRACTED_FILES));
            System.out.println(session.getAttribute(TOTAL_USAGE_COUNT));
            return ResponseEntity.ok().headers(headers).body(resource);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/converted/{filename}")
    @RateLimited
    public ResponseEntity<Resource> getConvertedFile(@PathVariable String filename, @RequestParam String destFormat, @CookieValue("twdownloaduuid") String userId) throws ExcessiveUsageException {

        System.out.println("Session: " + session.getId());
        setSession(session, userId);
        int userUsageCount = userService.findByUserId(userId).getUsageCount();

        if(userUsageCount >= MAX_USAGE){
            throw new ExcessiveUsageException();
        }

        String filePath = Paths.get(FILEPATH_EXTRACTED, filename).toAbsolutePath().toString();
        String newFilename = imageFormatService.imageConvert(filePath, destFormat);

        Set<String> convertedFiles = (Set<String>)session.getAttribute(CONVERTED_FILES);
        convertedFiles.add(newFilename);
        session.setAttribute(CONVERTED_FILES, convertedFiles);
        session.setAttribute(TOTAL_USAGE_COUNT, (int)session.getAttribute(TOTAL_USAGE_COUNT) + 1);

        if(Files.exists(Paths.get(GlobalVars.FILEPATH_CONVERTED, newFilename))){
            Resource resource = FileResolver.fileToResource(GlobalVars.FILEPATH_CONVERTED, newFilename);
            if(resource.exists() && resource.isReadable()){
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_JPEG);
                headers.setContentDispositionFormData("attachment", newFilename);
                headers.add("Returned-Filename", newFilename);
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

    /**
     * Post file to the server, save it to /tmpfiles
     * @param file: A multipart file
     * @return If file is passed, return 201, else return 400
     */
    @PostMapping("/convert")
    @RateLimited
    public ResponseEntity<String> postFileToConvert(@RequestParam("file") MultipartFile file, @CookieValue("twdownloaduuid") String userId) throws ExcessiveUsageException {

        int userUsageCount = userService.findByUserId(userId).getUsageCount();
        setSession(session, userId);

        if(userUsageCount >= MAX_USAGE){
            throw new ExcessiveUsageException();
        }

        if(fileSystemService.store(file)){
            Set<String> extractedFiles = (Set<String>) session.getAttribute(EXTRACTED_FILES);
            extractedFiles.add(file.getOriginalFilename());
            session.setAttribute(EXTRACTED_FILES, extractedFiles);
            return ResponseEntity.created(URI.create(file.getOriginalFilename())).build();
        }
        return ResponseEntity.badRequest().build();
    }

    private void setSession(HttpSession session, String userId){
        session.setAttribute(USER_ID, userId);
        if(session.getMaxInactiveInterval() != SESSION_MAX_INTERVAL) session.setMaxInactiveInterval(SESSION_MAX_INTERVAL);
        if(session.getAttribute(EXTRACTED_FILES) == null) session.setAttribute(EXTRACTED_FILES, new HashSet<String>());
        if(session.getAttribute(CONVERTED_FILES) == null) session.setAttribute(CONVERTED_FILES, new HashSet<String>());
        if(session.getAttribute(TOTAL_USAGE_COUNT) == null) session.setAttribute(TOTAL_USAGE_COUNT, 0);
    }
}
