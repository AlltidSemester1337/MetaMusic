package com.demo.metamusic.core.service;

import com.demo.metamusic.TestConstants;
import com.demo.metamusic.adapter.persistence.ArtistAlreadyExistsException;
import com.demo.metamusic.adapter.persistence.ArtistInformationRepository;
import com.demo.metamusic.adapter.persistence.NoArtistFoundException;
import com.demo.metamusic.adapter.persistence.TrackInformationRepository;
import com.demo.metamusic.adapter.persistence.dto.ArtistInformationEntity;
import com.demo.metamusic.adapter.persistence.dto.TrackInformationEntity;
import com.demo.metamusic.core.model.ArtistInformation;
import com.demo.metamusic.core.model.TrackInformation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockSettings;
import org.mockito.MockitoAnnotations;

import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


class MetaMusicServiceImplTest {

    @Mock
    private TrackInformationRepository trackInformationRepository;

    @Mock
    private ArtistInformationRepository artistInformationRepository;
    @InjectMocks
    private MetaMusicServiceImpl metaMusicService;
    private AutoCloseable autoCloseable;
    private final ArtistInformationEntity mockedArtistDto = mock(ArtistInformationEntity.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
    private final TrackInformation trackInformation = new TrackInformation(
            TestConstants.EXAMPLE_TRACK_TITLE, TestConstants.EXAMPLE_GENRE,
            Duration.ofSeconds(3), LocalDate.EPOCH);

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void givenValidTrackInformation_makesNecessaryCallsToPersistArtistData() {
        long artistId = 1L;
        when(mockedArtistDto.getId()).thenReturn(artistId);
        when(artistInformationRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME))).thenReturn(List.of(mockedArtistDto));

        metaMusicService.addTrack(TestConstants.EXAMPLE_ARTIST_NAME, trackInformation);

        TrackInformationEntity expected = TrackInformation.toEntity(trackInformation);
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

        assertThrows(IllegalArgumentException.class, () -> metaMusicService.addTrack(TestConstants.EXAMPLE_ARTIST_NAME, trackInformation));
    }

    @Test
    void givenNoMatchingArtistsForTrack_throwsException() {
        when(artistInformationRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME))).thenReturn(List.of());

        assertThrows(NoArtistFoundException.class, () -> metaMusicService.addTrack(TestConstants.EXAMPLE_ARTIST_NAME, trackInformation));
    }

    @Test
    void givenValidUpdateArtistInformation_returnsExpectedUpdatedArtistInformation() {
        ArtistInformationEntity oldArtistInformation = new ArtistInformationEntity("oldName", List.of());
        ArtistInformationEntity updatedArtistEntity = new ArtistInformationEntity("newName", List.of());

        when(artistInformationRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME)))
                .thenReturn(List.of(oldArtistInformation));
        when(artistInformationRepository.save(eq(updatedArtistEntity))).thenReturn(updatedArtistEntity);

        ArtistInformation newArtistInformation = ArtistInformation.fromEntity(updatedArtistEntity);
        ArtistInformation updatedArtistInformation = metaMusicService.updateArtistInformation(
                TestConstants.EXAMPLE_ARTIST_NAME, newArtistInformation);

        assertEquals(newArtistInformation, updatedArtistInformation);
        verify(artistInformationRepository).save(eq(updatedArtistEntity));
    }

    @Test
    void givenNonExistingArtist_whenCallingUpdateArtist_throwsException() {
        when(artistInformationRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME))).thenReturn(List.of());

        assertThrows(NoArtistFoundException.class,
                () -> metaMusicService.updateArtistInformation(TestConstants.EXAMPLE_ARTIST_NAME, null));
    }

    @Test
    void givenMultipleMatchingArtists_whenCallingUpdateArtist_shouldThrowException() {
        when(artistInformationRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME)))
                .thenReturn(List.of(mockedArtistDto, mockedArtistDto));

        assertThrows(IllegalArgumentException.class,
                () -> metaMusicService.updateArtistInformation(TestConstants.EXAMPLE_ARTIST_NAME, null));
    }

    @Test
    void givenAlreadyExistingArtistWithNewName_whenCallingUpdateArtist_shouldThrowException() {
        String newName = "newName";
        when(artistInformationRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME))).thenReturn(List.of(mockedArtistDto));
        when(artistInformationRepository.findByName(eq(newName))).thenReturn(List.of(mockedArtistDto));

        ArtistInformation newArtistInformation = new ArtistInformation(newName, List.of());
        assertThrows(ArtistAlreadyExistsException.class,
                () -> metaMusicService.updateArtistInformation(TestConstants.EXAMPLE_ARTIST_NAME, newArtistInformation));
    }
    /*

    @Test
    void givenValidBrokerName_whenGetBrokerInformation_returnsExpectedBrokerInformation() {
        Map<String, metamusicInformation> testMap = new HashMap<>();
        testMap.put(TestConstants.BROKER_NAME, TestConstants.BROKER_INFORMATION);
        metamusicServiceImpl brokerService = new metamusicServiceImpl(testMap);
        assertEquals(TestConstants.BROKER_INFORMATION, brokerService.getBrokerInformation(TestConstants.BROKER_NAME));
    }

    @Test
    void givenValidBrokerName_whenDeleteBrokerInformation_returnsExpectedResult() {
        Map<String, metamusicInformation> testMap = new HashMap<>();
        String anotherBrokerName = TestConstants.BROKER_NAME + "2";

        testMap.put(TestConstants.BROKER_NAME, TestConstants.BROKER_INFORMATION);
        testMap.put(anotherBrokerName, TestConstants.BROKER_INFORMATION);
        metamusicServiceImpl brokerService = new metamusicServiceImpl(testMap);

        assertEquals(2, testMap.size());
        brokerService.deleteBrokerInformation(TestConstants.BROKER_NAME);

        assertEquals(1, testMap.size());
        assertEquals(TestConstants.BROKER_INFORMATION, brokerService.getBrokerInformation(anotherBrokerName));
    }

     */

}