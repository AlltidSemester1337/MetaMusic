package com.demo.metamusic;

import com.demo.metamusic.core.model.TrackInformation;

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
    // TODO: 2023-10-15 We could put this long Json string templates in a file to make it more convenient to read the code
    String EXAMPLE_PAGINATION_RESPONSE_TEMPLATE = """
            {
                "content": [
                    {
                        "title": "%s",
                        "genre": "%s",
                        "duration": "%s",
                        "releaseDate": "%s"
                    },
                    {
                        "title": "%s2",
                        "genre": "%s",
                        "duration": "%s",
                        "releaseDate": "%s"
                    }
                ],
                "pageable": "INSTANCE",
                "totalPages": 1,
                "totalElements": 2,
                "last": true,
                "number": 0,
                "size": 2,
                "numberOfElements": 2,
                "sort": {
                    "sorted": false,
                    "empty": true,
                    "unsorted": true
                },
                "first": true,
                "empty": false
            }
             """;
    String EXAMPLE_RESPONSE_PAGINATION_FIRST_PAGE_TEMPLATE = """
            {
                 "content": [
                     {
                         "title": "%s",
                         "genre": "%s",
                         "duration": "%s",
                         "releaseDate": "%s"
                     }
                 ],
                 "pageable": {
                     "pageNumber": 0,
                     "pageSize": 1,
                     "sort": {
                         "sorted": false,
                         "empty": true,
                         "unsorted": true
                     },
                     "offset": 0,
                     "paged": true,
                     "unpaged": false
                 },
                 "totalPages": 2,
                 "totalElements": 2,
                 "last": false,
                 "number": 0,
                 "size": 1,
                 "numberOfElements": 1,
                 "sort": {
                     "sorted": false,
                     "empty": true,
                     "unsorted": true
                 },
                 "first": true,
                 "empty": false
             }""";
    String EXAMPLE_RESPONSE_PAGINATION_SECOND_PAGE_TEMPLATE = """
            {
                 "content": [
                     {
                         "title": "%s2",
                         "genre": "%s",
                         "duration": "%s",
                         "releaseDate": "%s"
                     }
                 ],
                 "pageable": {
                     "pageNumber": 1,
                     "pageSize": 1,
                     "sort": {
                         "sorted": false,
                         "empty": true,
                         "unsorted": true
                     },
                     "offset": 1,
                     "paged": true,
                     "unpaged": false
                 },
                 "totalPages": 2,
                 "totalElements": 2,
                 "last": true,
                 "number": 1,
                 "size": 1,
                 "numberOfElements": 1,
                 "sort": {
                     "sorted": false,
                     "empty": true,
                     "unsorted": true
                 },
                 "first": false,
                 "empty": false
             }""";
    TrackInformation EXAMPLE_TRACK = new TrackInformation(EXAMPLE_TRACK_TITLE, EXAMPLE_GENRE, EXAMPLE_TRACK_DURATION, EXAMPLE_TRACK_RELEASE_DATE);


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
