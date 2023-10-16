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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Slf4j
public class MetaMusicServiceImpl implements MetaMusicService {

    public static final ZoneId DEFAULT_ZONE = ZoneId.of("Europe/Paris");
    @Autowired
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

        Set<String> aliases = new HashSet<>(artistToUpdate.getAliases());
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
        return artistRepository.fetchTracksPaginated(Artist.getId(), pageRequest)
                .map(Track::fromEntity);
    }

    @Override
    public Optional<Artist> getArtistOfTheDay() {
        if (artistRepository.count() == 0) {
            return Optional.empty();
        }

        Optional<ArtistEntity> possibleMostRecentArtistOfTheDay = artistRepository.getMostRecentArtistOfTheDay();
        if (possibleMostRecentArtistOfTheDay.isEmpty()) {
            return Optional.of(Artist.fromEntity(assignNewArtistOfTheDay(1L)));
        }

        ArtistEntity mostRecentArtistOfTheDay = possibleMostRecentArtistOfTheDay.get();
        if (isExpired(mostRecentArtistOfTheDay)) {
            return Optional.of(updateArtistOfTheDay(mostRecentArtistOfTheDay));
        }
        return Optional.of(Artist.fromEntity(mostRecentArtistOfTheDay));
    }

    private ArtistEntity assignNewArtistOfTheDay(Long mostRecentArtistOfTheDayId) {
        ArtistEntity newArtistOfTheDay = findNewArtistOfTheDay(mostRecentArtistOfTheDayId);
        newArtistOfTheDay.setDayRotation(new ArtistDayRotationEntity(Date.valueOf(LocalDate.now(DEFAULT_ZONE)), true));
        return artistRepository.save(newArtistOfTheDay);
    }

    private boolean isExpired(ArtistEntity mostRecentArtistOfTheDay) {
        return mostRecentArtistOfTheDay.getDayRotation().getDate().toLocalDate()
                .isBefore(LocalDate.now(DEFAULT_ZONE));
    }

    @Transactional
    public Artist updateArtistOfTheDay(ArtistEntity mostRecentArtistOfTheDay) {
        ArtistEntity newArtistOfTheDay = assignNewArtistOfTheDay(mostRecentArtistOfTheDay.getId());
        mostRecentArtistOfTheDay.getDayRotation().setMostRecent(false);
        artistRepository.save(mostRecentArtistOfTheDay);

        return Artist.fromEntity(newArtistOfTheDay);
    }

    private ArtistEntity findNewArtistOfTheDay(Long mostRecentId) {
        long current = mostRecentId - 1;
        while (0 < current) {
            Optional<ArtistEntity> possibleNextArtistOfTheDay = artistRepository.findById(current);
            if (possibleNextArtistOfTheDay.isPresent()) {
                return possibleNextArtistOfTheDay.get();
            }
            current--;
        }
        Long maxId = artistRepository.findMaxId()
                .orElseThrow(() -> new IllegalStateException("Could not fetch max id value in artist table despite table assumed not empty"));
        return artistRepository.findById(maxId)
                .orElseThrow(() -> new IllegalArgumentException("Failed to find artist with id: " + maxId));
    }
}