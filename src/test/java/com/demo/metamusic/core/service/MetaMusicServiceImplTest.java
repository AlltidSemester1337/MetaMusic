package com.demo.metamusic.core.service;

import com.demo.metamusic.TestConstants;
import com.demo.metamusic.adapter.persistence.ArtistInformationRepository;
import com.demo.metamusic.adapter.persistence.TrackInformationRepository;
import com.demo.metamusic.adapter.persistence.dto.ArtistInformationDTO;
import com.demo.metamusic.adapter.persistence.dto.TrackInformationDTO;
import com.demo.metamusic.core.model.TrackInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


class MetaMusicServiceImplTest {

    @Mock
    private TrackInformationRepository trackInformationRepository;

    @Mock
    private ArtistInformationRepository artistInformationRepository;
    @InjectMocks
    private MetaMusicServiceImpl metaMusicService;

    @BeforeEach
    void setUp() {
        // TODO: 10/11/23 Do we need to treat the returned autoclosable?
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenValidTrackInformation_makesCallToPersistData() {
        long artistId = 1L;
        ArtistInformationDTO mockedArtistDto = mock(ArtistInformationDTO.class);
        when(mockedArtistDto.getId()).thenReturn(artistId);
        when(artistInformationRepository.findByName(eq(TestConstants.EXAMPLE_ARTIST_NAME))).thenReturn(List.of(mockedArtistDto));

        metaMusicService.addTrack(new TrackInformation(TestConstants.EXAMPLE_TRACK_TITLE, TestConstants.EXAMPLE_ARTIST_NAME, TestConstants.EXAMPLE_GENRE, Duration.ofSeconds(3), LocalDate.EPOCH));
        
        verify(trackInformationRepository).save(eq(new TrackInformationDTO(artistId, TestConstants.EXAMPLE_TRACK_TITLE, TestConstants.EXAMPLE_GENRE, "0:3", Date.valueOf(LocalDate.EPOCH))));
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