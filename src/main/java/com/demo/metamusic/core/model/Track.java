package com.demo.metamusic.core.model;

import com.demo.metamusic.adapter.http.dto.request.TrackDTO;
import com.demo.metamusic.adapter.persistence.dto.TrackEntity;
import org.apache.commons.lang3.Validate;

import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;

public record Track(String title, String genre, Duration duration,
                               LocalDate releaseDate) {
    public Track {
        Validate.notBlank(title);
        Validate.notBlank(genre);
    }

    public static Track fromDTO(TrackDTO TrackDTO) {
        Duration duration = parseDuration(TrackDTO.duration());
        LocalDate releaseDate = parseReleaseDate(TrackDTO.releaseDate());

        return new Track(TrackDTO.title(),
                TrackDTO.genre(), duration, releaseDate);
    }

    public static TrackDTO toDTO(Track Track) {
        return new TrackDTO(Track.title(), Track.genre(),
                convertDurationToString(Track.duration()), Track.releaseDate().toString());
    }

    public static TrackEntity toEntity(Track Track) {
        String duration = convertDurationToString(Track.duration());
        Date releaseDate = Date.valueOf(Track.releaseDate());

        return new TrackEntity(Track.title(),
                Track.genre(), duration, releaseDate);
    }

    private static String convertDurationToString(Duration duration) {
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

    public static Track fromEntity(TrackEntity TrackEntity) {
        return new Track(TrackEntity.getTitle(), TrackEntity.getGenre(),
                parseDuration(TrackEntity.getDuration()), parseReleaseDate(TrackEntity.getReleaseDate().toString()));
    }
}
