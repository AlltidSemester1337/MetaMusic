package com.demo.metamusic.core.model;

import com.demo.metamusic.TestConstants;
import com.demo.metamusic.adapter.http.dto.TrackInformationHttpDTO;
import com.demo.metamusic.adapter.persistence.dto.TrackInformationDTO;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

class TrackInformationTest {

    @Test
    void givenValidDTO_shouldConvertIntoExpectedResult() {

        TrackInformationHttpDTO trackInformationHttpDTO =
                new TrackInformationHttpDTO(TestConstants.EXAMPLE_TRACK_TITLE, TestConstants.EXAMPLE_ARTIST_NAME,
                        TestConstants.EXAMPLE_GENRE, TestConstants.EXAMPLE_DURATION_TEXT, TestConstants.EXAMPLE_RELEASE_DATE_TEXT);
        TrackInformation expected = new TrackInformation(TestConstants.EXAMPLE_TRACK_TITLE,
                TestConstants.EXAMPLE_ARTIST_NAME, TestConstants.EXAMPLE_GENRE, TestConstants.EXAMPLE_TRACK_DURATION,
                TestConstants.EXAMPLE_TRACK_RELEASE_DATE);

        assertEquals(expected, TrackInformation.fromHttpDTO(trackInformationHttpDTO));
    }

    @Test
    void givenDTOWithInvalidDuration_shouldThrowException() {

        TrackInformationHttpDTO trackInformationHttpDTO =
                new TrackInformationHttpDTO(TestConstants.EXAMPLE_TRACK_TITLE, TestConstants.EXAMPLE_ARTIST_NAME,
                        TestConstants.EXAMPLE_GENRE, "4:66", TestConstants.EXAMPLE_RELEASE_DATE_TEXT);

        assertThrows(IllegalArgumentException.class, () -> TrackInformation.fromHttpDTO(trackInformationHttpDTO));
    }

    @Test
    void givenDTOWithInvalidDate_shouldThrowException() {

        TrackInformationHttpDTO trackInformationHttpDTOInvalidDateFormat =
                new TrackInformationHttpDTO(TestConstants.EXAMPLE_TRACK_TITLE, TestConstants.EXAMPLE_ARTIST_NAME,
                        TestConstants.EXAMPLE_GENRE, TestConstants.EXAMPLE_DURATION_TEXT, "1997/04/12");

        TrackInformationHttpDTO trackInformationHttpDTOInvalidDate =
                new TrackInformationHttpDTO(TestConstants.EXAMPLE_TRACK_TITLE, TestConstants.EXAMPLE_ARTIST_NAME,
                        TestConstants.EXAMPLE_GENRE, TestConstants.EXAMPLE_DURATION_TEXT, "1997/04/66");

        assertThrows(DateTimeParseException.class, () -> TrackInformation.fromHttpDTO(trackInformationHttpDTOInvalidDateFormat));
        assertThrows(DateTimeParseException.class, () -> TrackInformation.fromHttpDTO(trackInformationHttpDTOInvalidDate));
    }

    @Test
    void givenValidTrackInformation_convertsToExpectedDTO() {
        Long artistId = 1L;

        TrackInformation trackInformation =
                new TrackInformation(TestConstants.EXAMPLE_TRACK_TITLE, TestConstants.EXAMPLE_ARTIST_NAME,
                        TestConstants.EXAMPLE_GENRE, TestConstants.EXAMPLE_TRACK_DURATION,
                        TestConstants.EXAMPLE_TRACK_RELEASE_DATE);
        TrackInformationDTO expected = new TrackInformationDTO(artistId, TestConstants.EXAMPLE_TRACK_TITLE,
                TestConstants.EXAMPLE_GENRE, TestConstants.EXAMPLE_DURATION_TEXT, Date.valueOf("1977-02-04"));

        assertEquals(expected, TrackInformation.toDTO(trackInformation, artistId));
    }
}