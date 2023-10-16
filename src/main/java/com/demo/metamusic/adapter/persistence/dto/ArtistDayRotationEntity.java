package com.demo.metamusic.adapter.persistence.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

import java.sql.Date;

@Entity(name = "artistofthedayrotation")
@Data
public class ArtistDayRotationEntity {

    @OneToOne
    @JoinColumn(name = "artist_id")
    private ArtistEntity artist;

    private Date date;

    @Column(name = "most_recent")
    private boolean mostRecent;

    public ArtistDayRotationEntity(Date date, boolean mostRecent) {
        this.date = date;
        this.mostRecent = mostRecent;
    }

    @Override
    public String toString() {
        return "ArtistDayRotationEntity{" +
                "artist=" + artist.getId() +
                ", date=" + date +
                ", mostRecent=" + mostRecent +
                '}';
    }
}
