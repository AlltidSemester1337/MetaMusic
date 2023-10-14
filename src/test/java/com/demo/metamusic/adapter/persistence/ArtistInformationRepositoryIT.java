package com.demo.metamusic.adapter.persistence;

import com.demo.metamusic.TestConstants;
import com.demo.metamusic.adapter.persistence.dto.ArtistInformationEntity;
import com.demo.metamusic.adapter.persistence.dto.TrackInformationEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ArtistInformationRepositoryIT extends PersistenceIntegrationTestParent {

    @Autowired
    private ArtistInformationRepository artistInformationRepository;

    @Test
    void givenMatchingArtistName_shouldReturnExpectedArtists() {
        String artistName = "testName";
        assertEquals(List.of(), artistInformationRepository.findByName(artistName));
        ArtistInformationEntity expectedArtist = new ArtistInformationEntity(artistName, Set.of());

        artistInformationRepository.save(expectedArtist);
        assertEquals(List.of(expectedArtist), artistInformationRepository.findByName(artistName));

        artistInformationRepository.save(new ArtistInformationEntity(artistName, Set.of()));
        assertEquals(2, artistInformationRepository.findByName(artistName).size());
    }

    @Test
    void givenValidTrackInformation_shouldPersistOnSave() {
        ArtistInformationEntity expectedArtist = new ArtistInformationEntity("testName", Set.of());
        TrackInformationEntity expectedTrack = new TrackInformationEntity(TestConstants.EXAMPLE_TRACK_TITLE, TestConstants.EXAMPLE_GENRE, "0:01", Date.valueOf(LocalDate.EPOCH));
        expectedTrack.setArtist(expectedArtist);
        expectedArtist.getTracks().add(expectedTrack);

        ArtistInformationEntity saved = artistInformationRepository.save(expectedArtist);
        List<TrackInformationEntity> savedTracks = artistInformationRepository.findById(saved.getId()).get().getTracks();
        assertEquals(1, savedTracks.size());
        assertEquals(expectedTrack, savedTracks.get(0));
    }

    @Test
    void givenValidArtistInformationUpdateInformation_shouldPersistChangesOnSave() {
        String oldName = "oldName";
        ArtistInformationEntity oldArtist = new ArtistInformationEntity(oldName, Set.of());
        ArtistInformationEntity oldArtistEntity = artistInformationRepository.save(oldArtist);
        Set<String> newAliases = Set.of("newAlias");

        assertEquals(oldArtistEntity, artistInformationRepository.findByName(oldName).get(0));

        oldArtist.setName("newName");
        oldArtist.setAliases(newAliases);
        artistInformationRepository.save(oldArtist);

        assertTrue(artistInformationRepository.findByName(oldName).isEmpty());
        ArtistInformationEntity updatedArtistEntity = artistInformationRepository.findById(oldArtistEntity.getId()).get();
        assertEquals(oldArtistEntity, updatedArtistEntity);
        assertEquals(newAliases, updatedArtistEntity.getAliases());
    }
}