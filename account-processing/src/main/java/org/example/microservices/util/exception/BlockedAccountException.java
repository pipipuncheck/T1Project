package org.example.microservices.util.exception;

public class BlockedAccountException extends RuntimeException{
    public BlockedAccountException(String message){
        super(message);
    }
}
