package com.demo.metamusic.core.model;

import com.demo.metamusic.adapter.http.dto.request.ArtistUpdateDTO;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ArtistTest {

    @Test
    void givenValidDTO_shouldCreateExpectedArtist() {
        String expectedName = "name";
        String oldName = "oldName";
        Set<String> newAliases = Set.of(expectedName);

        assertEquals(expectedName, Artist.fromDTO(oldName, new ArtistUpdateDTO(expectedName, null)).name());
        assertEquals(oldName, Artist.fromDTO(oldName, new ArtistUpdateDTO(null, null)).name());
        assertEquals(newAliases, Artist.fromDTO(oldName, new ArtistUpdateDTO(null, newAliases)).aliases());
    }

    @Test
    void givenInvalidDTO_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> Artist.fromDTO("", new ArtistUpdateDTO("", Set.of())));
        assertThrows(IllegalArgumentException.class, () -> Artist.fromDTO("oldName", new ArtistUpdateDTO("valid", Set.of(""))));
    }
}