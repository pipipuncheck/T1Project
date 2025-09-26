package org.example.microservices.util.exception;

public class BlackListException extends RuntimeException{
    public BlackListException(String message){
        super(message);
    }
}
