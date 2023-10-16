package com.demo.metamusic.adapter.persistence;

import com.demo.metamusic.TestConstants;
import com.demo.metamusic.adapter.persistence.dto.ArtistDayRotationEntity;
import com.demo.metamusic.adapter.persistence.dto.ArtistEntity;
import com.demo.metamusic.adapter.persistence.dto.TrackEntity;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ArtistRepositoryIT {

    @Container
    static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16.0")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword");

    static {
        postgresContainer.start();
        System.setProperty("spring.datasource.url", postgresContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgresContainer.getUsername());
        System.setProperty("spring.datasource.password", postgresContainer.getPassword());
    }

    @Autowired
    private ArtistRepository artistRepository;

    @Test
    void givenMatchingArtistName_shouldReturnExpectedArtists() {
        String artistName = "testName";
        assertEquals(List.of(), artistRepository.findByName(artistName));
        ArtistEntity expectedArtist = new ArtistEntity(artistName, Set.of());

        artistRepository.save(expectedArtist);
        assertEquals(List.of(expectedArtist), artistRepository.findByName(artistName));

        artistRepository.save(new ArtistEntity(artistName, Set.of()));
        assertEquals(2, artistRepository.findByName(artistName).size());
    }

    @Test
    void givenValidTrack_shouldPersistOnSave() {
        ArtistEntity expectedArtist = new ArtistEntity("testName", Set.of());
        TrackEntity expectedTrack = new TrackEntity(TestConstants.EXAMPLE_TRACK_TITLE, TestConstants.EXAMPLE_GENRE, "0:01", Date.valueOf(LocalDate.EPOCH));
        expectedTrack.setArtist(expectedArtist);
        expectedArtist.getTracks().add(expectedTrack);

        ArtistEntity saved = artistRepository.save(expectedArtist);
        List<TrackEntity> savedTracks = artistRepository.findById(saved.getId()).get().getTracks();
        assertEquals(1, savedTracks.size());
        assertEquals(expectedTrack, savedTracks.get(0));
    }

    @Test
    void givenValidArtistUpdateInformation_shouldPersistChangesOnSave() {
        String oldName = "oldName";
        ArtistEntity oldArtist = new ArtistEntity(oldName, Set.of());
        ArtistEntity oldArtistEntity = artistRepository.save(oldArtist);
        Set<String> newAliases = Set.of("newAlias");

        assertEquals(oldArtistEntity, artistRepository.findByName(oldName).get(0));

        oldArtist.setName("newName");
        oldArtist.setAliases(newAliases);
        artistRepository.save(oldArtist);

        assertTrue(artistRepository.findByName(oldName).isEmpty());
        ArtistEntity updatedArtistEntity = artistRepository.findById(oldArtistEntity.getId()).get();
        assertEquals(oldArtistEntity, updatedArtistEntity);
        assertEquals(newAliases, updatedArtistEntity.getAliases());
    }

    @Test
    void givenValidFetchPaginatedTracks_withFewerTotalTracks_shouldReturnAllTracks() {
        String artistName = "name";
        ArtistEntity artist = new ArtistEntity(artistName, Set.of());
        List<TrackEntity> expectedArtistTracks = setupArtistTracks(artist);

        Page<TrackEntity> TrackEntities = artistRepository.fetchTracksPaginated(artist.getId(), PageRequest.of(0, 10));
        assertEquals(expectedArtistTracks, TrackEntities.getContent());
    }

    @NotNull
    private List<TrackEntity> setupArtistTracks(ArtistEntity artist) {
        TrackEntity firstTrack = new TrackEntity(TestConstants.EXAMPLE_TRACK_TITLE, TestConstants.EXAMPLE_GENRE, "0:01", Date.valueOf(LocalDate.EPOCH));
        TrackEntity anotherTrack = new TrackEntity(TestConstants.EXAMPLE_TRACK_TITLE + "2", TestConstants.EXAMPLE_GENRE, "0:01", Date.valueOf(LocalDate.EPOCH));
        List<TrackEntity> expectedArtistTracks = List.of(firstTrack, anotherTrack);

        expectedArtistTracks.forEach(track -> track.setArtist(artist));
        artist.setTracks(expectedArtistTracks);
        artistRepository.save(artist);
        return expectedArtistTracks;
    }

    @Test
    void givenValidFetchPaginatedTracks_withMultiplePages_shouldReturnExpectedTracks() {
        String artistName = "name";
        ArtistEntity artist = new ArtistEntity(artistName, Set.of());
        List<TrackEntity> expectedArtistTracks = setupArtistTracks(artist);

        Page<TrackEntity> firstTrackPage = artistRepository.fetchTracksPaginated(artist.getId(), PageRequest.of(0, 1));
        Page<TrackEntity> secondTrackPage = artistRepository.fetchTracksPaginated(artist.getId(), PageRequest.of(1, 1));
        assertEquals(List.of(expectedArtistTracks.get(0)), firstTrackPage.getContent());
        assertEquals(0, firstTrackPage.getPageable().getPageNumber());
        assertEquals(List.of(expectedArtistTracks.get(1)), secondTrackPage.getContent());
        assertEquals(1, secondTrackPage.getPageable().getPageNumber());
    }

    @Test
    void givenNoMostRecentArtistOfTheDay_shouldReturnEmptyOptional() {
        assertTrue(artistRepository.getMostRecentArtistOfTheDay().isEmpty());

        ArtistEntity artist = new ArtistEntity("valid", Set.of());
        ArtistDayRotationEntity dayRotation = new ArtistDayRotationEntity(Date.valueOf(LocalDate.EPOCH), false);
        dayRotation.setArtist(artist);
        artist.setDayRotation(dayRotation);
        artistRepository.save(artist);

        assertTrue(artistRepository.getMostRecentArtistOfTheDay().isEmpty());
    }

    @Test
    void givenMostRecentArtistOfTheDay_shouldReturnExpectedArtist() {
        ArtistEntity artist = new ArtistEntity("valid", Set.of());
        ArtistDayRotationEntity dayRotation = new ArtistDayRotationEntity(Date.valueOf(LocalDate.EPOCH), true);
        dayRotation.setArtist(artist);
        artist.setDayRotation(dayRotation);
        artistRepository.save(artist);

        assertEquals(artist, artistRepository.getMostRecentArtistOfTheDay().get());
    }

    @Test
    void givenNoArtists_whenGetMaxId_shouldReturnEmptyOptional() {
        assertTrue(artistRepository.findMaxId().isEmpty());
    }

    @Test
    void givenMultipleArtists_whenGetMaxId_shouldReturnHighestId() {
        ArtistEntity artist = new ArtistEntity("valid", Set.of());
        Long firstId = artistRepository.save(artist).getId();

        ArtistEntity anotherArtist = new ArtistEntity("anotherValid", Set.of());
        Long secondId = artistRepository.save(artist).getId();

        assertEquals(Math.max(firstId, secondId), artistRepository.findMaxId().get());
    }
}