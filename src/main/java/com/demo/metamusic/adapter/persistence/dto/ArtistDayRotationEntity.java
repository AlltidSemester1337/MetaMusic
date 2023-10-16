package com.demo.metamusic.adapter.persistence.dto;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;

@Entity(name = "artist_of_the_day_rotation")
@Data
public class ArtistDayRotationEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "artist_id")
    private ArtistEntity artist;

    private Date date;

    @Column(name = "most_recent")
    private boolean mostRecent;

    public ArtistDayRotationEntity() {
    }

    public ArtistDayRotationEntity(Date date, boolean mostRecent) {
        this.date = date;
        this.mostRecent = mostRecent;
    }

    @Override
    public String toString() {
        return "ArtistDayRotationEntity{" +
                "id=" + id +
                ", artist=" + artist.getId() +
                ", date=" + date +
                ", mostRecent=" + mostRecent +
                '}';
    }
}
