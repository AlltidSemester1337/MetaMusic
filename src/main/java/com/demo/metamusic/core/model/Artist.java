package com.demo.metamusic.core.model;

import com.demo.metamusic.adapter.http.dto.request.ArtistUpdateDTO;
import com.demo.metamusic.adapter.http.dto.response.ArtistDTO;
import com.demo.metamusic.adapter.persistence.dto.ArtistEntity;
import io.micrometer.common.util.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.Set;

public record Artist(String name, Set<String> aliases) {

    public Artist {
        Validate.notBlank(name);
        aliases.forEach(Validate::notBlank);
    }

    public static Artist fromDTO(String artistName, ArtistUpdateDTO artistUpdateDTO) {
        String newNameFromRequest = artistUpdateDTO.newName();
        String newName = StringUtils.isBlank(newNameFromRequest) ? artistName : newNameFromRequest;

        Set<String> aliasesFromRequest = artistUpdateDTO.aliases();
        Set<String> newAliases = aliasesFromRequest == null ? Set.of() : aliasesFromRequest;

        return new Artist(newName, newAliases);
    }

    public static ArtistDTO toDTO(Artist Artist) {
        return new ArtistDTO(Artist.name(), Artist.aliases());
    }

    public static Artist fromEntity(ArtistEntity updatedEntity) {
        return new Artist(updatedEntity.getName(), updatedEntity.getAliases());
    }
}
