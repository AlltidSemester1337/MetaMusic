package com.demo.metamusic.core.model;

import com.demo.metamusic.adapter.http.dto.request.ArtistUpdateDTO;
import com.demo.metamusic.adapter.http.dto.response.UpdatedArtistDTO;
import org.apache.commons.lang3.Validate;

import java.util.List;

public record ArtistInformation(String name, List<String> aliases) {

    public ArtistInformation {
        Validate.notBlank(name);
    }

    public static ArtistInformation fromDTO(ArtistUpdateDTO artistUpdateDTO) {
        return new ArtistInformation(artistUpdateDTO.name(), List.of());
    }

    public static UpdatedArtistDTO toDTO(ArtistInformation artistInformation) {
        return new UpdatedArtistDTO(artistInformation.name(), artistInformation.aliases());
    }
}
