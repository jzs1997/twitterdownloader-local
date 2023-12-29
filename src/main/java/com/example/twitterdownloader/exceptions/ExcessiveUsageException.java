package com.example.twitterdownloader.exceptions;

public class ExcessiveUsageException extends Exception{
    public ExcessiveUsageException(){
        super("Your usage has reach the limitation, you status will be cleaned up in 10 minutes and you can continue using this service");
    }
}
