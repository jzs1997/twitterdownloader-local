package com.example.twitterdownloader;

import com.example.twitterdownloader.globals.GlobalVars;
import com.example.twitterdownloader.utils.FileResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

@SpringBootApplication
public class TwitterdownloaderApplication {
	public static void main(String[] args) {
        if(System.getProperty("os.name").toLowerCase().startsWith("windows")){
            GlobalVars.SLASH = "\\\\";
        }else{
            GlobalVars.SLASH = "/";
        }
        System.out.println("Slash: " + GlobalVars.SLASH);
        String basePath = Paths.get("").toAbsolutePath().toString();
        System.out.println(basePath);
        String pathStringExtracted = Paths.get(basePath, "/files/extracted").toAbsolutePath().toString();
        String pathStringConverted = Paths.get(basePath, "/files/converted").toAbsolutePath().toString();
        File extracted = new File(pathStringExtracted);
        File converted = new File(pathStringConverted);
        extracted.mkdirs();
        converted.mkdirs();
        GlobalVars.FILEPATH_EXTRACTED = extracted.getAbsolutePath();
        GlobalVars.FILEPATH_CONVERTED = converted.getAbsolutePath();
        System.out.println(GlobalVars.FILEPATH_EXTRACTED);
        System.out.println(GlobalVars.FILEPATH_CONVERTED);
		SpringApplication.run(TwitterdownloaderApplication.class, args);
	}
//    @Bean
//    public CommandLineRunner run(ApplicationContext appContext){
//        return args -> {
//            String[] beans = appContext.getBeanDefinitionNames();
//            Arrays.stream(beans).sorted().forEach(System.out::println);
//        };
//    }
}
