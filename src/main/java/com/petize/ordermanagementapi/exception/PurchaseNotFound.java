package com.petize.ordermanagementapi.exception;

public class PurchaseNotFound extends RuntimeException {
    public PurchaseNotFound(String message) {
        super(message);
    }
}
