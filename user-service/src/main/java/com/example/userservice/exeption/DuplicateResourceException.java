package com.example.userservice.exeption;/*
    @author User
    @project lab4
    @class DuplicateResourceException
    @version 1.0.0
    @since 28.04.2025 - 15.27 
*/

public class DuplicateResourceException extends RuntimeException{
    public DuplicateResourceException(String message) {
        super(message);
    }
}
