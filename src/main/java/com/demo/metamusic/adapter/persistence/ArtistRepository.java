package com.demo.metamusic.adapter.persistence;


import com.demo.metamusic.adapter.persistence.dto.ArtistEntity;
import com.demo.metamusic.adapter.persistence.dto.TrackEntity;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Optional;

import static javax.management.timer.Timer.ONE_DAY;

public interface ArtistRepository extends CrudRepository<ArtistEntity, Long> {
    List<ArtistEntity> findByName(String artistName);

    @Query("SELECT track from tracks track WHERE track.artist.id = :artistId")
    Page<TrackEntity> fetchTracksPaginated(Long artistId, Pageable pageable);

    @Query("SELECT a from artists a WHERE a.dayRotation.mostRecent = true")
    @Cacheable(cacheNames = "artistOfTheDayCache")
    Optional<ArtistEntity> getMostRecentArtistOfTheDay();

    @Query("SELECT MAX(artist.id) from artists artist")
    Optional<Long> findMaxId();
}
