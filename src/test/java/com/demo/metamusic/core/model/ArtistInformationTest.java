package com.demo.metamusic.core.model;

import com.demo.metamusic.adapter.http.dto.request.ArtistUpdateDTO;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ArtistInformationTest {

    @Test
    void givenValidDTO_shouldCreateExpectedArtistInformation() {
        String expectedName = "name";
        String oldName = "oldName";
        Set<String> newAliases = Set.of(expectedName);

        assertEquals(expectedName, ArtistInformation.fromDTO(oldName, new ArtistUpdateDTO(expectedName, null)).name());
        assertEquals(oldName, ArtistInformation.fromDTO(oldName, new ArtistUpdateDTO(null, null)).name());
        assertEquals(newAliases, ArtistInformation.fromDTO(oldName, new ArtistUpdateDTO(null, newAliases)).aliases());
    }

    @Test
    void givenInvalidDTO_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> ArtistInformation.fromDTO("", new ArtistUpdateDTO("", Set.of())));
        assertThrows(IllegalArgumentException.class, () -> ArtistInformation.fromDTO("oldName", new ArtistUpdateDTO("valid", Set.of(""))));
    }
}