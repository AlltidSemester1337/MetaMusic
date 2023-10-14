package com.demo.metamusic.core.service;


import com.demo.metamusic.adapter.persistence.ArtistAlreadyExistsException;
import com.demo.metamusic.adapter.persistence.ArtistInformationRepository;
import com.demo.metamusic.adapter.persistence.NoArtistFoundException;
import com.demo.metamusic.adapter.persistence.TrackInformationRepository;
import com.demo.metamusic.adapter.persistence.dto.ArtistInformationEntity;
import com.demo.metamusic.adapter.persistence.dto.TrackInformationEntity;
import com.demo.metamusic.core.model.ArtistInformation;
import com.demo.metamusic.core.model.TrackInformation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Slf4j
public class MetaMusicServiceImpl implements MetaMusicService {

    @Autowired
    private final TrackInformationRepository trackInformationRepository;
    @Autowired
    private ArtistInformationRepository artistInformationRepository;

    public MetaMusicServiceImpl(TrackInformationRepository trackInformationRepository, ArtistInformationRepository artistInformationRepository) {
        this.trackInformationRepository = trackInformationRepository;
        this.artistInformationRepository = artistInformationRepository;
    }

    @Override
    public void addTrack(String artistName, TrackInformation trackInformation) {
        ArtistInformationEntity artistToUpdate = getSingleMatchingArtistByName(artistName);

        TrackInformationEntity newTrack = TrackInformation.toEntity(trackInformation);
        newTrack.setArtist(artistToUpdate);
        artistToUpdate.getTracks().add(newTrack);
        ArtistInformationEntity updatedArtist = artistInformationRepository.save(artistToUpdate);
        log.debug("Updated artist information: {}", updatedArtist);
    }

    private ArtistInformationEntity getSingleMatchingArtistByName(String artistName) {
        List<ArtistInformationEntity> matchingArtists = artistInformationRepository.findByName(artistName);
        log.debug("Found matching artists: {}", matchingArtists);

        if (matchingArtists.isEmpty()) {
            throw new NoArtistFoundException("Could not find any artist with provided name: " + artistName);
        }

        Validate.isTrue(matchingArtists.size() == 1, "Found multiple matching artists with name: " + artistName);
        return matchingArtists.get(0);
    }

    @Override
    public ArtistInformation updateArtistInformation(String oldArtistName, ArtistInformation newArtistInformation) {
        verifyNewArtistNameDoesNotExist(newArtistInformation.name());
        ArtistInformationEntity artistToUpdate = getSingleMatchingArtistByName(oldArtistName);

        artistToUpdate.setName(newArtistInformation.name());

        Set<String> aliases = new HashSet<>(artistToUpdate.getAliases());
        aliases.addAll(newArtistInformation.aliases());
        artistToUpdate.setAliases(aliases);

        ArtistInformationEntity updatedEntity = artistInformationRepository.save(artistToUpdate);
        log.debug("Updated artist information: {}", updatedEntity);
        return ArtistInformation.fromEntity(updatedEntity);
    }

    private void verifyNewArtistNameDoesNotExist(String artistName) {
        List<ArtistInformationEntity> matchingArtists = artistInformationRepository.findByName(artistName);
        log.debug("Found matching artists: {}", matchingArtists);

        if (!matchingArtists.isEmpty()) {
            throw new ArtistAlreadyExistsException("Found already existing artist with name: " + artistName);
        }
    }
}