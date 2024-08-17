package com.petize.ordermanagementapi.exception;

public class ProductNotFound extends RuntimeException {
    public ProductNotFound(String message) {
        super(message);
    }
}
