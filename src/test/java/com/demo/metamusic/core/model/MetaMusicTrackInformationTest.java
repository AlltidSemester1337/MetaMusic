package com.demo.metamusic.core.model;

import com.demo.metamusic.TestConstants;
import com.demo.metamusic.adapter.http.dto.MetaMusicTrackInformationDTO;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

class MetaMusicTrackInformationTest {

    @Test
    void givenValidDTO_shouldConvertIntoExpectedResult() {
        Duration expectedDuration = Duration.ofMinutes(4).plusSeconds(55);
        LocalDate releaseDate = LocalDate.of(1977, 2, 4);

        MetaMusicTrackInformationDTO metaMusicTrackInformationDTO =
                new MetaMusicTrackInformationDTO(TestConstants.EXAMPLE_TRACK_TITLE, TestConstants.EXAMPLE_ARTIST_NAME,
                        TestConstants.EXAMPLE_GENRE, TestConstants.EXAMPLE_DURATION_TEXT, TestConstants.EXAMPLE_RELEASE_DATE_TEXT);
        MetaMusicTrackInformation expected = new MetaMusicTrackInformation(TestConstants.EXAMPLE_TRACK_TITLE,
                TestConstants.EXAMPLE_ARTIST_NAME, TestConstants.EXAMPLE_GENRE, expectedDuration, releaseDate);

        assertEquals(expected, MetaMusicTrackInformation.fromDTO(metaMusicTrackInformationDTO));
    }

    @Test
    void givenDTOWithInvalidDuration_shouldThrowException() {

        MetaMusicTrackInformationDTO metaMusicTrackInformationDTO =
                new MetaMusicTrackInformationDTO(TestConstants.EXAMPLE_TRACK_TITLE, TestConstants.EXAMPLE_ARTIST_NAME,
                        TestConstants.EXAMPLE_GENRE, "4:66", TestConstants.EXAMPLE_RELEASE_DATE_TEXT);

        assertThrows(IllegalArgumentException.class, () -> MetaMusicTrackInformation.fromDTO(metaMusicTrackInformationDTO));
    }

    @Test
    void givenDTOWithInvalidDate_shouldThrowException() {

        MetaMusicTrackInformationDTO metaMusicTrackInformationDTOInvalidDateFormat =
                new MetaMusicTrackInformationDTO(TestConstants.EXAMPLE_TRACK_TITLE, TestConstants.EXAMPLE_ARTIST_NAME,
                        TestConstants.EXAMPLE_GENRE, TestConstants.EXAMPLE_DURATION_TEXT, "1997/04/12");

        MetaMusicTrackInformationDTO metaMusicTrackInformationDTOInvalidDate =
                new MetaMusicTrackInformationDTO(TestConstants.EXAMPLE_TRACK_TITLE, TestConstants.EXAMPLE_ARTIST_NAME,
                        TestConstants.EXAMPLE_GENRE, TestConstants.EXAMPLE_DURATION_TEXT, "1997/04/66");

        assertThrows(DateTimeParseException.class, () -> MetaMusicTrackInformation.fromDTO(metaMusicTrackInformationDTOInvalidDateFormat));
        assertThrows(DateTimeParseException.class, () -> MetaMusicTrackInformation.fromDTO(metaMusicTrackInformationDTOInvalidDate));
    }
}