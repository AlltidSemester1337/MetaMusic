package com.demo.metamusic.core.model;

import com.demo.metamusic.TestConstants;
import com.demo.metamusic.adapter.http.dto.request.TrackInformationDTO;
import com.demo.metamusic.adapter.persistence.dto.TrackInformationEntity;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

class TrackInformationTest {

    @Test
    void givenValidDTO_shouldConvertIntoExpectedResult() {

        TrackInformationDTO trackInformationDTO =
                new TrackInformationDTO(TestConstants.EXAMPLE_TRACK_TITLE,
                        TestConstants.EXAMPLE_GENRE, TestConstants.EXAMPLE_DURATION_TEXT, TestConstants.EXAMPLE_RELEASE_DATE_TEXT);

        assertEquals(TestConstants.EXAMPLE_TRACK, TrackInformation.fromDTO(trackInformationDTO));
    }

    @Test
    void givenDTOWithInvalidDuration_shouldThrowException() {

        TrackInformationDTO trackInformationDTO =
                new TrackInformationDTO(TestConstants.EXAMPLE_TRACK_TITLE,
                        TestConstants.EXAMPLE_GENRE, "4:66", TestConstants.EXAMPLE_RELEASE_DATE_TEXT);

        assertThrows(IllegalArgumentException.class, () -> TrackInformation.fromDTO(trackInformationDTO));
    }

    @Test
    void givenDTOWithInvalidDate_shouldThrowException() {

        TrackInformationDTO trackInformationDTOInvalidDateFormat =
                new TrackInformationDTO(TestConstants.EXAMPLE_TRACK_TITLE,
                        TestConstants.EXAMPLE_GENRE, TestConstants.EXAMPLE_DURATION_TEXT, "1997/04/12");

        TrackInformationDTO trackInformationDTOInvalidDate =
                new TrackInformationDTO(TestConstants.EXAMPLE_TRACK_TITLE,
                        TestConstants.EXAMPLE_GENRE, TestConstants.EXAMPLE_DURATION_TEXT, "1997/04/66");

        assertThrows(DateTimeParseException.class, () -> TrackInformation.fromDTO(trackInformationDTOInvalidDateFormat));
        assertThrows(DateTimeParseException.class, () -> TrackInformation.fromDTO(trackInformationDTOInvalidDate));
    }

    @Test
    void givenValidTrackInformation_convertsToExpectedEntity() {
        TrackInformationEntity expected = new TrackInformationEntity(TestConstants.EXAMPLE_TRACK_TITLE,
                TestConstants.EXAMPLE_GENRE, TestConstants.EXAMPLE_DURATION_TEXT, Date.valueOf("1977-02-04"));

        assertEquals(expected, TrackInformation.toEntity(TestConstants.EXAMPLE_TRACK));
    }

    @Test
    void givenValidTrackInformation_convertsToExpectedDto() {
        TrackInformationDTO expected = new TrackInformationDTO(TestConstants.EXAMPLE_TRACK_TITLE,
                TestConstants.EXAMPLE_GENRE, TestConstants.EXAMPLE_DURATION_TEXT, "1977-02-04");

        assertEquals(expected, TrackInformation.toDTO(TestConstants.EXAMPLE_TRACK));
    }

    @Test
    void givenValidEntity_shouldConvertIntoExpectedResult() {
        TrackInformationEntity trackInformationEntity =
                new TrackInformationEntity(TestConstants.EXAMPLE_TRACK_TITLE,
                        TestConstants.EXAMPLE_GENRE, TestConstants.EXAMPLE_DURATION_TEXT, Date.valueOf(TestConstants.EXAMPLE_TRACK_RELEASE_DATE));

        assertEquals(TestConstants.EXAMPLE_TRACK, TrackInformation.fromEntity(trackInformationEntity));
    }
}