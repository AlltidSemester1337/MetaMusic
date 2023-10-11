package com.demo.metamusic.core.service;


import com.demo.metamusic.adapter.persistence.ArtistInformationRepository;
import com.demo.metamusic.adapter.persistence.TrackInformationRepository;
import com.demo.metamusic.adapter.persistence.dto.ArtistInformationDTO;
import com.demo.metamusic.core.model.TrackInformation;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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
        List<ArtistInformationDTO> matchingArtists = artistInformationRepository.findByName(trackInformation.artist());
        if (matchingArtists.isEmpty()) {
            throw new IllegalArgumentException("Could not find any artist with provided name: " + trackInformation.artist());
        }
        Validate.isTrue(matchingArtists.size() == 1, "Found multiple matching artists with name: " + trackInformation.artist());

        Long artistId = matchingArtists.get(0).getId();
        trackInformationRepository.save(TrackInformation.toDTO(trackInformation, artistId));
    }
}
