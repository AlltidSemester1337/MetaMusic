package com.demo.metamusic.core.model;

import com.demo.metamusic.adapter.http.dto.MetaMusicTrackInformationDTO;
import org.apache.commons.lang3.Validate;

import java.time.Duration;
import java.time.LocalDate;

public record MetaMusicTrackInformation(String title, String artist, String genre, Duration duration,
                                        LocalDate releaseDate) {

    public static MetaMusicTrackInformation fromDTO(MetaMusicTrackInformationDTO metaMusicTrackInformationDTO) {
        Duration duration = convertDuration(metaMusicTrackInformationDTO.duration());
        LocalDate releaseDate = convertReleaseDate(metaMusicTrackInformationDTO.releaseDate());

        return new MetaMusicTrackInformation(metaMusicTrackInformationDTO.title(), metaMusicTrackInformationDTO.artist(),
                metaMusicTrackInformationDTO.genre(), duration, releaseDate);
    }

    private static Duration convertDuration(String durationText) {
        Validate.matchesPattern(durationText, "^[0-9]+:[0-5][0-9]$");
        String[] durationMinutesSeconds = durationText.split(":");
        return Duration.ofMinutes(Long.parseLong(durationMinutesSeconds[0])).plusSeconds(Long.parseLong(durationMinutesSeconds[1]));
    }

    private static LocalDate convertReleaseDate(String releaseDate) {
        return LocalDate.parse(releaseDate);
    }
}
