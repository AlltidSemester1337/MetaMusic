package com.demo.metamusic.core.service;

import com.demo.metamusic.core.model.ArtistInformation;
import com.demo.metamusic.core.model.TrackInformation;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MetaMusicService {


    void addTrack(String artistName, TrackInformation trackInformation);

    ArtistInformation updateArtistInformation(String oldArtistName, ArtistInformation newArtistInformation);

    Page<TrackInformation> getArtistTracksPaginated(String artistName, int page, int numTracks);
}
