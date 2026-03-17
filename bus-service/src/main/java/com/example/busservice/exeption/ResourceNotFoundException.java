package com.example.busservice.exeption;/*
    @author User
    @project lab4
    @class ResourceNotFoundException
    @version 1.0.0
    @since 28.04.2025 - 15.28 
*/

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
