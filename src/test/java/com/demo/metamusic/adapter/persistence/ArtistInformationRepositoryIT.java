package com.demo.metamusic.adapter.persistence;

import com.demo.metamusic.adapter.persistence.dto.ArtistInformationEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArtistInformationRepositoryIT extends PersistenceIntegrationTestParent {

    @Autowired
    private ArtistInformationRepository artistInformationRepository;
    @Test
    void givenMatchingArtistName_shouldReturnExpectedArtists() {
        String artistName = "testName";
        assertEquals(List.of(), artistInformationRepository.findByName(artistName));
        ArtistInformationEntity expectedArtist = new ArtistInformationEntity(artistName, List.of());

        artistInformationRepository.save(expectedArtist);
        assertEquals(List.of(expectedArtist), artistInformationRepository.findByName(artistName));

        artistInformationRepository.save(new ArtistInformationEntity(artistName, List.of()));
        assertEquals(2, artistInformationRepository.findByName(artistName).size());
    }
}