package com.example.twitterdownloader.exceptions;

public class RateLimitException extends Exception{
    public RateLimitException(){
        super("Too frequent access, please try later!");
    }

    public RateLimitException(String info){
        super(info);
    }
}
