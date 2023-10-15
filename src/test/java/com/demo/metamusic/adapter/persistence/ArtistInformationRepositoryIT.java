package com.demo.metamusic.adapter.persistence;

import com.demo.metamusic.TestConstants;
import com.demo.metamusic.adapter.persistence.dto.ArtistInformationEntity;
import com.demo.metamusic.adapter.persistence.dto.TrackInformationEntity;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
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

    @Test
    void givenValidFetchPaginatedTracks_withFewerTotalTracks_shouldReturnAllTracks() {
        String artistName = "name";
        ArtistInformationEntity artist = new ArtistInformationEntity(artistName, Set.of());
        List<TrackInformationEntity> expectedArtistTracks = setupArtistTracks(artist);

        Page<TrackInformationEntity> trackInformationEntities = artistInformationRepository.fetchTracksPaginated(artist.getId(), PageRequest.of(0, 10));
        assertEquals(expectedArtistTracks, trackInformationEntities.getContent());
    }

    @NotNull
    private List<TrackInformationEntity> setupArtistTracks(ArtistInformationEntity artist) {
        TrackInformationEntity firstTrack = new TrackInformationEntity(TestConstants.EXAMPLE_TRACK_TITLE, TestConstants.EXAMPLE_GENRE, "0:01", Date.valueOf(LocalDate.EPOCH));
        TrackInformationEntity anotherTrack = new TrackInformationEntity(TestConstants.EXAMPLE_TRACK_TITLE + "2", TestConstants.EXAMPLE_GENRE, "0:01", Date.valueOf(LocalDate.EPOCH));
        List<TrackInformationEntity> expectedArtistTracks = List.of(firstTrack, anotherTrack);

        expectedArtistTracks.forEach(track -> track.setArtist(artist));
        artist.setTracks(expectedArtistTracks);
        artistInformationRepository.save(artist);
        return expectedArtistTracks;
    }

    @Test
    void givenValidFetchPaginatedTracks_withMultiplePages_shouldReturnExpectedTracks() {
        String artistName = "name";
        ArtistInformationEntity artist = new ArtistInformationEntity(artistName, Set.of());
        List<TrackInformationEntity> expectedArtistTracks = setupArtistTracks(artist);

        Page<TrackInformationEntity> firstTrackInformationPage = artistInformationRepository.fetchTracksPaginated(artist.getId(), PageRequest.of(0, 1));
        Page<TrackInformationEntity> secondTrackInformationPage = artistInformationRepository.fetchTracksPaginated(artist.getId(), PageRequest.of(1, 1));
        assertEquals(List.of(expectedArtistTracks.get(0)), firstTrackInformationPage.getContent());
        assertEquals(0, firstTrackInformationPage.getPageable().getPageNumber());
        assertEquals(List.of(expectedArtistTracks.get(1)), secondTrackInformationPage.getContent());
        assertEquals(1, secondTrackInformationPage.getPageable().getPageNumber());
    }
}