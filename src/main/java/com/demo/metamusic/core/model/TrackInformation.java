package com.demo.metamusic.core.model;

import com.demo.metamusic.adapter.http.dto.request.TrackInformationDTO;
import com.demo.metamusic.adapter.persistence.dto.TrackInformationEntity;
import org.apache.commons.lang3.Validate;

import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;

public record TrackInformation(String title, String artist, String genre, Duration duration,
                               LocalDate releaseDate) {
    public TrackInformation {
        Validate.notBlank(title);
        Validate.notBlank(artist);
        Validate.notBlank(genre);
    }

    public static TrackInformation fromDTO(TrackInformationDTO trackInformationDTO) {
        Duration duration = parseDuration(trackInformationDTO.duration());
        LocalDate releaseDate = parseReleaseDate(trackInformationDTO.releaseDate());

        return new TrackInformation(trackInformationDTO.title(), trackInformationDTO.artist(),
                trackInformationDTO.genre(), duration, releaseDate);
    }

    public static TrackInformationEntity toEntity(TrackInformation trackInformation) {
        String duration = toString(trackInformation.duration());
        Date releaseDate = Date.valueOf(trackInformation.releaseDate());

        return new TrackInformationEntity(trackInformation.title(),
                trackInformation.genre(), duration, releaseDate);
    }

    private static String toString(Duration duration) {
        long minutes = duration.toMinutes();
        return minutes + ":" + duration.minusMinutes(minutes).toSeconds();
    }

    private static Duration parseDuration(String durationText) {
        Validate.matchesPattern(durationText, "^[0-9]+:[0-5][0-9]$");
        String[] durationMinutesSeconds = durationText.split(":");
        return Duration.ofMinutes(Long.parseLong(durationMinutesSeconds[0])).plusSeconds(Long.parseLong(durationMinutesSeconds[1]));
    }

    private static LocalDate parseReleaseDate(String releaseDate) {
        return LocalDate.parse(releaseDate);
    }
}
