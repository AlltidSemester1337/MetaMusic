package com.demo.metamusic.adapter.persistence;


import com.demo.metamusic.adapter.persistence.dto.ArtistEntity;
import com.demo.metamusic.adapter.persistence.dto.TrackEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ArtistRepository extends CrudRepository<ArtistEntity, Long> {
    List<ArtistEntity> findByName(String artistName);

    @Query("SELECT track from tracks track WHERE track.artist.id = :artistId")
    Page<TrackEntity> fetchTracksPaginated(Long artistId, Pageable pageable);

    Optional<ArtistEntity> getMostRecentArtistOfTheDay();

    Optional<Long> findMaxId();
}
