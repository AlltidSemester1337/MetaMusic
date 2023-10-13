package com.demo.metamusic.adapter.persistence;

public class ArtistAlreadyExistsException extends RuntimeException {

    public ArtistAlreadyExistsException(String message) {
        super(message);
    }
}
