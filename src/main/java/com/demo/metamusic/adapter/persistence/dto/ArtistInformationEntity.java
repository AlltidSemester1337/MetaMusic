package com.demo.metamusic.adapter.persistence.dto;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity(name="artists")
@Data
public class ArtistInformationEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String name;

    private Set<String> aliases;

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL)
    private List<TrackInformationEntity> tracks = new ArrayList<>();

    public ArtistInformationEntity() {
        tracks = new ArrayList<>();
    }

    public ArtistInformationEntity(String name, Set<String> aliases) {
        this.name = name;
        this.aliases = aliases;
    }
}
