package com.demo.metamusic.adapter.persistence.dto;

import jakarta.persistence.*;

import java.sql.Date;
import java.util.Objects;

@Entity(name = "tracks")
public class TrackInformationEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private ArtistInformationEntity artist;

    private String title;
    private String genre;
    private String duration;
    @Column(name="release_date")
    private Date releaseDate;

    public TrackInformationEntity() {
    }

    public TrackInformationEntity(String title, String genre, String duration, Date releaseDate) {
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.releaseDate = releaseDate;
    }

    public Long getId() {
        return id;
    }

    public ArtistInformationEntity getArtist() {
        return artist;
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

    public void setArtist(ArtistInformationEntity artist) {
        this.artist = artist;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackInformationEntity that = (TrackInformationEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(artist, that.artist) && Objects.equals(title, that.title) && Objects.equals(genre, that.genre) && Objects.equals(duration, that.duration) && Objects.equals(releaseDate, that.releaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, artist, title, genre, duration, releaseDate);
    }
}
