package com.demo.metamusic.core.service;

import com.demo.metamusic.core.model.ArtistInformation;
import com.demo.metamusic.core.model.TrackInformation;

import java.util.Optional;

public interface MetaMusicService {


    void addTrack(TrackInformation trackInformation);

    ArtistInformation updateArtistInformation(String artistName, ArtistInformation newArtistInformation);
}
