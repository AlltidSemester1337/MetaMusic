package com.demo.metamusic.core.service;


import com.demo.metamusic.adapter.persistence.ArtistInformationRepository;
import com.demo.metamusic.adapter.persistence.TrackInformationRepository;
import com.demo.metamusic.adapter.persistence.dto.ArtistInformationEntity;
import com.demo.metamusic.adapter.persistence.dto.TrackInformationEntity;
import com.demo.metamusic.core.model.ArtistInformation;
import com.demo.metamusic.core.model.TrackInformation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

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
    public void addTrack(TrackInformation trackInformation) {
        List<ArtistInformationEntity> matchingArtists = artistInformationRepository.findByName(trackInformation.artist());
        log.debug("Found matching artists: {}", matchingArtists);

        if (matchingArtists.isEmpty()) {
            throw new IllegalArgumentException("Could not find any artist with provided name: " + trackInformation.artist());
        }

        Validate.isTrue(matchingArtists.size() == 1, "Found multiple matching artists with name: " + trackInformation.artist());

        ArtistInformationEntity artistToUpdate = matchingArtists.get(0);
        TrackInformationEntity newTrack = TrackInformation.toEntity(trackInformation);
        newTrack.setArtist(artistToUpdate);
        artistToUpdate.getTracks().add(newTrack);
        ArtistInformationEntity updatedArtist = artistInformationRepository.save(artistToUpdate);
        log.debug("Updated artist information: {}", updatedArtist);
    }

    @Override
    public Optional<ArtistInformation> updateArtistInformation(String artistName, ArtistInformation newArtistInformation) {
        return Optional.empty();
    }
}