package com.demo.metamusic.adapter.persistence.dto;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
import java.util.Objects;

@Entity(name = "tracks")
@Data
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

    @Override
    public String toString() {
        return "TrackInformationEntity{" +
                "id=" + id +
                ", artist=" + artist.getName() +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", duration='" + duration + '\'' +
                ", releaseDate=" + releaseDate +
                '}';
    }
}
