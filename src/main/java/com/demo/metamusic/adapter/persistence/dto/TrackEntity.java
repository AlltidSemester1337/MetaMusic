package com.demo.metamusic.adapter.persistence.dto;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;

@Entity(name = "tracks")
@Data
public class TrackEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    // TODO: 10/23/23 Test
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id")
    private ArtistEntity artist;

    private String title;
    private String genre;
    private String duration;
    @Column(name="release_date")
    private Date releaseDate;

    public TrackEntity() {
    }

    public TrackEntity(String title, String genre, String duration, Date releaseDate) {
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {
        return "TrackEntity{" +
                "id=" + id +
                ", artist=" + artist.getName() +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", duration='" + duration + '\'' +
                ", releaseDate=" + releaseDate +
                '}';
    }
}
