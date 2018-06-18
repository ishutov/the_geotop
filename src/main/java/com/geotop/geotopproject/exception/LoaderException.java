package com.geotop.geotopproject.exception;

public class LoaderException extends RuntimeException {
    private String message;

    public LoaderException(String message) {
        this.message = message;
    }

    public LoaderException(String message, Throwable throwable) {
        super(message, throwable);
        this.message = message;
    }


    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
