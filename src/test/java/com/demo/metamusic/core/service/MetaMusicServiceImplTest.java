package com.demo.metamusic.core.service;

import com.demo.metamusic.TestConstants;
import com.demo.metamusic.adapter.persistence.ArtistAlreadyExistsException;
import com.demo.metamusic.adapter.persistence.ArtistRepository;
import com.demo.metamusic.adapter.persistence.NoArtistFoundException;
import com.demo.metamusic.adapter.persistence.dto.ArtistEntity;
import com.demo.metamusic.adapter.persistence.dto.TrackEntity;
import com.demo.metamusic.core.model.Artist;
import com.demo.metamusic.core.model.Track;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static com.demo.metamusic.TestConstants.*;
import static com.demo.metamusic.TestConstants.EXAMPLE_TRACK_RELEASE_DATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


class MetaMusicServiceImplTest {

    @Mock
    private ArtistRepository ArtistRepository;
    @InjectMocks
    private MetaMusicServiceImpl metaMusicService;
    private AutoCloseable autoCloseable;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ArtistEntity mockedArtistDto;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        long artistId = 1L;
        when(mockedArtistDto.getId()).thenReturn(artistId);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void givenValidTrack_makesNecessaryCallsToPersistArtistData() {
        when(ArtistRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME))).thenReturn(List.of(mockedArtistDto));

        metaMusicService.addTrack(TestConstants.EXAMPLE_ARTIST_NAME, EXAMPLE_TRACK);

        TrackEntity expected = Track.toEntity(EXAMPLE_TRACK);
        expected.setArtist(mockedArtistDto);
        verify(mockedArtistDto.getTracks()).add(eq(expected));
        verify(ArtistRepository).save(eq(mockedArtistDto));
    }

    // TODO: 10/12/23 With the current requirements this makes sense (One track can only have one artist),
    //  but as a more realistic model or improvement we should most likely consider multiple artists being credited for one song
    // TODO: 10/12/23 It's not clear however if there is one artist "owning" the song and other artists only collaborators
    //  or if it should be considered shared ownership but regardless it may be good to have the track show up as result for all contributing artists.
    @Test
    void givenMultipleMatchingArtistsForTrack_throwsException() {
        when(ArtistRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME)))
                .thenReturn(List.of(mockedArtistDto, mockedArtistDto));

        assertThrows(IllegalArgumentException.class, () -> metaMusicService.addTrack(TestConstants.EXAMPLE_ARTIST_NAME, EXAMPLE_TRACK));
    }

    @Test
    void givenNoMatchingArtistsForTrack_throwsException() {
        when(ArtistRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME))).thenReturn(List.of());

        assertThrows(NoArtistFoundException.class, () -> metaMusicService.addTrack(TestConstants.EXAMPLE_ARTIST_NAME, EXAMPLE_TRACK));
    }

    @Test
    void givenValidUpdateArtistNewName_returnsExpectedUpdatedArtist() {
        ArtistEntity oldArtist = new ArtistEntity(TestConstants.EXAMPLE_ARTIST_NAME, Set.of());
        ArtistEntity updatedArtistEntity = new ArtistEntity("newName", Set.of());

        when(ArtistRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME)))
                .thenReturn(List.of(oldArtist));
        when(ArtistRepository.save(eq(updatedArtistEntity))).thenReturn(updatedArtistEntity);

        Artist newArtist = Artist.fromEntity(updatedArtistEntity);
        Artist updatedArtist = metaMusicService.updateArtist(TestConstants.EXAMPLE_ARTIST_NAME,
                newArtist);

        assertEquals(newArtist, updatedArtist);
        verify(ArtistRepository).save(eq(updatedArtistEntity));
    }

    @Test
    void givenValidUpdateArtistNewAliases_returnsExpectedUpdatedArtist() {
        String testName = "testName";
        ArtistEntity oldArtist = new ArtistEntity(testName, Set.of("oldAlias"));
        ArtistEntity updatedArtistEntity = new ArtistEntity(testName, Set.of("oldAlias", "newAlias"));

        when(ArtistRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME)))
                .thenReturn(List.of(oldArtist));
        when(ArtistRepository.save(eq(updatedArtistEntity))).thenReturn(updatedArtistEntity);

        Artist newArtist = new Artist(testName, Set.of("newAlias"));
        Artist updatedArtist = metaMusicService.updateArtist(TestConstants.EXAMPLE_ARTIST_NAME,
                newArtist);

        assertEquals(Artist.fromEntity(updatedArtistEntity), updatedArtist);
        verify(ArtistRepository).save(eq(updatedArtistEntity));
    }

    @Test
    void givenNonExistingArtist_whenCallingUpdateArtist_throwsException() {
        when(ArtistRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME))).thenReturn(List.of());

        assertThrows(NoArtistFoundException.class,
                () -> metaMusicService.updateArtist(TestConstants.EXAMPLE_ARTIST_NAME, new Artist("valid", Set.of())));
    }

    @Test
    void givenMultipleMatchingArtists_whenCallingUpdateArtist_shouldThrowException() {
        when(ArtistRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME)))
                .thenReturn(List.of(mockedArtistDto, mockedArtistDto));

        assertThrows(IllegalArgumentException.class,
                () -> metaMusicService.updateArtist(TestConstants.EXAMPLE_ARTIST_NAME, new Artist("valid", Set.of())));
    }

    @Test
    void givenAlreadyExistingArtistWithNewName_whenCallingUpdateArtist_shouldThrowException() {
        String newName = "newName";
        when(ArtistRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME))).thenReturn(List.of(mockedArtistDto));
        when(ArtistRepository.findByName(eq(newName))).thenReturn(List.of(mockedArtistDto));

        Artist newArtist = new Artist(newName, Set.of());
        assertThrows(ArtistAlreadyExistsException.class,
                () -> metaMusicService.updateArtist(TestConstants.EXAMPLE_ARTIST_NAME, newArtist));
    }

    @Test
    void whenFetchArtistTracksPaginated_withValidArtistName_returnsExpectedTracks() {
        PageRequest pageRequest = PageRequest.of(0, 2);
        Track secondTrack = new Track(EXAMPLE_TRACK_TITLE + "2", EXAMPLE_GENRE, EXAMPLE_TRACK_DURATION, EXAMPLE_TRACK_RELEASE_DATE);
        Page<Track> expected = new PageImpl<>(List.of(EXAMPLE_TRACK, secondTrack));

        when(ArtistRepository.findByName(eq(EXAMPLE_ARTIST_NAME))).thenReturn(List.of(mockedArtistDto));
        when(ArtistRepository.fetchTracksPaginated(eq(1L), eq(pageRequest)))
                .thenReturn(expected.map(Track::toEntity));

        Page<Track> tracks = metaMusicService.getArtistTracksPaginated(TestConstants.EXAMPLE_ARTIST_NAME,
                0, 2);

        assertEquals(expected, tracks);
        verify(ArtistRepository).fetchTracksPaginated(eq(1L), eq(pageRequest));
    }

    @Test
    void whenFetchArtistTracksPaginated_withMultiplePages_returnsExpectedTracks() {
        Track secondTrack = new Track(EXAMPLE_TRACK_TITLE + "2", EXAMPLE_GENRE, EXAMPLE_TRACK_DURATION, EXAMPLE_TRACK_RELEASE_DATE);
        Page<Track> expectedFirstPage = new PageImpl<>(List.of(EXAMPLE_TRACK));
        Page<Track> expectedSecondPage = new PageImpl<>(List.of(secondTrack));

        when(ArtistRepository.findByName(eq(EXAMPLE_ARTIST_NAME))).thenReturn(List.of(mockedArtistDto));
        when(ArtistRepository.fetchTracksPaginated(eq(1L), any()))
                .thenReturn(expectedFirstPage.map(Track::toEntity), expectedSecondPage.map(Track::toEntity));

        Page<Track> firstTracksPage = metaMusicService.getArtistTracksPaginated(TestConstants.EXAMPLE_ARTIST_NAME,
                0, 1);
        Page<Track> secondTracksPage = metaMusicService.getArtistTracksPaginated(TestConstants.EXAMPLE_ARTIST_NAME,
                1, 1);

        PageRequest expectedFirstPageRequest = PageRequest.of(0, 1);
        PageRequest expectedSecondPageRequest = PageRequest.of(1, 1);
        assertEquals(expectedFirstPage, firstTracksPage);
        assertEquals(expectedSecondPage, secondTracksPage);
        verify(ArtistRepository).fetchTracksPaginated(eq(1L), eq(expectedFirstPageRequest));
        verify(ArtistRepository).fetchTracksPaginated(eq(1L), eq(expectedSecondPageRequest));
    }

    @Test
    void whenFetchArtistTracksPaginated_withNonExistingArtistName_throwsException() {
        when(ArtistRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME))).thenReturn(List.of());

        assertThrows(NoArtistFoundException.class, () -> metaMusicService.getArtistTracksPaginated(TestConstants.EXAMPLE_ARTIST_NAME,
                0, 1));
    }

    @Test
    void givenMultipleMatchingArtists_whenFetchArtistTracksPaginated_shouldThrowException() {
        when(ArtistRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME)))
                .thenReturn(List.of(mockedArtistDto, mockedArtistDto));

        assertThrows(IllegalArgumentException.class,
                () -> metaMusicService.getArtistTracksPaginated(TestConstants.EXAMPLE_ARTIST_NAME, 0, 1));
    }
}