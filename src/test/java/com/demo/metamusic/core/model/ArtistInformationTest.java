package com.demo.metamusic.core.model;

import com.demo.metamusic.adapter.http.dto.request.ArtistUpdateDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArtistInformationTest {

    @Test
    void givenValidDTO_shouldCreateExpectedArtistInformation() {
        String expectedName = "name";
        assertEquals(expectedName, ArtistInformation.fromDTO(new ArtistUpdateDTO(expectedName)).name());
    }

    @Test
    void givenInvalidDTO_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> ArtistInformation.fromDTO(new ArtistUpdateDTO("")));
    }
}