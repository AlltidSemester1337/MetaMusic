package com.demo.metamusic.adapter.persistence;

import com.demo.metamusic.adapter.persistence.dto.ArtistEntity;
import com.demo.metamusic.core.model.Artist;

import java.util.List;

public interface Artists {
    Artist save(ArtistEntity artistToUpdate);

    List<Artist> findByName(String artistName);
}