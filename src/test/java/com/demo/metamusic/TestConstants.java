package com.demo.metamusic;

import java.time.Duration;
import java.time.LocalDate;

public interface TestConstants {

    String EXAMPLE_TRACK_TITLE = "Gold Dust Woman";
    String EXAMPLE_ARTIST_NAME = "Fleetwood Mac";
    String EXAMPLE_ARTIST_NAME_URL_ENCODED = "Fleetwood+Mac";
    String EXAMPLE_GENRE = "Rock";
    String EXAMPLE_DURATION_TEXT = "4:55";
    String EXAMPLE_RELEASE_DATE_TEXT = "1977-02-04";
    String EXAMPLE_TRACK_PAYLOAD = createTrackPayload(EXAMPLE_TRACK_TITLE, EXAMPLE_GENRE, EXAMPLE_DURATION_TEXT, EXAMPLE_RELEASE_DATE_TEXT);
    String EXAMPLE_TRACK_INVALID_REQUEST_PAYLOAD_INVALID_TITLE = createTrackPayload("", EXAMPLE_GENRE, EXAMPLE_DURATION_TEXT, EXAMPLE_RELEASE_DATE_TEXT);
    String EXAMPLE_TRACK_INVALID_REQUEST_PAYLOAD_INVALID_DURATION = createTrackPayload(EXAMPLE_TRACK_TITLE, EXAMPLE_GENRE, "4:66", EXAMPLE_RELEASE_DATE_TEXT);
    String EXAMPLE_TRACK_INVALID_REQUEST_PAYLOAD_INVALID_RELEASE_DATE = createTrackPayload(EXAMPLE_TRACK_TITLE, EXAMPLE_GENRE, EXAMPLE_DURATION_TEXT, "1977/04/66");
    Duration EXAMPLE_TRACK_DURATION = Duration.ofMinutes(4).plusSeconds(55);

    LocalDate EXAMPLE_TRACK_RELEASE_DATE = LocalDate.of(1977, 2, 4);


    static String createTrackPayload(String title, String genre, String duration, String releaseDate) {
        return String.format("""
                {
                  "title": "%s",
                  "genre": "%s",
                  "duration": "%s",
                  "releaseDate": "%s"
                }
                """, title, genre, duration, releaseDate);
    }
}
