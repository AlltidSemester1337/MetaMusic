package com.demo.metamusic.core.service;

import com.demo.metamusic.TestConstants;
import com.demo.metamusic.adapter.persistence.ArtistInformationRepository;
import com.demo.metamusic.adapter.persistence.TrackInformationRepository;
import com.demo.metamusic.adapter.persistence.dto.ArtistInformationEntity;
import com.demo.metamusic.adapter.persistence.dto.TrackInformationEntity;
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
        ArtistInformationEntity mockedArtistDto = mock(ArtistInformationEntity.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
        when(mockedArtistDto.getId()).thenReturn(artistId);
        when(artistInformationRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME))).thenReturn(List.of(mockedArtistDto));

        TrackInformation newTrack = new TrackInformation(TestConstants.EXAMPLE_TRACK_TITLE, TestConstants.EXAMPLE_ARTIST_NAME,
                TestConstants.EXAMPLE_GENRE, Duration.ofSeconds(3), LocalDate.EPOCH);
        metaMusicService.addTrack(newTrack);

        verify(mockedArtistDto.getTracks()).add(eq(TrackInformation.toEntity(newTrack)));
        verify(artistInformationRepository).save(eq(mockedArtistDto));
    }

    // TODO: 10/12/23 With the current requirements this makes sense (One track can only have one artist),
    //  but as a more realistic model or improvement we should most likely consider multiple artists being credited for one song
    // TODO: 10/12/23 It's not clear however if there is one artist "owning" the song and other artists only collaborators
    //  or if it should be considered shared ownership but regardless it may be good to have the track show up as result for all contributing artists.
    @Test
    void givenMultipleMatchingArtistsForTrack_throwsException() {
        ArtistInformationEntity mockedArtistDto = mock(ArtistInformationEntity.class);
        when(artistInformationRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME)))
                .thenReturn(List.of(mockedArtistDto, mockedArtistDto));

        assertThrows(IllegalArgumentException.class, () -> metaMusicService.addTrack(new TrackInformation(
                TestConstants.EXAMPLE_TRACK_TITLE, TestConstants.EXAMPLE_ARTIST_NAME, TestConstants.EXAMPLE_GENRE,
                Duration.ofSeconds(3), LocalDate.EPOCH)));
    }

    @Test
    void givenNoMatchingArtistsForTrack_throwsException() {
        when(artistInformationRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME))).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> metaMusicService.addTrack(new TrackInformation(
                TestConstants.EXAMPLE_TRACK_TITLE, TestConstants.EXAMPLE_ARTIST_NAME, TestConstants.EXAMPLE_GENRE,
                Duration.ofSeconds(3), LocalDate.EPOCH)));
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