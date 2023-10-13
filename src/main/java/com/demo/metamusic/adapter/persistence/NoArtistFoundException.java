package com.demo.metamusic.adapter.persistence;

public class NoArtistFoundException extends RuntimeException {

    public NoArtistFoundException(String message) {
        super(message);
    }
}
