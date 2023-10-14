package com.demo.metamusic.core.model;

import com.demo.metamusic.adapter.http.dto.request.ArtistUpdateDTO;
import com.demo.metamusic.adapter.http.dto.response.UpdatedArtistDTO;
import com.demo.metamusic.adapter.persistence.dto.ArtistInformationEntity;
import io.micrometer.common.util.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.Set;

public record ArtistInformation(String name, Set<String> aliases) {

    public ArtistInformation {
        Validate.notBlank(name);
        aliases.forEach(Validate::notBlank);
    }

    public static ArtistInformation fromDTO(String artistName, ArtistUpdateDTO artistUpdateDTO) {
        String newNameFromRequest = artistUpdateDTO.newName();
        String newName = StringUtils.isBlank(newNameFromRequest) ? artistName : newNameFromRequest;

        Set<String> aliasesFromRequest = artistUpdateDTO.aliases();
        Set<String> newAliases = aliasesFromRequest == null ? Set.of() : aliasesFromRequest;

        return new ArtistInformation(newName, newAliases);
    }

    public static UpdatedArtistDTO toDTO(ArtistInformation artistInformation) {
        return new UpdatedArtistDTO(artistInformation.name(), artistInformation.aliases());
    }

    public static ArtistInformation fromEntity(ArtistInformationEntity updatedEntity) {
        return new ArtistInformation(updatedEntity.getName(), updatedEntity.getAliases());
    }
}
