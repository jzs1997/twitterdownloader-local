package com.example.twitterdownloader.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.example.twitterdownloader.service.TweetResourceExtractionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.example.twitterdownloader.globals.GlobalVars.APIUrl;
import static com.example.twitterdownloader.globals.GlobalVars.FILEPATH_EXTRACTED;

@Service
public class TweetResourceExtractionServiceImpl implements TweetResourceExtractionService {

    /**
     * Extract Tweets media with given statusId
     * @param statusId
     */
    @Override
    public List<String> resourceExtraction(String statusId) {
        String response = HttpUtil.get(APIUrl + statusId);
        List<String> filenames = null;
        try{
            filenames = jsonParser(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(response);
        return filenames;
    }

    /**
     * Parse the given response, extract the resources, then save them.
     * @param response: response from api, this response has already been parsed and formatted in JSON.
     * @return filenames, if media types are not consistent, return null.
     * @throws JsonProcessingException
     */
    private List<String> jsonParser(String response) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsObj = null;
        jsObj = mapper.readTree(response);

        if(!jsObj.has("media_extended")){
            System.out.println("No media found");
            System.exit(-1);
        }

        Iterable<JsonNode> jsonNodeIterable = null;
        try {
            jsonNodeIterable = jsObj.get("media_extended");
        }catch (Exception e){
            System.out.println("Failed to get iterable object");
        }

        String[] resourceTypes = new String[4];
        List<String> resourceUrls = new ArrayList<>();
        String prevType = null;
        int typeConsistency = 0;

        Iterator<JsonNode> it = jsonNodeIterable.iterator();
        while(it.hasNext()){
            JsonNode tmpNode = it.next();
            String resourceUrl = tmpNode.get("url").asText();
            String type = tmpNode.get("type").asText();
            if(prevType != null && !type.equals(prevType)){
                typeConsistency = -1;
            }
            prevType = type;
            resourceUrls.add(resourceUrl);
        }

        if(typeConsistency == -1){
            System.out.println("Resources contain multiple types.");
            return null;
        }

        if(prevType.equals("image")){
            System.out.println("Returning image");
        }

        if(prevType.equals("video")){
            System.out.println("Returning video");
        }

        if(prevType.equals("gif")){
            System.out.println("Returning gif");
        }

        List<String> filenames = new ArrayList<>();

        for(int i=0; i<resourceUrls.size(); i++){
            String resourceUrl = resourceUrls.get(i);
            filenames.add(extractFilename(resourceUrl));
            long size = HttpUtil.downloadFile(resourceUrl, FileUtil.file(FILEPATH_EXTRACTED));
            System.out.println("Saving to: " + FILEPATH_EXTRACTED);
            System.out.format("Downloading from %s Size: %.2f MB%n", resourceUrl, size/ 1024.0 / 1024.0);
        }
        return filenames;
    }

    private String extractFilename(String resourceUrl){
        String[] strips = resourceUrl.split("/");
        String lastStrip = strips[strips.length-1];
        System.out.println(lastStrip);

        strips = lastStrip.split("\\?");

        return strips[0];
    }
}
