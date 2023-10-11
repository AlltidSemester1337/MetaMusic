package com.demo.metamusic.adapter.persistence.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.sql.Date;
import java.util.Objects;

@Entity
public class TrackInformationDTO {

        @GeneratedValue
        @Id
        private Long id;

        private Long artistId;

        private String title;
        private String genre;
        private String duration;
        private Date releaseDate;

        public TrackInformationDTO() {
        }

    public TrackInformationDTO(Long artistId, String title, String genre, String duration, Date releaseDate) {
        this.artistId = artistId;
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.releaseDate = releaseDate;
    }

    public Long getId() {
        return id;
    }

    public Long getArtistId() {
        return artistId;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public String getDuration() {
        return duration;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackInformationDTO that = (TrackInformationDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(artistId, that.artistId) && Objects.equals(title, that.title) && Objects.equals(genre, that.genre) && Objects.equals(duration, that.duration) && Objects.equals(releaseDate, that.releaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, artistId, title, genre, duration, releaseDate);
    }
}
