package com.demo.metamusic.core.service;


import com.demo.metamusic.adapter.persistence.ArtistAlreadyExistsException;
import com.demo.metamusic.adapter.persistence.ArtistRepository;
import com.demo.metamusic.adapter.persistence.NoArtistFoundException;
import com.demo.metamusic.adapter.persistence.dto.ArtistDayRotationEntity;
import com.demo.metamusic.adapter.persistence.dto.ArtistEntity;
import com.demo.metamusic.adapter.persistence.dto.TrackEntity;
import com.demo.metamusic.core.model.Artist;
import com.demo.metamusic.core.model.Track;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static javax.management.timer.Timer.ONE_DAY;

@Slf4j
@Service
public class MetaMusicServiceImpl implements MetaMusicService {

    public static final ZoneId DEFAULT_ZONE = ZoneId.of("Europe/Paris");
    // TODO: 10/24/23 Should work on a abstraction "Artists" interface with ArtistJpaAdapter handling JPA interactions and entity mappings
    private final ArtistRepository artistRepository;

    public MetaMusicServiceImpl(ArtistRepository ArtistRepository) {
        this.artistRepository = ArtistRepository;
    }

    @Override
    public void addTrack(String artistName, Track Track) {
        ArtistEntity artistToUpdate = getSingleMatchingArtistByName(artistName);

        TrackEntity newTrack = Track.toEntity(Track);
        newTrack.setArtist(artistToUpdate);
        artistToUpdate.getTracks().add(newTrack);
        ArtistEntity updatedArtist = artistRepository.save(artistToUpdate);
        log.debug("Updated artist: {}", updatedArtist);
    }

    private ArtistEntity getSingleMatchingArtistByName(String artistName) {
        List<ArtistEntity> matchingArtists = artistRepository.findByName(artistName);
        log.debug("Found matching artists: {}", matchingArtists);

        if (matchingArtists.isEmpty()) {
            throw new NoArtistFoundException("Could not find any artist with provided name: " + artistName);
        }

        Validate.isTrue(matchingArtists.size() == 1, "Found multiple matching artists with name: " + artistName);
        return matchingArtists.get(0);
    }

    @Override
    @Transactional
    public Artist updateArtist(String oldArtistName, Artist newArtist) {
        verifyNewArtistNameDoesNotExist(newArtist.name());
        ArtistEntity artistToUpdate = getSingleMatchingArtistByName(oldArtistName);

        artistToUpdate.setName(newArtist.name());

        Set<String> oldAliases = artistToUpdate.getAliases();
        Set<String> aliases = oldAliases == null ? new HashSet<>() : new HashSet<>(oldAliases);
        aliases.addAll(newArtist.aliases());
        artistToUpdate.setAliases(aliases);

        ArtistEntity updatedEntity = artistRepository.save(artistToUpdate);
        log.debug("Updated artist: {}", updatedEntity);
        return Artist.fromEntity(updatedEntity);
    }

    private void verifyNewArtistNameDoesNotExist(String artistName) {
        List<ArtistEntity> matchingArtists = artistRepository.findByName(artistName);
        log.debug("Found matching artists: {}", matchingArtists);

        if (!matchingArtists.isEmpty()) {
            throw new ArtistAlreadyExistsException("Found already existing artist with name: " + artistName);
        }
    }

    @Override
    public Page<Track> getArtistTracksPaginated(String artistName, int page, int numTracks) {
        ArtistEntity Artist = getSingleMatchingArtistByName(artistName);
        PageRequest pageRequest = PageRequest.of(page, numTracks);
        Page<TrackEntity> trackEntities = artistRepository.fetchTracksPaginated(Artist.getId(), pageRequest);
        log.debug("Paginated tracks result: {}", trackEntities);
        return trackEntities.map(Track::fromEntity);
    }

    @Override
    public Optional<Artist> getArtistOfTheDay() {
        if (artistRepository.count() == 0) {
            log.debug("Artist table empty, returning empty");
            return Optional.empty();
        }

        Optional<ArtistEntity> possibleMostRecentArtistOfTheDay = artistRepository.getMostRecentArtistOfTheDay();
        if (possibleMostRecentArtistOfTheDay.isEmpty()) {
            log.info("Found no most recent artist of the day");
            return Optional.of(Artist.fromEntity(assignNewArtistOfTheDay(1L)));
        }

        ArtistEntity mostRecentArtistOfTheDay = possibleMostRecentArtistOfTheDay.get();
        if (isExpired(mostRecentArtistOfTheDay)) {
            log.info("Most recent artist: {} expired", mostRecentArtistOfTheDay);
            Optional<Artist> newArtistOfTheDay = Optional.of(updateArtistOfTheDay(mostRecentArtistOfTheDay));
            evictArtistOfTheDayCache();
            return newArtistOfTheDay;
        }

        log.debug("Found valid artist of the day: {}", mostRecentArtistOfTheDay);
        return Optional.of(Artist.fromEntity(mostRecentArtistOfTheDay));
    }

    // TODO: 10/24/23 If we want to guarantee rotation without api calls we can schedule but then we should remove explicit call
    //@Scheduled(fixedRate = ONE_DAY)
    // TODO: 10/24/23 Test
    @CacheEvict(cacheNames = "artistOfTheDay")
    private void evictArtistOfTheDayCache() {
        log.info("artistOfTheDay cache cleared");
    }

    private ArtistEntity assignNewArtistOfTheDay(Long mostRecentArtistOfTheDayId) {
        ArtistEntity newArtistOfTheDay = findNewArtistOfTheDay(mostRecentArtistOfTheDayId);
        ArtistDayRotationEntity dayRotation = new ArtistDayRotationEntity(Date.valueOf(LocalDate.now(DEFAULT_ZONE)), true);
        dayRotation.setArtist(newArtistOfTheDay);
        newArtistOfTheDay.setDayRotation(dayRotation);
        log.info("Assigning new artist of the day: {}", newArtistOfTheDay);
        return artistRepository.save(newArtistOfTheDay);
    }

    private boolean isExpired(ArtistEntity mostRecentArtistOfTheDay) {
        return mostRecentArtistOfTheDay.getDayRotation().getDate().toLocalDate()
                .isBefore(LocalDate.now(DEFAULT_ZONE));
    }

    @Transactional
    public Artist updateArtistOfTheDay(ArtistEntity mostRecentArtistOfTheDay) {
        ArtistEntity newArtistOfTheDay = assignNewArtistOfTheDay(mostRecentArtistOfTheDay.getId());
        // TODO: 10/24/23 Delete old Artist of the day instead for performance optimization of the get, no need to keep historical entries
        mostRecentArtistOfTheDay.getDayRotation().setMostRecent(false);
        log.info("Updating yesterdays artist of the day, set as no longer most recent: {}", mostRecentArtistOfTheDay);
        artistRepository.save(mostRecentArtistOfTheDay);

        return Artist.fromEntity(newArtistOfTheDay);
    }

    private ArtistEntity findNewArtistOfTheDay(Long mostRecentId) {
        long current = mostRecentId - 1;
        while (0 < current) {
            Optional<ArtistEntity> possibleNextArtistOfTheDay = artistRepository.findById(current);
            if (possibleNextArtistOfTheDay.isPresent()) {
                ArtistEntity newArtistOfTheDay = possibleNextArtistOfTheDay.get();
                log.debug("Found new artist of the day: {}", newArtistOfTheDay);
                return newArtistOfTheDay;
            }
            current--;
        }
        Long maxId = artistRepository.findMaxId()
                .orElseThrow(() -> new IllegalStateException("Could not fetch max id value in artist table despite table assumed not empty"));

        ArtistEntity newArtistOfTheDay = artistRepository.findById(maxId)
                .orElseThrow(() -> new IllegalArgumentException("Failed to find artist with id: " + maxId));
        log.debug("Found new artist of the day: {}", newArtistOfTheDay);
        return newArtistOfTheDay;
    }
}