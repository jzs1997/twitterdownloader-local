package com.example.twitterdownloader.exceptions;

public class ConversionFailedException extends Exception{
    public ConversionFailedException(){
        super("Conversion failed");
    }

    public ConversionFailedException(String info){
        super(info);
    }
}
