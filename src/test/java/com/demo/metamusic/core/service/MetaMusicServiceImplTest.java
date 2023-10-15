package com.demo.metamusic.core.service;

import com.demo.metamusic.TestConstants;
import com.demo.metamusic.adapter.persistence.ArtistAlreadyExistsException;
import com.demo.metamusic.adapter.persistence.ArtistInformationRepository;
import com.demo.metamusic.adapter.persistence.NoArtistFoundException;
import com.demo.metamusic.adapter.persistence.dto.ArtistInformationEntity;
import com.demo.metamusic.adapter.persistence.dto.TrackInformationEntity;
import com.demo.metamusic.core.model.ArtistInformation;
import com.demo.metamusic.core.model.TrackInformation;
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
    private ArtistInformationRepository artistInformationRepository;
    @InjectMocks
    private MetaMusicServiceImpl metaMusicService;
    private AutoCloseable autoCloseable;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ArtistInformationEntity mockedArtistDto;

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
    void givenValidTrackInformation_makesNecessaryCallsToPersistArtistData() {
        when(artistInformationRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME))).thenReturn(List.of(mockedArtistDto));

        metaMusicService.addTrack(TestConstants.EXAMPLE_ARTIST_NAME, EXAMPLE_TRACK);

        TrackInformationEntity expected = TrackInformation.toEntity(EXAMPLE_TRACK);
        expected.setArtist(mockedArtistDto);
        verify(mockedArtistDto.getTracks()).add(eq(expected));
        verify(artistInformationRepository).save(eq(mockedArtistDto));
    }

    // TODO: 10/12/23 With the current requirements this makes sense (One track can only have one artist),
    //  but as a more realistic model or improvement we should most likely consider multiple artists being credited for one song
    // TODO: 10/12/23 It's not clear however if there is one artist "owning" the song and other artists only collaborators
    //  or if it should be considered shared ownership but regardless it may be good to have the track show up as result for all contributing artists.
    @Test
    void givenMultipleMatchingArtistsForTrack_throwsException() {
        when(artistInformationRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME)))
                .thenReturn(List.of(mockedArtistDto, mockedArtistDto));

        assertThrows(IllegalArgumentException.class, () -> metaMusicService.addTrack(TestConstants.EXAMPLE_ARTIST_NAME, EXAMPLE_TRACK));
    }

    @Test
    void givenNoMatchingArtistsForTrack_throwsException() {
        when(artistInformationRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME))).thenReturn(List.of());

        assertThrows(NoArtistFoundException.class, () -> metaMusicService.addTrack(TestConstants.EXAMPLE_ARTIST_NAME, EXAMPLE_TRACK));
    }

    @Test
    void givenValidUpdateArtistInformationNewName_returnsExpectedUpdatedArtistInformation() {
        ArtistInformationEntity oldArtistInformation = new ArtistInformationEntity(TestConstants.EXAMPLE_ARTIST_NAME, Set.of());
        ArtistInformationEntity updatedArtistEntity = new ArtistInformationEntity("newName", Set.of());

        when(artistInformationRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME)))
                .thenReturn(List.of(oldArtistInformation));
        when(artistInformationRepository.save(eq(updatedArtistEntity))).thenReturn(updatedArtistEntity);

        ArtistInformation newArtistInformation = ArtistInformation.fromEntity(updatedArtistEntity);
        ArtistInformation updatedArtistInformation = metaMusicService.updateArtistInformation(TestConstants.EXAMPLE_ARTIST_NAME,
                newArtistInformation);

        assertEquals(newArtistInformation, updatedArtistInformation);
        verify(artistInformationRepository).save(eq(updatedArtistEntity));
    }

    @Test
    void givenValidUpdateArtistInformationNewAliases_returnsExpectedUpdatedArtistInformation() {
        String testName = "testName";
        ArtistInformationEntity oldArtistInformation = new ArtistInformationEntity(testName, Set.of("oldAlias"));
        ArtistInformationEntity updatedArtistEntity = new ArtistInformationEntity(testName, Set.of("oldAlias", "newAlias"));

        when(artistInformationRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME)))
                .thenReturn(List.of(oldArtistInformation));
        when(artistInformationRepository.save(eq(updatedArtistEntity))).thenReturn(updatedArtistEntity);

        ArtistInformation newArtistInformation = new ArtistInformation(testName, Set.of("newAlias"));
        ArtistInformation updatedArtistInformation = metaMusicService.updateArtistInformation(TestConstants.EXAMPLE_ARTIST_NAME,
                newArtistInformation);

        assertEquals(ArtistInformation.fromEntity(updatedArtistEntity), updatedArtistInformation);
        verify(artistInformationRepository).save(eq(updatedArtistEntity));
    }

    @Test
    void givenNonExistingArtist_whenCallingUpdateArtist_throwsException() {
        when(artistInformationRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME))).thenReturn(List.of());

        assertThrows(NoArtistFoundException.class,
                () -> metaMusicService.updateArtistInformation(TestConstants.EXAMPLE_ARTIST_NAME, new ArtistInformation("valid", Set.of())));
    }

    @Test
    void givenMultipleMatchingArtists_whenCallingUpdateArtist_shouldThrowException() {
        when(artistInformationRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME)))
                .thenReturn(List.of(mockedArtistDto, mockedArtistDto));

        assertThrows(IllegalArgumentException.class,
                () -> metaMusicService.updateArtistInformation(TestConstants.EXAMPLE_ARTIST_NAME, new ArtistInformation("valid", Set.of())));
    }

    @Test
    void givenAlreadyExistingArtistWithNewName_whenCallingUpdateArtist_shouldThrowException() {
        String newName = "newName";
        when(artistInformationRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME))).thenReturn(List.of(mockedArtistDto));
        when(artistInformationRepository.findByName(eq(newName))).thenReturn(List.of(mockedArtistDto));

        ArtistInformation newArtistInformation = new ArtistInformation(newName, Set.of());
        assertThrows(ArtistAlreadyExistsException.class,
                () -> metaMusicService.updateArtistInformation(TestConstants.EXAMPLE_ARTIST_NAME, newArtistInformation));
    }

    @Test
    void whenFetchArtistTracksPaginated_withValidArtistName_returnsExpectedTracks() {
        PageRequest pageRequest = PageRequest.of(0, 2);
        TrackInformation secondTrack = new TrackInformation(EXAMPLE_TRACK_TITLE + "2", EXAMPLE_GENRE, EXAMPLE_TRACK_DURATION, EXAMPLE_TRACK_RELEASE_DATE);
        Page<TrackInformation> expected = new PageImpl<>(List.of(EXAMPLE_TRACK, secondTrack));

        when(artistInformationRepository.findByName(eq(EXAMPLE_ARTIST_NAME))).thenReturn(List.of(mockedArtistDto));
        when(artistInformationRepository.fetchTracksPaginated(eq(1L), eq(pageRequest)))
                .thenReturn(expected.map(TrackInformation::toEntity));

        Page<TrackInformation> tracks = metaMusicService.getArtistTracksPaginated(TestConstants.EXAMPLE_ARTIST_NAME,
                0, 2);

        assertEquals(expected, tracks);
        verify(artistInformationRepository).fetchTracksPaginated(eq(1L), eq(pageRequest));
    }

    @Test
    void whenFetchArtistTracksPaginated_withMultiplePages_returnsExpectedTracks() {
        TrackInformation secondTrack = new TrackInformation(EXAMPLE_TRACK_TITLE + "2", EXAMPLE_GENRE, EXAMPLE_TRACK_DURATION, EXAMPLE_TRACK_RELEASE_DATE);
        Page<TrackInformation> expectedFirstPage = new PageImpl<>(List.of(EXAMPLE_TRACK));
        Page<TrackInformation> expectedSecondPage = new PageImpl<>(List.of(secondTrack));

        when(artistInformationRepository.findByName(eq(EXAMPLE_ARTIST_NAME))).thenReturn(List.of(mockedArtistDto));
        when(artistInformationRepository.fetchTracksPaginated(eq(1L), any()))
                .thenReturn(expectedFirstPage.map(TrackInformation::toEntity), expectedSecondPage.map(TrackInformation::toEntity));

        Page<TrackInformation> firstTracksPage = metaMusicService.getArtistTracksPaginated(TestConstants.EXAMPLE_ARTIST_NAME,
                0, 1);
        Page<TrackInformation> secondTracksPage = metaMusicService.getArtistTracksPaginated(TestConstants.EXAMPLE_ARTIST_NAME,
                1, 1);

        PageRequest expectedFirstPageRequest = PageRequest.of(0, 1);
        PageRequest expectedSecondPageRequest = PageRequest.of(1, 1);
        assertEquals(expectedFirstPage, firstTracksPage);
        assertEquals(expectedSecondPage, secondTracksPage);
        verify(artistInformationRepository).fetchTracksPaginated(eq(1L), eq(expectedFirstPageRequest));
        verify(artistInformationRepository).fetchTracksPaginated(eq(1L), eq(expectedSecondPageRequest));
    }

    @Test
    void whenFetchArtistTracksPaginated_withNonExistingArtistName_throwsException() {
        when(artistInformationRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME))).thenReturn(List.of());

        assertThrows(NoArtistFoundException.class, () -> metaMusicService.getArtistTracksPaginated(TestConstants.EXAMPLE_ARTIST_NAME,
                0, 1));
    }

    @Test
    void givenMultipleMatchingArtists_whenFetchArtistTracksPaginated_shouldThrowException() {
        when(artistInformationRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME)))
                .thenReturn(List.of(mockedArtistDto, mockedArtistDto));

        assertThrows(IllegalArgumentException.class,
                () -> metaMusicService.getArtistTracksPaginated(TestConstants.EXAMPLE_ARTIST_NAME, 0, 1));
    }
}