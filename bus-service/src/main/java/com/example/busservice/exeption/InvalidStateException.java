package com.example.busservice.exeption;/*
    @author User
    @project lab4
    @class InvalidStateException
    @version 1.0.0
    @since 28.04.2025 - 15.49 
*/

public class InvalidStateException extends RuntimeException{
    public InvalidStateException(String message) {
        super(message);
    }
}
