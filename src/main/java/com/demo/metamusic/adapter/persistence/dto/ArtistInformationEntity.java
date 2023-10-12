package com.demo.metamusic.adapter.persistence.dto;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name="artists")
public class ArtistInformationEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String name;

    private List<String> aliases;

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL)
    private List<TrackInformationEntity> tracks = new ArrayList<>();

    public ArtistInformationEntity() {
        tracks = new ArrayList<>();
    }

    public ArtistInformationEntity(String name, List<String> aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public List<TrackInformationEntity> getTracks() {
        return tracks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtistInformationEntity that = (ArtistInformationEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(aliases, that.aliases) && Objects.equals(tracks, that.tracks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, aliases, tracks);
    }
}
