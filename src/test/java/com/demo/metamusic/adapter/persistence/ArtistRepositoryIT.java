package com.demo.metamusic.adapter.persistence;

import com.demo.metamusic.TestConstants;
import com.demo.metamusic.adapter.persistence.dto.ArtistEntity;
import com.demo.metamusic.adapter.persistence.dto.TrackEntity;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ArtistRepositoryIT extends PersistenceIntegrationTestParent {

    @Autowired
    private ArtistRepository ArtistRepository;

    @Test
    void givenMatchingArtistName_shouldReturnExpectedArtists() {
        String artistName = "testName";
        assertEquals(List.of(), ArtistRepository.findByName(artistName));
        ArtistEntity expectedArtist = new ArtistEntity(artistName, Set.of());

        ArtistRepository.save(expectedArtist);
        assertEquals(List.of(expectedArtist), ArtistRepository.findByName(artistName));

        ArtistRepository.save(new ArtistEntity(artistName, Set.of()));
        assertEquals(2, ArtistRepository.findByName(artistName).size());
    }

    @Test
    void givenValidTrack_shouldPersistOnSave() {
        ArtistEntity expectedArtist = new ArtistEntity("testName", Set.of());
        TrackEntity expectedTrack = new TrackEntity(TestConstants.EXAMPLE_TRACK_TITLE, TestConstants.EXAMPLE_GENRE, "0:01", Date.valueOf(LocalDate.EPOCH));
        expectedTrack.setArtist(expectedArtist);
        expectedArtist.getTracks().add(expectedTrack);

        ArtistEntity saved = ArtistRepository.save(expectedArtist);
        List<TrackEntity> savedTracks = ArtistRepository.findById(saved.getId()).get().getTracks();
        assertEquals(1, savedTracks.size());
        assertEquals(expectedTrack, savedTracks.get(0));
    }

    @Test
    void givenValidArtistUpdateInformation_shouldPersistChangesOnSave() {
        String oldName = "oldName";
        ArtistEntity oldArtist = new ArtistEntity(oldName, Set.of());
        ArtistEntity oldArtistEntity = ArtistRepository.save(oldArtist);
        Set<String> newAliases = Set.of("newAlias");

        assertEquals(oldArtistEntity, ArtistRepository.findByName(oldName).get(0));

        oldArtist.setName("newName");
        oldArtist.setAliases(newAliases);
        ArtistRepository.save(oldArtist);

        assertTrue(ArtistRepository.findByName(oldName).isEmpty());
        ArtistEntity updatedArtistEntity = ArtistRepository.findById(oldArtistEntity.getId()).get();
        assertEquals(oldArtistEntity, updatedArtistEntity);
        assertEquals(newAliases, updatedArtistEntity.getAliases());
    }

    @Test
    void givenValidFetchPaginatedTracks_withFewerTotalTracks_shouldReturnAllTracks() {
        String artistName = "name";
        ArtistEntity artist = new ArtistEntity(artistName, Set.of());
        List<TrackEntity> expectedArtistTracks = setupArtistTracks(artist);

        Page<TrackEntity> TrackEntities = ArtistRepository.fetchTracksPaginated(artist.getId(), PageRequest.of(0, 10));
        assertEquals(expectedArtistTracks, TrackEntities.getContent());
    }

    @NotNull
    private List<TrackEntity> setupArtistTracks(ArtistEntity artist) {
        TrackEntity firstTrack = new TrackEntity(TestConstants.EXAMPLE_TRACK_TITLE, TestConstants.EXAMPLE_GENRE, "0:01", Date.valueOf(LocalDate.EPOCH));
        TrackEntity anotherTrack = new TrackEntity(TestConstants.EXAMPLE_TRACK_TITLE + "2", TestConstants.EXAMPLE_GENRE, "0:01", Date.valueOf(LocalDate.EPOCH));
        List<TrackEntity> expectedArtistTracks = List.of(firstTrack, anotherTrack);

        expectedArtistTracks.forEach(track -> track.setArtist(artist));
        artist.setTracks(expectedArtistTracks);
        ArtistRepository.save(artist);
        return expectedArtistTracks;
    }

    @Test
    void givenValidFetchPaginatedTracks_withMultiplePages_shouldReturnExpectedTracks() {
        String artistName = "name";
        ArtistEntity artist = new ArtistEntity(artistName, Set.of());
        List<TrackEntity> expectedArtistTracks = setupArtistTracks(artist);

        Page<TrackEntity> firstTrackPage = ArtistRepository.fetchTracksPaginated(artist.getId(), PageRequest.of(0, 1));
        Page<TrackEntity> secondTrackPage = ArtistRepository.fetchTracksPaginated(artist.getId(), PageRequest.of(1, 1));
        assertEquals(List.of(expectedArtistTracks.get(0)), firstTrackPage.getContent());
        assertEquals(0, firstTrackPage.getPageable().getPageNumber());
        assertEquals(List.of(expectedArtistTracks.get(1)), secondTrackPage.getContent());
        assertEquals(1, secondTrackPage.getPageable().getPageNumber());
    }
}