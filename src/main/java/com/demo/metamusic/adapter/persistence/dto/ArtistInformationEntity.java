package com.demo.metamusic.adapter.persistence.dto;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity(name="artists")
public class ArtistInformationEntity {

    @SequenceGenerator(name="artists_id_seq",
            sequenceName="artists_id_seq",
            allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator="artists_id_seq")
    @Id
    private Long id;

    private String name;

    private List<String> aliases;

    public ArtistInformationEntity() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtistInformationEntity that = (ArtistInformationEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(aliases, that.aliases);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, aliases);
    }
}
