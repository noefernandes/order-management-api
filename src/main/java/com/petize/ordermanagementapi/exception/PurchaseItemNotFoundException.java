package com.petize.ordermanagementapi.exception;

public class PurchaseItemNotFoundException extends RuntimeException {
    public PurchaseItemNotFoundException(String message) {
        super(message);
    }
}
