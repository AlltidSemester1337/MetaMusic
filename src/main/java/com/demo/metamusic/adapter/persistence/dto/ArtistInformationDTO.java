package com.demo.metamusic.adapter.persistence.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.List;
import java.util.Objects;

@Entity
public class ArtistInformationDTO {

    @GeneratedValue
    @Id
    private Long id;

    private String name;

    private List<String> aliases;

    public ArtistInformationDTO() {
    }

    public ArtistInformationDTO(String name, List<String> aliases) {
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
        ArtistInformationDTO that = (ArtistInformationDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(aliases, that.aliases);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, aliases);
    }
}
