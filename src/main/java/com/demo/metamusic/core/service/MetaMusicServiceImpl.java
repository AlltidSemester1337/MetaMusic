package com.demo.metamusic.core.service;


import com.demo.metamusic.adapter.persistence.ArtistAlreadyExistsException;
import com.demo.metamusic.adapter.persistence.ArtistRepository;
import com.demo.metamusic.adapter.persistence.NoArtistFoundException;
import com.demo.metamusic.adapter.persistence.dto.ArtistEntity;
import com.demo.metamusic.adapter.persistence.dto.TrackEntity;
import com.demo.metamusic.core.model.Artist;
import com.demo.metamusic.core.model.Track;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;

@Slf4j
public class MetaMusicServiceImpl implements MetaMusicService {

    @Autowired
    private ArtistRepository ArtistRepository;

    public MetaMusicServiceImpl(ArtistRepository ArtistRepository) {
        this.ArtistRepository = ArtistRepository;
    }

    @Override
    public void addTrack(String artistName, Track Track) {
        ArtistEntity artistToUpdate = getSingleMatchingArtistByName(artistName);

        TrackEntity newTrack = Track.toEntity(Track);
        newTrack.setArtist(artistToUpdate);
        artistToUpdate.getTracks().add(newTrack);
        ArtistEntity updatedArtist = ArtistRepository.save(artistToUpdate);
        log.debug("Updated artist: {}", updatedArtist);
    }

    private ArtistEntity getSingleMatchingArtistByName(String artistName) {
        List<ArtistEntity> matchingArtists = ArtistRepository.findByName(artistName);
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

        ArtistEntity updatedEntity = ArtistRepository.save(artistToUpdate);
        log.debug("Updated artist: {}", updatedEntity);
        return Artist.fromEntity(updatedEntity);
    }

    @Override
    public Page<Track> getArtistTracksPaginated(String artistName, int page, int numTracks) {
        ArtistEntity Artist = getSingleMatchingArtistByName(artistName);
        PageRequest pageRequest = PageRequest.of(page, numTracks);
        return ArtistRepository.fetchTracksPaginated(Artist.getId(), pageRequest)
                .map(Track::fromEntity);
    }

    private void verifyNewArtistNameDoesNotExist(String artistName) {
        List<ArtistEntity> matchingArtists = ArtistRepository.findByName(artistName);
        log.debug("Found matching artists: {}", matchingArtists);

        if (!matchingArtists.isEmpty()) {
            throw new ArtistAlreadyExistsException("Found already existing artist with name: " + artistName);
        }
    }
}