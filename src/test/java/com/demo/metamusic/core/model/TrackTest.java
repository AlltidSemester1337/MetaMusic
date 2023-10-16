package com.demo.metamusic.core.model;

import com.demo.metamusic.TestConstants;
import com.demo.metamusic.adapter.http.dto.request.TrackDTO;
import com.demo.metamusic.adapter.persistence.dto.TrackEntity;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

class TrackTest {

    @Test
    void givenValidDTO_shouldConvertIntoExpectedResult() {

        TrackDTO TrackDTO =
                new TrackDTO(TestConstants.EXAMPLE_TRACK_TITLE,
                        TestConstants.EXAMPLE_GENRE, TestConstants.EXAMPLE_DURATION_TEXT, TestConstants.EXAMPLE_RELEASE_DATE_TEXT);

        assertEquals(TestConstants.EXAMPLE_TRACK, Track.fromDTO(TrackDTO));
    }

    @Test
    void givenDTOWithInvalidDuration_shouldThrowException() {

        TrackDTO TrackDTO =
                new TrackDTO(TestConstants.EXAMPLE_TRACK_TITLE,
                        TestConstants.EXAMPLE_GENRE, "4:66", TestConstants.EXAMPLE_RELEASE_DATE_TEXT);

        assertThrows(IllegalArgumentException.class, () -> Track.fromDTO(TrackDTO));
    }

    @Test
    void givenDTOWithInvalidDate_shouldThrowException() {

        TrackDTO TrackDTOInvalidDateFormat =
                new TrackDTO(TestConstants.EXAMPLE_TRACK_TITLE,
                        TestConstants.EXAMPLE_GENRE, TestConstants.EXAMPLE_DURATION_TEXT, "1997/04/12");

        TrackDTO TrackDTOInvalidDate =
                new TrackDTO(TestConstants.EXAMPLE_TRACK_TITLE,
                        TestConstants.EXAMPLE_GENRE, TestConstants.EXAMPLE_DURATION_TEXT, "1997/04/66");

        assertThrows(DateTimeParseException.class, () -> Track.fromDTO(TrackDTOInvalidDateFormat));
        assertThrows(DateTimeParseException.class, () -> Track.fromDTO(TrackDTOInvalidDate));
    }

    @Test
    void givenValidTrack_convertsToExpectedEntity() {
        TrackEntity expected = new TrackEntity(TestConstants.EXAMPLE_TRACK_TITLE,
                TestConstants.EXAMPLE_GENRE, TestConstants.EXAMPLE_DURATION_TEXT, Date.valueOf("1977-02-04"));

        assertEquals(expected, Track.toEntity(TestConstants.EXAMPLE_TRACK));
    }

    @Test
    void givenValidTrack_convertsToExpectedDto() {
        TrackDTO expected = new TrackDTO(TestConstants.EXAMPLE_TRACK_TITLE,
                TestConstants.EXAMPLE_GENRE, TestConstants.EXAMPLE_DURATION_TEXT, "1977-02-04");

        assertEquals(expected, Track.toDTO(TestConstants.EXAMPLE_TRACK));
    }

    @Test
    void givenValidEntity_shouldConvertIntoExpectedResult() {
        TrackEntity TrackEntity =
                new TrackEntity(TestConstants.EXAMPLE_TRACK_TITLE,
                        TestConstants.EXAMPLE_GENRE, TestConstants.EXAMPLE_DURATION_TEXT, Date.valueOf(TestConstants.EXAMPLE_TRACK_RELEASE_DATE));

        assertEquals(TestConstants.EXAMPLE_TRACK, Track.fromEntity(TrackEntity));
    }
}