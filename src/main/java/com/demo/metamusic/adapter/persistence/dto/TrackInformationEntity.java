package com.demo.metamusic.adapter.persistence.dto;

import jakarta.persistence.*;

import java.sql.Date;
import java.util.Objects;

@Entity(name = "tracks")
public class TrackInformationEntity {

    @SequenceGenerator(name = "tracks_id_seq",
            sequenceName = "tracks_id_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "tracks_id_seq")
    @Id
    private Long id;

    @Column(name="artist_id")
    private Long artistId;

    private String title;
    private String genre;
    private String duration;
    @Column(name="release_date")
    private Date releaseDate;

    public TrackInformationEntity() {
    }

    public TrackInformationEntity(Long artistId, String title, String genre, String duration, Date releaseDate) {
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
        TrackInformationEntity that = (TrackInformationEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(artistId, that.artistId) && Objects.equals(title, that.title) && Objects.equals(genre, that.genre) && Objects.equals(duration, that.duration) && Objects.equals(releaseDate, that.releaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, artistId, title, genre, duration, releaseDate);
    }
}
