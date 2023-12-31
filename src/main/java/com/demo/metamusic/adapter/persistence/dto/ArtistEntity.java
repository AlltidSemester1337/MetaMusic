package com.demo.metamusic.adapter.persistence.dto;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "artists")
@Data
public class ArtistEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String name;

    @ElementCollection
    private Set<String> aliases;

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL)
    private List<TrackEntity> tracks = new ArrayList<>();

    @OneToOne(mappedBy = "artist", cascade = CascadeType.ALL)
    private ArtistDayRotationEntity dayRotation;

    public ArtistEntity() {
        tracks = new ArrayList<>();
    }

    public ArtistEntity(String name, Set<String> aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    public ArtistDayRotationEntity getDayRotation() {
        return dayRotation;
    }
}
