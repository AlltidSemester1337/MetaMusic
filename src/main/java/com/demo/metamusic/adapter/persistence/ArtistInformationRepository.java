package com.demo.metamusic.adapter.persistence;


import com.demo.metamusic.adapter.persistence.dto.ArtistInformationEntity;
import com.demo.metamusic.adapter.persistence.dto.TrackInformationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ArtistInformationRepository extends CrudRepository<ArtistInformationEntity, Long> {
    List<ArtistInformationEntity> findByName(String artistName);

    @Query("SELECT track from tracks track WHERE track.artist.id = :artistId")
    Page<TrackInformationEntity> fetchTracksPaginated(Long artistId, Pageable pageable);
}
