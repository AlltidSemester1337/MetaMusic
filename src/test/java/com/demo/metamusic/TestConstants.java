package com.demo.metamusic;

public interface TestConstants {
    ;

    String EXAMPLE_TRACK_TITLE = "Gold Dust Woman";
    String EXAMPLE_ARTIST_NAME = "Fleetwood Mac";
    String EXAMPLE_GENRE = "Rock";
    String EXAMPLE_DURATION_TEXT = "4:55";
    String EXAMPLE_RELEASE_DATE_TEXT = "1977-02-04";
    String EXAMPLE_TRACK_PAYLOAD = createTrackPayload(EXAMPLE_TRACK_TITLE, EXAMPLE_ARTIST_NAME, EXAMPLE_GENRE, EXAMPLE_DURATION_TEXT, EXAMPLE_RELEASE_DATE_TEXT);
    String EXAMPLE_TRACK_INVALID_REQUEST_PAYLOAD_INVALID_TITLE = createTrackPayload("", EXAMPLE_ARTIST_NAME, EXAMPLE_GENRE, "4:66", EXAMPLE_RELEASE_DATE_TEXT);
    String EXAMPLE_TRACK_INVALID_REQUEST_PAYLOAD_INVALID_DURATION = createTrackPayload(EXAMPLE_TRACK_TITLE, EXAMPLE_ARTIST_NAME, EXAMPLE_GENRE, "4:66", EXAMPLE_RELEASE_DATE_TEXT);
    String EXAMPLE_TRACK_INVALID_REQUEST_PAYLOAD_INVALID_RELEASE_DATE = createTrackPayload(EXAMPLE_TRACK_TITLE, EXAMPLE_ARTIST_NAME, EXAMPLE_GENRE, EXAMPLE_DURATION_TEXT, "1977/04/66");

    static String createTrackPayload(String title, String artist, String genre, String duration, String releaseDate) {
        return String.format("""
                {
                  "title": "%s",
                  "artist": "%s",
                  "genre": "%s",
                  "duration": "%s",
                  "releaseDate": "%s"
                }
                """, title, artist, genre, duration, releaseDate);
    }
}
