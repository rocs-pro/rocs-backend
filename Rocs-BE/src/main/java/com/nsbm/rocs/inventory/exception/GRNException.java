package com.nsbm.rocs.inventory.exception;

public class GRNException extends RuntimeException {

    public GRNException(String message) {
        super(message);
    }

    public GRNException(String message, Throwable cause) {
        super(message, cause);
    }
}
